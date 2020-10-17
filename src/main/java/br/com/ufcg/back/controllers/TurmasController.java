package br.com.ufcg.back.controllers;
import java.util.List;

import br.com.ufcg.back.entities.Turma;
import br.com.ufcg.back.services.TurmasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping({"/v1/turmas"})
public class TurmasController {
    @Autowired
    private TurmasService turmasService;

    @RequestMapping(value = "")
    public ResponseEntity<List<Turma>> findAll() {
        List<Turma> turmas = turmasService.findAll();

        if (turmas == null)
            throw new InternalError("Something went wrong");

        return new ResponseEntity<>(turmas, HttpStatus.OK);
    }
}
