package Controller;

import dao.GamesDAO; // Ajoutez un DAO pour les jeux si ce n'est pas déjà fait
import dao.MemoryCardDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import entities.MemoryCard;
import entities.Games; // Ajoutez l'entité Game
import javafx.stage.Stage;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemoryCardController {

    @FXML private ComboBox<String> gameComboBox; // Utilisation du ComboBox pour les jeux
    @FXML private TableView<MemoryCard> memoryCardTable;
    @FXML private TableColumn<MemoryCard, Integer> idColumn;
    @FXML private TableColumn<MemoryCard, String> gameColumn; // Modifié de Integer à String pour afficher les noms
    @FXML private TableColumn<MemoryCard, String> imagesColumn;
    @FXML private TableColumn<MemoryCard, Void> actionsColumn;

    private Stage stage;


    private final MemoryCardDAO dao = new MemoryCardDAO();
    private final GamesDAO gameDAO = new GamesDAO(); // DAO pour les jeux
    private final ObservableList<MemoryCard> memoryCards = FXCollections.observableArrayList();
    private List<String> selectedImages = new ArrayList<>();

    private MemoryCardDAO memoryCardDAO;


    public MemoryCardController() {
        // Initialiser le DAO ici
        this.memoryCardDAO = new MemoryCardDAO(); // Assure-toi que MemoryCardDAO est correctement instancié
    }
    public MemoryCardController(MemoryCardDAO memoryCardDAO) {
        this.memoryCardDAO = memoryCardDAO;
    }
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleIntegerProperty(cell.getValue().getId()).asObject());

        gameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getGameName())); // Affiche le nom du jeu

        imagesColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getIndex() >= memoryCardTable.getItems().size()) {
                    setGraphic(null);
                } else {
                    MemoryCard mc = memoryCardTable.getItems().get(getIndex());
                    List<String> images = mc.getImages();
                    HBox imageBox = new HBox(5);
                    for (String path : images) {
                        File file = new File(path);
                        if (file.exists()) {
                            ImageView imgView = new ImageView(new javafx.scene.image.Image(file.toURI().toString()));
                            imgView.setFitWidth(50);
                            imgView.setFitHeight(50);
                            imageBox.getChildren().add(imgView);
                        }
                    }
                    setGraphic(imageBox);
                }
            }
        });

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final Button editBtn = new Button("Modifier");

            {
                deleteBtn.setOnAction(event -> {
                    MemoryCard mc = getTableView().getItems().get(getIndex());
                    try {
                        dao.delete(mc.getId());
                        loadMemoryCards();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });

                editBtn.setOnAction(event -> {
                    MemoryCard mc = getTableView().getItems().get(getIndex());
                    gameComboBox.setValue(mc.getGameName()); // Sélectionne le jeu par nom
                    selectedImages = mc.getImages();
                    memoryCardTable.getSelectionModel().select(mc);
                });

                HBox box = new HBox(5, editBtn, deleteBtn);
                setGraphic(box);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : getGraphic());
            }
        });






        memoryCardTable.setItems(memoryCards);
        loadMemoryCards();
        loadGames(); // Charge les jeux dans le ComboBox
    }

    @FXML
    private void handleAddMemoryCard() {
        try {
            // Crée un objet MemoryCard et appelle la méthode add du DAO
            MemoryCard memoryCard = new MemoryCard();
            memoryCard.setGameId(1);  // Exemple, à remplacer par une valeur dynamique
            memoryCard.setImages(selectedImages);
            String selectedGameName = gameComboBox.getValue();
            if (selectedGameName != null) {
                Integer selectedGameId = getGameIdByName(selectedGameName);  // Méthode pour récupérer l'ID à partir du nom
                memoryCard.setGameId(selectedGameId);  // Affecter l'ID du jeu à la MemoryCard
            }

            memoryCardDAO.add(memoryCard);  // Appelle la méthode add du DAO pour insérer la mémoire

            // Afficher un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "La MemoryCard a été ajoutée avec succès.");
        } catch (SQLException e) {
            // Gérer les erreurs
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la MemoryCard: " + e.getMessage());
        }
        loadMemoryCards();
        gameComboBox.getSelectionModel().clearSelection();
        selectedImages.clear();

    }
    // Charger la liste des jeux dans le ComboBox
    private void loadGames() {
        try {
            ObservableList<String> gameNames = FXCollections.observableArrayList();
            List<Games> games = gameDAO.getAll(); // Récupère tous les jeux depuis la base de données
            for (Games game : games) {
                gameNames.add(game.getName());
            }
            gameComboBox.setItems(gameNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void handleChooseImages() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));

        List<File> files = fileChooser.showOpenMultipleDialog(stage);  // Permet de sélectionner plusieurs fichiers

        if (files != null) {
            System.out.println("Nombre de fichiers sélectionnés: " + files.size());  // Vérifie combien de fichiers sont sélectionnés

            // Ajoute tous les fichiers dans la liste
            for (File file : files) {
                System.out.println("Fichier sélectionné : " + file.getAbsolutePath()); // Affiche le chemin de chaque fichier
                String imagePath = file.getAbsolutePath();
                // Vérifie si l'image est déjà dans la liste
                if (!selectedImages.contains(imagePath)) {
                    selectedImages.add(imagePath);
                }
            }
            System.out.println("Liste d'images avant l'ajout : " + selectedImages.size());
            // Affiche la liste finale des images
        } else {
            System.out.println("Aucun fichier sélectionné");
        }
    }



    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleUpdateMemoryCard() {
        try {
            MemoryCard selected = memoryCardTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String selectedGameName = gameComboBox.getSelectionModel().getSelectedItem();
                int gameId = getGameIdByName(selectedGameName); // Récupère l'ID du jeu à partir du nom sélectionné
                selected.setGameId(gameId);
                selected.setImages(selectedImages);
                dao.update(selected);
                loadMemoryCards();
                gameComboBox.getSelectionModel().clearSelection();
                selectedImages.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Récupère l'ID du jeu en fonction du nom
    private int getGameIdByName(String gameName) {
        try {
            Games game = gameDAO.getByName(gameName); // Rechercher le jeu par son nom
            return game != null ? game.getId() : -1; // Retourner l'ID du jeu
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void loadMemoryCards() {
        try {
            memoryCards.setAll(dao.getAll());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
