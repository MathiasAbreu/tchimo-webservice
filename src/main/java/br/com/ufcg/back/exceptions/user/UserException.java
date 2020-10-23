package br.com.ufcg.back.exceptions.user;

import org.springframework.stereotype.Component;

@Component
public class UserException extends Exception {

    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }
}
