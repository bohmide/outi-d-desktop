package controllers;

import services.CertificationService;
import Services.QuestionService;
import Services.ReponseService;
import entities.Chapitres;
import entities.Cours;
import entities.Question;
import entities.Quiz;
import entities.Reponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class QuestionControllerEtudiant {

    @FXML
    private Label lblQuizInfo;

    @FXML
    private VBox vboxQuestions;

    @FXML
    private Button btnSubmit;

    @FXML
    private Button btnRetour;

    @FXML
    private ScrollPane scrollPane;

    private final QuestionService questionService = new QuestionService();
    private final ReponseService reponseService = new ReponseService();

    private Quiz currentQuiz;
    private Chapitres currentChapter;
    private Cours currentCourse;
    private List<Question> questions;
    private ResourceBundle bundle;

    public void initData(Quiz quiz, Chapitres chapter, Cours course, ResourceBundle bundle) {
        this.currentQuiz = quiz;
        this.currentChapter = chapter;
        this.currentCourse = course;
        this.bundle = bundle;

        lblQuizInfo.setText(bundle.getString("quiz") + ": " + quiz.getTitre() +
                " | " + bundle.getString("chapter") + ": " + chapter.getNomChapitre() +
                " | " + bundle.getString("course") + ": " + course.getNom());

        loadQuestions();
    }

    private void loadQuestions() {
        vboxQuestions.getChildren().clear();

        if (currentQuiz != null) {
            questions = questionService.getQuestionsByQuiz(currentQuiz);
            for (Question q : questions) {
                addQuestionWithReponses(q);
            }
        } else {
            System.out.println("No quiz selected!");
        }
    }

    private void addQuestionWithReponses(Question question) {
        Platform.runLater(() -> {
            VBox questionBox = new VBox(8);
            questionBox.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 5;");

            HBox questionHeader = new HBox(5);
            Label numberLabel = new Label((vboxQuestions.getChildren().size() + 1) + ".");
            numberLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label questionLabel = new Label(question.getQuestion());
            questionLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-wrap-text: true;");

            questionHeader.getChildren().addAll(numberLabel, questionLabel);

            Label typeLabel = new Label(bundle.getString("type") + ": " + question.getType());
            typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

            VBox answersBox = new VBox(5);
            answersBox.setStyle("-fx-padding: 5 0 0 20;");

            List<Reponse> reponses = reponseService.getReponsesByQuestion(question.getId());

            if (reponses.isEmpty()) {
                answersBox.getChildren().add(new Label(bundle.getString("no_answer")));
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
        int correctAnswers = 0;
        int index = 0;

        for (var node : vboxQuestions.getChildren()) {
            if (node instanceof VBox box) {
                Question question = questions.get(index);
                boolean correct = true;
                List<Reponse> reponses = reponseService.getReponsesByQuestion(question.getId());

                if (box.getChildren().size() >= 3) {
                    VBox answersBox = (VBox) box.getChildren().get(2);

                    if (question.getType().equalsIgnoreCase("CHOIX_UNIQUE")) {
                        Reponse selected = null;
                        for (var child : answersBox.getChildren()) {
                            if (child instanceof HBox hbox) {
                                for (var element : hbox.getChildren()) {
                                    if (element instanceof RadioButton rb && rb.isSelected()) {
                                        selected = (Reponse) rb.getUserData();
                                    }
                                }
                            }
                        }
                        if (selected == null || !selected.isCorrect()) correct = false;
                    } else {
                        for (var child : answersBox.getChildren()) {
                            if (child instanceof HBox hbox) {
                                for (var element : hbox.getChildren()) {
                                    if (element instanceof CheckBox cb) {
                                        Reponse r = (Reponse) cb.getUserData();
                                        if (cb.isSelected() && !r.isCorrect()) correct = false;
                                        if (!cb.isSelected() && r.isCorrect()) correct = false;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    correct = false;
                }

                if (correct) correctAnswers++;
                index++;
            }
        }

        showAlert(bundle.getString("result"), bundle.getString("score") + ": " + correctAnswers + "/" + totalQuestions);

        if (correctAnswers == totalQuestions) {
            try {
                CertificationService.genererEtEnvoyerCertification(
                        "Mejri Takwa", "mejria742@gmail.com",
                        currentCourse.getNom(), currentCourse.getId(),
                        correctAnswers, totalQuestions
                );
                showAlert(bundle.getString("congrats"), bundle.getString("cert_sent"));
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(bundle.getString("error"), bundle.getString("cert_error"));
            }
        } else {
            showAlert(bundle.getString("try_again_title"), bundle.getString("try_again_msg"));
        }
    }

    @FXML
    private void retourAuxQuiz() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuizViewEtudiant.fxml"));
            loader.setResources(bundle);
            Parent root = loader.load();
            QuizControllerEtudiant controller = loader.getController();
            controller.initData(currentChapter, currentCourse, bundle);
            btnRetour.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(bundle.getString("error"), bundle.getString("back_error"));
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
