package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class GenreController {

    @FXML
    private Button addButton;

    @FXML
    private Button listButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button modifyButton;

    @FXML
    public void initialize() {
        addButton.setOnAction(event -> openWindow("/views/AddEventGenre.fxml", "Add Event Genre"));
        listButton.setOnAction(event -> openWindow("/views/ListEventGenres.fxml", "List Event Genres"));

        deleteButton.setOnAction(event -> {
            System.out.println("Delete Event Genre clicked");
            // Optional: Add logic to open a deletion interface or dialog.
        });

        modifyButton.setOnAction(event -> {
            System.out.println("Modify Event Genre clicked");
            // Optional: Add logic to open a modification interface or dialog.
        });
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Failed to load " + fxmlPath + ": " + e.getMessage());
        }
    }
}
