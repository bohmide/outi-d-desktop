package Controller;

import dao.GamesDAO;
import dao.PuzzleDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import entities.Games;
import entities.Puzzle;
import javafx.stage.Stage;

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
    private void openPuzzleGame(Puzzle puzzle) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PuzzleGameView.fxml"));
            Parent root = loader.load();

            PuzzleGameController controller = loader.getController();
            controller.setPuzzle(puzzle);
            controller.setupPuzzle();

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Jouer au puzzle");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
