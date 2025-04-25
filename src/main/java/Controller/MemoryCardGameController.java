package Controller;

import entities.MemoryCard;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;

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

    public void setMemoryCard(MemoryCard memoryCard) {
        this.memoryCard = memoryCard;
        initializeGame();
    }

    private void initializeGame() {
        memoryCardGrid.getChildren().clear();
        cards.clear();

        List<String> imagePaths = memoryCard.getImages();
        List<String> pairedImages = new ArrayList<>();

        // Création des paires
        for (String path : imagePaths) {
            pairedImages.add(path);
            pairedImages.add(path);
        }
        Collections.shuffle(pairedImages);

        // Configuration de la grille
        int columns = 4;
        memoryCardGrid.setHgap(15);
        memoryCardGrid.setVgap(15);
        memoryCardGrid.setPadding(new Insets(20));

        // Ajout des cartes
        for (int i = 0; i < pairedImages.size(); i++) {
            Card card = new Card(pairedImages.get(i));
            cards.add(card);
            memoryCardGrid.add(card.getContainer(), i % columns, i / columns);
        }
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
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(e -> {
            boolean match = firstSelected.matches(secondSelected);

            if (match) {
                firstSelected.setMatched(true);
                secondSelected.setMatched(true);
            } else {
                firstSelected.hide();
                secondSelected.hide();
            }

            resetSelections();
            Platform.runLater(this::checkWinCondition);
        });

        pause.play();
    }

    private void resetSelections() {
        firstSelected = null;
        secondSelected = null;
        processing = false;
    }

    private void checkWinCondition() {
        if (cards.stream().allMatch(Card::isMatched)) {
            showWinAlert();
        }
    }

    private void showWinAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Félicitations !");
        alert.setHeaderText(null);
        alert.setContentText("Vous avez trouvé toutes les paires !");

        // Affiche l'alerte de manière sécurisée
        if (Platform.isFxApplicationThread()) {
            alert.showAndWait();
        } else {
            Platform.runLater(alert::showAndWait);
        }
    }

    // Classe interne pour la gestion des cartes
    private class Card {
        private final String imagePath;
        private final ImageView frontView;
        private final StackPane backView;
        private final StackPane container;
        private boolean revealed;
        private boolean matched;

        public Card(String imagePath) {
            this.imagePath = imagePath;
            this.frontView = createImageView(imagePath);
            this.backView = createBlackBackView();
            this.container = new StackPane(backView, frontView);

            setupCardAppearance();
            setupClickHandler();
        }

        private StackPane createBlackBackView() {
            StackPane back = new StackPane();
            back.setStyle("-fx-background-color: #000000; " +
                    "-fx-border-color: #444444; " +
                    "-fx-border-width: 3; " +
                    "-fx-border-radius: 5;");
            back.setPrefSize(130, 130);
            return back;
        }

        private ImageView createImageView(String path) {
            try {
                File imageFile = new File(path);
                Image image = new Image(imageFile.toURI().toString());
                return createImageCard(image);
            } catch (Exception e) {
                System.err.println("Erreur de chargement de l'image: " + path);
                return createErrorImageView();
            }
        }

        private ImageView createImageCard(Image image) {
            ImageView iv = new ImageView(image);
            iv.setFitWidth(120);
            iv.setFitHeight(120);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setCache(true);
            return iv;
        }

        private ImageView createErrorImageView() {
            ImageView iv = new ImageView();
            iv.setFitWidth(120);
            iv.setFitHeight(120);
            iv.setStyle("-fx-background-color: #8B0000;");
            return iv;
        }

        private void setupCardAppearance() {
            container.getStyleClass().add("memory-card");
            container.setPrefSize(130, 130);
            frontView.setVisible(false);

            // Effet hover
            container.setOnMouseEntered(e -> {
                if (!revealed && !matched) {
                    container.setStyle("-fx-effect: dropshadow(gaussian, #FFFFFF, 15, 0.5, 0, 0);");
                }
            });

            container.setOnMouseExited(e -> {
                if (!revealed && !matched) {
                    container.setStyle("-fx-effect: null;");
                }
            });
        }

        private void setupClickHandler() {
            container.setOnMouseClicked(e -> handleCardClick(this));
        }

        public void reveal() {
            frontView.setVisible(true);
            backView.setVisible(false);
            revealed = true;
        }

        public void hide() {
            frontView.setVisible(false);
            backView.setVisible(true);
            revealed = false;
        }

        public boolean matches(Card other) {
            return this.imagePath.equals(other.imagePath);
        }

        // Getters
        public StackPane getContainer() { return container; }
        public boolean isRevealed() { return revealed; }
        public boolean isMatched() { return matched; }
        public void setMatched(boolean matched) { this.matched = matched; }
    }
}