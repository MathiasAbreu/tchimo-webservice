package br.com.ufcg.back.exceptions.grupo;

public class GroupNotFoundException extends GroupException {

    public GroupNotFoundException(String message) {
        super("     GroupNotFoundException() -> " + message);
    }
}
