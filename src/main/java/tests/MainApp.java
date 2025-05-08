package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("views/MainAppAccueil.fxml")));
            Parent root = loader.load();

            // Récupérer les boutons depuis le FXML
            Button btnProf = (Button) root.lookup("#btnProf");
            Button btnEtudiant = (Button) root.lookup("#btnEtudiant");

            // Action bouton Professeur
            btnProf.setOnAction(e -> {
                try {
                    Parent profRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("views/CoursProfView.fxml")));
                    primaryStage.setScene(new Scene(profRoot, 800, 600));
                    primaryStage.setTitle("Espace Professeur");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            // Action bouton Étudiant
            btnEtudiant.setOnAction(e -> {
                try {
                    Parent etuRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("views/CoursViewEtudiant.fxml")));
                    primaryStage.setScene(new Scene(etuRoot, 800, 600));
                    primaryStage.setTitle("Espace Étudiant");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            Scene scene = new Scene(root, 400, 300);
            primaryStage.setScene(scene);
            primaryStage.setTitle("OUTI-D - Accueil");
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
