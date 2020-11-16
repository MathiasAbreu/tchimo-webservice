package br.com.ufcg.back.exceptions.user;

public class UserPasswordIncorrectException extends UserException {

    public UserPasswordIncorrectException() {
        super();
    }
    public UserPasswordIncorrectException(String message) {
        super("     UserPasswordIncorrectException() -> " + message);
    }
}
