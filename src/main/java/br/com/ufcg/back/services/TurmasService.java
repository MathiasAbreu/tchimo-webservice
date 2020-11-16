package br.com.ufcg.back.services;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import br.com.ufcg.back.daos.TurmasDAO;
import br.com.ufcg.back.daos.UsuariosDAO;
import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.exceptions.grupo.GroupNotFoundException;
import br.com.ufcg.back.exceptions.grupo.OverflowNumberOfGroupsException;
import br.com.ufcg.back.exceptions.turma.TurmaException;
import br.com.ufcg.back.exceptions.turma.TurmaManagerException;
import br.com.ufcg.back.exceptions.turma.TurmaNotFoundException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import br.com.ufcg.back.exceptions.user.UserUnauthorizedException;
import org.springframework.stereotype.Service;

@Service
public class TurmasService {

    private final TurmasDAO turmasDAO;

    private UsuariosDAO<Usuario, Long> usuariosDAO;

    public TurmasService(TurmasDAO turmasDAO, UsuariosDAO usuariosDAO) {

        super();
        this.turmasDAO = turmasDAO;
        this.usuariosDAO = usuariosDAO;
    }

    public Turma create(Turma course) {
        return turmasDAO.save(course);
    }

    public Turma buscaTurma(String idTurma, String emailUser) throws TurmaNotFoundException, UserUnauthorizedException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        if(turma.isPresent()) {

            if(turma.get().getManager().getEmail().equals(emailUser) || turma.get().verificaSeUsuarioJaPertece(emailUser))
                return turma.get();
            throw new UserUnauthorizedException();
        }
        throw new TurmaNotFoundException();
    }

    public List<Turma> findAll() {
        return turmasDAO.findAll();
    }

    public String criaTurma(Turma turma, String emailUsuario) throws UserNotFoundException {

        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUsuario);
        if(usuario.isPresent()) {

            turma.setId(gerarId());
            turma.setManager(usuario.get());

            turmasDAO.save(turma);
            return turma.getId();
        }
        throw new UserNotFoundException();
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

    public String addUsuarioEmTurma(String idTurma, String emailUser) throws TurmaManagerException, TurmaNotFoundException, UserException {

        Turma turma = buscaTurma(idTurma, emailUser);
        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);

        if(turma.getManager().getEmail().equals(emailUser))
            throw new TurmaManagerException("Usuário não pode entrar na turma pois é o manager dela!");

        if(turma.verificaSeUsuarioJaPertece(emailUser))
            throw new UserAlreadyExistException("Usuário já pertence a turma.");

        usuariosDAO.findByEmail(emailUser).map(record -> {
            record.addTurma(turma);
            return usuariosDAO.save(record);
        });

        turma.addUser(usuario.get());
        create(turma);
        return turma.getId();
    }

    public Grupo addGrupo(String emailUser, String idTurma) throws TurmaException, UserUnauthorizedException, OverflowNumberOfGroupsException {
        Turma turma = buscaTurma(idTurma, emailUser);

        if (turma.verificaSeUsuarioJaPertece(emailUser)) {

            int currentAmountOfGroups = turma.getCurrentAmountOfGroups();
            if (currentAmountOfGroups < turma.getMaximumAmountOfGroups()) {

                Grupo grupo = new Grupo((currentAmountOfGroups + 1), emailUser);
                turma.adicionaGrupo(grupo);
                create(turma);
                return grupo;
            }
            throw new OverflowNumberOfGroupsException("A turma já atingiu o número permitido de grupos.");
        }
        throw new UserUnauthorizedException("Usuário não tem permissão para criar grupos.");
    }

    public Boolean removeUserFromGroup(String id, Long groupID, String emailUser) throws UserNotFoundException, GroupNotFoundException, TurmaNotFoundException, UserUnauthorizedException {
        Turma t = buscaTurma(id, emailUser);
        t.removeUserFromGroup(groupID, emailUser);
        create(t);
        return true;
    }

    public Grupo[] listGroups(String id, String usrEmail) throws TurmaNotFoundException, UserUnauthorizedException {
        return buscaTurma(id, usrEmail).listGroups();
    }

    public String[] listMembers(String id, String usrEmail) throws TurmaNotFoundException, UserUnauthorizedException {
        return buscaTurma(id, usrEmail).listMembers();
    }
}
