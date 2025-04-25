package Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import entities.Puzzle;

import java.io.File;
import java.net.URL;
import java.util.*;

public class PuzzleGameController implements Initializable {

    @FXML
    private GridPane puzzleGrid;

    private Puzzle currentPuzzle;
    private int rows = 4;
    private int cols = 4;
    private ImageView selectedPiece = null;

    public void setPuzzle(Puzzle puzzle) {
        this.currentPuzzle = puzzle;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisation optionnelle si nécessaire
    }

    public void setupPuzzle() {
        List<String> pieces = new ArrayList<>(currentPuzzle.getPieces());
        Collections.shuffle(pieces);

        puzzleGrid.getChildren().clear();

        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                String piecePath = pieces.get(index);
                Image image = new Image(new File(piecePath).toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                imageView.setPreserveRatio(true);
                imageView.setUserData(piecePath); // Stocker le chemin dans userData

                enableSelection(imageView, row, col);
                puzzleGrid.add(imageView, col, row);
                index++;
            }
        }
    }

    private void enableSelection(ImageView imageView, int row, int col) {
        imageView.setOnMouseClicked(event -> {
            if (selectedPiece == null) {
                selectedPiece = imageView;
                selectedPiece.setStyle("-fx-border-color: blue; -fx-border-width: 3;");
            } else {
                swapPieces(selectedPiece, imageView);
                selectedPiece.setStyle("");
                selectedPiece = null;
            }
        });
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
        boolean success = true;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                ImageView imageView = getImageViewAt(row, col);
                if (imageView == null) continue;

                String currentPath = (String) imageView.getUserData();
                String correctPath = currentPuzzle.getPieces().get(row * cols + col);

                if (!currentPath.equals(correctPath)) {
                    imageView.setStyle("-fx-border-color: red; -fx-border-width: 3;");
                    success = false;
                } else {
                    imageView.setStyle("");
                }
            }
        }

        if (success) {
            showAlert("Bravo !", "Tu as réussi le puzzle !");
        } else {
            showAlert("Oups !", "Certaines pièces ne sont pas au bon endroit. Essaie encore !");
        }
    }

    private ImageView getImageViewAt(int row, int col) {
        for (javafx.scene.Node node : puzzleGrid.getChildren()) {
            if (node instanceof ImageView imageView) {
                Integer r = GridPane.getRowIndex(node);
                Integer c = GridPane.getColumnIndex(node);
                if (r == null) r = 0;
                if (c == null) c = 0;
                if (r == row && c == col) {
                    return imageView;
                }
            }
        }
        return null;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}