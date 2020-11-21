package br.com.ufcg.back.services;

import br.com.ufcg.back.daos.NotificationDAO;
import br.com.ufcg.back.daos.UsuariosDAO;
import br.com.ufcg.back.entities.Notification;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.entities.dtos.NotificationDTO;
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
    private NotificationDAO<Notification, Long> notificationDAO;

    public UsuariosService(UsuariosDAO<Usuario, String> usuariosDao, NotificationDAO<Notification, Long> notificationDAO) {

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

    public List<NotificationDTO> retornaNotificacoesUser(String emailUser) throws UserException {

        Optional<Usuario> usuario = usuariosDao.findByEmail(emailUser);
        if(usuario.isPresent()) {
            List<Notification> notifications = usuario.get().getNotifications();
            Collections.sort(notifications, new ComparatorNotificationsByDate());

            List<NotificationDTO> notificationDTOS = new ArrayList<>();
            for(Notification notification : notifications) {
                notificationDTOS.add(createNotificationDTO(notification));
            }
            return notificationDTOS;
        }
        throw new UserNotFoundException("Usuário não encontrado!");
    }

    private NotificationDTO createNotificationDTO(Notification notification) {

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setId_user(notification.getId_user());
        notificationDTO.setId_turma(notification.getId_turma());
        notificationDTO.setCreationDate(notification.getCreationDate());
        notificationDTO.setId_group(notification.getId_group());
        notificationDTO.setType(notification.getType());
        notificationDTO.setTargetUsers(configureTargetUsersDTO(notification));
        return notificationDTO;
    }

    private List<UsuarioDTO> configureTargetUsersDTO(Notification notification) {

        List<UsuarioDTO> usuarioDTOS = new ArrayList<>();
        for(Long id_user : notification.getAlvos()) {

            Optional<Usuario> usuario = usuariosDao.findById(id_user);
            if(usuario.isPresent())
                usuarioDTOS.add(new UsuarioDTO(id_user,usuario.get().getName()));
        }
        return usuarioDTOS;
    }
}
