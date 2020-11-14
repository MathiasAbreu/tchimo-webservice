package br.com.ufcg.back.exceptions.turma;

public class TurmaNotFoundException extends TurmaException {

    public TurmaNotFoundException() {
        super();
    }

    public TurmaNotFoundException(String message) {
        super(message);
    }
}
