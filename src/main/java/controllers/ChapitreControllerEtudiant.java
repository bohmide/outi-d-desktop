package controllers;

import Services.ChapitreService;
import entities.Chapitres;
import entities.Cours;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;

public class ChapitreControllerEtudiant {

    @FXML
    private Label lblCoursTitle;
    @FXML
    private Button btnRetour;
    @FXML
    private VBox vboxChapitres; // Reference to the VBox in FXML

    private final ChapitreService chapitreService = new ChapitreService();
    private Cours currentCours;

    public void initData(Cours cours) {
        this.currentCours = cours;
        lblCoursTitle.setText("Chapitres du cours : " + cours.getNom());
        loadChapitresAsCards();
    }

    private void loadChapitresAsCards() {
        vboxChapitres.getChildren().clear(); // Clear existing content

        if (currentCours != null) {
            chapitreService.recuperer(currentCours).forEach(chapitre -> {
                // Create a card for each chapter
                VBox card = createChapterCard(chapitre);
                vboxChapitres.getChildren().add(card);
            });
        }
    }
    private VBox createChapterCard(Chapitres chapitre) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.setPadding(new Insets(15));
        card.setSpacing(10);
        card.setMaxWidth(800);

        // Chapter name
        Label nameLabel = new Label(chapitre.getNomChapitre());
        nameLabel.setFont(Font.font(16));
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Content area — DÉCLARÉ AVANT le listener
        TextArea contentArea = new TextArea(chapitre.getContenuText());
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setVisible(false);
        contentArea.setMaxHeight(200);
        contentArea.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");

        // Toggle button with icon
        HBox toggleContainer = new HBox();
        toggleContainer.setAlignment(Pos.CENTER_LEFT);
        toggleContainer.setSpacing(5);

        Label toggleIcon = new Label("▼");
        toggleIcon.setStyle("-fx-text-fill: #4a6baf; -fx-font-weight: bold;");

        Label toggleLabel = new Label("Afficher le contenu");
        toggleLabel.setStyle("-fx-text-fill: #4a6baf;");

        toggleContainer.getChildren().addAll(toggleIcon, toggleLabel);
        toggleContainer.setStyle("-fx-cursor: hand; -fx-padding: 5;");

        // Maintenant que contentArea est déjà défini, c'est bon
        toggleContainer.setOnMouseClicked(e -> toggleContent(contentArea, toggleIcon, toggleLabel));

        // Quiz button
        Button quizButton = new Button("Voir Quiz");
        quizButton.setStyle("-fx-background-color: #4a6baf; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        quizButton.setOnAction(event -> navigateToQuiz(chapitre));

        // Add elements to card
        card.getChildren().addAll(nameLabel, toggleContainer, contentArea, quizButton);

        return card;
    }


    private void toggleContent(TextArea contentArea, Label icon, Label label) {
        boolean willShow = !contentArea.isVisible();
        contentArea.setVisible(willShow);
        icon.setText(willShow ? "▲" : "▼");
        label.setText(willShow ? "Masquer le contenu" : "Afficher le contenu");
    }
    private void navigateToQuiz(Chapitres chapitre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuizViewEtudiant.fxml"));
            Parent root = loader.load();

            QuizControllerEtudiant controller = loader.getController();
            controller.initData(chapitre, currentCours);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement du quiz.");
        }
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CoursViewEtudiant.fxml"));
            Parent root = loader.load();
            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}