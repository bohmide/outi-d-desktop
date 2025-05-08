package Controller;

import entities.Badge;
import entities.QuizKids;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.BadgeService;
import services.EmailService;
import services.FreeAIImageService;
import utils.VoiceAssistant;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class QuizKidsPageController {

    @FXML private VBox questionsContainer;
    private List<QuizKids> quizzes;
    private List<ToggleButton> selectedButtons = new ArrayList<ToggleButton>();

    public void initialize(List<QuizKids> quizzes) {
        this.quizzes = quizzes;
        displayQuestions();
    }

    private void displayQuestions() {
        for (QuizKids quiz : quizzes) {
            VBox questionBox = new VBox(10);
            questionBox.getStyleClass().add("answers-container");

            Label questionLabel = new Label(quiz.getQuestion());
            questionLabel.getStyleClass().add("question-label");
            questionBox.getChildren().add(questionLabel);

            // Utilisez les options nettoyées
            ToggleGroup group = new ToggleGroup();
            for (String option : quiz.getCleanOptions()) {
                ToggleButton optionButton = new ToggleButton(option);
                optionButton.getStyleClass().add("answer-button");
                optionButton.setToggleGroup(group);
                optionButton.setUserData(option);

                optionButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    if(newVal) {
                        optionButton.getStyleClass().add("selected");
                        selectedButtons.add(optionButton);
                    } else {
                        optionButton.getStyleClass().remove("selected");
                        selectedButtons.remove(optionButton);
                    }
                });

                questionBox.getChildren().add(optionButton);
            }
            questionsContainer.getChildren().add(questionBox);
        }
        VoiceAssistant.speak("Now start with answering the questions to win your badge! ");
    }

    @FXML
    private void handleSubmitQuiz() throws SQLException {
        int totalScoree = 0;
        int totalScore = 0;
        int totalQuestions = quizzes.size();
        StringBuilder resultDetails = new StringBuilder();

        for (int i = 0; i < totalQuestions; i++) {
            QuizKids quiz = quizzes.get(i);
            VBox questionBox = (VBox) questionsContainer.getChildren().get(i);
            boolean questionCorrect = false;

            // Réinitialiser tous les styles pour cette question
            for (int j = 1; j < questionBox.getChildren().size(); j++) {
                ToggleButton btn = (ToggleButton) questionBox.getChildren().get(j);
                btn.getStyleClass().removeAll("correct", "incorrect", "selected");
            }

            // Vérifier les réponses
            for (int j = 1; j < questionBox.getChildren().size(); j++) {
                ToggleButton btn = (ToggleButton) questionBox.getChildren().get(j);
                String userAnswer = btn.getText().trim();
                String correctAnswer = quiz.getCorrectAnswer().trim();

                // Marquer la bonne réponse
                if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                    btn.getStyleClass().add("correct");
                }

                // Vérifier la sélection utilisateur
                if (btn.isSelected()) {
                    if (userAnswer.equalsIgnoreCase(correctAnswer)) {
                        totalScore += quiz.getScore();
                        totalScoree++;
                        questionCorrect = true;
                    } else {
                        btn.getStyleClass().add("incorrect");
                    }
                }
            }

            // Ajouter les détails par question
            resultDetails.append("Question ").append(i + 1)
                    .append(" : ").append(questionCorrect ? "✓" : "✗")
                    .append("\n");
        }

        // Afficher le résultat
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Résultats détaillés");
        alert.setHeaderText("Performance finale");
        alert.setContentText(
                "Totales réponses correctes :" + totalScoree+ "/" + totalQuestions + "\n\n"+
                "Score total : " + totalScore  + "\n\n"+
                        "Détails par question :\n" + resultDetails.toString()
        );

        // Personnaliser le style de l'alerte
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
        dialogPane.getStyleClass().add("result-alert");

        alert.showAndWait();
        // Vérification des badges débloqués
        BadgeService badgeService = new BadgeService();
        EmailService emailService = new EmailService();
        List<Badge> unlockedBadges = new ArrayList<>();

        // Récupérer tous les badges
        List<Badge> allBadges = badgeService.getAll();

        // Vérifier les badges débloqués
        for (Badge badge : allBadges) {
            if (totalScore >= badge.getRequiredScore()) {
                unlockedBadges.add(badge);
            }
        }

        // Envoyer les emails pour les badges débloqués
        if (!unlockedBadges.isEmpty()) {
            int finalTotalScore = totalScore;
            new Thread(() -> {
                FreeAIImageService imageService = new FreeAIImageService();


                for (Badge badge : unlockedBadges) {
                    try {
                        String imageUrl = imageService.generateCertificate(
                                badge.getName(),
                                finalTotalScore
                        );

                        String emailContent = buildEmailContent(
                                badge,
                                finalTotalScore

                        );

                        emailService.sendEmailWithImage(
                                "mouhamedaminebenali8@gmail.com",
                                "Certificat de Réussite - " + badge.getName(),
                                emailContent,
                                imageUrl
                        );
                    } catch (Exception e) {
                        System.err.println("Erreur génération certificat: " + e.getMessage());
                    }
                }
            }).start();
        }
    }

    private String buildEmailContent(Badge badge, int score) {
        return String.format(
                "<div style='max-width: 600px; margin: auto; padding: 20px;'>" +
                        "  <h1 style='color: #2c3e50;'>Félicitations ! 🎉</h1>" +
                        "  <p>Vous avez obtenu le badge: <strong>%s</strong></p>" +
                        "  <p>Score: %d/%d</p>" +
                        "  <img src='cid:certificate-image' style='width: 100%%; border: 2px solid #3498db; border-radius: 10px; margin: 20px 0;'>" +
                        "  <p>Conservez précieusement ce certificat !</p>" +
                        "</div>",
                badge.getName(),
                score,
                badge.getRequiredScore()
        );
    }
    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainGamification_View.fxml"));
            Parent root = loader.load();

            // Charger la scène du menu principal
            Stage stage = (Stage) questionsContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

            // Optionnel : Ajouter un message vocal
            VoiceAssistant.speak("Retour au menu principal. Choisissez une nouvelle aventure!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}