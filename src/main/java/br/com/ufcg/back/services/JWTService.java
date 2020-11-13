package br.com.ufcg.back.services;

import br.com.ufcg.back.controllers.filters.TokenFilter;
import br.com.ufcg.back.exceptions.user.UserException;
import br.com.ufcg.back.exceptions.user.UserTokenBadlyFormattedException;
import br.com.ufcg.back.exceptions.user.UserTokenExpired;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

    private UsuariosService usuariosService;

    public JWTService(UsuariosService usuariosService) {

        super();
        this.usuariosService = usuariosService;
    }

    public boolean usuarioExiste(String authorizationHeader) throws UserException {

        String subject = getUsuarioDoToken(authorizationHeader);
        return usuariosService.getUsuario(subject).isPresent();
    }

    public String getUsuarioDoToken(String authorizationHeader) throws UserException {

        if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new UserTokenBadlyFormattedException();

        String token = authorizationHeader.substring(TokenFilter.TOKEN_INDEX);
        String subject = null;

        try {

            subject = Jwts.parser().setSigningKey("DefaultUserLogin").parseClaimsJws(token).getBody().getSubject();
            if(!(usuariosService.getUsuario(subject)).isPresent())
                throw new UserTokenExpired();

        } catch (Exception err) {

            throw new UserTokenBadlyFormattedException();
        }

        return subject;
    }
}
