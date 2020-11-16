package br.com.ufcg.back.exceptions.user;

import org.springframework.stereotype.Component;

@Component
public class UserTokenExpired extends UserException {

    public UserTokenExpired() {
        super();
    }
    public UserTokenExpired(String message) {
        super("     UserTokenExpired() -> " + message);
    }
}
