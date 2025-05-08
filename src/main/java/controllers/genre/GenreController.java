package controllers.genre;

import entities.EventGenre;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.EventGenreService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class GenreController implements javafx.fxml.Initializable {

    @FXML private TableView<EventGenre> genreTable;
    @FXML private TableColumn<EventGenre, String> genreName;
    @FXML private TableColumn<EventGenre, Integer> nbrGenre;
    @FXML private TableColumn<EventGenre, String> imageColumn;
    @FXML private TableColumn<EventGenre, String> dateCreation;
    @FXML private TableColumn<EventGenre, Void> actionColumn;
    @FXML private TextField searchField;


    private final EventGenreService genreService = new EventGenreService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableColumns();
        loadEventGenres();
    }

    private void configureTableColumns() {
        genreName.setCellValueFactory(new PropertyValueFactory<>("nomGenre"));
        nbrGenre.setCellValueFactory(new PropertyValueFactory<>("nbr"));
        dateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        // Configuration de la colonne image
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageColumn.setCellFactory(column -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    Image image = loadImageWithRetry(imagePath);
                    if (image != null) {
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } else {
                        System.err.println("Failed to load image after multiple attempts: " + imagePath);
                        setGraphic(null);
                    }
                }
            }
        });

        // Colonne d'actions
        actionColumn.setCellFactory(createActionCellFactory());
    }

    private Image loadImageWithRetry(String imagePath) {
        // Essai 1: Chargement normal depuis les ressources
        try {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("First try failed for: " + imagePath);
        }

        // Essai 2: Chargement direct depuis le système de fichiers (pour le développement)
        try {
            Path path = Paths.get("src/main/resources" + imagePath);
            if (Files.exists(path)) {
                return new Image(path.toUri().toString());
            }
        } catch (Exception e) {
            System.err.println("Second try failed for: " + imagePath);
        }

        // Essai 3: Attendre et réessayer (pour les systèmes lents)
        try {
            Thread.sleep(200);
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("Third try failed for: " + imagePath);
        }

        return null;
    }

    private Callback<TableColumn<EventGenre, Void>, TableCell<EventGenre, Void>> createActionCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                editButton.setOnAction(event -> {
                    EventGenre genre = getTableView().getItems().get(getIndex());
                    openEditDialog(genre);
                });

                deleteButton.setOnAction(event -> {
                    EventGenre genre = getTableView().getItems().get(getIndex());
                    genreService.deleteEventGenreById(genre.getId());
                    loadEventGenres();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        };
    }

    private void openEditDialog(EventGenre genre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/genre/addGenreView.fxml"));
            Parent root = loader.load();

            AddGenreController controller = loader.getController();
            controller.setGenreToEdit(genre);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Genre");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setOnHidden(e -> loadEventGenres());
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/evenement/eventView.fxml"));
        Parent root = loader.load();
        searchField.getScene().setRoot(root);
    }

    @FXML
    private void handleAjouter(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/genre/addGenreView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Genre");
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.setOnHidden(e -> loadEventGenres());
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEventGenres() {
        List<EventGenre> genreList = genreService.listEventGenre();
        ObservableList<EventGenre> observableList = FXCollections.observableArrayList(genreList);

        FilteredList<EventGenre> filteredList = new FilteredList<>(observableList, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(genre -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Filter based on all properties
                return genre.getNomGenre().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(genre.getNbr()).contains(lowerCaseFilter)
                        || (genre.getDateCreation() != null && genre.getDateCreation().toString().toLowerCase().contains(lowerCaseFilter));
            });
        });

        SortedList<EventGenre> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(genreTable.comparatorProperty());

        genreTable.setItems(sortedList);
    }


}