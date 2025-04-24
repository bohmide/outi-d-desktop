package Controller;

import entities.Badge;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import services.BadgeService;

import java.io.File;
import java.sql.SQLException;

public class BadgeController {

    @FXML
    private TableView<Badge> badgeTable;
    @FXML
    private TableColumn<Badge, Integer> idColumn;
    @FXML
    private TableColumn<Badge, String> nameColumn;
    @FXML
    private TableColumn<Badge, Integer> requiredScoreColumn;
    @FXML
    private TableColumn<Badge, String> iconColumn;
    @FXML
    private TableColumn<Badge, Void> actionColumn;

    @FXML
    private TextField nameField;
    @FXML
    private TextField scoreField;
    @FXML
    private TextField iconField;

    private final BadgeService badgeService = new BadgeService();
    private final ObservableList<Badge> badgeList = FXCollections.observableArrayList();

    private Badge selectedBadge = null;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        requiredScoreColumn.setCellValueFactory(new PropertyValueFactory<>("requiredScore"));
        iconColumn.setCellValueFactory(new PropertyValueFactory<>("icon"));

        loadBadges();
        setupActions();
        setupIconColumn();
    }

    private void loadBadges() {
        try {
            badgeList.setAll(badgeService.getAll());
            badgeTable.setItems(badgeList);
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
                    Badge b = getTableView().getItems().get(getIndex());
                    try {
                        badgeService.delete(b.getId());
                        loadBadges();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                btnEdit.setOnAction(event -> {
                    selectedBadge = getTableView().getItems().get(getIndex());
                    nameField.setText(selectedBadge.getName());
                    scoreField.setText(String.valueOf(selectedBadge.getRequiredScore()));
                    iconField.setText(selectedBadge.getIcon());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private void setupIconColumn() {
        iconColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
            }

            @Override
            protected void updateItem(String iconPath, boolean empty) {
                super.updateItem(iconPath, empty);
                if (empty || iconPath == null || iconPath.isEmpty()) {
                    setGraphic(null);
                } else {
                    try {
                        Image image = new Image("file:" + iconPath);
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } catch (Exception e) {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    @FXML
    private void handleAddBadge() {
        String name = nameField.getText();
        String scoreStr = scoreField.getText();
        String icon = iconField.getText();

        if (!name.isEmpty() && !scoreStr.isEmpty() && !icon.isEmpty() ) {
            try {
                if ( validateInput()) {
                int score = Integer.parseInt(scoreStr);
                badgeService.add(new Badge(name, score, icon));
                loadBadges();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Badge ajouté avec succès !");

                clearFields();
                }
            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                clearFields();
            }
        }
    }

    @FXML
    private void handleUpdateBadge() {
        if (selectedBadge != null) {
            selectedBadge.setName(nameField.getText());
            selectedBadge.setRequiredScore(Integer.parseInt(scoreField.getText()));
            selectedBadge.setIcon(iconField.getText());
            try {
                badgeService.update(selectedBadge);
                loadBadges();
                clearFields();
                selectedBadge = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleChooseIcon() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une icône");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            iconField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void clearFields() {
        nameField.clear();
        scoreField.clear();
        iconField.clear();
    }
    private boolean validateInput() {
        String name = nameField.getText();
        String scoreStr = scoreField.getText();
        String iconPath = iconField.getText();

        if (name == null || name.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ 'Nom' est obligatoire.");
            return false;
        }

        if (scoreStr == null || scoreStr.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ 'Score requis' est obligatoire.");
            return false;
        }

        try {
            Double.parseDouble(scoreStr); // autorise les nombres réels
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ 'Score requis' doit être un nombre.");
            return false;
        }

        if (iconPath == null || iconPath.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", "Le champ 'Icône' est obligatoire.");
            return false;
        }

        return true;
    }
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
