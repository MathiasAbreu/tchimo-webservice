package br.com.ufcg.back.controllers;

import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Notification;
import br.com.ufcg.back.entities.Response;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.dtos.TurmaDTO;
import br.com.ufcg.back.exceptions.grupo.GroupException;
import br.com.ufcg.back.exceptions.grupo.GroupNotFoundException;
import br.com.ufcg.back.exceptions.grupo.OverflowNumberOfGroupsException;
import br.com.ufcg.back.exceptions.turma.TurmaException;
import br.com.ufcg.back.exceptions.turma.TurmaManagerException;
import br.com.ufcg.back.exceptions.turma.TurmaNotFoundException;
import br.com.ufcg.back.exceptions.user.*;
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
    public ResponseEntity<TurmaDTO> buscaTurma(@ApiParam("Token válido.") @RequestHeader("Authorization") String header, @ApiParam("Id da Turma") @PathVariable String id) {

        try {

            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<TurmaDTO>(turmasService.buscaTurma(id,jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuário não encontrado!");

        } catch (TurmaNotFoundException errTurma) {
            return new ResponseEntity<TurmaDTO>(new TurmaDTO(),HttpStatus.NOT_FOUND);
        } catch (UserException errUser) {
            return new ResponseEntity<TurmaDTO>(new TurmaDTO(), HttpStatus.UNAUTHORIZED);
        }
    }

    @ApiOperation(value = "Operação que permite que um usuário entre em uma turma através do Id dela.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuario adicionado."),
            @ApiResponse(code = 404, message = "Turma não encontrada."),
            @ApiResponse(code = 409, message = "Usuário já pertence a turma, ou é o proprietário da mesma."),
            @ApiResponse(code = 401, message = "Usuario não autorizado pelo token.")
    })
    @RequestMapping(value = "turmas/{id}/membership", method = RequestMethod.POST, produces = "application/json")
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
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Grupo adicionado com sucesso."),
            @ApiResponse(code = 401, message = "Usuário não autorizado."),
            @ApiResponse(code = 404, message = "Usuário não encontrado."),
            @ApiResponse(code = 409, message = "Quantidade limite de grupos atingida.")
    })
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
    @RequestMapping(value = "turmas/{id}/grupos", method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> removeUserFromGroup(
            @ApiParam("Token válido") @RequestHeader("Authorization") String header,
            @ApiParam("Id da Turma") @PathVariable String id,
            @RequestParam(name="groupId", required=true, defaultValue="") Long groupId) {
        try
        {
            if (jwtService.usuarioExiste(header))
                return new ResponseEntity<String>(turmasService.removeUserFromGroup(id, groupId, jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuário não encontrado.");
        } catch (UserUnauthorizedException userUna) {
            return new ResponseEntity<String>(userUna.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserException | GroupNotFoundException | TurmaNotFoundException ex) {
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.NOT_FOUND);
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
                return new ResponseEntity<List<TurmaDTO>>(turmasService.buscaTodasAsTurmas(jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuario não foi encontrado!");
        } catch (UserException userErr) {
            return new ResponseEntity<>(new ArrayList<TurmaDTO>(),HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Permite ao usuário que ele saia de uma turma da qual participa como integrante.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna uma confirmação que o usuário saiu da turma desejada."),
            @ApiResponse(code = 404, message = "Usuário não encontrado.")
    })
    @RequestMapping(value = "turmas/{id}/membership", method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<String> sairDeTurma(@ApiParam("Token de verificação do usuário.") @RequestHeader("Authorization") String header, @ApiParam("Id da turma") @PathVariable String id) {

        try {
            if (jwtService.usuarioExiste(header))
                return new ResponseEntity<String>(turmasService.removeUserFromTurma(id, jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuário não foi encontrado.");
        } catch (UserUnauthorizedException | UserTokenBadlyFormattedException | UserTokenExpired err) {
            return new ResponseEntity<String>(err.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserException | TurmaException userErr) {
            return new ResponseEntity<String>(userErr.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Permite que um usuário remova uma turma que administra.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna uma confirmação que o usuario apagou a turma.")
    })
    @RequestMapping(value = "turmas/{id}",method = RequestMethod.DELETE, produces = "application/json")
    public ResponseEntity<String> apagaTurma(@ApiParam("Token de verificação do usuário.") @RequestHeader("Authorization") String header, @ApiParam("Id da turma") @PathVariable String id) {

        try {
            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<String>(turmasService.removeTurma(id,jwtService.getUsuarioDoToken(header)),HttpStatus.OK);
            throw new UserNotFoundException("Usuário não foi encontrado!");
        } catch (UserUnauthorizedException | UserTokenBadlyFormattedException | UserTokenExpired err) {
            return new ResponseEntity<String>(err.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (UserException | TurmaException err) {
            return new ResponseEntity<String>(err.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Permite que um usuário solicite sua entrada em um grupo.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna que uma solicitação foi feita.")
    })
    @RequestMapping(value = "turmas/solicitations", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> solicitaEntradaEmGrupo(@ApiParam("Token do Usuário.") @RequestHeader("Authorization") String header, @ApiParam("Notificação pré construida no JSON.") @RequestBody Notification notification) {
        try {
            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<String>(turmasService.solicitaEntradaEmGrupo(notification,jwtService.getUsuarioDoToken(header)), HttpStatus.CREATED);
            throw new UserNotFoundException("Usuário não encontrado!");
        } catch (UserException | GroupException err) {
            return new ResponseEntity<String>(err.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @ApiOperation(value = "Recece uma resposta de uma solicitação feita. Somente solicitações com procedimentos podem gerar respoastas no frontend.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna uma confirmação que o backend aceitou a resposta.")
    })
    @RequestMapping(value = "turmas/response", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> recebeRespostaDeSolicitacao(@ApiParam("Token de Usuário.") @RequestHeader("Authorization") String header, @ApiParam("Resposta") @RequestBody Response resposta) {

        try {
            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<String>(turmasService.processaResposta(resposta, jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuário não encontrado!");
        } catch (UserException | TurmaException | GroupException err) {
            return new ResponseEntity<String>(err.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}