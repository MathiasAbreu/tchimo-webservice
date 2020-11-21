package br.com.ufcg.back.entities.dtos;

import java.util.ArrayList;
import java.util.List;

public class NotificationDTO {

    private Long id;

    private Long id_user;
    private Long id_group;

    private String id_turma;
    private String type;

    private Long creationDate;

    private List<UsuarioDTO> targetUsers = new ArrayList<>();

    public NotificationDTO() {
        super();
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

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }

    public List<UsuarioDTO> getTargetUsers() {
        return targetUsers;
    }

    public void setTargetUsers(List<UsuarioDTO> targetUsers) {
        this.targetUsers = targetUsers;
    }

    public void addTargetUser(UsuarioDTO usuarioDTO) {
        targetUsers.add(usuarioDTO);
    }
}
