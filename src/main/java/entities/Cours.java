package entities;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class Cours {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nom = new SimpleStringProperty();
    private ObjectProperty<LocalDate> date_creation = new SimpleObjectProperty<>(LocalDate.now());
    private StringProperty etat = new SimpleStringProperty();

    private List<Chapitres> chapitres;
    private Certification certification;

    // Constructeurs
    public Cours() {}

    public Cours(String nom, Date date_creation, String etat) {
        this.nom.set(nom);
        this.date_creation.set(convertToLocalDate(date_creation));
        this.etat.set(etat);
    }

    public LocalDate convertToLocalDate(java.util.Date date) {
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }

    // Getters & Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public LocalDate getDateCreation() {
        return date_creation.get();
    }

    public void setDateCreation(Date date) {
        this.date_creation.set(convertToLocalDate(date));

    }

    public ObjectProperty<LocalDate> dateCreationProperty() {
        return date_creation;
    }


    public String getEtat() {
        return etat.get();
    }

    public void setEtat(String etat) {
        this.etat.set(etat);
    }

    public StringProperty etatProperty() {
        return etat;
    }

    public List<Chapitres> getChapitres() {
        return chapitres;
    }

    public void setChapitres(List<Chapitres> chapitres) {
        this.chapitres = chapitres;
    }

    public Certification getCertification() {
        return certification;
    }

    public void setCertification(Certification certification) {
        this.certification = certification;
    }

    @Override
    public String toString() {
        return getNom();
    }
}
