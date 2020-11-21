package br.com.ufcg.back.services;

import br.com.ufcg.back.daos.NotificationDAO;
import br.com.ufcg.back.daos.UsuariosDAO;
import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Notifications;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.entities.dtos.GrupoDTO;
import br.com.ufcg.back.entities.dtos.TurmaDTO;
import br.com.ufcg.back.entities.dtos.UsuarioDTO;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import br.com.ufcg.back.services.comparators.ComparatorNotificationsByDate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UsuariosService {

    private UsuariosDAO<Usuario, String> usuariosDao;
    private NotificationDAO<Notifications, Long> notificationDAO;

    public UsuariosService(UsuariosDAO<Usuario, String> usuariosDao, NotificationDAO<Notifications, Long> notificationDAO) {

        super();
        this.usuariosDao = usuariosDao;
        this.notificationDAO = notificationDAO;
    }

    public void adicionaUsuario(Usuario usuario) throws UserAlreadyExistException {

        Optional<Usuario> verificaUsuario = usuariosDao.findByEmail(usuario.getEmail());

        if(!verificaUsuario.isPresent())
            usuariosDao.save(usuario);
        else
            throw new UserAlreadyExistException("Usuário já existe: " + usuario.getEmail());
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

        throw new UserNotFoundException("Usuário não encontrado: " + email);
    }

    public List<Notifications> retornaNotificacoesUser(String emailUser) throws UserException {

        Optional<Usuario> usuario = usuariosDao.findByEmail(emailUser);
        if(usuario.isPresent()) {
            List<Notifications> notifications = notificationDAO.findByIdUser(usuario.get().getIdUser());
            Collections.sort(notifications, new ComparatorNotificationsByDate());
            return notifications;
        }
        throw new UserNotFoundException("Usuário não encontrado!");
    }
}
