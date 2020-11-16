package br.com.ufcg.back.exceptions.user;

import org.springframework.stereotype.Component;

@Component
public class UserTokenBadlyFormattedException extends UserException {

    public UserTokenBadlyFormattedException() {
        super();
    }
    public UserTokenBadlyFormattedException(String message) {
        super("     UserTokenBadlyFormattedException() -> " + message);
    }
}
