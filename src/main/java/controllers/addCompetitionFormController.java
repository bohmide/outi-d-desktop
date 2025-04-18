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
public class addCompetitionFormController {

    @FXML private Label errorLabel;
    @FXML private TextField nomCompField;
    @FXML private TextField nomEntrepriseField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextArea descriptionArea;
    @FXML private ComboBox<Organisation> organisationComboBox;
    @FXML private Button actionButton;
    @FXML private TextField fichierField;
    private String fichierPath; // Pour stocker le chemin du fichier
    private final ServiceCompetition serviceCompetition = new ServiceCompetition();
    private final ServiceOrganisation serviceOrganisation = new ServiceOrganisation();
    private Integer currentCompetitionId = null;

    @FXML
    public void initialize() {
        List<Organisation> organisations = serviceOrganisation.afficherOrganisations();
        organisationComboBox.setItems(FXCollections.observableArrayList(organisations));
    }
    @FXML
    private void handleFileSelection() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner un fichier");

        // Filtres pour les types de fichiers
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf"),
                new FileChooser.ExtensionFilter("Images PNG", "*.png")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            this.fichierPath = selectedFile.getAbsolutePath();
            fichierField.setText(selectedFile.getName());
        }
    }
    public void setCompetitionData(Competition competition) {
        currentCompetitionId = competition.getId();
        nomCompField.setText(competition.getNomComp());
        nomEntrepriseField.setText(competition.getNomEntreprise());
        dateDebutPicker.setValue(competition.getDateDebut());
        dateFinPicker.setValue(competition.getDateFin());
        descriptionArea.setText(competition.getDescription());
        organisationComboBox.getSelectionModel().select(competition.getOrganisation());

        // Modifier le texte du bouton lorsque en mode édition
        actionButton.setText("Modifier");
        if (competition.getFichier() != null && !competition.getFichier().isEmpty()) {
            fichierField.setText(competition.getFichier());
            this.fichierPath = competition.getFichier(); // Conserver le chemin original
        }
    }

    @FXML
    private void handleAction() {
        // Réinitialiser le message d'erreur
        errorLabel.setVisible(false);
        errorLabel.setText("");

        String nomComp = nomCompField.getText().trim();
        String nomEntreprise = nomEntrepriseField.getText().trim();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        String description = descriptionArea.getText().trim();
        Organisation organisation = organisationComboBox.getValue();

        // Validation des champs obligatoires
        if (nomComp.isEmpty() || nomEntreprise.isEmpty() || dateDebut == null || dateFin == null || organisation == null || description.isEmpty()) {
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
        String nomFichier = null;
        if (organisation == null) {
            showAlert("Erreur", "Veuillez choisir une organisation.");
            return;
        }

        if (fichierPath != null && !fichierPath.isEmpty()) {
            try {
                // Créer un répertoire 'uploads' s'il n'existe pas
                File uploadDir = new File("uploads");
                if (!uploadDir.exists()) uploadDir.mkdir();

                // Copier le fichier dans le répertoire uploads
                File source = new File(fichierPath);
                File dest = new File(uploadDir, source.getName());
                Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);

                nomFichier = dest.getName();
            } catch (IOException e) {
                showAlert("Erreur", "Impossible d'enregistrer le fichier");
                return;
            }
        }

        Competition comp = new Competition();
        comp.setNomComp(nomComp);
        comp.setNomEntreprise(nomEntreprise);
        comp.setDateDebut(dateDebut);
        comp.setDateFin(dateFin);
        comp.setDescription(description);
        comp.setOrganisation(organisation);
        comp.setFichier(nomFichier);

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