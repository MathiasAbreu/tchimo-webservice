package br.com.ufcg.back.exceptions.turma;

import br.com.ufcg.back.exceptions.user.UserException;
import org.springframework.stereotype.Component;

@Component
public class TurmaMaximoGruposException extends TurmaException {

    public TurmaMaximoGruposException() {
        super("Máximo de grupos já criados em turma.");
    }
}
