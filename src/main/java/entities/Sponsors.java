package entities;

import com.mysql.cj.x.protobuf.MysqlxCrud;

import java.time.LocalDate;
import java.util.List;

public class Sponsors {
    int id;
    String nomSponsor;
    String description;
    String imagePath;
    LocalDate dateCreation;
    List<Event_sponsors> evenement;


    public Sponsors() {}

    public Sponsors(String nomSponsor, String description, String imagePath, LocalDate dateCreation, List<Event_sponsors> evenement) {
        this.nomSponsor = nomSponsor;
        this.description = description;
        this.imagePath = imagePath;
        this.dateCreation = dateCreation;
        this.evenement = evenement;
    }

    public Sponsors(int id, String nomSponsor, String description, String imagePath, LocalDate dateCreation, List<Event_sponsors> evenement) {
        this.id = id;
        this.nomSponsor = nomSponsor;
        this.description = description;
        this.imagePath = imagePath;
        this.dateCreation = dateCreation;
        this.evenement = evenement;
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

    public List<Event_sponsors> getEvenement() {
        return evenement;
    }

    public void setEvenement(List<Event_sponsors> evenement) {
        this.evenement = evenement;
    }

    @Override
    public String toString() {
        return "Sponsors{" +
                "id=" + id +
                ", nomSponsor='" + nomSponsor + '\'' +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                ", dateCreation=" + dateCreation +
                ", evenement=" + evenement +
                '}';
    }
}
