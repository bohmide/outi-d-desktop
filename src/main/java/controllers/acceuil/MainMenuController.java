package controllers.acceuil;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainMenuController {
    @FXML
    private StackPane rootPane;

    @FXML
    private void showCompetions() {
        loadView("/views/MainView.fxml");
    }

    @FXML
    private void showEvents() {
        loadView("/views/eventView.fxml");
    }

    @FXML
    private void showForum() {
        loadView("/views/ForumView.fxml");
    }

    @FXML
    private void showCours() {
        loadView("/views/CoursViewEtudiant.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            rootPane.getChildren().setAll(node);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Impossible de charger la vue: " + fxmlPath);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.err.println("Erreur: Le panneau root n'a pas été initialisé correctement");
        }
    }
}