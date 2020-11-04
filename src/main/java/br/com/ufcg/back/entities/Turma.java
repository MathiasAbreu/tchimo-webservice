package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.grupo.GrupoNotFoundException;
import br.com.ufcg.back.exceptions.turma.TurmaMaximoGruposException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Turma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String linkIdentifier;
    private Long managerID;
    private ArrayList<Long> memberIDs;

    private int numMinGrupos;
    private int numMaxGrupos;
    private int numMinAlunosPorGrupo;
    private int numMaxAlunosPorGrupo;

    private ArrayList<Grupo> groups;

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

    public void adicionaUsuarioANovoGrupo(Long usrId) throws TurmaMaximoGruposException, UserAlreadyExistException {
        if (groups.size() < numMaxGrupos) {
            Grupo grupo = new Grupo();
            grupo.adicionaUsuario(usrId);
            groups.add(grupo);
        }
        else throw new TurmaMaximoGruposException();
    }

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
