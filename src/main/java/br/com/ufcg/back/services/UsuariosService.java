package br.com.ufcg.back.services;

import br.com.ufcg.back.daos.NotificationDAO;
import br.com.ufcg.back.daos.TurmasDAO;
import br.com.ufcg.back.daos.UsuariosDAO;
import br.com.ufcg.back.entities.Notification;
import br.com.ufcg.back.entities.Turma;
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
    private TurmasDAO<Turma, String> turmasDAO;
    private NotificationDAO<Notification, Long> notificationDAO;

    public UsuariosService(UsuariosDAO<Usuario, String> usuariosDao, TurmasDAO turmasDAO, NotificationDAO<Notification, Long> notificationDAO) {

        super();
        this.usuariosDao = usuariosDao;
        this.turmasDAO = turmasDAO;
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

        Optional<Usuario> usuario = usuariosDao.findById(notification.getTargetUser());
        Optional<Turma> turma = turmasDAO.findById(notification.getId_turma());

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setUser(new UsuarioDTO(notification.getTargetUser(),usuario.get().getName()));
        notificationDTO.setId_turma(notification.getId_turma());
        notificationDTO.setName_turma(turma.get().getName());
        notificationDTO.setCreationDate(notification.getCreationDate());
        notificationDTO.setId_group(notification.getId_group());
        notificationDTO.setType(notification.getType());
        return notificationDTO;
    }
}
