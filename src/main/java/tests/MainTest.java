package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            System.out.println("Looking for FXML at: " + getClass().getResource("/views/test/MainView.fxml"));


            Parent root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/mainF.css").toExternalForm());
            primaryStage.setTitle("Gestion des Compétitions et Organisations");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true); // Pour démarrer en mode plein écran

            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Error loading FXML file:");
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}