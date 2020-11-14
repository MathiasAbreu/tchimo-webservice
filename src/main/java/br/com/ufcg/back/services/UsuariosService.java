package br.com.ufcg.back.services;

import br.com.ufcg.back.daos.UsuariosDAO;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.entities.dtos.TurmaDTO;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuariosService {

    private UsuariosDAO<Usuario, String> usuariosDao;

    public UsuariosService(UsuariosDAO<Usuario, String> usuariosDao) {

        super();
        this.usuariosDao = usuariosDao;
    }

    public void adicionaUsuario(Usuario usuario) throws UserException {

        Optional<Usuario> verificaUsuario = usuariosDao.findByEmail(usuario.getEmail());

        if(!verificaUsuario.isPresent())
            usuariosDao.save(usuario);
        else
            throw new UserAlreadyExistException(usuario.getEmail());
    }

    public List<TurmaDTO> buscaTodasAsTurmas(String emailUser) {

        Optional<Usuario> usuario = usuariosDao.findByEmail(emailUser);
        List<TurmaDTO> turmas = new ArrayList<>();

        for(Turma turma : usuario.get().getManagedTurma())
            turmas.add(new TurmaDTO(turma.getName(),turma.getCreationDate(),turma.getEndDate(),turma.getFormationStrategy(),turma.getEndingStrategy(),turma.getQuantityOfGroups(),true,turma.getIntegrantes()));
        for(Turma turma : usuario.get().getMembersTurma())
            turmas.add(new TurmaDTO(turma.getName(),turma.getCreationDate(),turma.getEndDate(),turma.getFormationStrategy(),turma.getEndingStrategy(),turma.getQuantityOfGroups(),false,turma.getIntegrantes()));

        return turmas;
    }

    /**
     * Método apenas para consulta ao banco de dados com o intuito de verificar se o usuário está sendo realmente criado
     * e guardado lá.
     *
     */
    public Optional<Usuario> getUsuario(String email) {

        return usuariosDao.findByEmail(email);
    }

    public long getIdUsuario(String email) throws UserNotFoundException {

        Optional<Usuario> usuario = usuariosDao.findByEmail(email);
        if(usuario.isPresent())
            return usuario.get().getIdUser();

        throw new UserNotFoundException();
    }
}
