package controllers.genre;

/*import entities.EventGenre;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.EventGenreService;

import java.time.LocalDate;


public class AddGenreController {
    @FXML
    private TextField nameField;

    @FXML
    private TextField eventCountField;

    @FXML
    private TextField imageField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private EventGenreService genreService = new EventGenreService();  // Assuming you have this service class.

    @FXML
    public void handleSave(javafx.event.ActionEvent actionEvent) {

        String name = nameField.getText();
        String eventCountText = eventCountField.getText();
        String image = imageField.getText();

        try {
            int eventCount = Integer.parseInt(eventCountText);

            // Create a new genre and save it
            EventGenre newGenre = new EventGenre(name, eventCount, image, LocalDate.now());
            genreService.addEventGenre(newGenre);  // Assuming you have a method in your service to save genres

            // Close the popup
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            // Handle invalid input
            showAlert("Invalid Input", "Event count must be a valid number.");
        }
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        // Close the popup without saving
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}*/



import entities.EventGenre;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.EventGenreService;

import java.time.LocalDate;

public class AddGenreController {

    @FXML private TextField nameField;
    @FXML private TextField eventCountField;
    @FXML private TextField imageField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final EventGenreService genreService = new EventGenreService();
    private EventGenre genreToEdit = null;

    // Méthode pour configurer le formulaire en mode édition
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

        try {
            int eventCount = Integer.parseInt(eventCountText);

            if (genreToEdit != null) {
                // Modifier un genre existant
                genreToEdit.setNomGenre(name);
                genreToEdit.setNbr(eventCount);
                genreToEdit.setImagePath(image);
                genreService.updateEventGenre(genreToEdit);
            } else {
                // Ajouter un nouveau genre
                EventGenre newGenre = new EventGenre(name, eventCount, image, LocalDate.now());
                genreService.addEventGenre(newGenre);
            }

            // Fermer la fenêtre
            ((Stage) saveButton.getScene().getWindow()).close();
        } catch (NumberFormatException e) {
            showAlert("Entrée invalide", "Le nombre d’événements doit être un nombre valide.");
        }
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        // Fermer la fenêtre sans rien faire
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
