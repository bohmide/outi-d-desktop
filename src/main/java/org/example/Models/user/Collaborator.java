package org.example.Models.user;

public class Collaborator {
    private Long id;
    private String nomCol;

    // ✅ Constructors
    public Collaborator() {}

    public Collaborator(String nomCol) {
        this.nomCol = nomCol;
    }

    // ✅ Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomcol() {
        return nomCol;
    }

    public void setNomCol(String nomCol) {
        this.nomCol = nomCol;
    }

    // ✅ toString method
    @Override
    public String toString() {
        return "Collaborator{" +
                "id=" + id +
                ", nomCol='" + nomCol + '\'' +
                '}';
    }
}
