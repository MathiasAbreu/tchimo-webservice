package br.com.ufcg.back.entities;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Turma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String linkIdentifier;
    private Long managerID;
    private ArrayList<Long> memberIDs;

    private int numMinGrupos;
    private int numMaxGrupos;
    private int numMinAlunosPorGrupo;
    private int numMaxAlunosPorGrupo;

    private ArrayList<Grupo> groups;
}
