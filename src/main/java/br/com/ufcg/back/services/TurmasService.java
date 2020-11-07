package br.com.ufcg.back.services;

import java.util.List;
import java.util.Random;

import br.com.ufcg.back.daos.TurmasDAO;
import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.exceptions.grupo.GrupoNotFoundException;
import br.com.ufcg.back.exceptions.turma.TurmaMaximoGruposException;
import br.com.ufcg.back.exceptions.turma.TurmaNotFoundException;
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

    public Turma findByID(String id) throws TurmaNotFoundException {
        Turma t = turmasDAO.findByID(id);
        if (t == null)
            throw new TurmaNotFoundException();
        else return turmasDAO.findByID(id);
    }

    public List<Turma> findAll() {
        return turmasDAO.findAll();
    }

    /*
        Necessita revisão
     */
    /*public void adicionaUsuarioANovoGrupo(Long id, Long usrId) throws TurmaMaximoGruposException, UserAlreadyExistException, TurmaNotFoundException {
        Turma t = findByID(id);
        t.adicionaUsuarioANovoGrupo(usrId);
        create(t);
    }*/

    public void removeUsuarioDeGrupo(String id, Long groupID, Long usrId) throws UserNotFoundException, GrupoNotFoundException, TurmaNotFoundException {
        Turma t = findByID(id);
        t.removeUsuarioDeGrupo(groupID, usrId);
        create(t);
    }

    public Grupo[] listarGrupos(String id) throws TurmaNotFoundException {
        return findByID(id).listarGrupos();
    }

    public String criaTurma(Turma turma) {

        turma.setId(gerarId());
        turmasDAO.save(turma);
        return turma.getId();
    }

    private String gerarId() {

        String retorno = "";
        Random random = new Random();

        while(retorno.length() < 8) {

            int number = random.nextInt(16);
            int decision = random.nextInt(2);

            if(decision > 0)
                retorno = (number > 10) ? String.format("%s%d",retorno,(number - 10)) : String.format("%s%d",retorno,(number));
            else
                retorno = String.format("%s%s",retorno,Character.toString((char) (65 + number)));
        }
        if(turmasDAO.existsById(retorno))
            return gerarId();
        else
            return retorno;
    }
}
