package Controller;

import entities.MemoryCard;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import utils.VoiceAssistant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryCardGameController {

    @FXML
    private GridPane memoryCardGrid;

    private MemoryCard memoryCard;
    private List<Card> cards = new ArrayList<>();
    private Card firstSelected;
    private Card secondSelected;
    private boolean processing = false;
    private MemoryCardSelectionController selectionController;

    public void setMemoryCard(MemoryCard memoryCard) {
        this.memoryCard = memoryCard;
        initializeGame();
    }

    public void setSelectionController(MemoryCardSelectionController selectionController) {
        this.selectionController = selectionController;
    }

    private void initializeGame() {
        memoryCardGrid.getChildren().clear();
        cards.clear();

        List<String> imagePaths = memoryCard.getImages();
        List<String> pairedImages = new ArrayList<>();

        for (String path : imagePaths) {
            pairedImages.add(path);
            pairedImages.add(path);
        }
        Collections.shuffle(pairedImages);

        // Configuration originale avec 4 colonnes fixes
        int columns = 4;
        memoryCardGrid.setHgap(10);
        memoryCardGrid.setVgap(10);
        memoryCardGrid.setPadding(new Insets(15));

        for (int i = 0; i < pairedImages.size(); i++) {
            Card card = new Card(pairedImages.get(i));
            cards.add(card);
            memoryCardGrid.add(card.getContainer(), i % columns, i / columns);
        }
        VoiceAssistant.speak("🌟 Good ! Now start with fliping the cards to match each same two cards !");
    }

    private void handleCardClick(Card card) {
        if (processing || card.isMatched() || card.isRevealed()) return;

        card.reveal();

        if (firstSelected == null) {
            firstSelected = card;
        } else {
            secondSelected = card;
            processing = true;
            checkMatch();
        }
    }

    private void checkMatch() {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.8));
        pause.setOnFinished(e -> {
            boolean match = firstSelected != null &&
                    secondSelected != null &&
                    firstSelected.matches(secondSelected);

            if (match) {
                animateSuccess(firstSelected, secondSelected);
                firstSelected.setMatched(true);
                secondSelected.setMatched(true);
            } else {
                animateMismatch(firstSelected);
                animateMismatch(secondSelected);
            }

            resetSelections();
            checkWinCondition();
        });
        pause.play();
    }

    private void animateSuccess(Card card1, Card card2) {
        ScaleTransition st1 = new ScaleTransition(Duration.millis(300), card1.getContainer());
        st1.setFromX(1.0);
        st1.setFromY(1.0);
        st1.setToX(1.1);
        st1.setToY(1.1);
        st1.setAutoReverse(true);
        st1.setCycleCount(2);

        ScaleTransition st2 = new ScaleTransition(Duration.millis(300), card2.getContainer());
        st2.setFromX(1.0);
        st2.setFromY(1.0);
        st2.setToX(1.1);
        st2.setToY(1.1);
        st2.setAutoReverse(true);
        st2.setCycleCount(2);

        st1.play();
        st2.play();
        VoiceAssistant.speak("Good ! well played ");
    }

    private void animateMismatch(Card card) {
        if (card == null) return;

        FadeTransition ft = new FadeTransition(Duration.millis(300), card.getContainer());
        ft.setFromValue(1.0);
        ft.setToValue(0.4);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();

        PauseTransition pt = new PauseTransition(Duration.millis(600));
        pt.setOnFinished(e -> card.hide());
        pt.play();
        VoiceAssistant.speak(" Nice try again!");
    }

    private void resetSelections() {
        firstSelected = null;
        secondSelected = null;
        processing = false;
    }

    private void checkWinCondition() {
        boolean allMatched = cards.stream().allMatch(Card::isMatched);
        if (allMatched) {
            Platform.runLater(this::showCustomVictoryDialog);
        }
    }

    private void showCustomVictoryDialog() {
        Stage dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initOwner(memoryCardGrid.getScene().getWindow());
        VoiceAssistant.speak("Congratulations !");

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: rgba(44, 62, 80, 0.95);"
                + "-fx-border-color: #27ae60;"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 15;"
                + "-fx-background-radius: 15;");

        // Ajout du trophée original
        ImageView confetti = new ImageView();
        try {
            confetti.setImage(new Image(getClass().getResourceAsStream("/images/trophy.png")));
            confetti.setFitWidth(250);
            confetti.setFitHeight(150);
            content.getChildren().add(confetti);
        } catch (Exception e) {
            Label fallbackLabel = new Label("🎉🎊");
            fallbackLabel.setStyle("-fx-font-size: 40px;");
            content.getChildren().add(fallbackLabel);
        }

        Label title = new Label("FÉLICITATIONS !");
        title.setStyle("-fx-text-fill: #2ecc71;"
                + "-fx-font-size: 28px;"
                + "-fx-font-weight: bold;");

        Label message = new Label("Vous avez trouvé toutes les paires !");
        message.setStyle("-fx-text-fill: white;"
                + "-fx-font-size: 18px;");

        Button restartButton = new Button("Rejouer");
        restartButton.setStyle("-fx-background-color: #27ae60;"
                + "-fx-text-fill: white;"
                + "-fx-padding: 10 20;");
        restartButton.setOnAction(e -> {
            dialogStage.close();
            initializeGame();

        });

        Button menuButton = new Button("Menu Principal");
        menuButton.setStyle("-fx-background-color: #8e44ad;"
                + "-fx-text-fill: white;"
                + "-fx-padding: 10 20;");
        menuButton.setOnAction(e -> {
            dialogStage.close();
            ((Stage) memoryCardGrid.getScene().getWindow()).close();
            selectionController.showSelectionWindow();
        });

        HBox buttons = new HBox(20, restartButton, menuButton);
        buttons.setAlignment(Pos.CENTER);

        content.getChildren().addAll(title, message, buttons);

        Scene scene = new Scene(content);
        scene.setFill(Color.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.show();
    }

    private class Card {
        private final String imagePath;
        private final ImageView frontView;
        private final StackPane backView;
        private final StackPane container;
        private boolean revealed;
        private boolean matched;

        public Card(String imagePath) {
            this.imagePath = imagePath;
            this.frontView = new ImageView();
            this.backView = createBackView();
            this.container = new StackPane(backView, frontView);
            initializeCard();
            loadImageAsync();
        }

        private StackPane createBackView() {
            StackPane back = new StackPane();
            back.setStyle("-fx-background-color: #34495e;"
                    + "-fx-border-color: #2c3e50;"
                    + "-fx-border-width: 2;"
                    + "-fx-border-radius: 10;"
                    + "-fx-background-radius: 10;");
            back.setPrefSize(120, 120);
            return back;
        }

        private void initializeCard() {
            frontView.setVisible(false);
            frontView.setFitWidth(120);
            frontView.setFitHeight(120);
            frontView.setPreserveRatio(true);

            container.setPrefSize(120, 120);
            container.setStyle("-fx-cursor: hand;");
            container.setOnMouseClicked(e -> handleCardClick(this));
        }

        private void loadImageAsync() {
            new Thread(() -> {
                try {
                    File file = new File(imagePath);
                    Image image = new Image(file.toURI().toString());
                    Platform.runLater(() -> {
                        frontView.setImage(image);
                        frontView.setVisible(false);
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        Label errorLabel = new Label("!");
                        errorLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
                        container.getChildren().add(errorLabel);
                    });
                }
            }).start();
        }

        public void reveal() {
            frontView.setVisible(true);
            backView.setVisible(false);
            revealed = true;
        }

        public void hide() {
            FadeTransition ft = new FadeTransition(Duration.millis(300), container);
            ft.setFromValue(0.4);
            ft.setToValue(1.0);
            ft.setOnFinished(e -> {
                frontView.setVisible(false);
                backView.setVisible(true);
            });
            ft.play();
            revealed = false;
        }

        public boolean matches(Card other) {
            return this.imagePath.equals(other.imagePath);
        }

        public void showSuccessEffect() {
            container.setStyle("-fx-effect: dropshadow(gaussian, #2ecc71, 20, 0.8, 0, 0);"
                    + "-fx-border-color: #27ae60;"
                    + "-fx-border-width: 3;");
        }

        public StackPane getContainer() { return container; }
        public boolean isRevealed() { return revealed; }
        public boolean isMatched() { return matched; }
        public void setMatched(boolean matched) {
            this.matched = matched;
            container.setStyle("-fx-opacity: 0.7;");
            showSuccessEffect();
        }
    }
}