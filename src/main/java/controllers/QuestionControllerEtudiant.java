package controllers;

import Services.QuestionService;
import Services.ReponseService;
import entities.Chapitres;
import entities.Cours;
import entities.Question;
import entities.Quiz;
import entities.Reponse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class QuestionControllerEtudiant {

    @FXML
    private Label lblQuizInfo;

    @FXML
    private VBox vboxQuestions;

    @FXML
    private Button btnEnvoyer;

    @FXML
    private Button btnRetour;
    @FXML
    private ScrollPane scrollPane;

    private final QuestionService questionService = new QuestionService();
    private final ReponseService reponseService = new ReponseService();

    private Quiz currentQuiz;
    private Chapitres currentChapitre;
    private Cours currentCours;

    private List<Question> questions; // Liste de toutes les questions

    public void initData(Quiz quiz, Chapitres chapitre, Cours cours) {
        this.currentQuiz = quiz;
        this.currentChapitre = chapitre;
        this.currentCours = cours;

        lblQuizInfo.setText("Quiz : " + quiz.getTitre() +
                " | Chapitre : " + chapitre.getNomChapitre() +
                " | Cours : " + cours.getNom());

        loadQuestions();
    }

    private void loadQuestions() {
        vboxQuestions.getChildren().clear();

        if (currentQuiz != null) {
            questions = questionService.getQuestionsByQuiz(currentQuiz);

            for (Question q : questions) {
                List<Reponse> reps = reponseService.getReponsesByQuestion(q.getId());
                addQuestionWithReponses(q);
            }
        } else {
            System.out.println("Aucun quiz sélectionné!");
        }
    }

    private void addQuestionWithReponses(Question question) {
        Platform.runLater(() -> {
            // Conteneur principal
            VBox questionBox = new VBox(8);
            questionBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

            // Numéro et texte de la question
            HBox questionHeader = new HBox(5);
            Label numberLabel = new Label((vboxQuestions.getChildren().size() + 1) + ".");
            numberLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label questionLabel = new Label(question.getQuestion());
            questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-wrap-text: true;");

            questionHeader.getChildren().addAll(numberLabel, questionLabel);

            // Type de question
            Label typeLabel = new Label("Type: " + question.getType());
            typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

            // Réponses
            VBox answersBox = new VBox(5);
            answersBox.setStyle("-fx-padding: 5 0 0 20;");

            List<Reponse> reponses = reponseService.getReponsesByQuestion(question.getId());

            if (reponses.isEmpty()) {
                answersBox.getChildren().add(new Label("Aucune réponse disponible"));
            } else {
                ToggleGroup group = new ToggleGroup();

                for (Reponse reponse : reponses) {
                    HBox answerLine = new HBox(8);

                    if (question.getType().equalsIgnoreCase("CHOIX_UNIQUE")) {
                        RadioButton rb = new RadioButton(reponse.getReponse());
                        rb.setToggleGroup(group);
                        rb.setUserData(reponse);
                        answerLine.getChildren().add(rb);
                    } else {
                        CheckBox cb = new CheckBox(reponse.getReponse());
                        cb.setUserData(reponse);
                        answerLine.getChildren().add(cb);
                    }

                    answersBox.getChildren().add(answerLine);
                }
            }

            questionBox.getChildren().addAll(questionHeader, typeLabel, answersBox);
            vboxQuestions.getChildren().add(questionBox);
        });
    }
    @FXML
    private void envoyerQuiz(ActionEvent event) {
        int totalQuestions = questions.size();
        int bonnesReponses = 0;

        int index = 0;
        for (var node : vboxQuestions.getChildren()) {
            if (node instanceof VBox box) {
                Question question = questions.get(index);
                boolean correct = true;

                List<Reponse> reponses = reponseService.getReponsesByQuestion(question.getId());

                if (question.getType().equalsIgnoreCase("QCU")) {
                    // Pour QCU, on doit avoir UNE seule réponse sélectionnée, et elle doit être correcte
                    Reponse selected = null;
                    for (var child : box.getChildren()) {
                        if (child instanceof RadioButton radioButton && radioButton.isSelected()) {
                            selected = (Reponse) radioButton.getUserData();
                            break;
                        }
                    }
                    if (selected == null || !selected.isCorrect()) {
                        correct = false;
                    }
                } else { // QCM
                    for (var child : box.getChildren()) {
                        if (child instanceof CheckBox checkBox) {
                            Reponse reponse = (Reponse) checkBox.getUserData();
                            if (checkBox.isSelected() && !reponse.isCorrect()) {
                                correct = false;
                            }
                            if (!checkBox.isSelected() && reponse.isCorrect()) {
                                correct = false;
                            }
                        }
                    }
                }

                if (correct) {
                    bonnesReponses++;
                }
                index++;
            }
        }

        showAlert("Résultat", "Vous avez obtenu " + bonnesReponses + " bonnes réponses sur " + totalQuestions + " !");
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
            showAlert("Erreur", "Impossible de retourner au menu Quiz.");
        }
    }

    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
