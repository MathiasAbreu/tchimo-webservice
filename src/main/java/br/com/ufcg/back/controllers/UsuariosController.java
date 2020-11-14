package br.com.ufcg.back.controllers;

import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.exceptions.user.UserException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import br.com.ufcg.back.services.JWTService;
import br.com.ufcg.back.services.TurmasService;
import br.com.ufcg.back.services.UsuariosService;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Api(value = "Controle de Usuários da API")
@RestController
@RequestMapping("usuarios")
public class UsuariosController {

    private UsuariosService usuariosService;
    private TurmasService turmasService;
    private JWTService jwtService;

    public UsuariosController(UsuariosService usuariosService, TurmasService turmasService, JWTService jwtService) {

        super();

        this.usuariosService = usuariosService;
        this.turmasService = turmasService;
        this.jwtService = jwtService;
    }

    @ApiOperation(value = "Adiciona um novo usuário ao sistema.", notes = "Adição de um novo Usuário. Recebe como parâmetro de entrada, " +
                           "um JSON contendo todos os dados básicos de um novo usuário. Se a adição for um sucesso, retorna uma mensagem de boas vindas, caso contrário retorna uma mensagem de erro.")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Codigo de confirmação do cadastro do novo usuário."),
            @ApiResponse(code = 409, message = "Codigo de erro. O usuário não pode ser criado pois já existe no sistema!"),
    })
    @RequestMapping(value = "/adiciona", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<String> adicionaUsuario(@ApiParam(value = "Novo Usuário.") @RequestBody Usuario usuario) {

        try {

            usuariosService.adicionaUsuario(usuario);
            return new ResponseEntity<String>("Bem vindo! ", HttpStatus.CREATED);

        } catch (UserException uaee) {
            return new ResponseEntity<String>(uaee.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @ApiOperation(value = "MÉTODO DE USO EXCLUSIVO DO BACKEND -> Busca um usuário no banco de dados do sistema.", notes = "Busca um usuário na base de dados no sistema. " +
            "Recebe como parâmetro um JSON contendo o email do usuário de interesse. Retorna o usuário caso seja encontrado, ou retorna um Usuário nulo.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna com sucesso que o usuario foi encontrado e retornado sem eventuais problemas."),
            @ApiResponse(code = 404, message = "Não foi possível encontrar o usuário na base de dados.")
    })
    @RequestMapping(value = "/busca", method = RequestMethod.GET, produces = "application/json", consumes = "application/json")
    public ResponseEntity<Usuario> buscaUsuario(@ApiParam(value = "Email do Usuário.") @RequestBody Usuario usuario) {

        Optional<Usuario> retornoUsuario = usuariosService.getUsuario(usuario.getEmail());
        if(retornoUsuario.isPresent()) {
            return new ResponseEntity<Usuario>(retornoUsuario.get(), HttpStatus.OK);

        }

        return new ResponseEntity<Usuario>(new Usuario(), HttpStatus.NOT_FOUND);
    }

    @ApiOperation(value = "Método que retorna todas as turmas que um usuário participa ou administra.", notes = "Busca todas as turmas relacionadas a um usuário.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Retorna todas as turmas de um usuário.")
    })
    @RequestMapping(value = "/buscaTotal", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<Turma>> buscaTodasAsTurmas(@ApiParam(value = "Token de usuário.") @RequestHeader("Authorization") String header) {

        try {
            if(jwtService.usuarioExiste(header))
                return new ResponseEntity<List<Turma>>(usuariosService.buscaTodasAsTurmas(jwtService.getUsuarioDoToken(header)), HttpStatus.OK);
            throw new UserNotFoundException("Usuario não foi encontrado!");
        } catch (UserException userErr) {
            return new ResponseEntity<>(new ArrayList<Turma>(),HttpStatus.NOT_FOUND);
        }
    }
}
