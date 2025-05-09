package Controller;

import entities.Games;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import services.GamesService;

import java.sql.SQLException;

public class GamesController {

    @FXML
    private TableView<Games> gamesTable;
    @FXML
    private TableColumn<Games, Integer> idColumn;
    @FXML
    private TableColumn<Games, String> nameColumn;
    @FXML
    private TableColumn<Games, Void> actionColumn;

    @FXML
    private TextField nameField;

    private final GamesService gamesService = new GamesService();
    private final ObservableList<Games> gamesList = FXCollections.observableArrayList();

    private Games selectedGame = null;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        loadGames();
        setupActions();
    }

    private void loadGames() {
        try {
            gamesList.setAll(gamesService.getAll());
            gamesTable.setItems(gamesList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setupActions() {
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button btnDelete = new Button("Supprimer");
            private final Button btnEdit = new Button("Modifier");
            private final HBox pane = new HBox(5, btnDelete, btnEdit);

            {
                btnDelete.setOnAction(event -> {
                    Games g = getTableView().getItems().get(getIndex());
                    try {
                        gamesService.delete(g.getId());
                        loadGames();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                btnEdit.setOnAction(event -> {
                    selectedGame = getTableView().getItems().get(getIndex());
                    nameField.setText(selectedGame.getName());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    @FXML
    private void handleAddGame() {
        String name = nameField.getText();
        if (!name.isEmpty()) {
            try {
                gamesService.add(new Games(name));
                loadGames();
                nameField.clear();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleUpdateGame() {
        if (selectedGame != null) {
            selectedGame.setName(nameField.getText());
            try {
                gamesService.update(selectedGame);
                loadGames();
                nameField.clear();
                selectedGame = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
