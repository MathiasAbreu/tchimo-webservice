package br.com.ufcg.back.entities.dtos;

import br.com.ufcg.back.entities.Usuario;

import java.util.ArrayList;
import java.util.List;
public class TurmaDTO {

    private String id;
    private String name;

    private long creationDate;
    private long endDate;

    private String formationStrategy;
    private String endingStrategy;

    private int quantityOfGroupsAvailable;

    private boolean usuario;

    private List<String> integrantes = new ArrayList<>();
    private List<String> groups = new ArrayList<>();

    public TurmaDTO(String id, String name, long creationDate, long endDate, String formationStrategy, String endingStrategy, int quantityOfGroupsAvailable, boolean usuario, List<Usuario> integrantes) {

        this.id = id;
        this.name = name;
        this.creationDate = creationDate;
        this.endDate = endDate;
        this.formationStrategy = formationStrategy;
        this.endingStrategy = endingStrategy;
        this.quantityOfGroupsAvailable = quantityOfGroupsAvailable;
        this.usuario = usuario;

        configureIntegrantes(integrantes);
    }

    public TurmaDTO() {

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

    public List<String> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<String> integrantes) {
        this.integrantes = integrantes;
    }

    public int getTotalGroups() {
        return groups.size();
    }
    private void configureIntegrantes(List<Usuario> integrantes) {

        for(Usuario usuario: integrantes)
            this.integrantes.add(usuario.getName());
    }
}
