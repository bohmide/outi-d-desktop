package entities;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;


public class Equipe {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty travail = new SimpleStringProperty("");
    private StringProperty nomEquipe = new SimpleStringProperty("");
    private StringProperty ambassadeur = new SimpleStringProperty("");
    private StringProperty membres = new SimpleStringProperty("");
    private StringProperty competitionNom = new SimpleStringProperty("");
    private ListProperty<Competition> competitions = new SimpleListProperty<>(FXCollections.observableArrayList());



    public Equipe() {}
    // Ajoutez ce constructeur
    public Equipe(int id, String travail, String nomEquipe,
                  String ambassadeur, String membres, String competitionNom) {
        this(id, travail, nomEquipe, ambassadeur, membres, competitionNom,
                FXCollections.observableArrayList());
    }
    public Equipe(int id, String travail, String nomEquipe, String ambassadeur, String membres,String competitionNom ,List<Competition> competitions ) {
        this.id.set(id);
        this.travail.set(travail);
        this.nomEquipe.set(nomEquipe);
        this.ambassadeur.set(ambassadeur);
        this.membres.set(membres);
        this.competitionNom.set (competitionNom);
        if (competitions != null) {
            this.competitions.setAll(competitions);
        }
    }

    // Propriétés JavaFX
    public IntegerProperty idProperty() { return id; }
    public StringProperty travailProperty() { return travail; }
    public StringProperty nomEquipeProperty() { return nomEquipe; }
    public StringProperty ambassadeurProperty() { return ambassadeur; }
    public StringProperty membresProperty() { return membres; }
    public StringProperty competitionNomProperty() {return competitionNom;}
    public ListProperty<Competition> competitionsProperty() {return competitions;}

    // Getters classiques
    public int getId() { return id.get(); }
    public String getTravail() { return travail.get(); }
    public String getNomEquipe() { return nomEquipe.get(); }
    public String getAmbassadeur() { return ambassadeur.get(); }
    public String getMembres() { return membres.get(); }
    public String getCompetitionNom() {return competitionNom.get();}
    public ObservableList<Competition> getCompetitions() {return competitions.get();}

    // Setters classiques
    public void setId(int id) { this.id.set(id); }
    public void setTravail(String travail) { this.travail.set(travail); }
    public void setNomEquipe(String nomEquipe) { this.nomEquipe.set(nomEquipe); }
    public void setAmbassadeur(String ambassadeur) { this.ambassadeur.set(ambassadeur); }
    public void setMembres(String membres) { this.membres.set(membres); }
    public void setCompetitionNom(String competitionNom) {
        this.competitionNom.set(competitionNom);
    }
    public void setCompetitions(ObservableList<Competition> competitions) {
        this.competitions.set(competitions);
    }
}
