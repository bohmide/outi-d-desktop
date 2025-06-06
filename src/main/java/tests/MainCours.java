package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainCours extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/CoursViewEtudiant.fxml"));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            primaryStage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}