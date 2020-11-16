package br.com.ufcg.back.exceptions.turma;

import br.com.ufcg.back.exceptions.user.UserException;
import org.springframework.stereotype.Component;

@Component
public class TurmaMaximumMinimumOfGroupsException extends TurmaException {

    public TurmaMaximumMinimumOfGroupsException() {
        super();
    }

    public TurmaMaximumMinimumOfGroupsException(String message) {
        super("     TurmaMaximumMinimumOfGroupsException() -> " + message);
    }
}
