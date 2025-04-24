package controllers.tests;

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
    public void initialize() {
        addButton.setOnAction(event -> openWindow("/views/test/AddEventGenre.fxml", "Add Event Genre"));
        listButton.setOnAction(event -> openWindow("/views/test/ListEventGenres.fxml", "List Event Genres"));


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
