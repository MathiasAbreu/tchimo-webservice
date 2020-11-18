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

    public Turma buscaTurma(String idTurma, String emailUser) throws TurmaNotFoundException, UserUnauthorizedException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        if(turma.isPresent()) {

            if(turma.get().getManager().getEmail().equals(emailUser) || turma.get().verificaSeUsuarioJaPertece(emailUser))
                return turma.get();
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

    public String addUsuarioEmTurma(String idTurma, String emailUser) throws TurmaManagerException, TurmaNotFoundException, UserException {

        Optional<Turma> turma = turmasDAO.findById(idTurma);
        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        if (turma.isPresent()) {

            if (turma.get().getManager().getEmail().equals(emailUser))
                throw new TurmaManagerException("Usuário não pode entrar na turma pois é o manager dela!");

            if (turma.get().verificaSeUsuarioJaPertece(emailUser))
                throw new UserAlreadyExistException("Usuário já pertence a turma.");

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

        if(turma.isPresent()) {
            if(turma.get().verificaSeUsuarioJaPertece(emailUser)) {

                int quantidadeDegrupos = turma.get().quantidadeGruposNaTurma();
                if(quantidadeDegrupos < turma.get().getQuantityOfGroups()) {

                    Grupo grupo = gruposDAO.save(new Grupo((quantidadeDegrupos + 1),emailUser));
                    turma.get().adicionaGrupo(grupo);
                    turma.get().addQGrupo();
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
            TurmaDTO turmaDTO = new TurmaDTO(turma.getId(), turma.getName(), turma.getCreationDate(), turma.getEndDate(), turma.getFormationStrategy(), turma.getEndingStrategy(), turma.getQuantityOfGroups(), true);
            turmaDTO.setIntegrantes(configureIntegrantes(turma.getIntegrantes()));
            turmaDTO.setGroups(configureGrupos(turma.getGroups()));
            turmas.add(turmaDTO);
        }
        for(Turma turma : usuario.get().getMembersTurma()) {

            TurmaDTO turmaDTO = new TurmaDTO(turma.getId(), turma.getName(), turma.getCreationDate(), turma.getEndDate(), turma.getFormationStrategy(), turma.getEndingStrategy(), turma.getQuantityOfGroups(), false);
            turmaDTO.setIntegrantes(configureIntegrantes(turma.getIntegrantes()));
            turmaDTO.setGroups(configureGrupos(turma.getGroups()));
            turmas.add(turmaDTO);
        }

        return turmas;
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

            GrupoDTO grupoDTO = new GrupoDTO(grupo.getIdGroup(),recuperaIdUser(grupo.getEmailManager(),usuariosDAO));
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

    private Long recuperaIdUser(String emailUser, UsuariosDAO usuariosDAO) {

        Optional<Usuario> usuario = usuariosDAO.findByEmail(emailUser);
        if(usuario.isPresent())
            return usuario.get().getIdUser();

        return 0L;
    }

    public Boolean removeUserFromGroup(String id, Long groupID, String emailUser) throws UserNotFoundException, GroupNotFoundException, TurmaNotFoundException, UserUnauthorizedException {
        Turma t = buscaTurma(id, emailUser);
        t.removeUserFromGroup(groupID, emailUser);
        create(t);
        return true;
    }

    /*public Grupo[] listGroups(String id, String usrEmail) throws TurmaNotFoundException, UserUnauthorizedException {
        return buscaTurma(id, usrEmail).listGroups();
    }*/

    public String[] listMembers(String id, String usrEmail) throws TurmaNotFoundException, UserUnauthorizedException {
        return buscaTurma(id, usrEmail).listMembers();
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
            throw new TurmaManagerException("Usuário não pode sair da turma que administra ou não pertence a mesma.");
        }
        throw new TurmaNotFoundException("Turma não encontrada: " + idTurma);
    }*/
}
