package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Grupo {

    @Id
    @GeneratedValue
    private Long idUnique;

    private Long idGroup;
    private String emailManager;

    private ArrayList<Long> memberIDs = new ArrayList<>();

    @JsonCreator
    public Grupo(long idGroup, String emailManager) {

        super();

        this.idGroup = idGroup;
        this.emailManager = emailManager;
    }

    @JsonCreator
    public Grupo(Long idUnique, Long idGroup, String emailManager) {

        super();

        this.idUnique = idUnique;
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

    public List<Long> getMemberIDs() {
        return memberIDs;
    }

    public boolean usuarioParticipa(String email) {
        if(memberIDs.contains(email))
            return true;
        return false;
    }

    public void removeUsuario(Long usrId) {
        memberIDs.remove(usrId);
    }

    public void addUser(Long usrId) throws UserAlreadyExistException {
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
