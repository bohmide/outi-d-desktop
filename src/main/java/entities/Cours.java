package entities;

import javafx.beans.property.*;

import java.util.Date;
import java.util.List;

public class Cours {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nom = new SimpleStringProperty();
    private ObjectProperty<Date> dateCreation = new SimpleObjectProperty<>(new Date());
    private StringProperty etat = new SimpleStringProperty();

    private List<Chapitres> chapitres;
    private Certification certification;

    // Constructeurs
    public Cours() {}

    public Cours(String nom, Date dateCreation, String etat) {
        this.nom.set(nom);
        this.dateCreation.set(dateCreation);
        this.etat.set(etat);
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

    public Date getDateCreation() {
        return dateCreation.get();
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation.set(dateCreation);
    }

    public ObjectProperty<Date> dateCreationProperty() {
        return dateCreation;
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
