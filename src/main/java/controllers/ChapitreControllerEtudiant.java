package controllers;

import Services.ChapitreService;
import entities.Chapitres;
import entities.Cours;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

public class ChapitreControllerEtudiant {

    @FXML private Label lblCoursTitle;
    @FXML private Button btnRetour;
    @FXML private VBox vboxChapitres;
    @FXML private Label lblProgression;
    @FXML private ComboBox<String> languageCombo;

    private final ChapitreService chapitreService = new ChapitreService();
    private Cours currentCours;
    private ResourceBundle bundle;
    private int totalChapitres = 0;
    private final Set<Integer> chapitresVus = new HashSet<>();

    @FXML
    public void initialize() {
        // Initialise avec la langue par défaut
        if (bundle == null) {
            this.bundle = ResourceBundle.getBundle("lang/messages", Locale.getDefault());

            languageCombo.getItems().addAll("Français", "English");
            languageCombo.setValue("Français");
            languageCombo.setOnAction(e -> {
                Locale locale = languageCombo.getValue().equals("Français") ? Locale.FRENCH : Locale.ENGLISH;
                this.bundle = ResourceBundle.getBundle("lang/messages", locale);
                refreshUI();
            });
            }

    }

    public void initData(Cours cours, ResourceBundle bundle) {
        this.currentCours = cours;
        this.bundle = bundle;
        refreshUI();
        loadChapitresAsCards();
    }

    private void refreshUI() {
        if (currentCours != null && bundle != null) {
            lblCoursTitle.setText(MessageFormat.format(
                    bundle.getString("course.title"),
                    currentCours.getNom()
            ));
            btnRetour.setText(bundle.getString("back.button"));
            updateProgressionLabel();
            loadChapitresAsCards();
        }
    }

    private void loadChapitresAsCards() {
        vboxChapitres.getChildren().clear();

        if (currentCours != null) {
            var listeChapitres = chapitreService.recuperer(currentCours);
            totalChapitres = listeChapitres.size();
            updateProgressionLabel();

            listeChapitres.forEach(chapitre -> {
                VBox card = createChapterCard(chapitre);
                vboxChapitres.getChildren().add(card);
            });
        }
    }

    private VBox createChapterCard(Chapitres chapitre) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0); -fx-padding: 15;");
        card.setSpacing(10);
        card.setMaxWidth(800);

        Label nameLabel = new Label(chapitre.getNomChapitre());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TextArea contentArea = new TextArea(chapitre.getContenuText());
        contentArea.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-radius: 5;");
        contentArea.setEditable(false);
        contentArea.setWrapText(true);
        contentArea.setVisible(false);
        contentArea.setMaxHeight(200);

        HBox toggleContainer = new HBox();
        toggleContainer.setStyle("-fx-cursor: hand; -fx-padding: 5;");
        toggleContainer.setAlignment(Pos.CENTER_LEFT);
        toggleContainer.setSpacing(5);

        Label toggleIcon = new Label("▼");
        toggleIcon.setStyle("-fx-text-fill: #4a6baf; -fx-font-weight: bold;");

        Label toggleLabel = new Label(bundle.getString("show.content"));
        toggleLabel.setStyle("-fx-text-fill: #4a6baf;");

        toggleContainer.getChildren().addAll(toggleIcon, toggleLabel);

        toggleContainer.setOnMouseClicked(e -> {
            boolean willShow = !contentArea.isVisible();
            contentArea.setVisible(willShow);
            toggleIcon.setText(willShow ? "▲" : "▼");
            toggleLabel.setText(willShow ? bundle.getString("hide.content") : bundle.getString("show.content"));

            if (!chapitresVus.contains(chapitre.getId())) {
                chapitresVus.add(chapitre.getId());
                updateProgressionLabel();
            }
        });

        Button quizButton = new Button(bundle.getString("quiz.button"));
        quizButton.setStyle("-fx-background-color: #4a6baf; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        quizButton.setOnAction(event -> navigateToQuiz(chapitre));
        card.getChildren().addAll(nameLabel, toggleContainer, contentArea, quizButton);
        return card;
    }

    private void updateProgressionLabel() {
        lblProgression.setText(MessageFormat.format(
                bundle.getString("progress"),
                chapitresVus.size(),
                totalChapitres
        ));
    }

    @FXML
    private void changeLanguage(ActionEvent event) {
        String selected = languageCombo.getValue();
        Locale locale = selected.equals("Français") ? Locale.FRENCH : Locale.ENGLISH;
        this.bundle = ResourceBundle.getBundle("lang/messages", locale);
        refreshUI();
    }

    private void navigateToQuiz(Chapitres chapitre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuizViewEtudiant.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            QuizControllerEtudiant controller = loader.getController();
            controller.initData(chapitre, currentCours,bundle);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error loading quiz view");
        }
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CoursViewEtudiant.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();
            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}