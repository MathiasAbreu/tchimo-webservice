package br.com.ufcg.back.entities.dtos;

import java.util.ArrayList;

public class GrupoDTO {

    private Long idGroup;
    private Long idUserManager;

    private ArrayList<UsuarioDTO> members = new ArrayList<>();

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

    public ArrayList<UsuarioDTO> getMembers() {
        return members;
    }

    public void setMembers(ArrayList<UsuarioDTO> members) {
        this.members = members;
    }

    public void addUserDTO(UsuarioDTO usuarioDTO) {
        members.add(usuarioDTO);
    }
}
