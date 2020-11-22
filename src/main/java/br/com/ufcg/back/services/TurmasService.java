package br.com.ufcg.back.services;

import java.util.*;

import br.com.ufcg.back.daos.GruposDAO;
import br.com.ufcg.back.daos.NotificationDAO;
import br.com.ufcg.back.daos.TurmasDAO;
import br.com.ufcg.back.daos.UsuariosDAO;
import br.com.ufcg.back.entities.*;
import br.com.ufcg.back.entities.dtos.GrupoDTO;
import br.com.ufcg.back.entities.dtos.TurmaDTO;
import br.com.ufcg.back.entities.dtos.UsuarioDTO;
import br.com.ufcg.back.exceptions.grupo.GroupException;
import br.com.ufcg.back.exceptions.grupo.GroupNotFoundException;
import br.com.ufcg.back.exceptions.grupo.OverflowNumberOfGroupsException;
import br.com.ufcg.back.exceptions.turma.TurmaException;
import br.com.ufcg.back.exceptions.turma.TurmaLockedException;
import br.com.ufcg.back.exceptions.turma.TurmaManagerException;
import br.com.ufcg.back.exceptions.turma.TurmaNotFoundException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import br.com.ufcg.back.exceptions.user.UserUnauthorizedException;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;

@Service
public class TurmasService {

    private GruposDAO<Grupo, Long> gruposDAO;
    private TurmasDAO<Turma, String> turmasDAO;
    private UsuariosDAO<Usuario, Long> usuariosDAO;
    private NotificationDAO<Notification, Long> notificationDAO;

    public TurmasService(TurmasDAO turmasDAO, GruposDAO gruposDAO, UsuariosDAO usuariosDAO, NotificationDAO notificationDAO) {

        super();
        this.turmasDAO = turmasDAO;
        this.gruposDAO = gruposDAO;
        this.usuariosDAO = usuariosDAO;
        this.notificationDAO = notificationDAO;
    }

    public Turma create(Turma course) {
        return turmasDAO.save(course);
    }

    public TurmaDTO buscaTurma(String idTurma, String emailUser) throws TurmaNotFoundException, UserUnauthorizedException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        if(turma.isPresent()) {

            if(turma.get().getManager().getEmail().equals(emailUser) || turma.get().verificaSeUsuarioJaPertece(emailUser)) {
                Turma turmaVerificada = verifyTurmaLocked(turma.get());
                TurmaDTO turmaDTO = createTurmaDTO(turmaVerificada,(turmaVerificada.getManager().getEmail().equals(emailUser)));
                configureIntegrantes(turmaVerificada,turmaDTO);
                turmaDTO.setGroups(configureGrupos(turmaVerificada.getGroups()));
                return turmaDTO;
            }
            throw new UserUnauthorizedException("O usuário não possui autorização. ");
        }
        throw new TurmaNotFoundException("Turma não encontrada: " + idTurma);
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
        throw new UserNotFoundException("Usuário não encontrado.");
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

