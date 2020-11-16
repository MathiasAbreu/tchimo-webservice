package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.grupo.GroupNotFoundException;
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

    private int maximumAmountOfGroups;

    @JsonCreator
    public Turma(String name, String formationStrategy, String endingStrategy, int maximumAmountOfGroups, int endTime, int minutes) {

        super();

        this.name = name;
        this.creationDate = ((new Date()).getTime() / 1000L);

        this.formationStrategy = formationStrategy;
        this.endingStrategy = endingStrategy;

        this.maximumAmountOfGroups = maximumAmountOfGroups;

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

    public int getMaximumAmountOfGroups() {
        return maximumAmountOfGroups;
    }

    public void setMaximumAmountOfGroups(int maximumAmountOfGroups) {
        this.maximumAmountOfGroups = maximumAmountOfGroups;
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

    public int getCurrentAmountOfGroups() {
        return groups.size();
    }

    private Grupo grupoComId(Long groupID) throws GroupNotFoundException {
        Grupo grupo = null;

        for (Grupo g : groups)
            if (g.getIdGroup().equals(groupID))
                grupo = g;

        if (grupo == null)
            throw new GroupNotFoundException();

        return grupo;
    }

    private void removeGroup(Long groupID) throws GroupNotFoundException {
        groups.remove(grupoComId(groupID));
    }

    public void adicionaGrupo(Grupo grupo) {
        groups.add(grupo);
    }

    public void removeUserFromGroup(Long groupID, String emailUser) throws UserNotFoundException, GroupNotFoundException {
        Grupo grupo = grupoComId(groupID);

        grupo.removeUser(emailUser);
        if (grupo.amountOfMembers() == 0)
            removeGroup(groupID);
    }

    public Grupo[] listGroups() {
        return (Grupo[]) groups.toArray();
    }
}
