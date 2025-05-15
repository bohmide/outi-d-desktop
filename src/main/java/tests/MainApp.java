package tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            //Parent root = FXMLLoader.load(getClass().getResource("/views/sponsor/sponsorView.fxml"));
            //Parent root = FXMLLoader.load(getClass().getResource("/views/genre/genreView.fxml"));
            Parent root = FXMLLoader.load(getClass().getResource("/views/eventView.fxml"));
            //Parent root = FXMLLoader.load(getClass().getResource("/views/eventStudentView.fxml"));

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            primaryStage.show();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}


/*


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Create the BorderPane as the main layout
        BorderPane root = new BorderPane();

        // Load the initial FXML (a welcome screen or a default service screen)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/test/WelcomeView.fxml"));
        root.setCenter(loader.load());

        // Create buttons to navigate between different services
        Button eventGenreButton = new Button("Event Genre Service");
        eventGenreButton.setOnAction(event -> navigateToService("/views/test/EventGenreView.fxml", root));

        Button eventServiceButton = new Button("Event Service");
        eventServiceButton.setOnAction(event -> navigateToService("/views/test/EventView.fxml", root));

        Button sponsorServiceButton = new Button("Sponsor Service");
        sponsorServiceButton.setOnAction(event -> navigateToService("/views/test/SponsorView.fxml", root));

        // Create a horizontal menu at the top
        root.setTop(createNavigationMenu(eventGenreButton, eventServiceButton, sponsorServiceButton));

        // Set up the main scene
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setTitle("Main Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to navigate to different services by loading the corresponding FXML view
    private void navigateToService(String fxmlPath, BorderPane root) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            root.setCenter(loader.load());
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getClassLoader().getResource("/views/MainAppMain.fxml")));
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

    // Method to create a navigation menu
    private HBox createNavigationMenu(Button... buttons) {
        HBox menu = new HBox(20);
        menu.setPadding(new Insets(10));
        menu.getChildren().addAll(buttons);
        return menu;
    }

    public static void main(String[] args) {
        launch(args);
    }
}*/


