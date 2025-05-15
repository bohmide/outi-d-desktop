package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainControlleretudiant {
    @FXML private StackPane contentPane;

    @FXML
    public void initialize() {
        showCompetitions(); // Affiche les compétitions par défaut
    }

    @FXML
    private void showCompetitions() {
        loadView("/views/CompetitionViewEtudiant.fxml");
    }



    @FXML
    private void showEquipes() {
        loadView("/views/EquipeView.fxml");
    }
    @FXML
    public void showTopOrganisations(){loadView("/views/TopOrganisationsView.fxml");}

    private void loadView(String fxmlPath) {
        try {
            contentPane.getChildren().clear();
            contentPane.getChildren().add(
                    FXMLLoader.load(getClass().getResource(fxmlPath))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void returnToMainMenu() {
        try {
            // Load the MainMenu.fxml
            Parent mainMenu = FXMLLoader.load(getClass().getResource("/views/MainMenu.fxml"));

            // Get the current stage
            Stage stage = (Stage) (contentPane != null ? contentPane.getScene().getWindow() : null);
            if (stage == null) {
                throw new IllegalStateException("Stage not found. Ensure the contentPane is part of a scene.");
            }

            // Create a new scene with the loaded FXML
            Scene scene = new Scene(mainMenu);
            scene.getStylesheets().add(getClass().getResource("/styles/mainF.css").toExternalForm());

            // Set the new scene to the stage
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

}
}