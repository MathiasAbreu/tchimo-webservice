package br.com.ufcg.back.controllers;
import java.util.List;
import java.util.Optional;

import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.exceptions.grupo.GrupoNotFoundException;
import br.com.ufcg.back.exceptions.turma.TurmaMaximoGruposException;
import br.com.ufcg.back.exceptions.turma.TurmaNotFoundException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import br.com.ufcg.back.services.JWTService;
import br.com.ufcg.back.services.TurmasService;
import br.com.ufcg.back.services.UsuariosService;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "Controle de Turmas da API")
@RestController
@RequestMapping("turma")
public class TurmasController {

    private TurmasService turmasService;
    private UsuariosService usuariosService;
    private JWTService jwtService;

    @JsonCreator
    public TurmasController(TurmasService turmasService, UsuariosService usuariosService, JWTService jwtService) {

        super();
        this.turmasService = turmasService;
        this.usuariosService = usuariosService;
        this.jwtService = jwtService;
    }

    @RequestMapping(value = "")
    public ResponseEntity<List<Turma>> findAll() {
        List<Turma> turmas = turmasService.findAll();

        if (turmas == null)
            throw new InternalError("Something went wrong");

        return new ResponseEntity<>(turmas, HttpStatus.OK);
    }

    /*
        Necessita revisão
     */
    /*
    @ApiOperation(value = "Cria um novo grupo na turma e adiciona o usuário que o criou a ele.")
    @RequestMapping(value = "/{id}/adiciona", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> adicionaUsuarioANovoGrupo(
            @PathVariable Long id,
            @RequestParam(name="usrId", required=true, defaultValue="") Long usrId) {
        try {
            turmasService.adicionaUsuarioANovoGrupo(id, usrId);
            return new ResponseEntity<Boolean>(true, HttpStatus.CREATED);

        } catch (TurmaMaximoGruposException | UserAlreadyExistException | TurmaNotFoundException ex) {
            return new ResponseEntity<Boolean>(false, HttpStatus.NOT_ACCEPTABLE);
        }
    }*/

    @ApiOperation(value = "Remove um usuário de um grupo de uma turma e remove o grupo caso esteja vazio.")
    @RequestMapping(value = "/{id}/remove", method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> removeUsuarioDeGrupo(
            @PathVariable String id,
            @RequestParam(name="groupId", required=true, defaultValue="") Long groupId,
            @RequestParam(name="usrId", required=true, defaultValue="") Long usrId) {
        try {
            turmasService.removeUsuarioDeGrupo(id, groupId, usrId);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);

        } catch (UserNotFoundException | GrupoNotFoundException | TurmaNotFoundException ex) {
            return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Lista os grupos de uma turma.")
    @RequestMapping(value = "/{id}/grupos", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Grupo[]> listarGrupos(@PathVariable String id) {
        try {
            return new ResponseEntity<Grupo[]>(turmasService.listarGrupos(id), HttpStatus.OK);
        } catch (TurmaNotFoundException e) {
            return new ResponseEntity<Grupo[]>(new Grupo[0], HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Cria uma nova turma.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Retorna confirmação que a turma foi criada."),
            @ApiResponse(code = 401, message = "Token inválido."),
            @ApiResponse(code = 404, message = "Usuário não encontrado.")
    })
    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> criaTurma(@ApiParam("Token Válido.") @RequestHeader("Authorization") String header, @ApiParam("Turma a ser criada.") @RequestBody Turma turma) {

        try {

            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<String>(turmasService.criaTurma(turma,jwtService.getUsuarioDoToken(header)), HttpStatus.CREATED);
            return new ResponseEntity<String>("Usuário não encontrado!",HttpStatus.NOT_FOUND);
        } catch (UserException userE) {
            return new ResponseEntity<String>(userE.getMessage(),HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "Busca um turma. Recebe como paramêtro o id da turma.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna uma determinada turma."),
            @ApiResponse(code = 401, message = "O token do usuário não é válido."),
            @ApiResponse(code = 404, message = "O usuário não foi encontrado.")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Turma> buscaTurma(@ApiParam("Token válido.") @RequestHeader("Authorization") String header, @ApiParam("Id da Turma") @PathVariable String id) {

        try {

            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<Turma>(turmasService.findByID(id), HttpStatus.OK);
            throw new UserNotFoundException("Usuário não encontrado!");

        } catch (TurmaNotFoundException errTurma) {
            return new ResponseEntity<Turma>(new Turma(),HttpStatus.NOT_FOUND);
        } catch (UserException errUser) {
            return new ResponseEntity<Turma>(new Turma(), HttpStatus.UNAUTHORIZED);
        }
    }
}