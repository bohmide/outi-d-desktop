package entities;

import javafx.beans.property.*;

public class Organisation {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nomOrganisation = new SimpleStringProperty();
    private StringProperty domaine = new SimpleStringProperty();

    // Constructeurs
    public Organisation() {}

    public Organisation(int id, String nomOrganisation, String domaine) {
        this.id.set(id);
        this.nomOrganisation.set(nomOrganisation);
        this.domaine.set(domaine);
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

    public String getNomOrganisation() {
        return nomOrganisation.get();
    }

    public void setNomOrganisation(String nomOrganisation) {
        this.nomOrganisation.set(nomOrganisation);
    }

    public StringProperty nomOrganisationProperty() {
        return nomOrganisation;
    }

    public String getDomaine() {
        return domaine.get();
    }

    public void setDomaine(String domaine) {
        this.domaine.set(domaine);
    }

    public StringProperty domaineProperty() {
        return domaine;
    }

    @Override
    public String toString() {
        return nomOrganisation.get(); // Utile pour l’affichage dans un ComboBox
    }
}
