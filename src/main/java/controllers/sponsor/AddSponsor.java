package controllers.sponsor;

import entities.Sponsors;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.SponsorsService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

public class AddSponsor {
    @FXML private TextField nameField;
    @FXML private TextField descriptionField;
    @FXML private TextField imageField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button browseButton;

    private SponsorsService sponsorsService = new SponsorsService();
    private Sponsors currentSponsor;

    public void setSponsor(Sponsors sponsor) {
        this.currentSponsor = sponsor;
        nameField.setText(sponsor.getNomSponsor());
        descriptionField.setText(sponsor.getDescription());
        imageField.setText(sponsor.getImagePath());
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String desc = descriptionField.getText().trim();
        String img = imageField.getText().trim();

        if (name.isEmpty() || desc.isEmpty() || img.isEmpty()) {
            showAlert("Erreur", "Tous les champs sont obligatoires.");
            return;
        }

        try {
            if (currentSponsor == null) {
                Sponsors sp = new Sponsors(name, desc, img, LocalDate.now());
                sponsorsService.addSponsor(sp);
            } else {
                currentSponsor.setNomSponsor(name);
                currentSponsor.setDescription(desc);
                currentSponsor.setImagePath(img);
                sponsorsService.updateSponsor(currentSponsor);
            }
            ((Stage) saveButton.getScene().getWindow()).close();
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Créer le dossier images s'il n'existe pas
                Path destDir = Paths.get("src/main/resources/images");
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }

                // Générer un nom unique pour le fichier
                String uniqueName = UUID.randomUUID() + "_" + selectedFile.getName();
                Path destination = destDir.resolve(uniqueName);

                // Copier le fichier
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le champ avec le chemin relatif
                imageField.setText("/images/" + uniqueName);
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de copier l'image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}