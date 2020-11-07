package br.com.ufcg.back.exceptions.user;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {
        super();
    }
}