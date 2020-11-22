package br.com.ufcg.back.entities;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Notification {

    @Id
    @GeneratedValue
    private Long id;

    private Long id_user;
    private Long id_group;

    private String id_turma;
    private String type;

    private Long creationDate = ((new Date()).getTime() / 1000L);

    private Long targetUser;

    @JsonCreator
    public Notification() {
        super();
    }

    @JsonCreator
    public Notification(Long id_user, String id_turma, Long id_group, String type) {
        super();
        this.id_user = id_user;
        this.id_turma = id_turma;
        this.id_group = id_group;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_user() {
        return id_user;
    }

    public void setId_user(Long id_user) {
        this.id_user = id_user;
    }

    public Long getId_group() {
        return id_group;
    }

    public void setId_group(Long id_group) {
        this.id_group = id_group;
    }

    public String getId_turma() {
        return id_turma;
    }

    public void setId_turma(String id_turma) {
        this.id_turma = id_turma;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public Long getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(Long targetUser) {
        this.targetUser = targetUser;
    }
}
