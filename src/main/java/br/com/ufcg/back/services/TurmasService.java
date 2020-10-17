package br.com.ufcg.back.services;

import java.util.List;
import br.com.ufcg.back.daos.TurmasDAO;
import br.com.ufcg.back.entities.Turma;
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
}
