package controllers.genre;

import entities.EventGenre;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.EventGenreService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

public class AddGenreController {

    @FXML private TextField nameField;
    @FXML private TextField eventCountField;
    @FXML private TextField imageField;
    @FXML private Button browseImageButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final EventGenreService genreService = new EventGenreService();
    private EventGenre genreToEdit = null;

    public void setGenreToEdit(EventGenre genre) {
        this.genreToEdit = genre;
        nameField.setText(genre.getNomGenre());
        eventCountField.setText(String.valueOf(genre.getNbr()));
        imageField.setText(genre.getImagePath());
        saveButton.setText("Modifier");
    }

    @FXML
    public void handleSave(javafx.event.ActionEvent actionEvent) {
        String name = nameField.getText();
        String eventCountText = eventCountField.getText();
        String image = imageField.getText();

        if (name.isEmpty() || eventCountText.isEmpty() || image.isEmpty()) {
            showAlert("Erreur", "Tous les champs doivent être remplis");
            return;
        }

        try {
            int eventCount = Integer.parseInt(eventCountText);

            if (genreToEdit != null) {
                // Mise à jour
                genreToEdit.setNomGenre(name);
                genreToEdit.setNbr(eventCount);
                genreToEdit.setImagePath(image);
                genreService.updateEventGenre(genreToEdit);
            } else {
                // Création
                EventGenre newGenre = new EventGenre(name, eventCount, image, LocalDate.now());
                genreService.addEventGenre(newGenre);
            }

            // Fermer la fenêtre
            ((Stage) saveButton.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le nombre d'événements doit être un nombre valide");
        }
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(browseImageButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Chemin relatif dans le classpath
                String relativePath = "/images/" + selectedFile.getName();

                // Chemin absolu pour la copie
                Path destDir = Paths.get("src/main/resources/images");
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }

                Path destination = destDir.resolve(selectedFile.getName());

                // Copier le fichier
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le champ avec le chemin relatif
                imageField.setText(relativePath);

                // Forcer le rafraîchissement du système de fichiers
                try {
                    URL url = getClass().getResource("/images");
                    if (url != null && url.toURI().getScheme().equals("file")) {
                        File dir = new File(url.toURI());
                        dir.listFiles(); // Force la mise à jour du cache
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                showAlert("Erreur", "Impossible de copier l'image: " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}