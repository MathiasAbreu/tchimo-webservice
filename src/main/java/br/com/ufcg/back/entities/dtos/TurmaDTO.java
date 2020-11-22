package br.com.ufcg.back.entities.dtos;

import java.util.ArrayList;
import java.util.List;

public class TurmaDTO {

    private String id;
    private String name;

    private long creationDate;
    private long endDate;

    private String formationStrategy;
    private String endingStrategy;

    private int maxNumberOfGroups;
    private int currentNumberOfGroups;

    private boolean usuario;

    private boolean locked;

    private List<UsuarioDTO> integrantes = new ArrayList<>();
    private List<UsuarioDTO> integrantesSemGrupo = new ArrayList<>();

    private List<GrupoDTO> groups = new ArrayList<>();
    
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

    public int getMaxNumberOfGroups() {
        return maxNumberOfGroups;
    }

    public void setMaxNumberOfGroups(int maxNumberOfGroups) {
        this.maxNumberOfGroups = maxNumberOfGroups;
    }

    public int getCurrentNumberOfGroups() {
        return currentNumberOfGroups;
    }

    public void setCurrentNumberOfGroups(int currentNumberOfGroups) {
        this.currentNumberOfGroups = currentNumberOfGroups;
    }

    public boolean getUsuario() {
        return usuario;
    }

    public void setUsuario(boolean usuario) {
        this.usuario = usuario;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public List<UsuarioDTO> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<UsuarioDTO> integrantes) {
        this.integrantes = integrantes;
    }

    public List<UsuarioDTO> getIntegrantesSemGrupo() {
        return integrantesSemGrupo;
    }

    public void setIntegrantesSemGrupo(List<UsuarioDTO> integrantesSemGrupo) {
        this.integrantesSemGrupo = integrantesSemGrupo;
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

    public void addIntegrante(UsuarioDTO usuarioDTO) {
        integrantes.add(usuarioDTO);
    }

    public void addIntegranteSemGrupo(UsuarioDTO usuarioDTO) {
        integrantesSemGrupo.add(usuarioDTO);
    }

}