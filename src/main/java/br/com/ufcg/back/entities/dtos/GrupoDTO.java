package br.com.ufcg.back.entities.dtos;

import java.util.ArrayList;
import java.util.List;

public class GrupoDTO {

    private Long idGroup;
    private Long idUserManager;

    private ArrayList<UsuarioDTO> memberIDs = new ArrayList<>();

    public GrupoDTO(Long idGroup, Long idUserManager) {

        super();
        this.idGroup = idGroup;
        this.idUserManager = idUserManager;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public Long getIdUserManager() {
        return idUserManager;
    }

    public void setIdUserManager(Long idUserManager) {
        this.idUserManager = idUserManager;
    }

    public ArrayList<UsuarioDTO> getMemberIDs() {
        return memberIDs;
    }

    public void setMemberIDs(ArrayList<UsuarioDTO> memberIDs) {
        this.memberIDs = memberIDs;
    }

    public void addUserDTO(UsuarioDTO usuarioDTO) {
        memberIDs.add(usuarioDTO);
    }
}
