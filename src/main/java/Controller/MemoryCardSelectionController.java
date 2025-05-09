package Controller;

import dao.GamesDAO;
import dao.MemoryCardDAO;
import entities.Games;
import entities.MemoryCard;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.VoiceAssistant;

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
    private Stage currentStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadMemoryCards();
        VoiceAssistant.speak(" You like memory cards ! Choose the game you want");
    }

    private void loadMemoryCards() {
        try {
            List<MemoryCard> memoryCards = memoryCardDAO.getAll();
            memoryCardList.getChildren().clear();

            for (MemoryCard memoryCard : memoryCards) {
                Games game = gameDAO.getById(memoryCard.getGameId());
                Button btn = createMemoryCardButton(game, memoryCard);
                memoryCardList.getChildren().add(btn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Button createMemoryCardButton(Games game, MemoryCard memoryCard) {
        Button button = new Button(game.getName());
        button.getStyleClass().addAll("menu-button", "memory-card-button");
        button.setOnAction(e -> openMemoryCardGame(memoryCard));
        return button;
    }

    private void openMemoryCardGame(MemoryCard memoryCard) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MemoryCardGameView.fxml"));
            Parent root = loader.load();

            MemoryCardGameController controller = loader.getController();
            controller.setMemoryCard(memoryCard);
            controller.setSelectionController(this);

            Stage gameStage = new Stage();
            gameStage.setScene(new Scene(root));
            gameStage.setTitle(memoryCard.getGameName());
            gameStage.show();


            // Conserver la référence au stage actuel
            currentStage = (Stage) memoryCardList.getScene().getWindow();
            currentStage.hide();

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void showSelectionWindow() {
        if (currentStage != null) {
            currentStage.show();
            loadMemoryCards(); // Rafraîchir la liste si nécessaire
        }
    }
    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainGamification_View.fxml"));
            Parent root = loader.load();

            // Charger la scène du menu principal
            Stage stage = (Stage) memoryCardList.getScene().getWindow();
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