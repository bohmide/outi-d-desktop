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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class QuizControllerEtudiant {

    @FXML
    private Label lblChapitreInfo;
    @FXML
    private Button btnRetour;
    @FXML
    private VBox vboxQuiz;
    @FXML
    private Label lblNoQuizMessage;

    private final QuizService quizService = new QuizService();
    private ObservableList<Quiz> quizList;
    private Chapitres currentChapitre;
    private Cours currentCours;

    public void initData(Chapitres chapitre, Cours cours) {
        this.currentChapitre = chapitre;
        this.currentCours = cours;
        lblChapitreInfo.setText("Quiz disponibles - Chapitre : " + chapitre.getNomChapitre());
        loadQuiz();
    }

    @FXML
    public void initialize() {
        lblNoQuizMessage.setVisible(false);
        lblNoQuizMessage.setTextFill(Color.GRAY);
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
            showErrorAlert("Erreur lors du chargement des quizzes", e);
        }
    }

    private void updateQuizDisplay() {
        vboxQuiz.getChildren().clear();

        if (quizList.isEmpty()) {
            lblNoQuizMessage.setText("Aucun quiz disponible pour ce chapitre.");
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

        Label scoreLabel = new Label();
        scoreLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Button btnVoirQuiz = new Button("Lancer Quiz");
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
            Parent root = loader.load();

            QuestionControllerEtudiant controller = loader.getController();
            controller.initData(quiz, currentChapitre, currentCours);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Erreur lors du chargement des questions", e);
        }
    }

    @FXML
    private void retourAuxChapitres() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChapitreEtudiantView.fxml"));
            Parent root = loader.load();

            ChapitreControllerEtudiant controller = loader.getController();
            controller.initData(currentCours);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showErrorAlert("Erreur lors du retour aux chapitres", e);
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
}