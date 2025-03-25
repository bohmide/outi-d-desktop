package entities;

import java.time.LocalDate;

public class EventGenre {
    int id;
    String nomGenre;
    int nbr;
    String imagePath;
    LocalDate dateCreation;

    public EventGenre() {}

    public EventGenre(int id, String nomGenre, int nbr, String imagePath, LocalDate dateCreation) {
        this.id = id;
        this.nomGenre = nomGenre;
        this.nbr = nbr;
        this.imagePath = imagePath;
        this.dateCreation = dateCreation;
    }

    public EventGenre(String nomGenre, int nbr, String imagePath, LocalDate dateCreation) {
        this.nomGenre = nomGenre;
        this.nbr = nbr;
        this.imagePath = imagePath;
        this.dateCreation = dateCreation;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomGenre() {
        return nomGenre;
    }

    public void setNomGenre(String nomGenre) {
        this.nomGenre = nomGenre;
    }

    public int getNbr() {
        return nbr;
    }

    public void setNbr(int nbr) {
        this.nbr = nbr;
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
        return "EventGenre{" +
                "id=" + id +
                ", nomGenre='" + nomGenre + '\'' +
                ", nbr=" + nbr +
                ", imagePath='" + imagePath + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }


}
