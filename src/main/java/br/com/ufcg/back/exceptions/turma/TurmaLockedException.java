package br.com.ufcg.back.exceptions.turma;

public class TurmaLockedException extends TurmaException {

    public TurmaLockedException() {
        super();
    }

    public TurmaLockedException(String message) {
        super("     TurmaLockedException() -> " + message);
    }
}
