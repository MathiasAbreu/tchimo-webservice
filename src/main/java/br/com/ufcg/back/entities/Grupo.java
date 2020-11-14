package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Grupo {

    @Id
    private long idGroup;
    private String emailManager;

    private ArrayList<Long> memberIDs;

    @JsonCreator
    public Grupo(long idGroup, String emailManager) {

        super();

        this.idGroup = idGroup;
        this.emailManager = emailManager;

        memberIDs = new ArrayList<Long>();
    }

    @JsonCreator
    public Grupo() {
        super();
    }

    public String getEmailManager() {
        return emailManager;
    }

    public void setEmailManager(String emailManager) {
        this.emailManager = emailManager;
    }

    public Long getIdGroup(){
        return idGroup;
    }

    public int quantidadeDeMembros(){
        return memberIDs.size();
    }

    public void adicionaUsuario(Long usrId) throws UserAlreadyExistException {
        if (memberIDs.contains(usrId))
            throw new UserAlreadyExistException();
        else memberIDs.add(usrId);
    }

    public void removeUsuario(Long usrId) throws UserNotFoundException {
        if (memberIDs.contains(usrId))
            memberIDs.remove(usrId);
        else throw new UserNotFoundException();
    }
}
