package br.com.ufcg.back.exceptions.user;

public class UserUnauthorizedException extends UserException{

    public UserUnauthorizedException() {
        super();
    }

    public UserUnauthorizedException(String message) {
        super("     UserUnauthorizedException() -> " + message);
    }
}
