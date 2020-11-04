package br.com.ufcg.back.exceptions.turma;

import br.com.ufcg.back.exceptions.user.UserException;
import org.springframework.stereotype.Component;

@Component
public class TurmaMaximoGruposException extends UserException {

    public TurmaMaximoGruposException() {
        super("Máximo de grupos já criados em turma.");
    }
}
