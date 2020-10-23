package br.com.ufcg.back.services;

import br.com.ufcg.back.daos.UsuariosDAO;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuariosService {

    private UsuariosDAO<Usuario, String> usuariosDao;

    public UsuariosService(UsuariosDAO<Usuario, String> usuariosDao) {

        super();
        this.usuariosDao = usuariosDao;
    }

    public void adicionaUsuario(Usuario usuario) throws UserException {

        Optional<Usuario> verificaUsuario = usuariosDao.findById(usuario.getEmail());

        if(!verificaUsuario.isPresent())
            usuariosDao.save(usuario);
        else
            throw new UserAlreadyExistException(usuario.getEmail());
    }

    /**
     * Método apenas para consulta ao banco de dados com o intuito de verificar se o usuário está sendo realmente criado
     * e guardado lá.
     *
     */
    public Optional<Usuario> getUsuario(String email) {

        return usuariosDao.findById(email);
    }

    public void rotinaDeLimpeza() {

        usuariosDao.deleteAll();
    }
}
