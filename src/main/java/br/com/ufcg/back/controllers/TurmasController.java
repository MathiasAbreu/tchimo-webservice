package br.com.ufcg.back.controllers;

import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.dtos.TurmaDTO;
import br.com.ufcg.back.exceptions.grupo.GroupNotFoundException;
import br.com.ufcg.back.exceptions.grupo.OverflowNumberOfGroupsException;
import br.com.ufcg.back.exceptions.turma.TurmaException;
import br.com.ufcg.back.exceptions.turma.TurmaManagerException;
import br.com.ufcg.back.exceptions.turma.TurmaNotFoundException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import br.com.ufcg.back.exceptions.user.UserUnauthorizedException;
import br.com.ufcg.back.services.JWTService;
import br.com.ufcg.back.services.TurmasService;
import br.com.ufcg.back.services.UsuariosService;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(value = "Controle de Turmas da API")
@RestController
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

    @ApiOperation(value = "Cria uma nova turma.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Retorna confirmação que a turma foi criada."),
            @ApiResponse(code = 401, message = "Token inválido."),
            @ApiResponse(code = 404, message = "Usuário não encontrado.")
    })
    @RequestMapping(value = "turmas", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
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
    @RequestMapping(value = "turmas/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Turma> buscaTurma(@ApiParam("Token válido.") @RequestHeader("Authorization") String header, @ApiParam("Id da Turma") @PathVariable String id) {

        try {

            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<Turma>(turmasService.buscaTurma(id,jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuário não encontrado!");

        } catch (TurmaNotFoundException errTurma) {
            return new ResponseEntity<Turma>(new Turma(),HttpStatus.NOT_FOUND);
        } catch (UserException errUser) {
            return new ResponseEntity<Turma>(new Turma(), HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "Operação que permite que um usuário entre em uma turma através do Id dela.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario adicionado."),
            @ApiResponse(code = 404, message = "Turma não encontrada."),
            @ApiResponse(code = 409, message = "Usuário já pertence a turma, ou é o proprietário da mesma."),
            @ApiResponse(code = 401, message = "Usuario não autorizado pelo token.")
    })
    @RequestMapping(value = "turmas/{id}", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> entrarEmUmTurma(@ApiParam("Token Válido") @RequestHeader("Authorization") String header, @ApiParam("Turma") @PathVariable String id) {

        try {

            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<String>(turmasService.addUsuarioEmTurma(id,jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuário não encontrado.");
        } catch (TurmaManagerException | UserAlreadyExistException turmaErr) {
            return new ResponseEntity<String>(turmaErr.getMessage(), HttpStatus.CONFLICT);
        } catch (TurmaNotFoundException errTurma) {
            return new ResponseEntity<String>("Turma não encontrada.",HttpStatus.NOT_FOUND);
        } catch (UserException errUser) {
            return new ResponseEntity<String>("Sem autorização.", HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "Cria um novo grupo.")
    @RequestMapping(value = "turmas/{id}/grupos", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Grupo> adicionaGrupo(@ApiParam("Token válido") @RequestHeader("Authorization") String header, @ApiParam("Id da Turma") @PathVariable String id) {

        try {

            if (jwtService.usuarioExiste(header))
                return new ResponseEntity<Grupo>(turmasService.addGrupo(jwtService.getUsuarioDoToken(header), id), HttpStatus.OK);
            throw new UserNotFoundException("Usuário não encontrado.");
        } catch (UserUnauthorizedException userUna) {
            return new ResponseEntity<Grupo>(new Grupo(), HttpStatus.UNAUTHORIZED);
        } catch (UserException | TurmaException userErr) {
            return new ResponseEntity<Grupo>(new Grupo(), HttpStatus.NOT_FOUND);
        } catch (OverflowNumberOfGroupsException errOver) {
            return new ResponseEntity<Grupo>(new Grupo(), HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "Remove um usuário de um grupo de uma turma e remove o grupo caso esteja vazio.")
    @RequestMapping(value = "turmas/{id}/remove", method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> removeUserFromGroup(
            @ApiParam("Token válido") @RequestHeader("Authorization") String header,
            @ApiParam("Id da Turma") @PathVariable String id,
            @RequestParam(name="groupId", required=true, defaultValue="") Long groupId) {
        try
        {
            if (jwtService.usuarioExiste(header))
                return new ResponseEntity<Boolean>(turmasService.removeUserFromGroup(id, groupId, jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuário não encontrado.");
        } catch (UserUnauthorizedException userUna) {
            return new ResponseEntity<Boolean>(false, HttpStatus.UNAUTHORIZED);
        } catch (UserException | GroupNotFoundException | TurmaNotFoundException ex) {
            return new ResponseEntity<Boolean>(false, HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Método que retorna todas as turmas que um usuário participa ou administra.", notes = "Busca todas as turmas relacionadas a um usuário.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna todas as turmas de um usuário.")
    })
    @RequestMapping(value = "turmas", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<TurmaDTO>> buscaTodasAsTurmas(@ApiParam(value = "Token de usuário.") @RequestHeader("Authorization") String header) {

        try {
            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<List<TurmaDTO>>(usuariosService.buscaTodasAsTurmas(jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuario não foi encontrado!");
        } catch (UserException userErr) {
            return new ResponseEntity<>(new ArrayList<TurmaDTO>(),HttpStatus.NOT_FOUND);
        }
    }



    /*@ApiOperation(value = "Lista os grupos de uma turma.")
    @RequestMapping(value = "/{id}/grupos", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Grupo[]> listarGrupos(@PathVariable String id) {
        try {
            return new ResponseEntity<Grupo[]>(turmasService.listarGrupos(id), HttpStatus.OK);
        } catch (TurmaNotFoundException e) {
            return new ResponseEntity<Grupo[]>(new Grupo[0], HttpStatus.NOT_FOUND);
        }
    }*/
}