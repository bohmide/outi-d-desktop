package controllers;

import Services.ReponseService;
import entities.Chapitres;
import entities.Cours;
import entities.Question;
import entities.Quiz;
import entities.Reponse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class ReponseControllerEtudiant {

    @FXML
    private Label lblQuestionInfo;

    @FXML
    private TableView<Reponse> tableReponses;
    @FXML
    private TableColumn<Reponse, String> colReponse;
    @FXML
    private TableColumn<Reponse, Boolean> colCorrect;

    @FXML
    private Button btnRetour;

    private final ReponseService reponseService = new ReponseService();
    private Question currentQuestion;
    private Quiz currentQuiz;
    private Chapitres currentChapitre;
    private Cours currentCours;

    public void initData(Question question, Quiz quiz, Chapitres chapitre, Cours cours) {
        this.currentQuestion = question;
        this.currentQuiz = quiz;
        this.currentChapitre = chapitre;
        this.currentCours = cours;

        lblQuestionInfo.setText("Réponses pour : " + question.getQuestion());
        loadReponses();
    }

    @FXML
    public void initialize() {
        colReponse.setCellValueFactory(new PropertyValueFactory<>("reponse"));
        colCorrect.setCellValueFactory(new PropertyValueFactory<>("correct"));
    }

    private void loadReponses() {
        if (currentQuestion != null) {
            ObservableList<Reponse> reponseList = FXCollections.observableArrayList(
                    reponseService.getReponsesByQuestion(currentQuestion.getId())
            );
            tableReponses.setItems(reponseList);
        }
    }

    @FXML
    private void retourAuxQuestions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuestionViewEtudiant.fxml"));
            Parent root = loader.load();

            QuestionControllerEtudiant controller = loader.getController();
            controller.initData(currentQuiz, currentChapitre, currentCours);

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
