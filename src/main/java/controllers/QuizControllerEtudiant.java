package controllers;

import Services.QuizService;
import entities.Chapitres;
import entities.Cours;
import entities.Quiz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class QuizControllerEtudiant {

    @FXML
    private Label lblChapitreInfo;
    @FXML
    private Button btnRetour;
    @FXML
    private VBox vboxQuiz;
    @FXML
    private Label lblNoQuizMessage;
    @FXML
    private ComboBox<String> languageCombo;

    private final QuizService quizService = new QuizService();
    private ObservableList<Quiz> quizList;
    private Chapitres currentChapitre;
    private Cours currentCours;
    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        lblNoQuizMessage.setVisible(false);
        lblNoQuizMessage.setTextFill(Color.GRAY);

        // Initialisation des langues
        languageCombo.getItems().addAll("Français", "English");
        languageCombo.setValue("Français"); // Valeur par défaut
        languageCombo.setOnAction(e -> changeLanguage());
    }

    public void initData(Chapitres chapitre, Cours cours, ResourceBundle bundle) {
        this.currentChapitre = chapitre;
        this.currentCours = cours;
        this.bundle = bundle;
        refreshUI();
        loadQuiz();
    }

    private void refreshUI() {
        lblChapitreInfo.setText(bundle.getString("quiz.available") + " - " + currentChapitre.getNomChapitre());
        btnRetour.setText(bundle.getString("back.button"));
        updateQuizDisplay(); // Recharge les éléments avec les bons textes
    }

    private void changeLanguage() {
        Locale locale = languageCombo.getValue().equals("Français") ? Locale.FRENCH : Locale.ENGLISH;
        this.bundle = ResourceBundle.getBundle("lang/messages", locale);
        refreshUI();
    }

    private void loadQuiz() {
        try {
            if (currentChapitre != null) {
                List<Quiz> quizzes = quizService.getAllQuizzes().stream()
                        .filter(q -> q.getChapitre() != null && q.getChapitre().getId() == currentChapitre.getId())
                        .toList();

                quizList = FXCollections.observableArrayList(quizzes);
                updateQuizDisplay();
            }
        } catch (SQLException e) {
            showErrorAlert(bundle.getString("error.load.quizzes"), e);
        }
    }

    private void updateQuizDisplay() {
        vboxQuiz.getChildren().clear();

        if (quizList == null || quizList.isEmpty()) {
            lblNoQuizMessage.setText(bundle.getString("quiz.none.available"));
            lblNoQuizMessage.setVisible(true);
            return;
        }

        lblNoQuizMessage.setVisible(false);
        afficherQuiz(quizList);
    }

    private void afficherQuiz(List<Quiz> quizzes) {
        for (Quiz quiz : quizzes) {
            VBox card = createQuizCard(quiz);
            vboxQuiz.getChildren().add(card);
        }
    }

    private VBox createQuizCard(Quiz quiz) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);");

        Label titreQuiz = new Label(quiz.getTitre());
        titreQuiz.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label scoreLabel = new Label(); // Tu peux plus tard afficher un score si besoin
        scoreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Button btnVoirQuiz = new Button(bundle.getString("quiz.start"));
        btnVoirQuiz.setStyle("-fx-background-color: #4a6baf; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 6; " +
                "-fx-cursor: hand; " +
                "-fx-padding: 6 14;");
        btnVoirQuiz.setOnAction(event -> navigateToQuestions(quiz));

        card.getChildren().addAll(titreQuiz, scoreLabel, btnVoirQuiz);
        return card;
    }

    private void navigateToQuestions(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuestionViewEtudiant.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            QuestionControllerEtudiant controller = loader.getController();
            controller.initData(quiz, currentChapitre, currentCours,bundle);

            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showErrorAlert(bundle.getString("error.load.questions"), e);
        }
    }

    @FXML
    private void retourAuxChapitres() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChapitreEtudiantView.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();

            ChapitreControllerEtudiant controller = loader.getController();
            controller.initData(currentCours, bundle);

            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showErrorAlert(bundle.getString("error.back.chapters"), e);
        }
    }

    private void showErrorAlert(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(bundle.getString("error.title"));
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
}
