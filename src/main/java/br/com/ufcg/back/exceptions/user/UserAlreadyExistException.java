package br.com.ufcg.back.exceptions.user;

import org.springframework.stereotype.Component;

@Component
public class UserAlreadyExistException extends UserException {

    public UserAlreadyExistException() {
        super();
    }

    public UserAlreadyExistException(String message) {
        super("     UserAlreadyExistException -> " + message);
    }
}
