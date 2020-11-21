package br.com.ufcg.back.entities.dtos;

import br.com.ufcg.back.entities.Usuario;

public class NotificationDTO {

    private Long id;

    private UsuarioDTO user;
    private Long id_group;

    private String id_turma;
    private String name_turma;

    private String type;

    private Long creationDate;

    public NotificationDTO() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioDTO getUser() {
        return user;
    }

    public void setUser(UsuarioDTO user) {
        this.user = user;
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

    public String getName_turma() {
        return name_turma;
    }

    public void setName_turma(String name_turma) {
        this.name_turma = name_turma;
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

}
