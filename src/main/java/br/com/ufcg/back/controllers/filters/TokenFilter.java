package br.com.ufcg.back.controllers.filters;

import io.jsonwebtoken.*;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenFilter extends GenericFilterBean {

    public final static int TOKEN_INDEX = 7;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest requestAux = (HttpServletRequest) request;
        String header = requestAux.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer "))
            throw new ServletException("Token Inexistente!");

        String token = header.substring(TOKEN_INDEX);
        try {

            Jwts.parser().setSigningKey("DefaultUserLogin").parseClaimsJws(token).getBody().getSubject();
        } catch (SignatureException | ExpiredJwtException | MalformedJwtException | PrematureJwtException | UnsupportedJwtException | IllegalArgumentException err) {

            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, err.getMessage());
            return;
        }

        chain.doFilter(request, response);
    }
}
