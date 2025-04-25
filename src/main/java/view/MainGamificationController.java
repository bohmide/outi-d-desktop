package view;

import dao.QuizKidsDAO;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public class MainGamificationController extends Application {




    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/MainGamification_View.fxml")));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
        primaryStage.setTitle("Menu Principal");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @FXML private VBox gameMenu;
    @FXML
    private void toggleGameMenu() {
        boolean isVisible = gameMenu.isVisible();
        gameMenu.setVisible(!isVisible);
        gameMenu.setManaged(!isVisible); // pour que ça ne prenne pas de place quand caché
    }

    @FXML
    private void handleQuiz() {
        loadScene("/QuizSelectionView.fxml", (Stage) gameMenu.getScene().getWindow());

    }


    @FXML
    private void handleGames() {
        loadScene("/QuizSelectionView.fxml", (Stage) gameMenu.getScene().getWindow());
    }
    @FXML
    private void handleMemoryCard() {
        loadScene("/MemoryCardSelection.fxml", (Stage) gameMenu.getScene().getWindow());
    }

    @FXML
    private void handlePuzzle() {
        loadScene("/PuzzleSelectionView.fxml", (Stage) gameMenu.getScene().getWindow());
    }


    @FXML
    private void handleGlobtrotter() {
        loadScene("/QuizSelectionView.fxml", (Stage) gameMenu.getScene().getWindow());
    }

    private void loadScene(String fxmlPath, Stage stage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
            stage.setScene(scene);
            stage.centerOnScreen();

            root.setOpacity(0);
            Timeline fadeIn = new Timeline(new KeyFrame(Duration.seconds(0.3), new KeyValue(root.opacityProperty(), 1)));
            fadeIn.play();

        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de chargement : " + e.getMessage());
        }
    }


    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
