package br.com.ufcg.back.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import br.com.ufcg.back.daos.GruposDAO;
import br.com.ufcg.back.daos.TurmasDAO;
import br.com.ufcg.back.daos.UsuariosDAO;
import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.entities.dtos.GrupoDTO;
import br.com.ufcg.back.entities.dtos.TurmaDTO;
import br.com.ufcg.back.entities.dtos.UsuarioDTO;
import br.com.ufcg.back.exceptions.grupo.GroupException;
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

    private TurmasDAO turmasDAO;
    private GruposDAO<Grupo, Long> gruposDAO;
    private UsuariosDAO<Usuario, Long> usuariosDAO;

    public TurmasService(TurmasDAO turmasDAO, GruposDAO gruposDAO, UsuariosDAO usuariosDAO) {

        super();
        this.turmasDAO = turmasDAO;
        this.gruposDAO = gruposDAO;
        this.usuariosDAO = usuariosDAO;
    }

    public Turma create(Turma course) {
        return turmasDAO.save(course);
    }

    public TurmaDTO buscaTurma(String idTurma, String emailUser) throws TurmaNotFoundException, UserUnauthorizedException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        if(turma.isPresent()) {

            if(turma.get().getManager().getEmail().equals(emailUser) || turma.get().verificaSeUsuarioJaPertece(emailUser)) {
                TurmaDTO turmaDTO = createTurmaDTO(turma.get(),(turma.get().getManager().getEmail().equals(emailUser)));
                turmaDTO.setIntegrantes(configureIntegrantes(turma.get().getIntegrantes()));
                turmaDTO.setGroups(configureGrupos(turma.get().getGroups()));
                return turmaDTO;
            }
            throw new UserUnauthorizedException("O usu·rio n„o possui autorizaÁ„o. ");
        }
        throw new TurmaNotFoundException("Turma n„o encontrada: " + idTurma);
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
        throw new UserNotFoundException("Usu·rio n„o encontrado.");
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

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        if (turma.isPresent()) {

            if (turma.get().getManager().getEmail().equals(emailUser))
                throw new TurmaManagerException("Usu·rio n„o pode entrar na turma pois È o manager dela!");

            if (turma.get().verificaSeUsuarioJaPertece(emailUser))
                throw new UserAlreadyExistException("Usu·rio j· pertence a turma.");

            usuariosDAO.findByEmail(emailUser).map(record -> {
                record.addTurma(turma.get());
                return usuariosDAO.save(record);
            });

            turma.get().addUser(usuario.get());
            turmasDAO.save(turma.get());
            return turma.get().getId();
        }
        throw new TurmaNotFoundException();
    }

    public Grupo addGrupo(String emailUser, String idTurma) throws TurmaException, UserUnauthorizedException, OverflowNumberOfGroupsException {
        Optional<Turma> turma = turmasDAO.findById(idTurma);
        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        if(turma.isPresent()) {
            if(turma.get().verificaSeUsuarioJaPertece(emailUser)) {

                int quantidadeDegrupos = turma.get().quantidadeGruposNaTurma();
                if(quantidadeDegrupos < turma.get().getQuantityOfGroups()) {

                    Grupo grupo = gruposDAO.save(new Grupo((quantidadeDegrupos + 1),emailUser,usuario.get().getIdUser()));
                    turma.get().adicionaGrupo(grupo);
                    turma.get().addQGrupo();
                    turmasDAO.save(turma.get());
                    return grupo;
                }
                throw new OverflowNumberOfGroupsException("A turma j· atingiu o n˙mero permitido de grupos.");
            }
            throw new UserUnauthorizedException("Usu·rio n„o tem permiss„o para criar grupos.");
        }
        throw new TurmaNotFoundException("Turma n„o encontrada.");
    }

    public List<TurmaDTO> buscaTodasAsTurmas(String emailUser) {

        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        List<TurmaDTO> turmas = new ArrayList<>();

        for(Turma turma : usuario.get().getManagedTurma()) {
            TurmaDTO turmaDTO = createTurmaDTO(turma, true);
            turmaDTO.setIntegrantes(configureIntegrantes(turma.getIntegrantes()));
            turmaDTO.setGroups(configureGrupos(turma.getGroups()));
            turmas.add(turmaDTO);
        }
        for(Turma turma : usuario.get().getMembersTurma()) {

            TurmaDTO turmaDTO = createTurmaDTO(turma, false);
            turmaDTO.setIntegrantes(configureIntegrantes(turma.getIntegrantes()));
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
        dto.setQuantityOfGroupsAvailable(turma.getQuantityOfGroups());
        dto.setUsuario(usuario);
    	return dto;
    }

    private List<UsuarioDTO> configureIntegrantes(List<Usuario> integrantes) {

        ArrayList<UsuarioDTO> usuarioDTOS = new ArrayList<>();
        for(Usuario usuario: integrantes)
            usuarioDTOS.add(new UsuarioDTO(usuario.getIdUser(),usuario.getName()));
        return usuarioDTOS;
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

            if(turma.get().verificaSeUsuarioJaPertece(emailUser)) {

                Grupo grupo = turma.get().removeUser(emailUser);
                if(grupo != null) {
                    if(grupo.getEmailManager().equals(emailUser))
                        turma.get().removeGrupo(grupo.getIdGroup());
                    else
                        turma.get().substituiGrupo(grupo);
                }
                turmasDAO.save(turma.get());
                usuariosDAO.findByEmail(emailUser).map(record -> {
                    record.removeTurma(idTurma);
                    return usuariosDAO.save(record);
                });
                return "Operac„o bem sucedida.";
            }
            throw new UserNotFoundException("Usu·rio n„o pertence a turma!");
        }
        throw new TurmaNotFoundException("Turma n„o encontrada!");
    }

    public String addUsuarioEmGrupo(String idTurma, Long idGroup, String emailUser) throws TurmaException, UserException, GroupException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        if(turma.isPresent()) {

            if(turma.get().verificaSeUsuarioJaPertece(emailUser)) {
                turma.get().addUserFromGroup(idGroup,emailUser);
            }
            throw new UserNotFoundException("Usu√°rio n√£o pertence a turma!");
        }
        throw new TurmaNotFoundException("Turma n√£o encontrada!");
    }

    /*public Boolean removeUserFromGroup(String id, Long groupID, String emailUser) throws UserNotFoundException, GroupNotFoundException, TurmaNotFoundException, UserUnauthorizedException {
        Turma t = buscaTurma(id, emailUser);
        t.removeUserFromGroup(groupID, emailUser);
        create(t);
        return true;
    }*/

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
            throw new UserUnauthorizedException("Usu·rio n„o pode apagar uma turma que n„o È sua.");
        }
        throw new TurmaNotFoundException("Turma n„o encontrada.");
    }

    /*
    No metodo deve ter o id do usuario no formato long ao inves do email dele."
     */
    /*public void removerUsuarioDeTurma(String emailUser, String idTurma) throws TurmaNotFoundException, TurmaManagerException {

        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        Optional<Turma> turma = turmasDAO.findById(idTurma);

        if(turma.isPresent()) {

            if(turma.get().verificaSeUsuarioJaPertece(emailUser)) {
                turma.get().removeUser(emailUser);
                turmasDAO.save(turma.get());

                usuariosDAO.findByEmail(emailUser).map(record -> {
                   record.removeTurma(idTurma);
                   return usuariosDAO.save(record);
                });
            }
            throw new TurmaManagerException("Usu·rio n„o pode sair da turma que administra ou n„o pertence a mesma.");
        }
        throw new TurmaNotFoundException("Turma n„o encontrada: " + idTurma);
    }*/
}