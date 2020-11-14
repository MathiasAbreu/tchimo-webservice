package br.com.ufcg.back.exceptions.grupo;

public class GroupNotFoundException extends GroupException {

    public GroupNotFoundException() {
        super();
    }

    public GroupNotFoundException(String message) {
        super(message);
    }
}
