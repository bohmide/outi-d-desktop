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

            System.out.println("Looking for FXML at: " + getClass().getResource("/views/MainView.fxml"));


            Parent root = FXMLLoader.load(getClass().getResource("/views/MainView.fxml"));

            primaryStage.setTitle("Gestion des Compétitions et Organisations");
            primaryStage.setScene(new Scene(root));
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