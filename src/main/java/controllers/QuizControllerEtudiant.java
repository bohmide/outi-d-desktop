package controllers;

import Services.QuizService;
import entities.Chapitres;
import entities.Cours;
import entities.Quiz;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class QuizControllerEtudiant {

    @FXML
    private Label lblChapitreInfo;

    @FXML
    private TableView<Quiz> tableQuiz;
    @FXML
    private TableColumn<Quiz, String> colNomQuiz;
    @FXML
    private TableColumn<Quiz, Void> colAction;

    @FXML
    private Button btnRetour;

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
        colNomQuiz.setCellValueFactory(new PropertyValueFactory<>("titre"));
        addViewQuestionsButtonToTable();
    }

    private void loadQuiz() {
        if (currentChapitre != null) {
            quizList = FXCollections.observableArrayList(
                    quizService.getAllQuizzes().stream()
                            .filter(q -> q.getChapitre() != null && q.getChapitre().getId() == currentChapitre.getId())
                            .toList()
            );
            tableQuiz.setItems(quizList);
        }
    }

    private void addViewQuestionsButtonToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Voir Questions");

            {
                btn.setOnAction(event -> {
                    Quiz quiz = getTableView().getItems().get(getIndex());
                    navigateToQuestions(quiz);
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

    private void navigateToQuestions(Quiz quiz) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuestionViewEtudiant.fxml"));
            Parent root = loader.load();

           QuestionControllerEtudiant controller = loader.getController();
          controller.initData(quiz, currentChapitre, currentCours);

            Scene scene = tableQuiz.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des questions.");
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
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
