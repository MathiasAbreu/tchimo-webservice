package br.com.ufcg.back.exceptions.user;

import org.springframework.stereotype.Component;

@Component
public class UserAlreadyExistException extends UserException {

    public UserAlreadyExistException(String email) {
        super("UserAlreadyExistException -> Já existe um usuário cadastrado no email: " + email);
    }

    public UserAlreadyExistException() {
        super();
    }
}
