package entities;

import java.time.LocalDate;

public class Sponsors {
    int id;
    String nomSponsor;
    String description;
    String imagePath;
    LocalDate dateCreation;

    public Sponsors() {}

    public Sponsors(String nomSponsor, String description, String imagePath, LocalDate dateCreation) {
        this.nomSponsor = nomSponsor;
        this.description = description;
        this.imagePath = imagePath;
        this.dateCreation = dateCreation;
    }

    public Sponsors(int id, String nomSponsor, String description, String imagePath, LocalDate dateCreation) {
        this.id = id;
        this.nomSponsor = nomSponsor;
        this.description = description;
        this.imagePath = imagePath;
        this.dateCreation = dateCreation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomSponsor() {
        return nomSponsor;
    }

    public void setNomSponsor(String nomSponsor) {
        this.nomSponsor = nomSponsor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Sponsors{" +
                "id=" + id +
                ", nomSponsor='" + nomSponsor + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
