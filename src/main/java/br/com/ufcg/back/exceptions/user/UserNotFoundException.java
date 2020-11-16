package br.com.ufcg.back.exceptions.user;

public class UserNotFoundException extends UserException {

    public UserNotFoundException() {
        super();
    }
    public UserNotFoundException(String message) {
        super("     UserNotFoundException() -> " + message);
    }
}