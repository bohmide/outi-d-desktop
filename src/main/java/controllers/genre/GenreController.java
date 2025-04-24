package controllers.genre;

import entities.EventGenre;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.EventGenreService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class GenreController implements javafx.fxml.Initializable {

    @FXML
    private TableView<EventGenre> genreTable;
    @FXML
    private TableColumn<EventGenre, String> genreName;
    @FXML
    private TableColumn<EventGenre, Integer> nbrGenre;
    @FXML
    private TableColumn<EventGenre, String> imagePath;
    @FXML
    private TableColumn<EventGenre, String> dateCreation;
    @FXML
    private TableColumn<EventGenre, Void> actionColumn;


    private final EventGenreService genreService = new EventGenreService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        genreName.setCellValueFactory(new PropertyValueFactory<>("nomGenre"));
        nbrGenre.setCellValueFactory(new PropertyValueFactory<>("nbr"));
        imagePath.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        dateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        loadEventGenres(); // charge les données
        addActionButtons(); // ajoute les boutons
    }

    private void loadEventGenres() {
        List<EventGenre> genres = genreService.listEventGenre();
        genreTable.setItems(FXCollections.observableArrayList(genres));
    }

    @FXML
    private void handleAjouter(javafx.event.ActionEvent event) {
        try {
            // Load the add genre popup FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/genre/addGenreView.fxml"));
            Parent root = loader.load();

            // Create a new stage for the popup
            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Ajouter un Genre");
            popupStage.initModality(Modality.APPLICATION_MODAL);  // Makes the popup modal (blocks interaction with main window)
            popupStage.showAndWait();  // Show the popup and wait for it to close

            // Reload table after closing popup
            loadEventGenres();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addActionButtons() {
        Callback<TableColumn<EventGenre, Void>, TableCell<EventGenre, Void>> cellFactory = param -> new TableCell<>() {
            private final Button modifierButton = new Button("Modifier");
            private final Button supprimerButton = new Button("Supprimer");

            {
                modifierButton.setOnAction(event -> {
                    EventGenre genre = getTableView().getItems().get(getIndex());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/genre/addGenreView.fxml"));
                        Parent root = loader.load();

                        AddGenreController controller = loader.getController();
                        controller.setGenreToEdit(genre); // envoie le genre à modifier

                        Stage popupStage = new Stage();
                        popupStage.setScene(new Scene(root));
                        popupStage.setTitle("Modifier le Genre");
                        popupStage.initModality(Modality.APPLICATION_MODAL);
                        popupStage.showAndWait();

                        loadEventGenres(); // recharge après modif

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                supprimerButton.setOnAction(event -> {
                    EventGenre genre = getTableView().getItems().get(getIndex());
                    genreService.deleteEventGenreById(genre.getId()); // supprime
                    loadEventGenres(); // recharge après suppression
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, modifierButton, supprimerButton);
                    setGraphic(buttons);
                }
            }
        };

        actionColumn.setCellFactory(cellFactory);
    }
}
