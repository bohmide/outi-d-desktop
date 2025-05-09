package Controller;

import dao.GamesDAO;
import dao.PuzzleDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import entities.Games;
import entities.Puzzle;
import javafx.stage.Stage;
import utils.VoiceAssistant;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class PuzzleSelectionController implements Initializable {

    @FXML
    private VBox puzzleList;


    private final PuzzleDAO puzzleDAO = new PuzzleDAO();
    private final GamesDAO gameDAO = new GamesDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Puzzle> puzzles = null;
        try {
            puzzles = puzzleDAO.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        VoiceAssistant.speak(" Are you ready to enjoy our puzzle games! Please choose the game you want!");

        for (Puzzle puzzle : puzzles) {
            Games game = null;
            try {
                game = gameDAO.getById(puzzle.getGameId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Button puzzleButton = new Button(game.getName());
            puzzleButton.getStyleClass().add("menu-button");

            Games finalGame = game;
            puzzleButton.setOnAction(e -> {
                // Appel de la scène de jeu pour ce puzzle
                // Par exemple : openPuzzleGame(puzzle);
                System.out.println("Lancement du puzzle : " + finalGame.getName());
            });

            puzzleList.getChildren().add(puzzleButton);
            puzzleButton.setOnAction(e -> openPuzzleGame(puzzle));

        }
    }
    // Modifiez la méthode openPuzzleGame
    private void openPuzzleGame(Puzzle puzzle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PuzzleGameView.fxml"));
            Parent root = loader.load();

            PuzzleGameController controller = loader.getController();
            controller.setPuzzle(puzzle);
            controller.setSelectionController(this); // Ajout important
            controller.setupPuzzle();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Jouer au puzzle");
            stage.show();
            VoiceAssistant.speak(" Good choice , Now start with moving the cards to complete the full image ");

            // Cache la fenêtre de sélection
            ((Stage) puzzleList.getScene().getWindow()).hide();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ajoutez cette méthode
    public void showSelectionWindow() {
        Stage stage = (Stage) puzzleList.getScene().getWindow();
        stage.show();
    }

    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainGamification_View.fxml"));
            Parent root = loader.load();

            // Charger la scène du menu principal
            Stage stage = (Stage) puzzleList.getScene().getWindow();
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
