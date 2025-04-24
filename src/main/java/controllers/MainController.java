package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {
    @FXML private StackPane contentPane;

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

}