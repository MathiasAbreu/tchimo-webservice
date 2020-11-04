package br.com.ufcg.back.controllers;
import java.util.List;

import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.exceptions.grupo.GrupoNotFoundException;
import br.com.ufcg.back.exceptions.turma.TurmaMaximoGruposException;
import br.com.ufcg.back.exceptions.turma.TurmaNotFoundException;
import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import br.com.ufcg.back.services.TurmasService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = "Controle de Turmas da API")
@RestController
@RequestMapping({"/v1/turmas"})
public class TurmasController {
    @Autowired
    private TurmasService turmasService;

    public static String[] getPrivatePatterns() {
        return new String[] {
                "/v1/turmas/*"
        };
    }

    @RequestMapping(value = "")
    public ResponseEntity<List<Turma>> findAll() {
        List<Turma> turmas = turmasService.findAll();

        if (turmas == null)
            throw new InternalError("Something went wrong");

        return new ResponseEntity<>(turmas, HttpStatus.OK);
    }

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
    }

    @ApiOperation(value = "Remove um usuário de um grupo de uma turma e remove o grupo caso esteja vazio.")
    @RequestMapping(value = "/{id}/remove", method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Boolean> removeUsuarioDeGrupo(
            @PathVariable Long id,
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
    public ResponseEntity<Grupo[]> listarGrupos(@PathVariable Long id) {
        try {
            return new ResponseEntity<Grupo[]>(turmasService.listarGrupos(id), HttpStatus.OK);
        } catch (TurmaNotFoundException e) {
            return new ResponseEntity<Grupo[]>(new Grupo[0], HttpStatus.NOT_FOUND);
        }
    }
}