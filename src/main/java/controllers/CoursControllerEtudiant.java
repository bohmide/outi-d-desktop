package controllers;

import entities.Cours;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import Services.CoursService;

import java.io.IOException;
import java.util.List;

public class CoursControllerEtudiant {

    @FXML
    private GridPane gridCours;

    private final CoursService coursService = new CoursService();

    @FXML
    public void initialize() {
        chargerCoursDansGrid();
    }

    private void chargerCoursDansGrid() {
        List<Cours> coursList = coursService.recuperer();
        gridCours.getChildren().clear();

        int column = 0;
        int row = 1;

        for (Cours cours : coursList) {
            VBox card = creerCarteCours(cours);
            gridCours.add(card, column, row);
            GridPane.setMargin(card, new Insets(10));

            column++;
            if (column == 3) {
                column = 0;
                row++;
            }
        }
    }

    private VBox creerCarteCours(Cours cours) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 5, 0, 0);");
        box.setPrefWidth(280);
        box.setPrefHeight(150);

        // Course name
        Label nom = new Label(cours.getNom());
        nom.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2c3e50;");

        // Status with colored dot
        HBox statusBox = new HBox(8);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Circle statusDot = new Circle(6);
        statusDot.setFill(getStatusColor(cours.getEtat()));

        Label etat = new Label("État : " + cours.getEtat());
        etat.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        statusBox.getChildren().addAll(statusDot, etat);

        // View chapters button
        Button btn = new Button("Voir les chapitres");
        btn.setStyle("-fx-background-color: #4a6baf; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 5; " +
                "-fx-padding: 8 15; " +
                "-fx-cursor: hand;");
        btn.setOnAction(e -> navigateToChapitres(cours));
        btn.setMaxWidth(Double.MAX_VALUE);

        box.getChildren().addAll(nom, statusBox, btn);
        return box;
    }

    private Color getStatusColor(String etat) {
        return switch (etat.toLowerCase()) {
            case "facile" -> Color.web("#2ecc71"); // Green
            case "moyen" -> Color.web("#f39c12");  // Orange
            case "avancé" -> Color.web("#e74c3c"); // Red
            default -> Color.web("#3498db");        // Blue
        };
    }

    private void navigateToChapitres(Cours cours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChapitreEtudiantView.fxml"));
            Parent root = loader.load();

            ChapitreControllerEtudiant controller = loader.getController();
            controller.initData(cours);

            Scene scene = gridCours.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des chapitres.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/MainAppAccueil.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 200));
            stage.setTitle("OUTI-D - Choisissez votre rôle");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du retour à l'accueil.");
        }
    }
}