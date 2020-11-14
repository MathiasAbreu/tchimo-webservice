package br.com.ufcg.back.entities;

import br.com.ufcg.back.exceptions.user.UserAlreadyExistException;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Grupo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idGroup;

    private ArrayList<Long> memberIDs;

    public Grupo() {

        memberIDs = new ArrayList<Long>();
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
