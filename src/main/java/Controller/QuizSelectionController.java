package Controller;

import entities.QuizKids;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import services.QuizKidsService;
import utils.VoiceAssistant;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QuizSelectionController {

    @FXML private ComboBox<String> genreComboBox;
    @FXML private ComboBox<String> levelComboBox;

    private final QuizKidsService quizKidsService = new QuizKidsService(); // ou injecté différemment selon ton archi

    @FXML
    public void initialize() {

        List<QuizKids> allQuizzes = quizKidsService.getAllQuizzes();

        Set<String> genres = allQuizzes.stream()
                .map(QuizKids::getGenre)
                .collect(Collectors.toSet());

        Set<String> levels = allQuizzes.stream()
                .map(QuizKids::getLevel)
                .collect(Collectors.toSet());

        System.out.println("all quizzes: " +allQuizzes);
        System.out.println("Genres disponibles: " + genres);
        System.out.println("Niveaux disponibles: " + levels);

        genreComboBox.setItems(FXCollections.observableArrayList(genres));
        levelComboBox.setItems(FXCollections.observableArrayList(levels));
        VoiceAssistant.speak(" Welcome to outid Quizzes , please choose the type and the level of the quiz you want to play!");
    }

    @FXML
    private void handleStartQuiz() {
        String selectedGenre = genreComboBox.getValue();
        String selectedLevel = levelComboBox.getValue();

        if (selectedGenre == null || selectedLevel == null) {
            // Afficher une alerte à l'utilisateur
            System.out.println("Merci de sélectionner un genre et un niveau.");
            return;
        }

        // Filtrer les QuizKids en fonction du genre et du niveau sélectionnés
        List<QuizKids> filteredQuizzes = quizKidsService.getAllQuizzes().stream()
                .filter(q -> q.getGenre().equals(selectedGenre) && q.getLevel().equals(selectedLevel))
                .collect(Collectors.toList());

        // Passer à une nouvelle scène avec les quiz filtrés
        if (!filteredQuizzes.isEmpty()) {
            // Exemple : transition vers la page du quiz avec les données filtrées
            showQuizPage(filteredQuizzes);
        } else {
            System.out.println("Aucun quiz trouvé pour ce genre et niveau.");
        }
    }

    private void showQuizPage(List<QuizKids> filteredQuizzes) {
        // Crée un objet QuizPageController qui affichera les quiz filtrés
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/QuizKidsPage.fxml"));
            Parent root = loader.load();

            QuizKidsPageController quizPageController = loader.getController();
            quizPageController.initialize(filteredQuizzes); // Initialiser le contrôleur avec la liste des quiz

            Stage stage = new Stage();
            stage.setTitle("Quiz");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
