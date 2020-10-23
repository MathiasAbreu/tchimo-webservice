package br.com.ufcg.back.exceptions.user;

import org.springframework.stereotype.Component;

@Component
public class UserTokenBadlyFormattedException extends UserException {

    public UserTokenBadlyFormattedException() {
        super("Token do Usuário inexistente ou mal formatado!");
    }
}
