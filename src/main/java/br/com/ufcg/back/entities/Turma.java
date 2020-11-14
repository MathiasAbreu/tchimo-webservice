package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.grupo.GrupoNotFoundException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

@Entity
public class Turma {
    @Id
    private String id;
    private String name;
    private long creationDate;
    private long endDate;

    @ApiModelProperty(value = "Usuario que criou a turma.")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idUser")
    private Usuario manager;

    @ManyToMany(mappedBy = "membersTurma")
    private List<Usuario> integrantes = new ArrayList<>();

    @OneToMany(mappedBy = "idGroup", fetch = FetchType.LAZY)
    private List<Grupo> groups = new ArrayList<>();

    private String formationStrategy;
    private String endingStrategy;

    private int quantityOfGroups;

    @JsonCreator
    public Turma(String name, String formationStrategy, String endingStrategy, int quantityOfGroups, int endTime, int minutes) {

        super();

        this.name = name;
        this.creationDate = ((new Date()).getTime() / 1000L);

        this.formationStrategy = formationStrategy;
        this.endingStrategy = endingStrategy;

        this.quantityOfGroups = quantityOfGroups;
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

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNumGrupos() {
        return quantityOfGroups;
    }

    public void setNumGrupos(int numGrupos) {
        this.quantityOfGroups = numGrupos;
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

    private Grupo grupoComId(Long groupID) throws GrupoNotFoundException {
        Grupo grupo = null;

        for (Grupo g : groups)
            if (g.getIdGroup().equals(groupID))
                grupo = g;

        if (grupo == null)
            throw new GrupoNotFoundException();

        return grupo;
    }

    private void removeGrupo(Long groupID) throws GrupoNotFoundException {
        groups.remove(grupoComId(groupID));
    }

    public void adicionaGrupo(Grupo grupo) {
        groups.add(grupo);
    }

    /*
        Necessita revisão
     */
    /*public void adicionaUsuarioANovoGrupo(Long usrId) throws TurmaMaximoGruposException, UserAlreadyExistException {
        if (groups.size() < numMaxGrupos) {
            Grupo grupo = new Grupo();
            grupo.adicionaUsuario(usrId);
            groups.add(grupo);
        }
        else throw new TurmaMaximoGruposException();
    }*/

    public void removeUsuarioDeGrupo(Long groupID, Long usrId) throws UserNotFoundException, GrupoNotFoundException {
        Grupo grupo = grupoComId(groupID);

        grupo.removeUsuario(usrId);
        if (grupo.quantidadeDeMembros() == 0)
            removeGrupo(groupID);
    }

    public Grupo[] listarGrupos() {
        return (Grupo[]) groups.toArray();
    }
}