    public String addUsuarioEmTurma(String idTurma, String emailUser) throws TurmaManagerException, TurmaNotFoundException, UserException, TurmaLockedException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        if (turma.isPresent()) {

            if(turma.get().getLocked())
                throw new TurmaLockedException("Usuário não podem entrar em uma turma já trancada!");

            if (turma.get().getManager().getEmail().equals(emailUser))
                throw new TurmaManagerException("Usuário não pode entrar na turma pois é o manager dela!");

            if (turma.get().verificaSeUsuarioJaPertece(emailUser))
                throw new UserAlreadyExistException("Usuário já pertence a turma.");

            usuariosDAO.findByEmail(emailUser).map(record -> {
                record.addTurma(turma.get());
                return usuariosDAO.save(record);
            });

            turma.get().addUser(usuario.get());
            configureGroups(turma.get());
            turmasDAO.save(turma.get());
            return turma.get().getId();
        }
        throw new TurmaNotFoundException();
    }

    public Grupo addGrupo(String emailUser, String idTurma) throws TurmaException, UserUnauthorizedException, OverflowNumberOfGroupsException {
        Optional<Turma> turma = turmasDAO.findById(idTurma);
        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        if(turma.isPresent()) {
            if(turma.get().getLocked())
                throw new TurmaLockedException("Um grupo não pode ser criado em uma turma trancada.");

            if(turma.get().verificaSeUsuarioJaPertece(emailUser) && !turma.get().verificaSeUsuarioAlocado(usuario.get().getIdUser())) {

                int quantidadeDegrupos = turma.get().quantidadeGruposNaTurma();
                if(quantidadeDegrupos < turma.get().getQuantityOfGroups()) {

                    Grupo grupo = gruposDAO.save(new Grupo((quantidadeDegrupos + 1),emailUser,usuario.get().getIdUser()));
                    turma.get().adicionaGrupo(grupo);
                    turma.get().addQGrupo();
                    configureGroups(turma.get());
                    turmasDAO.save(turma.get());
                    return grupo;
                }
                throw new OverflowNumberOfGroupsException("A turma já atingiu o número permitido de grupos.");
            }
            throw new UserUnauthorizedException("Usuário não tem permissão para criar grupos.");
        }
        throw new TurmaNotFoundException("Turma não encontrada.");
    }

    public List<TurmaDTO> buscaTodasAsTurmas(String emailUser) {

        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        List<TurmaDTO> turmas = new ArrayList<>();

        for(Turma turma : usuario.get().getManagedTurma()) {
            turma = verifyTurmaLocked(turma);
            TurmaDTO turmaDTO = createTurmaDTO(turma, true);
            configureIntegrantes(turma,turmaDTO);
            turmaDTO.setGroups(configureGrupos(turma.getGroups()));
            turmas.add(turmaDTO);
        }
        for(Turma turma : usuario.get().getMembersTurma()) {
        turma = verifyTurmaLocked(turma);
            TurmaDTO turmaDTO = createTurmaDTO(turma, false);
            configureIntegrantes(turma,turmaDTO);
            turmaDTO.setGroups(configureGrupos(turma.getGroups()));
            turmas.add(turmaDTO);
        }

        return turmas;
    }
    private TurmaDTO createTurmaDTO(Turma turma, boolean usuario) {
    	TurmaDTO dto = new TurmaDTO();
    	dto.setId(turma.getId());
        dto.setName(turma.getName());
        dto.setCreationDate(turma.getCreationDate());
        dto.setEndDate(turma.getEndDate());
        dto.setFormationStrategy(turma.getFormationStrategy());
        dto.setEndingStrategy(turma.getEndingStrategy());
        dto.setMaxNumberOfGroups(turma.getQuantityOfGroups());
        dto.setCurrentNumberOfGroups(turma.getTotalNumberOfGroups());
        dto.setUsuario(usuario);
        dto.setLocked(turma.getLocked());
    	return dto;
    }

    private void configureIntegrantes(Turma turma, TurmaDTO turmaDTO) {

        for(Usuario usuario: turma.getIntegrantes()) {

            turmaDTO.addIntegrante(new UsuarioDTO(usuario.getIdUser(), usuario.getName()));
            if(!turma.verificaSeUsuarioAlocado(usuario.getIdUser()))
                turmaDTO.addIntegranteSemGrupo(new UsuarioDTO(usuario.getIdUser(), usuario.getName()));
        }
    }

    private List<GrupoDTO> configureGrupos(List<Grupo> grupos) {

        ArrayList<GrupoDTO> groups = new ArrayList<>();
        for(Grupo grupo : grupos) {

            GrupoDTO grupoDTO = new GrupoDTO(grupo.getIdGroup(),recuperaIdUser(grupo.getEmailManager()));
            for(Long idUsers : grupo.getMemberIDs()) {

                Optional<Usuario> usuario = usuariosDAO.findById(idUsers);
                if(usuario.isPresent()) {
                    UsuarioDTO usuarioDTO = new UsuarioDTO(idUsers,usuario.get().getName());
                    grupoDTO.addUserDTO(usuarioDTO);
                }
            }
            groups.add(grupoDTO);
        }
        return groups;
    }

    private Long recuperaIdUser(String emailUser) {

        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        if(usuario.isPresent())
            return usuario.get().getIdUser();

        return 0L;
    }

        public String removeUserFromTurma(String idTurma, String emailUser) throws UserException, TurmaException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        if(turma.isPresent()) {

            if(turma.get().getLocked())
                throw new TurmaLockedException("Não é possivel sair de uma turma já fechada!");

            if(turma.get().verificaSeUsuarioJaPertece(emailUser)) {

                Grupo grupo = turma.get().removeUser(emailUser);
                if(grupo != null) {
                    if(grupo.getEmailManager().equals(emailUser))
                        turma.get().removeGrupo(grupo.getIdGroup());
                    else
                        turma.get().substituiGrupo(grupo);
                }
                configureGroups(turma.get());
                turmasDAO.save(turma.get());
                usuariosDAO.findByEmail(emailUser).map(record -> {
                    record.removeTurma(idTurma);
                    return usuariosDAO.save(record);
                });
                return "Operacão bem sucedida.";
            }
            throw new UserNotFoundException("Usuário não pertence a turma!");
        }
        throw new TurmaNotFoundException("Turma não encontrada!");
    }

    public String addUsuarioEmGrupo(String idTurma, Long idGroup, String emailUser) throws TurmaException, UserException, GroupException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        if(turma.isPresent()) {

            if(turma.get().verificaSeUsuarioJaPertece(emailUser)) {
                turma.get().addUserFromGroup(idGroup,emailUser);
                return "Usuário adicionado com sucesso!";
            }
            throw new UserNotFoundException("Usuário não pertence a turma!");
        }
        throw new TurmaNotFoundException("Turma não encontrada!");
    }

    public String removeUserFromGroup(String id, Long groupID, String emailUser) throws UserNotFoundException, GroupNotFoundException, TurmaNotFoundException, TurmaLockedException {

        Optional<Turma> turma = turmasDAO.findById(id);
        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);

        if(turma.isPresent()) {

            if(turma.get().getLocked())
                throw new TurmaLockedException("Não é possível sair de um grupo após o fechamento da turma.");

            if(usuario.isPresent()) {
                turma.get().removeUserFromGroup(groupID, usuario.get().getIdUser(), emailUser);
                turmasDAO.save(turma.get());
                return "Usuário removido com sucesso!";
            }
            throw new UserNotFoundException("Usuário não encontrado.");
        }
        throw new TurmaNotFoundException("Turma não encontrada!");
    }

    public String removeTurma(String idturma, String emailUser) throws TurmaException, UserException {

        Optional<Turma> turma = turmasDAO.findById(idturma);
        if(turma.isPresent()) {
            if(turma.get().getManager().getEmail().equals(emailUser)) {
                List<Usuario> integrantes = turma.get().getIntegrantes();

                for (Usuario usuario : integrantes)
                    usuariosDAO.findById(usuario.getIdUser()).map(record -> {
                        record.removeTurma(turma.get().getId());
                        return usuariosDAO.save(record);
                    });
                usuariosDAO.findByEmail(emailUser).map(record -> {
                    record.removeTurmaManager(idturma);
                    return usuariosDAO.save(record);
                });

                turmasDAO.delete(turma.get());
                return "Turma deletada com sucesso.";
            }
            throw new UserUnauthorizedException("Usuário não pode apagar uma turma que não é sua.");
        }
        throw new TurmaNotFoundException("Turma não encontrada.");
    }

    public String solicitaEntradaEmGrupo(Notification notification, String emailUser) throws UserException, GroupException, TurmaLockedException {

        Optional<Turma> turma = turmasDAO.findById(notification.getId_turma());

        Optional<Usuario> usuarioManager;
        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);

        if(usuario.isPresent()) {
            if(turma.get().getLocked())
                throw new TurmaLockedException("Solicitação para entrar em grupo não podem ocorrer em uma turma fechada!");

            if(turma.get().verificaGrupoAloca(notification.getId_group())) {
                String emailManager = turma.get().returnIdManagerGroup(notification.getId_group());
                usuarioManager = usuariosDAO.findByEmail(emailManager);

                notification.setId_user(usuarioManager.get().getIdUser());
                notification.setTargetUser(usuario.get().getIdUser());

                usuariosDAO.findByEmail(emailManager).map(record -> {
                   record.addNotification(notificationDAO.save(notification));
                   return usuariosDAO.save(record);
                });

                return "Solicitação para entrar no grupo criada!";
            }
            throw new GroupNotFoundException("Grupo não encontrado!");
        }
        throw new UserNotFoundException("Usuário não encontrado!");
    }

    public String processaResposta(Response resposta, String emailUser) throws UserException, TurmaException, GroupException {

        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        Optional<Notification> notification = notificationDAO.findById(resposta.getId_notification());
        Optional<Turma> turma = turmasDAO.findById(notification.get().getId_turma());
        if(turma.get().getLocked())
            resposta.setProcedure(false);

        if(notification.get().getType().equals("ENTRY-GROUP"))
            return processaEntradaGrupo(notification.get(), resposta, usuario.get());
        else
            return processaConviteGrupo(notification.get(), resposta, usuario.get());
    }

    public String processaEntradaGrupo(Notification notification, Response resposta, Usuario usuario) throws UserException, GroupException, TurmaException {
        if(notification.getId_user().equals(usuario.getIdUser())) {
            if(resposta.isProcedure()) {

                Optional<Usuario> usuarioParaGrupo = usuariosDAO.findById((notification.getTargetUser()));
                if(usuarioParaGrupo.isPresent()) {
                    addUsuarioEmGrupo(notification.getId_turma(), notification.getId_group(), usuarioParaGrupo.get().getEmail());
                    adicionaNotificacaoDeConfirmacao(notification,notification.getTargetUser(), "ACK-SOLICITATION");
                }
                else
                    throw new UserNotFoundException("Usuário que requisitou entrada no grupo não foi encontrado!");
            }
            removeSolicitacao(notification);
            return "Solicitação respondida com sucesso!";
        }
        throw new UserUnauthorizedException("O usuário não pode responder uma solicitação que não lhe pertence.");
    }

    public String processaConviteGrupo(Notification notification, Response resposta, Usuario usuario) throws UserException, GroupException, TurmaException {
        if(notification.getId_user().equals(usuario.getIdUser())) {
            if(resposta.isProcedure()) {
                addUsuarioEmGrupo(notification.getId_turma(), notification.getId_group(), usuario.getEmail());
                adicionaNotificacaoDeConfirmacao(notification,notification.getTargetUser(), "ACK-INVITATION");
            }
            removeSolicitacao(notification);
            return "Resposta enviada com sucesso.";
        }
        throw new UserUnauthorizedException("O usuário não pode responder um convite que não lhe pertence.");
    }

    public String criarConviteParaGrupo(Notification notification, String emailUser) throws TurmaException, UserException {

        Optional<Turma> turma = turmasDAO.findById(notification.getId_turma());
        Optional<Usuario> usuario = usuariosDAO.findById(notification.getId_user());
        Optional<Usuario> usuarioAlvo = usuariosDAO.findByEmail(emailUser);

        if(turma.isPresent()) {
            if(turma.get().getLocked())
                throw new TurmaLockedException("Convites para entrada em grupo não sãoa aceitos após o fechamento da turma.");

            if(usuario.isPresent() && usuarioAlvo.isPresent()) {
                notification.setTargetUser(usuarioAlvo.get().getIdUser());
                usuariosDAO.findById(usuario.get().getIdUser()).map(record -> {
                   record.addNotification(notificationDAO.save(notification));
                   return usuariosDAO.save(record);
                });
                return "Convite enviado!";
            }
            throw new UserNotFoundException("Usuario não foi encontrado!");
        }
        throw new TurmaNotFoundException("A Turma não foi encontrada!");
    }

    private void removeSolicitacao(Notification notification) {

        usuariosDAO.findById(notification.getId_user()).map(record -> {
           record.removeNotification(notification);
           return usuariosDAO.save(record);
        });
        notificationDAO.delete(notification);
    }

    private void adicionaNotificacaoDeConfirmacao(Notification notification, Long idUser, String message) {

        Notification newNotification = notificationDAO.save(new Notification(notification.getId_user(),notification.getId_turma(),notification.getId_group(),message));
        usuariosDAO.findById(idUser).map(record -> {
            record.addNotification(newNotification);
            return usuariosDAO.save(record);
        });
    }

    public String fecharTurma(String idTurma, String emailUser) throws TurmaException, UserException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        if(turma.isPresent()) {
            if(turma.get().getManager().getEmail().equals(emailUser)) {
                if(turma.get().getEndingStrategy().equals("MANUAL")) {
                    turmasDAO.findById(idTurma).map(record -> {
                        record.setLocked(true);
                        return turmasDAO.save(record);
                    });
                    return "Turma encerrada!";
                }
                throw new TurmaManagerException("O gerente não pode encerrar manualmente uma turma do tipo 'CRONOMETRO'");
            }
            throw new UserUnauthorizedException("Somente o gerente da turma pode encerrar ela!");
        }
        throw new TurmaNotFoundException("Turma não encontrada!");
    }

    /*
        Método que faz o balanceamento de integrantes por grupo caso a turma tenha estrategia de formação 'UNIFORME'.
     */
    private void configureGroups(Turma turma) {

        if(turma.getFormationStrategy().equals("UNIFORME"))
            return;

        int numberIntegrantes = turma.getIntegrantes().size();
        int numberOfGrupos = turma.getQuantityOfGroups();

        if(numberIntegrantes == 0 || turma.getTotalNumberOfGroups() == 0)
            return;

        int[] integrantesPorGrupo = new int[numberOfGrupos];
        int index = 0;
        int auxiliar = 1;
        while(auxiliar <= numberIntegrantes) {
            if(index == integrantesPorGrupo.length)
                index = 0;

            integrantesPorGrupo[index] += 1;
            index += 1;
            auxiliar += 1;
        }

        turma.configureGroups(integrantesPorGrupo);
    }

    private Turma verifyTurmaLocked(Turma turma) {

        long timestampAtual = ((new Date()).getTime() / 1000L);
        if(timestampAtual >= turma.getEndDate() && turma.getEndingStrategy().equals("CRONOMETRO")) {
            return turmasDAO.findById(turma.getId()).map(record -> {
                record.setLocked(true);
                return turmasDAO.save(record);
            }).get();
        }
        return turma;
    }

    private void alocaUserInGroups(Turma turma) throws UserAlreadyExistException {

        if(turma.getFormationStrategy().equals("UNIFORME")) {

            List<Usuario> integrantesSemGrupo = turma.retornaIntegrantesSemGrupo();
            while (turma.quantidadeGruposNaTurma() < turma.getQuantityOfGroups()) {
                turma.adicionaGrupo(new Grupo(turma.quantidadeGruposNaTurma() + 1, integrantesSemGrupo.get(0).getEmail(), integrantesSemGrupo.get(0).getIdUser()));
                turma.addQGrupo();
                integrantesSemGrupo.remove(0);
                configureGroups(turma);
            }
        }

        turma.alocaUsersInGroups();
        turmasDAO.save(turma);
    }

    public String configureIntegrantesSemGrupo(String idTurma, String emailUser) {

        return "";
    }
}