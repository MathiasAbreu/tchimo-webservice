package br.com.ufcg.back.entities.dtos;

import br.com.ufcg.back.daos.UsuariosDAO;
import br.com.ufcg.back.entities.Grupo;
import br.com.ufcg.back.entities.Usuario;
import br.com.ufcg.back.exceptions.user.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TurmaDTO {

    private String id;
    private String name;

    private long creationDate;
    private long endDate;

    private String formationStrategy;
    private String endingStrategy;

    private int quantityOfGroupsAvailable;

    private boolean usuario;

    private List<UsuarioDTO> integrantes = new ArrayList<>();
    private List<UsuarioDTO> integrantesSemGrupo = new ArrayList<>();

    private List<GrupoDTO> groups = new ArrayList<>();

    public TurmaDTO(String id, String name, long creationDate, long endDate, String formationStrategy, String endingStrategy, int quantityOfGroupsAvailable, boolean usuario) {

        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.endDate = endDate;
        this.formationStrategy = formationStrategy;
        this.endingStrategy = endingStrategy;
        this.quantityOfGroupsAvailable = quantityOfGroupsAvailable;
        this.usuario = usuario;
    }

    public TurmaDTO() {

    }

    public TurmaDTO(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getFormationStrategy() {
        return formationStrategy;
    }

    public void setFormationStrategy(String formationStrategy) {
        this.formationStrategy = formationStrategy;
    }

    public String getEndingStrategy() {
        return endingStrategy;
    }

    public void setEndingStrategy(String endingStrategy) {
        this.endingStrategy = endingStrategy;
    }

    public int getQuantityOfGroupsAvailable() {
        return quantityOfGroupsAvailable;
    }

    public void setQuantityOfGroupsAvailable(int quantityOfGroupsAvailable) {
        this.quantityOfGroupsAvailable = quantityOfGroupsAvailable;
    }

    public boolean getUsuario() {
        return usuario;
    }

    public void setUsuario(boolean usuario) {
        this.usuario = usuario;
    }

    public List<UsuarioDTO> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<UsuarioDTO> integrantes) {
        this.integrantes = integrantes;
    }

    public List<GrupoDTO> getGroups() {
        return groups;
    }

    public void setGroups(List<GrupoDTO> groups) {
        this.groups = groups;
    }

    public int getTotalGroups() {
        return groups.size();
    }

}
