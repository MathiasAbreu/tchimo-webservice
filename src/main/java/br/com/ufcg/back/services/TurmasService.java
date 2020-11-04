package br.com.ufcg.back.services;

import java.util.List;
import br.com.ufcg.back.daos.TurmasDAO;
import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.exceptions.grupo.GrupoNotFoundException;
import br.com.ufcg.back.exceptions.turma.TurmaMaximoGruposException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TurmasService {
    private final TurmasDAO turmasDAO;

    TurmasService(TurmasDAO turmasDAO) {
        this.turmasDAO = turmasDAO;
    }

    public Turma create(Turma course) {
        return turmasDAO.save(course);
    }

    public Turma findByID(Long id) {
        return turmasDAO.findByID(id);
    }

    public List<Turma> findAll() {
        return turmasDAO.findAll();
    }

    public void adicionaUsuarioANovoGrupo(Long id, Long usrId) throws TurmaMaximoGruposException, UserAlreadyExistException {
        Turma t = findByID(id);
        t.adicionaUsuarioANovoGrupo(usrId);
        create(t);
    }

    public void removeUsuarioDeGrupo(Long id, Long groupID, Long usrId) throws UserNotFoundException, GrupoNotFoundException {
        Turma t = findByID(id);
        t.removeUsuarioDeGrupo(groupID, usrId);
        create(t);
    }

    public Grupo[] listarGrupos(Long id) {
        return findByID(id).listarGrupos();
    }
}
