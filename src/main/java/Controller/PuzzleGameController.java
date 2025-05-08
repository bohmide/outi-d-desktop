package Controller;

import entities.Puzzle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
import utils.VoiceAssistant;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

public class PuzzleGameController implements Initializable {

    @FXML
    private GridPane puzzleGrid;

    private Puzzle currentPuzzle;
    private int rows = 4;
    private int cols = 4;
    private ImageView selectedPiece = null;
    private PuzzleSelectionController selectionController;

    public void setPuzzle(Puzzle puzzle) {
        this.currentPuzzle = puzzle;
    }

    public void setSelectionController(PuzzleSelectionController selectionController) {
        this.selectionController = selectionController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation optionnelle
    }

    public void setupPuzzle() {
        List<String> pieces = new ArrayList<>(currentPuzzle.getPieces());
        Collections.shuffle(pieces);

        puzzleGrid.getChildren().clear();
        puzzleGrid.setHgap(2);
        puzzleGrid.setVgap(2);
        puzzleGrid.setPadding(new Insets(10));

        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if(index >= pieces.size()) break;

                String piecePath = pieces.get(index);
                ImageView imageView = createPuzzlePiece(piecePath);
                setupPieceInteractivity(imageView, row, col);
                puzzleGrid.add(imageView, col, row);
                index++;
            }
        }
    }

    private ImageView createPuzzlePiece(String piecePath) {
        Image image = new Image("file:" + piecePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setUserData(piecePath);
        return imageView;
    }

    private void setupPieceInteractivity(ImageView imageView, int row, int col) {
        imageView.setOnMouseClicked(event -> handlePieceClick(imageView));
        imageView.setStyle("-fx-cursor: hand; -fx-border-color: #eeeeee;");
    }

    private void handlePieceClick(ImageView clickedPiece) {
        if (selectedPiece == null) {
            selectPiece(clickedPiece);
        } else {
            swapPieces(selectedPiece, clickedPiece);
            unselectPiece();
        }
    }

    private void selectPiece(ImageView piece) {
        selectedPiece = piece;
        piece.setStyle("-fx-border-color: #3498db; -fx-border-width: 3;");
    }

    private void unselectPiece() {
        if(selectedPiece != null) {
            selectedPiece.setStyle("-fx-border-color: #eeeeee;");
            selectedPiece = null;
        }
    }

    private void swapPieces(ImageView piece1, ImageView piece2) {
        int row1 = GridPane.getRowIndex(piece1);
        int col1 = GridPane.getColumnIndex(piece1);
        int row2 = GridPane.getRowIndex(piece2);
        int col2 = GridPane.getColumnIndex(piece2);

        puzzleGrid.getChildren().removeAll(piece1, piece2);
        puzzleGrid.add(piece1, col2, row2);
        puzzleGrid.add(piece2, col1, row1);
    }

    @FXML
    private void handleCheckSolution() {
        if(isPuzzleSolved()) {
            Platform.runLater(this::showVictoryDialog);
        } else {
            highlightIncorrectPieces();
            showAlert("Solution incorrecte", "Continue à essayer !");
        }
    }

    private boolean isPuzzleSolved() {
        for(int i = 0; i < currentPuzzle.getPieces().size(); i++) {
            ImageView piece = getPieceAtPosition(i/cols, i%cols);
            if(piece == null || !piece.getUserData().equals(currentPuzzle.getPieces().get(i))) {
                return false;
            }
        }
        return true;
    }

    private void highlightIncorrectPieces() {
        for(int i = 0; i < currentPuzzle.getPieces().size(); i++) {
            ImageView piece = getPieceAtPosition(i/cols, i%cols);
            if(piece != null && !piece.getUserData().equals(currentPuzzle.getPieces().get(i))) {
                piece.setStyle("-fx-border-color: #e74c3c;");
            }
        }
    }

    private ImageView getPieceAtPosition(int row, int col) {
        for(javafx.scene.Node node : puzzleGrid.getChildren()) {
            if(GridPane.getRowIndex(node) == row &&
                    GridPane.getColumnIndex(node) == col) {
                return (ImageView) node;
            }
        }
        return null;
    }

    private void showVictoryDialog() {
        Stage dialog = new Stage();
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.initOwner(puzzleGrid.getScene().getWindow());

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(25));
        content.setStyle("-fx-background-color: rgba(44, 62, 80, 0.95);"
                + "-fx-border-color: #27ae60;"
                + "-fx-border-width: 3;"
                + "-fx-border-radius: 15;");

        // Image de victoire
        ImageView trophy = new ImageView(new Image(getClass().getResourceAsStream("/images/trophy.png")));
        trophy.setFitWidth(200);
        trophy.setFitHeight(120);

        Label title = new Label("PUZZLE RÉUSSI !");
        title.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox buttons = createDialogButtons(dialog);

        content.getChildren().addAll(trophy, title, buttons);

        Scene scene = new Scene(content);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
        dialog.setScene(scene);
        dialog.show();
        VoiceAssistant.speak("Congratulation , you doing well with this game!");
    }

    private HBox createDialogButtons(Stage dialog) {
        Button replayBtn = new Button("Rejouer");
        replayBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10 20;");
        replayBtn.setOnAction(e -> {
            dialog.close();
            setupPuzzle();
        });

        Button menuBtn = new Button("Menu Principal");
        menuBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-padding: 10 20;");
        menuBtn.setOnAction(e -> {
            dialog.close();
            ((Stage) puzzleGrid.getScene().getWindow()).close();
            if(selectionController != null) {
                selectionController.showSelectionWindow();
            }
            VoiceAssistant.speak("🌟 You want to play another one ?");
        });

        return new HBox(20, replayBtn, menuBtn);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainGamification_View.fxml"));
            Parent root = loader.load();

            // Charger la scène du menu principal
            Stage stage = (Stage) puzzleGrid.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
            stage.setScene(scene);
            stage.show();


            VoiceAssistant.speak("Retour au menu principal. Choisissez une nouvelle aventure!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}