package controllers;

import entities.Competition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import javafx.scene.control.Hyperlink; // Ajoutez cette ligne
import services.PDFService;



public class CompetitionDetailsController {
    @FXML private Label titleLabel;
    @FXML private Label organisationLabel;
    @FXML private Label dateLabel;
    @FXML private TextArea descriptionLabel;
    @FXML private VBox fichierContainer;
    @FXML private Label fichierLabel;
    @FXML private TextField localisationField;
    @FXML

    private Hyperlink localisationLink;
    private String googleMapsUrl;
    private File fichier; // Déclarer une variable pour le fichier
    private Stage stage;
    private Scene previousScene;
    private File competitionFile;
    private Competition competition;

    private final PDFService pdfService = new PDFService();
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }

    public void setCompetitionDetails(Competition comp,String title, String organisation, String date, String description, File fichier) {
        titleLabel.setText(title);
        organisationLabel.setText(organisation);
        dateLabel.setText(date);
        descriptionLabel.setText(description);
        // Ajout explicite de la localisation
        if (comp.getLocalisation() != null && !comp.getLocalisation().isEmpty()) {
            String address = comp.getLocalisation();
            localisationField.setText(comp.getLocalisation());
            try {
                String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
                googleMapsUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
                localisationLink.setText(address);
                localisationLink.setOnAction(e -> openGoogleMaps());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            localisationField.setText("Localisation non spécifiée");
        }

        // Gestion du fichier
        this.competitionFile = fichier;  // Stocke le fichier dans la variable d'instance

        if (comp.getFichier() != null && !comp.getFichier().isEmpty()) {
            fichierLabel.setText(comp.getFichier());
            fichierContainer.setVisible(true);
        } else {
            fichierContainer.setVisible(false);
        }
    }
    private void openGoogleMaps() {
        if (googleMapsUrl != null) {
            try {
                Desktop.getDesktop().browse(new URI(googleMapsUrl));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void goBack() {
        stage.setScene(previousScene);
    }
    @FXML
    private void downloadFile() {
        if (competitionFile != null) {
            try {
                // Obtenir le nom du fichier
                String fileName = competitionFile.getName();

                // Construire le chemin source dans le dossier "uploads"
                Path sourcePath = Paths.get("uploads", fileName);

                // Ouvrir le FileChooser pour sélectionner l’emplacement de sauvegarde
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Enregistrer le fichier");
                fileChooser.setInitialFileName(fileName);
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

                File destination = fileChooser.showSaveDialog(stage);

                if (destination != null) {
                    // Copier le fichier depuis "uploads" vers l'emplacement choisi
                    Files.copy(sourcePath, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Téléchargement réussi");
                    alert.setHeaderText(null);
                    alert.setContentText("Le fichier a été téléchargé avec succès !");
                    alert.showAndWait();
                }  if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(destination);
                    } catch (IOException e) {
                        showAlert("Ouverture automatique échouée",
                                "Le PDF a été généré avec succès mais n'a pas pu être ouvert.\n" +
                                        "Chemin d'accès : " + destination.getAbsolutePath());
                    }
                } else {

                    showAlert("PDF généré avec succès!", "Le fichier a été enregistré ici:\n" + destination.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors du téléchargement.");
                alert.showAndWait();
            }
        }
    }
    @FXML
    private void generatePDF() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Enregistrer le PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        File file = fileChooser.showSaveDialog(localisationField.getScene().getWindow());

        if (file != null) {
            try {
                pdfService.generateCompetitionPDF(
                        titleLabel.getText(),
                        organisationLabel.getText(),
                        dateLabel.getText(),
                        descriptionLabel.getText(),
                        localisationField.getText(),
                        file.getAbsolutePath()
                );
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().open(file);
                    } catch (IOException e) {
                        showAlert("Ouverture automatique échouée",
                                "Le PDF a été généré avec succès mais n'a pas pu être ouvert.\n" +
                                        "Chemin d'accès : " + file.getAbsolutePath());
                    }
                } else {

                    showAlert("PDF généré avec succès!", "Le fichier a été enregistré ici:\n" + file.getAbsolutePath());
                }
            } catch (IOException e) {
                showAlert("Erreur", "Échec de la génération du PDF: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}