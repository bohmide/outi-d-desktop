package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML private StackPane contentPane;
    @FXML private Button returnButton;



    @FXML
    public void initialize() {
        showCompetitions(); // Affiche les compétitions par défaut
    }

    @FXML
    private void showCompetitions() {
        loadView("/views/CompetitionView.fxml");
    }

    @FXML
    private void showOrganisations() {
        loadView("/views/OrganisationView.fxml");
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
            Node mainMenu = FXMLLoader.load(getClass().getResource("/views/MainMenu.fxml"));
            StackPane parentContainer = (StackPane) returnButton.getScene().getRoot();
            parentContainer.getChildren().setAll(mainMenu);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}