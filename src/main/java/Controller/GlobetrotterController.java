package Controller;

import entities.QuizKids;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import netscape.javascript.JSObject;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import services.QuizKidsService;
import utils.VoiceAssistant;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GlobetrotterController {

    @FXML
    private WebView webView;
    private QuizKidsService quizService = new QuizKidsService();

    @FXML
    public void initialize() {
        System.out.println("Chargement du WebView...");
        WebEngine webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true); // Activation cruciale

        // Écouteur pour l'initialisation complète de la page
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                System.out.println("Page WebView chargée avec succès.");
                JSObject window = (JSObject) webEngine.executeScript("window");
                if (window == null) {
                    System.err.println("Erreur : javaConnector non disponible.");
                } else {
                    window.setMember("javaConnector", new JavaConnector());
                    System.out.println("javaConnector lié à la fenêtre JavaScript.");
                }
            }
        });
        webEngine.load(getClass().getResource("/interactive-map.html").toExternalForm());
    }

    public class JavaConnector {
        public void showQuizzes(String countryName) {
            System.out.println(">>> Méthode showQuizzes appelée avec : " + countryName);
            Platform.runLater(() -> {
                String normalizedName = countryName.trim().toLowerCase();

                List<QuizKids> quizzes = quizService.getAllQuizzes().stream()
                        .filter(q -> q.getCountry().toLowerCase().contains(normalizedName))
                        .collect(Collectors.toList());

                if (!quizzes.isEmpty()) {
                    openQuizWindow(quizzes);
                } else {
                    System.out.println("Aucun quiz pour : " + normalizedName);
                }
            });
        }

        public void speak(String countryName) {
            Platform.runLater(() -> {
                System.out.println("▶️ Voice Manager: " + countryName);
                // Ici tu peux appeler un TTS ou afficher une nouvelle scène de présentation vocale
                // Exemple simple (à remplacer par ton propre système vocal) :
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Voice Manager");
                alert.setHeaderText("Présentation du pays");
                alert.setContentText("Lecture vocale sur : " + countryName);
                alert.show();
            });
        }

        public void log(String msg) {
            System.out.println("JS Log: " + msg);
        }

        private void openQuizWindow(List<QuizKids> quizzes) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/QuizKidsPage.fxml"));
                Parent root = loader.load();

                QuizKidsPageController controller = loader.getController();
                controller.initialize(quizzes);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                System.err.println("Erreur d'ouverture des quizzes: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainGamification_View.fxml"));
            Parent root = loader.load();

            // Charger la scène du menu principal
            Stage stage = (Stage) webView.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

            // Optionnel : Ajouter un message vocal
            VoiceAssistant.speak("Retour au menu principal. Choisissez une nouvelle aventure!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
