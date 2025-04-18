package entities;

import javafx.beans.property.*;

public class Certification {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nomCertification = new SimpleStringProperty();
    private ObjectProperty<Cours> cours = new SimpleObjectProperty<>();

    public Certification() {}

    public Certification(int id, String nomCertification, Cours cours) {
        this.id.set(id);
        this.nomCertification.set(nomCertification);
        this.cours.set(cours);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNomCertification() {
        return nomCertification.get();
    }

    public void setNomCertification(String nomCertification) {
        this.nomCertification.set(nomCertification);
    }

    public StringProperty nomCertificationProperty() {
        return nomCertification;
    }

    public Cours getCours() {
        return cours.get();
    }

    public void setCours(Cours cours) {
        this.cours.set(cours);
    }

    public ObjectProperty<Cours> coursProperty() {
        return cours;
    }
}
