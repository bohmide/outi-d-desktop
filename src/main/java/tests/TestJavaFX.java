package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TestJavaFX extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Load the ForumView FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ForumView.fxml"));
            Parent root = loader.load();
            
            // Create the scene
            Scene scene = new Scene(root);
            
            // Configure and show the stage
            stage.setTitle("Forums Application");
            stage.setScene(scene);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}

