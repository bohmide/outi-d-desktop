package entities;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Competition {
    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nomComp = new SimpleStringProperty();
    private StringProperty nomEntreprise = new SimpleStringProperty();
    private ObjectProperty<LocalDate> dateDebut = new SimpleObjectProperty<>();
    private ObjectProperty<LocalDate> dateFin = new SimpleObjectProperty<>();
    private StringProperty description = new SimpleStringProperty();
    private StringProperty fichier = new SimpleStringProperty();
    private ObjectProperty<Organisation> organisation = new SimpleObjectProperty<>();
  //  private DoubleProperty latitude = new SimpleDoubleProperty();
   // private DoubleProperty longitude = new SimpleDoubleProperty();
    private StringProperty localisation = new SimpleStringProperty();

    // Ajout de la liste des équipes
    private ListProperty<Equipe> equipes = new SimpleListProperty<>(FXCollections.observableArrayList());
    // Constructeurs
    public Competition() {}
    // Ajoutez ce constructeur
    public Competition(int id, String nomComp, String nomEntreprise,
                       LocalDate dateDebut, LocalDate dateFin,
                       String description, String fichier,
                       Organisation organisation) {
        this(id, nomComp, nomEntreprise, dateDebut, dateFin,
                description, fichier, organisation,
                FXCollections.observableArrayList(),"");
    }
    public Competition(int id, String nomComp, String nomEntreprise,
                       LocalDate dateDebut, LocalDate dateFin,
                       String description, String fichier,
                       Organisation organisation, List<Equipe> equipes) {
        this(id, nomComp, nomEntreprise, dateDebut, dateFin,
                description, fichier, organisation, equipes, "");
    }

    public Competition(int id, String nomComp, String nomEntreprise, LocalDate dateDebut, LocalDate dateFin, String description, String fichier, Organisation organisation,List<Equipe> equipes, String localisation) {
        this.id.set(id);
        this.nomComp.set(nomComp);
        this.nomEntreprise.set(nomEntreprise);
        this.dateDebut.set(dateDebut);
        this.dateFin.set(dateFin);
        this.description.set(description);
        this.fichier.set(fichier);
        this.organisation.set(organisation);
        //this.latitude.set(latitude);
       // this.longitude.set(longitude);
        this.localisation.set(localisation);
        if (equipes != null) {
            this.equipes.setAll(equipes);
        }
    }

    // Getters & Setters
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty nomCompProperty() {
        return nomComp;
    }

    public StringProperty nomEntrepriseProperty() {
        return nomEntreprise;
    }

    public ObjectProperty<LocalDate> dateDebutProperty() {
        return dateDebut;
    }

    public ObjectProperty<LocalDate> dateFinProperty() {
        return dateFin;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty fichierProperty() {
        return fichier;
    }

    public ObjectProperty<Organisation> organisationProperty() {
        return organisation;
    }
    public ListProperty<Equipe> equipesProperty() {
        return equipes;
    }

    public StringProperty localisationProperty() {
        return localisation;
    }



    // Getters classiques
    public int getId() {
        return id.get();
    }

    public String getNomComp() {
        return nomComp.get();
    }

    public String getNomEntreprise() {
        return nomEntreprise.get();
    }

    public LocalDate getDateDebut() {
        return dateDebut.get();
    }

    public LocalDate getDateFin() {
        return dateFin.get();
    }

    public String getDescription() {
        return description.get();
    }

    public String getFichier() {
        return fichier.get();
    }

    public Organisation getOrganisation() {
        return organisation.get();
    }
    public ObservableList<Equipe> getEquipes() {
        return equipes.get();
    }
    public String getNomOrganisation() {
        return organisation.get() != null ? organisation.get().getNomOrganisation() : "";
    }

    public String getLocalisation() {
        return localisation.get();
    }



    // Setters classiques
    public void setId(int id) {
        this.id.set(id);
    }

    public void setNomComp(String nomComp) {
        this.nomComp.set(nomComp);
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise.set(nomEntreprise);
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut.set(dateDebut);
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin.set(dateFin);
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public void setFichier(String fichier) {
        this.fichier.set(fichier);
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation.set(organisation);
    }
    public void setEquipes(ObservableList<Equipe> equipes) {
        this.equipes.set(equipes);
    }

    public void setLocalisation(String localisation) {
        this.localisation.set(localisation);
    }

}
