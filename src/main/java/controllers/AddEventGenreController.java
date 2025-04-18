package controllers;

import entities.EventGenre;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import services.EventGenreService;

import java.time.LocalDate;

public class AddEventGenreController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField nbrField;

    @FXML
    private TextField imagePathField;

    @FXML
    private Button saveButton;

    private EventGenreService eventGenreService = new EventGenreService();

    @FXML
    public void initialize() {
        saveButton.setOnAction(event -> {
            String name = nameField.getText();
            int nbr = Integer.parseInt(nbrField.getText());
            String imagePath = imagePathField.getText();
            LocalDate currentDate = LocalDate.now();

            EventGenre newGenre = new EventGenre(name, nbr, imagePath, currentDate);
            eventGenreService.addEventGenre(newGenre);
            showAlert("Success", "Event genre added successfully!", AlertType.INFORMATION);
        });
    }

    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
