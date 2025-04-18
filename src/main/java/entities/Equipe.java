package entities;

import javafx.beans.property.*;

public class Equipe {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty travail = new SimpleStringProperty("");
    private StringProperty nomEquipe = new SimpleStringProperty("");
    private StringProperty ambassadeur = new SimpleStringProperty("");
    private StringProperty membres = new SimpleStringProperty("");

    public Equipe() {}

    public Equipe(int id, String travail, String nomEquipe, String ambassadeur, String membres) {
        this.id.set(id);
        this.travail.set(travail);
        this.nomEquipe.set(nomEquipe);
        this.ambassadeur.set(ambassadeur);
        this.membres.set(membres);
    }

    // Propriétés JavaFX
    public IntegerProperty idProperty() { return id; }
    public StringProperty travailProperty() { return travail; }
    public StringProperty nomEquipeProperty() { return nomEquipe; }
    public StringProperty ambassadeurProperty() { return ambassadeur; }
    public StringProperty membresProperty() { return membres; }

    // Getters classiques
    public int getId() { return id.get(); }
    public String getTravail() { return travail.get(); }
    public String getNomEquipe() { return nomEquipe.get(); }
    public String getAmbassadeur() { return ambassadeur.get(); }
    public String getMembres() { return membres.get(); }

    // Setters classiques
    public void setId(int id) { this.id.set(id); }
    public void setTravail(String travail) { this.travail.set(travail); }
    public void setNomEquipe(String nomEquipe) { this.nomEquipe.set(nomEquipe); }
    public void setAmbassadeur(String ambassadeur) { this.ambassadeur.set(ambassadeur); }
    public void setMembres(String membres) { this.membres.set(membres); }
}
