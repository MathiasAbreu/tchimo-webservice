package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.grupo.GroupException;
import br.com.ufcg.back.exceptions.grupo.GroupNotFoundException;
import br.com.ufcg.back.exceptions.grupo.OverflowNumberOfGroupsException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Objects;

import java.util.Random;


import javax.persistence.*;

@Entity
public class Turma {

    @Id
    private String id;
    private String name;
    private long creationDate;
    private long endDate;

    @ApiModelProperty(value = "Usuario que criou a turma.")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "idUser")
    private Usuario manager;

    @ManyToMany(mappedBy = "membersTurma")
    private List<Usuario> integrantes = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "turmas_grupos", joinColumns = {
            @JoinColumn(name = "turma_grupo")}, inverseJoinColumns = {
            @JoinColumn(name = "group_id")})
    private List<Grupo> groups = new ArrayList<>();

    private String formationStrategy;
    private String endingStrategy;

    private int quantityOfGroups;
    private int totalNumberOfGroups;

    private boolean locked = false;

    @JsonCreator
    public Turma(String name, String formationStrategy, String endingStrategy, int quantityOfGroups, int endTime, int minutes) {

        super();

        this.name = name;
        this.creationDate = ((new Date()).getTime() / 1000L);

        this.formationStrategy = formationStrategy;
        this.endingStrategy = endingStrategy;

        this.quantityOfGroups = quantityOfGroups;
        this.totalNumberOfGroups = 0;

        this.endDate = creationDate + ((endTime * 3600) + (minutes * 60));
    }

    @JsonCreator
    public Turma() {

        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public String getFormationStrategy() {
        return formationStrategy;
    }

    public String getEndingStrategy() {
        return endingStrategy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantityOfGroups() {
        return quantityOfGroups;
    }

    public int getTotalNumberOfGroups() {
        return totalNumberOfGroups;
    }

    public void setQuantityOfGroups(int quantityOfGroups) {
        this.quantityOfGroups = quantityOfGroups;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Turma turma = (Turma) o;
        return id.equals(turma.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @JsonIgnore
    public Usuario getManager() {
        return manager;
    }

    public void setManager(Usuario manager) {
        this.manager = manager;
    }

    @JsonIgnore
    public List<Usuario> getIntegrantes() {
        return integrantes;
    }

    public List<Grupo> getGroups() {
        return groups;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public boolean verificaSeUsuarioJaPertece(String email) {
        for(Usuario usuario : integrantes) {
            if(usuario.getEmail().equals(email))
                return true;
        }
        return false;
    }

    public void addUser(Usuario usuario) {
        integrantes.add(usuario);
    }

    public int quantidadeGruposNaTurma() {
        return totalNumberOfGroups;
    }

    public void addQGrupo() {
        this.totalNumberOfGroups += 1;
    }

    private Grupo grupoComId(Long groupID) throws GroupNotFoundException {
        for(Grupo grupo : groups)
            if(grupo.getIdGroup().equals(groupID))
                return grupo;
        throw new GroupNotFoundException("Grupo não encontrado!");
    }

    public Grupo removeUser(String email) {

        long idCapturado = 0L;
        for(Usuario usuario : integrantes)
            if(usuario.getEmail().equals(email)) {
                integrantes.remove(usuario);
                idCapturado = usuario.getIdUser();
                break;
            }

        for(Grupo grupo : groups)
            if(grupo.usuarioParticipa(idCapturado)) {
                grupo.removeUsuario(idCapturado);
                return grupo;
            }
        return null;
    }

    public void adicionaGrupo(Grupo grupo) {
        groups.add(grupo);
    }

    public void substituiGrupo(Grupo grupo) {
        for(Grupo group : groups)
            if(group.getIdGroup().equals(grupo.getIdGroup())){
                groups.remove(group);
                groups.add(grupo);
                return;
            }
    }

    public void addUserFromGroup(Long idGroup, String emailUser) throws UserAlreadyExistException, GroupNotFoundException, OverflowNumberOfGroupsException {

        Usuario usuario = getUsuarioParaAdicionar(emailUser);

        for(Grupo grupo : groups) {

            if(grupo.getNumberOfMembers() < grupo.getNumberFoMembersPermitted() && formationStrategy.equals("UNIFORME")) {
                if (grupo.getIdGroup().equals(idGroup))
                    grupo.addUser(usuario.getIdUser());

            }
            throw new OverflowNumberOfGroupsException("O grupo não aceita mais integrantes!");
        }
        throw new GroupNotFoundException("O grupo não foi encontrado.");

    }

    private Usuario getUsuarioParaAdicionar(String emailUser) throws UserAlreadyExistException {
        for(Usuario usuario : integrantes)
            if(usuario.getEmail().equals(emailUser) && verificaSeUsuarioAlocado(usuario.getIdUser()))
                return usuario;
        throw new UserAlreadyExistException("O usuário já pertence a um grupo.");
    }

    public void removeUserFromGroup(Long groupID, Long idUser, String emailUser) throws UserNotFoundException, GroupNotFoundException {
        Grupo grupo = grupoComId(groupID);

        grupo.removeUser(idUser);
        if (grupo.amountOfMembers() == 0 || grupo.getEmailManager().equals(emailUser))
            removeGrupo(groupID);
    }

    public void removeGrupo(Long idGroup) {
        for(Grupo grupo : groups)
            if(grupo.getIdGroup().equals(idGroup)) {
                groups.remove(grupo);
                this.totalNumberOfGroups -= 1;
                return;
            }
    }

    public boolean verificaSeUsuarioAlocado(long idUser) {

        for(Grupo grupo : groups)
            if(grupo.usuarioParticipa(idUser) || grupo.getEmailManager().equals(manager.getEmail()))
                return true;
        return false;
    }

    public boolean verificaGrupo(long idUser) {

        for(Grupo grupo : groups)
            if(grupo.getIdGroup().equals(idUser))
                return true;
        return false;
    }

    public boolean verificaGrupoAloca(long idUser) {
        for(Grupo grupo : groups)
            if(grupo.getIdGroup().equals(idUser) && (grupo.getNumberFoMembersPermitted() == 0 || grupo.getNumberOfMembers() < grupo.getNumberFoMembersPermitted()))
                return true;
        return false;
    }

    public String returnIdManagerGroup(Long idGroup) throws GroupException {
        for(Grupo grupo : groups)
            if(grupo.getIdGroup().equals(idGroup))
                return grupo.getEmailManager();
        throw new GroupNotFoundException("Grupo não foi encontrado!");
    }

    public void configureGroups(int[] integrantesPorGrupo) {
        for(int i = 0; i < groups.size(); i++) {
            groups.get(i).setNumberFoMembersPermitted(integrantesPorGrupo[i]);
        }
    }

    public List<Usuario> retornaIntegrantesSemGrupo() {
        ArrayList<Usuario> integrantesSemGrupo = new ArrayList<>();
        for(Usuario usuario : integrantes)
            if(!verificaSeUsuarioAlocado(usuario.getIdUser()))
                integrantesSemGrupo.add(usuario);
        return integrantesSemGrupo;
    }

    public void alocaUsersInGroups(boolean typeDistribuiton) throws UserAlreadyExistException {

        ArrayList<Usuario> integrantesSemGrupo = new ArrayList<>();
        for(Usuario usuario : integrantes)
            if(!verificaSeUsuarioAlocado(usuario.getIdUser())) {
                integrantesSemGrupo.add(usuario);
            }

        int[] sorteio = new int[integrantesSemGrupo.size()];
        for(int i = 0; i < sorteio.length; i++)
            sorteio[i] = new Random().nextInt(integrantesSemGrupo.size());

        if(typeDistribuiton) {
            int index = 0;
            for (Grupo grupo : groups) {
                while (grupo.getNumberOfMembers() < grupo.getNumberFoMembersPermitted()) {
                    grupo.addUser(integrantesSemGrupo.get(sorteio[index]).getIdUser());
                    index += 1;
                }
            }
        }
        else {
            int index = 0;
            for(int i = 0; i < sorteio.length; i++) {
                if(index >= groups.size())
                    index = 0;

                groups.get(index).addUser(integrantesSemGrupo.get(sorteio[index]).getIdUser());
                index += 1;
            }
        }
    }

    public boolean verificaSeGruposConsistem() throws UserNotFoundException {
        for(Grupo grupo : groups) {
            if(grupo.getNumberFoMembersPermitted() < grupo.getNumberOfMembers()) {
                grupo.removeUser(grupo.getMemberIDs().get((grupo.getMemberIDs().size() - 1)));
                return false;
            }
        }
        return true;
    }
}
