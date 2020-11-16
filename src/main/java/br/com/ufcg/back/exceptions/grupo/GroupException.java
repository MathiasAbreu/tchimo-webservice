package br.com.ufcg.back.exceptions.grupo;

public class GroupException extends Exception {

    public GroupException() {
        super();
    }
    public GroupException(String message) {
        super("throw: GroupException() by: \n" + message);
    }
}
