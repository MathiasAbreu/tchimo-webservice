package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Grupo {

    @Id
    @GeneratedValue
    private long idUnique;

    private long idGroup;
    private String emailManager;

    private ArrayList<String> memberIDs = new ArrayList<String>();

    @JsonCreator
    public Grupo(long idGroup, String emailManager) {

        super();

        this.idGroup = idGroup;
        this.emailManager = emailManager;
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

    public int amountOfMembers(){
        return memberIDs.size();
    }

    public void addUser(String usrId) throws UserAlreadyExistException {
        if (memberIDs.contains(usrId))
            throw new UserAlreadyExistException();
        else memberIDs.add(usrId);
    }

    public void removeUser(String usrId) throws UserNotFoundException {
        if (memberIDs.contains(usrId))
            memberIDs.remove(usrId);
        else throw new UserNotFoundException();
    }
}
