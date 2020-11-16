package br.com.ufcg.back.exceptions.grupo;

public class OverflowNumberOfGroupsException extends GroupException{

    public OverflowNumberOfGroupsException() {
        super();
    }
    public OverflowNumberOfGroupsException(String message) {
        super("     OverflowNumberOfGroupsException() -> " + message);
    }
}
