package br.com.ufcg.back.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Usuario {

    @Id
    private String email;
    private String password;
    private String name;

    private ArrayList<Long> turmaIDs;

    @JsonCreator
    public Usuario(String email, String password, String name) {

        super();

        this.email = email;
        this.password = password;
        this.name = name;

    }

    @JsonCreator
    public Usuario() {
        super();
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

    public String getFirstName() {
        return name;
    }

    public void setFirstName(String firstName) {
        this.name = name;
    }

}