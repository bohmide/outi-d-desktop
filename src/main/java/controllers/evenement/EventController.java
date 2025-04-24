package controllers.evenement;

import entities.Event;
import entities.EventGenre;
import entities.Sponsors;
import services.EventService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EventController implements Initializable {

    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, String> nomEvent;

    @FXML
    private TableColumn<Event, String> description;

    @FXML
    private TableColumn<Event, String> imagePath;

    @FXML
    private TableColumn<Event, String> genre;

    @FXML
    private TableColumn<Event, String> sponsor;

    @FXML
    private TableColumn<Event, Integer> nbrMemeber;

    @FXML
    private TableColumn<Event, Double> prix;

    @FXML
    private TableColumn<Event, LocalDate> dateCreation;

    @FXML
    private TableColumn<Event, LocalDate> eventDate;

    @FXML
    private TableColumn<Event, Void> actionColumn;  // Column for actions (Modifier, Supprimer)

    @FXML
    private Button addEvent;

    private final EventService eventService = new EventService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Standard properties for other columns
        nomEvent.setCellValueFactory(new PropertyValueFactory<>("nomEvent"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        imagePath.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        nbrMemeber.setCellValueFactory(new PropertyValueFactory<>("nbrMemebers"));
        prix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        dateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        eventDate.setCellValueFactory(new PropertyValueFactory<>("dateEvent"));
        genre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGenre().getNomGenre()));
        sponsor.setCellValueFactory(cellData -> {
            List<Sponsors> sponsors = cellData.getValue().getListSponsors();
            String sponsorNames = (sponsors != null && !sponsors.isEmpty())
                    ? sponsors.stream().map(Sponsors::getNomSponsor).collect(Collectors.joining(", "))
                    : "Aucun";
            return new SimpleStringProperty(sponsorNames);
        });

        // Actions column (Modifier and Supprimer buttons)
        actionColumn.setCellFactory(col -> {
            TableCell<Event, Void> cell = new TableCell<Event, Void>() {
                private final Button modifyButton = new Button("Modifier");
                private final Button deleteButton = new Button("Supprimer");

                {
                    modifyButton.setOnAction(event -> {
                        Event selectedEvent = getTableView().getItems().get(getIndex());
                        openModifierView(selectedEvent);  // Open the event for editing
                    });

                    deleteButton.setOnAction(event -> {
                        Event selectedEvent = getTableView().getItems().get(getIndex());
                        supprimerEvent(selectedEvent);  // Delete the selected event
                    });
                }

                @Override
                public void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        HBox hBox = new HBox(10);
                        hBox.getChildren().addAll(modifyButton, deleteButton);
                        setGraphic(hBox);
                    }
                }
            };
            return cell;
        });

        // Load the data
        loadEvents();

        // Set button action
        addEvent.setOnAction(this::handleAjouter);
    }

    private void loadEvents() {
        List<Event> events = eventService.listEvenets();  // Ensure this method is implemented in EventService
        eventTable.setItems(FXCollections.observableArrayList(events));
    }

    @FXML
    private void handleAjouter(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/evenement/addEventView.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Ajouter un Événement");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

            // Reload table after closing popup
            loadEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openModifierView(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/evenement/addEventView.fxml"));
            Parent root = loader.load();
            AddEventController controller = loader.getController();
            controller.setEventForEdit(event);  // Pass the event to the controller for editing

            Stage popupStage = new Stage();
            popupStage.setScene(new Scene(root));
            popupStage.setTitle("Modifier un Événement");
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();

            // Reload table after modification
            loadEvents();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void supprimerEvent(Event event) {
        eventService.deleteEvent(event.getId());  // Assuming you have a deleteEvent method in EventService
        loadEvents();  // Reload the table after deletion
    }
}
