package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.grupo.GrupoNotFoundException;
import br.com.ufcg.back.exceptions.turma.TurmaMaximoGruposException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Turma {
    @Id
    private String id;
    private String name;
    private long creationDate;
    private long endDate;

    private String managerId;

    private String trainingStrategy;
    private String closureForm;

    private ArrayList<String> memberIDs;
    private int numGrupos;

    private ArrayList<Grupo> groups;

    @JsonCreator
    public Turma(String name, String trainingStrategy, String closureForm, int endTime, int minutes) {

        super();

        this.name = name;
        this.creationDate = ((new Date()).getTime() / 1000L);

        this.trainingStrategy = trainingStrategy;
        this.closureForm = closureForm;

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

    public String getTrainingStrategy() {
        return trainingStrategy;
    }

    public String getClosureForm() {
        return closureForm;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    private Grupo grupoComId(Long groupID) throws GrupoNotFoundException {
        Grupo grupo = null;

        for (Grupo g : groups)
            if (g.getId().equals(groupID))
                grupo = g;

        if (grupo == null)
            throw new GrupoNotFoundException();

        return grupo;
    }

    private void removeGrupo(Long groupID) throws GrupoNotFoundException {
        groups.remove(grupoComId(groupID));
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
