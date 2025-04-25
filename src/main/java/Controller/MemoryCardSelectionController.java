package Controller;

import dao.GamesDAO;
import dao.MemoryCardDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import entities.Games;
import entities.MemoryCard;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class MemoryCardSelectionController implements Initializable {

    @FXML
    private VBox memoryCardList;

    private final MemoryCardDAO memoryCardDAO = new MemoryCardDAO();
    private final GamesDAO gameDAO = new GamesDAO();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<MemoryCard> memoryCards = null;
        try {
            memoryCards = memoryCardDAO.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (MemoryCard memoryCard : memoryCards) {
            Games game = null;
            try {
                game = gameDAO.getById(memoryCard.getGameId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Button memoryCardButton = new Button(game.getName());
            memoryCardButton.getStyleClass().add("menu-button");

            Games finalGame = game;
            memoryCardButton.setOnAction(e -> {
                // Appel de la scène de jeu pour cette MemoryCard
                System.out.println("Lancement de la MemoryCard : " + finalGame.getName());
                openMemoryCardGame(memoryCard);
            });

            memoryCardList.getChildren().add(memoryCardButton);
        }
    }

    private void openMemoryCardGame(MemoryCard memoryCard) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MemoryCardGameView.fxml"));
            Parent root = loader.load();

            // IMPORTANT: Initialiser le controller APRÈS le load()
            MemoryCardGameController controller = loader.getController();
            controller.setMemoryCard(memoryCard); // Set before showing

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Jouer à la MemoryCard - " + memoryCard.getGameName());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
