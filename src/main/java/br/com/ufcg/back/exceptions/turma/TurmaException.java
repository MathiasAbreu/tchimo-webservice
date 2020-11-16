package br.com.ufcg.back.exceptions.turma;

public class TurmaException extends Exception {

    public TurmaException() {
        super();
    }
    public TurmaException(String message) {
        super("throw: TurmaException() by: \n" + message);
    }

}
