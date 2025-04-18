package Controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dao.GamesDAO;
import dao.PuzzleDAO;
import entities.Games;
import entities.Puzzle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PuzzleController {
    @FXML private TableView<Puzzle> puzzleTable;
    @FXML private TableColumn<Puzzle, Integer> idColumn;
    @FXML private TableColumn<Puzzle, String> gameColumn;
    @FXML private TableColumn<Puzzle, String> finalImageColumn;
    @FXML private TableColumn<Puzzle, String> piecesColumn;
    @FXML private TableColumn<Puzzle, Void> actionColumn;


    @FXML private ComboBox<Games> gameComboBox;
    @FXML private TextField finalImageField;
    @FXML private ListView<String> piecesListView;

    private final PuzzleDAO puzzleDAO = new PuzzleDAO();
    private final GamesDAO gamesDAO = new GamesDAO();
    private final Gson gson = new Gson();
    private int selectedId = -1;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());
        gameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getGameName()));

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox pane = new HBox(10, editButton, deleteButton);

            {
                editButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                editButton.setOnAction(event -> {
                    Puzzle puzzle = getTableView().getItems().get(getIndex());
                    selectedId = puzzle.getId();
                    gameComboBox.getSelectionModel().select(new Games(puzzle.getGameId(), puzzle.getGameName()));
                    finalImageField.setText(puzzle.getFinalImage());
                    piecesListView.setItems(FXCollections.observableArrayList(puzzle.getPieces()));
                });

                deleteButton.setOnAction(event -> {
                    Puzzle puzzle = getTableView().getItems().get(getIndex());
                    try {
                        puzzleDAO.delete(puzzle.getId());
                        refreshTable();
                        clearFields();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(pane);
                }
            }
        });


        // Final Image Column avec imageView
        finalImageColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getFinalImage()));
        finalImageColumn.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null) {
                    setGraphic(null);
                } else {
                    try {
                        imageView.setImage(new Image(new FileInputStream(path)));
                        setGraphic(imageView);
                    } catch (FileNotFoundException e) {
                        setGraphic(null);
                    }
                }
            }
        });

        piecesColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(gson.toJson(cell.getValue().getPieces())));
        piecesColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String json, boolean empty) {
                super.updateItem(json, empty);
                if (empty || json == null) {
                    setGraphic(null);
                } else {
                    try {
                        Type listType = new TypeToken<List<String>>() {}.getType();
                        List<String> images = gson.fromJson(json, listType);
                        HBox hbox = new HBox(5); // espace entre les images
                        for (String path : images) {
                            try {
                                ImageView imageView = new ImageView(new Image(new FileInputStream(path)));
                                imageView.setFitWidth(40);
                                imageView.setFitHeight(40);
                                imageView.setPreserveRatio(true);
                                hbox.getChildren().add(imageView);
                            } catch (FileNotFoundException e) {
                                // Optionnel : ajouter une icône de remplacement ici
                            }
                        }
                        setGraphic(hbox);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });


        try {
            refreshTable();
            loadGames();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        puzzleTable.setOnMouseClicked(event -> {
            Puzzle selected = puzzleTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selectedId = selected.getId();
                gameComboBox.getSelectionModel().select(new Games(selected.getGameId(), selected.getGameName()));
                finalImageField.setText(selected.getFinalImage());
                piecesListView.setItems(FXCollections.observableArrayList(selected.getPieces()));
            }
        });
    }

    private void loadGames() throws SQLException {
        List<Games> gamesList = gamesDAO.getAll();
        gameComboBox.setItems(FXCollections.observableArrayList(gamesList));
    }

    private void refreshTable() throws SQLException {
        List<Puzzle> puzzleList = puzzleDAO.getAll();
        puzzleTable.setItems(FXCollections.observableArrayList(puzzleList));
    }

    @FXML
    void handleAddPuzzle(ActionEvent event) {
        try {
            Games selectedGame = gameComboBox.getValue();
            String finalImg = finalImageField.getText();
            List<String> pieces = new ArrayList<>(piecesListView.getItems());

            if (selectedGame == null || finalImg.isEmpty() || pieces.isEmpty()) {
                showAlert("Champs requis", "Tous les champs doivent être remplis.");
                return;
            }

            Puzzle puzzle = new Puzzle(0, selectedGame.getId(), finalImg, pieces);
            puzzleDAO.add(puzzle);
            refreshTable();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleUpdatePuzzle(ActionEvent event) {
        if (selectedId == -1) return;

        try {
            Games selectedGame = gameComboBox.getValue();
            String finalImg = finalImageField.getText();
            List<String> pieces = new ArrayList<>(piecesListView.getItems());

            Puzzle puzzle = new Puzzle(selectedId, selectedGame.getId(), finalImg, pieces);
            puzzleDAO.update(puzzle);
            refreshTable();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        if (selectedId == -1) return;
        try {
            puzzleDAO.delete(selectedId);
            refreshTable();
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSelectFinalImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            finalImageField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void handleAddPieceImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null) {
            for (File f : files) {
                piecesListView.getItems().add(f.getAbsolutePath());
            }
        }
    }

    private void clearFields() {
        selectedId = -1;
        gameComboBox.getSelectionModel().clearSelection();
        finalImageField.clear();
        piecesListView.getItems().clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
