package entities;

import java.time.LocalDate;

public class Event {
    int id;
    String nomEvent;
    String description;
    LocalDate dateEvent;
    int nbrMemebers;
    String imagePath;
    LocalDate dateCreation;
    float prix;

    public Event() {}

    public Event(int id, String nomEvent, String description, LocalDate dateEvent, int nbrMemebers, String imagePath, float prix) {
        this.id = id;
        this.nomEvent = nomEvent;
        this.description = description;
        this.dateEvent = dateEvent;
        this.nbrMemebers = nbrMemebers;
        this.imagePath = imagePath;
        this.prix = prix;
    }

    public Event(String nomEvent, String description, LocalDate dateEvent, int nbrMemebers, String imagePath, float prix) {
        this.nomEvent = nomEvent;
        this.description = description;
        this.dateEvent = dateEvent;
        this.nbrMemebers = nbrMemebers;
        this.imagePath = imagePath;
        this.prix = prix;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomEvent() {
        return nomEvent;
    }

    public void setNomEvent(String nomEvent) {
        this.nomEvent = nomEvent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateEvent() {
        return dateEvent;
    }

    public void setDateEvent(LocalDate dateEvent) {
        this.dateEvent = dateEvent;
    }

    public int getNbrMemebers() {
        return nbrMemebers;
    }

    public void setNbrMemebers(int nbrMemebers) {
        this.nbrMemebers = nbrMemebers;
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

    public float getPrix() {
        return prix;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nomEvent='" + nomEvent + '\'' +
                ", description='" + description + '\'' +
                ", dateEvent=" + dateEvent +
                ", nbrMemebers=" + nbrMemebers +
                ", imagePath='" + imagePath + '\'' +
                ", dateCreation=" + dateCreation +
                ", prix=" + prix +
                '}';
    }
}
