package controllers;
import entities.Competition;
import entities.Organisation;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceCompetition;
import services.ServiceOrganisation;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import javafx.stage.FileChooser;
import java.io.File;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;
import javafx.event.ActionEvent;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import javafx.concurrent.Worker;
import javafx.application.Platform;
import java.net.URL; // Ajoutez cette ligne avec les autres imports

public class addCompetitionFormController {

    @FXML private Label errorLabel;
    @FXML private TextField nomCompField;
    //@FXML private TextField nomEntrepriseField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Organisation> organisationComboBox;
    @FXML private Button actionButton;
    @FXML private TextField fichierField;
    @FXML private TextField localisationField;
    @FXML private WebView mapView;


    private WebEngine webEngine;

    private JSObject jsConnector;
    private Competition competition; // Pour stocker la compétition en cours d'édition
    private String fichierPath; // Pour stocker le chemin du fichier
    private final ServiceCompetition serviceCompetition = new ServiceCompetition();
    private final ServiceOrganisation serviceOrganisation = new ServiceOrganisation();
    private Integer currentCompetitionId = null;
    private String fichierExistant; // Pour stocker le nom du fichier existant

    @FXML
    public void initialize() {
        List<Organisation> organisations = serviceOrganisation.afficherOrganisations();
        organisationComboBox.setItems(FXCollections.observableArrayList(organisations));
        // Initialiser la carte
        initMap();
        }
    private void initMap() {
        if (mapView != null) {
            mapView.setPrefHeight(400); // Définir une hauteur fixe

            webEngine = mapView.getEngine();
            webEngine.setJavaScriptEnabled(true);

            // Activer les logs de la console
            webEngine.setOnAlert(event -> System.out.println("JS Alert: " + event.getData()));

            // Charger depuis les ressources
            URL mapHtml = getClass().getResource("/views/leafletMap.html");
            if (mapHtml != null) {
                webEngine.load(mapHtml.toExternalForm());

                webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        // Initialiser la communication
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaConnector", new JavaConnector());

                        // Vérifier le chargement de Leaflet
                        Boolean leafletLoaded = (Boolean) webEngine.executeScript("typeof L !== 'undefined'");
                        if (leafletLoaded != null && leafletLoaded) {
                            System.out.println("Leaflet chargé avec succès");
                        }
                    }
                });
            }
        }
    }

    // Modification de la méthode setMapLocation
    private void setMapLocation(double lat, double lng) {
        if (webEngine != null) {
            webEngine.executeScript(
                    "if(marker) marker.setLatLng([" + lat + ", " + lng + "]);" +
                            "if(map) map.setView([" + lat + ", " + lng + "]);"
            );
        }
    }
    public class JavaConnector {
        public void updateLocation(double lat, double lng, String address) {
            Platform.runLater(() -> {

                if (localisationField != null) {
                    localisationField.setText(address);
                }
            });
        }}
    @FXML
    private void handleFileSelection(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier");

        // Filtres pour les types de fichiers
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Images PNG", "*.png")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Créer le dossier uploads s'il n'existe pas
                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                // Copier le fichier dans le dossier uploads
                String fileName = selectedFile.getName();
                File destination = new File("uploads/" + fileName);
                Files.copy(selectedFile.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                fichierField.setText(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du téléchargement du fichier");
            }}
    }
    public void setCompetitionData(Competition competition) {
        currentCompetitionId = competition.getId();
        nomCompField.setText(competition.getNomComp());
      //  nomEntrepriseField.setText(competition.getNomEntreprise());
        dateDebutPicker.setValue(competition.getDateDebut());
        dateFinPicker.setValue(competition.getDateFin());
        descriptionArea.setText(competition.getDescription());
        localisationField.setText(competition.getLocalisation());
        organisationComboBox.getSelectionModel().select(competition.getOrganisation());




        // Modifier le texte du bouton lorsque en mode édition
        actionButton.setText("Modifier");

        // Sauvegarder le nom du fichier existant
        this.fichierExistant = competition.getFichier();

        // Afficher le nom du fichier existant s'il y en a un
        if (competition.getFichier() != null && !competition.getFichier().isEmpty()) {
            fichierField.setText(competition.getFichier());
        }
    }

    @FXML
    private void handleAction() {
        // Réinitialiser le message d'erreur
        errorLabel.setVisible(false);
        errorLabel.setText("");

        String nomComp = nomCompField.getText().trim();
        //String nomEntreprise = nomEntrepriseField.getText().trim();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        String description = descriptionArea.getText().trim();
        Organisation organisation = organisationComboBox.getValue();
        String localisation = localisationField.getText();

        // Validation des champs obligatoires
        if (nomComp.isEmpty()  || dateDebut == null || dateFin == null || organisation == null || description.isEmpty()) {
            errorLabel.setText("Tous les champs sont obligatoires (sauf le fichier).");
            errorLabel.setVisible(true);
            return;
        }

        // Vérification de la longueur de la description
        if (description.length() < 10) {
            errorLabel.setText("La description doit contenir au moins 10 caractères.");
            errorLabel.setVisible(true);
            return;
        }

        if (organisation == null) {
            showAlert("Erreur", "Veuillez choisir une organisation.");
            return;
        }




    // Gestion du fichier
        String nomFichier = fichierField.getText().isEmpty() ? fichierExistant : fichierField.getText();



        Competition comp = new Competition();
        comp.setNomComp(nomComp);
        comp.setNomEntreprise(organisation.getNomOrganisation());
        comp.setDateDebut(dateDebut);
        comp.setDateFin(dateFin);
        comp.setDescription(description);
        comp.setOrganisation(organisation);
        comp.setFichier(nomFichier);
        comp.setLocalisation(localisation);

        if (currentCompetitionId != null) {
            // Mode modification
            comp.setId(currentCompetitionId);
            serviceCompetition.modifierCompetition(comp);
            showAlert("Succès", "Compétition modifiée avec succès !");
        } else {
            // Mode ajout
            serviceCompetition.ajouterCompetition(comp);
            showAlert("Succès", "Compétition ajoutée avec succès !");
        }

        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) actionButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
