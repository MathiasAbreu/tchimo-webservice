package br.com.ufcg.back.controllers.filters;

import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.exceptions.user.UserException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import br.com.ufcg.back.exceptions.user.UserPasswordIncorrectException;
import br.com.ufcg.back.services.JWTService;
import br.com.ufcg.back.services.UsuariosService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@Api(value = "Controle de Login dos Usuários")
@RestController
public class LoginController {

    private final String TOKEN_KEY = "DefaultUserLogin";

    private UsuariosService usuariosService;
    private JWTService jwtService;

    public LoginController(UsuariosService usuariosService, JWTService jwtService) {

        super();
        this.usuariosService = usuariosService;
        this.jwtService = jwtService;
    }

    @ApiOperation(value = "Realiza a autenticação de um usuário no sistema. Retornando um token se o usuário for válido.", notes = "Autenticação de Usuário. Atributos obrigatórios: Apenas email e senha do usuário!")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Usuário verificado. Token retornado."),
            @ApiResponse(code = 404, message = "Usuário não encontrado!")
    })
    @RequestMapping(value = "usuarios/login", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public ResponseEntity<LoginResponse> authenticate(@ApiParam(value = "Usuário a ser autenticado.") @RequestBody Usuario usuario) throws UserException {

        Optional<Usuario> authUsuario = usuariosService.getUsuario(usuario.getEmail());

        try {

            if (!authUsuario.isPresent())
                throw new UserNotFoundException();

            if (!authUsuario.get().getPassword().equals(usuario.getPassword()))
                throw new UserPasswordIncorrectException();

            String token = Jwts.builder().setSubject(authUsuario.get().getEmail()).signWith(SignatureAlgorithm.HS512, TOKEN_KEY).setExpiration(new Date(System.currentTimeMillis() + 21600 * 1000)).compact();
            return new ResponseEntity<>(new LoginResponse(token), HttpStatus.OK);
        } catch (UserException err) {

            return new ResponseEntity<>(new LoginResponse("Usuário não encontrado!"), HttpStatus.NOT_FOUND);
        }
    }

    private class LoginResponse {

        public String token;

        public LoginResponse(String token) {
            this.token = token;
        }
    }
}
