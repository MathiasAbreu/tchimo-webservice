package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Grupo {

    @Id
    private Long idGroup;
    private String emailManager;

    private ArrayList<Long> memberIDs = new ArrayList<>();

    private int numberFoMembersPermitted = 0;

    @JsonCreator
    public Grupo(long idGroup, String emailManager, long idManager) {

        super();

        this.idGroup = idGroup;
        this.emailManager = emailManager;
        memberIDs.add(idManager);
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

    public int getNumberFoMembersPermitted() {
        return numberFoMembersPermitted;
    }

    public void setNumberFoMembersPermitted(int numberFoMembersPermitted) {
        this.numberFoMembersPermitted = numberFoMembersPermitted;
    }

    public int getNumberOfMembers() {
        return memberIDs.size();
    }

    public boolean usuarioParticipa(long idUser) {
        if(memberIDs.contains(idUser))
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

    public void removeUser(Long usrId) throws UserNotFoundException {
        if (memberIDs.contains(usrId))
            memberIDs.remove(usrId);
        else throw new UserNotFoundException();
    }
}
