package br.com.ufcg.back.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario {

    @Id @GeneratedValue
    private Long idUser;

    private String email;
    private String password;
    private String name;

    @OneToMany(mappedBy = "manager", fetch = FetchType.LAZY)
    private List<Turma> managedTurma = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "integrantes_turma", joinColumns = @JoinColumn(name = "id_user"), inverseJoinColumns = @JoinColumn(name = "turma_id"))
    private List<Turma> membersTurma = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "user_notifications", joinColumns = {
            @JoinColumn(name = "user_notification")}, inverseJoinColumns = {
            @JoinColumn(name = "grupo_id")})
    private List<Notifications> notifications = new ArrayList<>();

    @JsonCreator
    public Usuario(long idUser, String email, String password, String name) {

        super();

        this.idUser = idUser;
        this.email = email;
        this.password = password;
        this.name = name;

    }

    @JsonCreator
    public Usuario() {
        super();
    }

    public long getIdUser() {
        return idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String firstName) {
        this.name = firstName;
    }

    public List<Turma> getManagedTurma() {
        return managedTurma;
    }

    public List<Turma> getMembersTurma() {
        return membersTurma;
    }

    public void addTurma(Turma turma) {
        membersTurma.add(turma);
    }

    public void removeTurma(String idTurma) {
        for(Turma turma : membersTurma)
            if(turma.getId().equals(idTurma)) {
                membersTurma.remove(turma);
                return;
            }
    }
    public void removeTurmaManager(String idTurma) {
        for(Turma turma : managedTurma)
            if(turma.getId().equals(idTurma)) {
                managedTurma.remove(turma);
                return;
            }
    }
}