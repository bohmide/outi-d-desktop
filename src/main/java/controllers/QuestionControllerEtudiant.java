package controllers;

import Services.QuestionService;
import entities.Chapitres;
import entities.Cours;
import entities.Question;
import entities.Quiz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class QuestionControllerEtudiant {

    @FXML
    private Label lblQuizInfo;

    @FXML
    private TableView<Question> tableQuestions;
    @FXML
    private TableColumn<Question, String> colQuestion;
    @FXML
    private TableColumn<Question, String> colType;
    @FXML
    private TableColumn<Question, Void> colActionReponses;

    @FXML
    private Button btnRetour;

    private final QuestionService questionService = new QuestionService();
    private ObservableList<Question> questionList;
    private Quiz currentQuiz;
    private Chapitres currentChapitre;
    private Cours currentCours;

    public void initData(Quiz quiz, Chapitres chapitre, Cours cours) {
        this.currentQuiz = quiz;
        this.currentChapitre = chapitre;
        this.currentCours = cours;
        lblQuizInfo.setText("Quiz - Score: " + quiz.getScore() +
                " | Chapitre: " + chapitre.getNomChapitre() +
                " | Cours: " + cours.getNom());
        loadQuestions();
    }

    @FXML
    public void initialize() {
        colQuestion.setCellValueFactory(new PropertyValueFactory<>("question"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        addReponsesButtonToTable();
    }

    private void loadQuestions() {
        if (currentQuiz != null) {
            questionList = FXCollections.observableArrayList(
                    questionService.getQuestionsByQuiz(currentQuiz)
            );
            tableQuestions.setItems(questionList);
        }
    }

    private void addReponsesButtonToTable() {
        colActionReponses.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Voir Réponses");

            {
                btn.setOnAction(event -> {
                    Question question = getTableView().getItems().get(getIndex());
                    navigateToReponses(question);
                });
                btn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void navigateToReponses(Question question) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReponseViewEtudiant.fxml"));
            Parent root = loader.load();

            ReponseControllerEtudiant controller = loader.getController();
            controller.initData(question, currentQuiz, currentChapitre, currentCours);

            Scene scene = tableQuestions.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des réponses.");
        }
    }

    @FXML
    private void retourAuxQuiz() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuizViewEtudiant.fxml"));
            Parent root = loader.load();

            QuizControllerEtudiant controller = loader.getController();
            controller.initData(currentChapitre, currentCours);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
