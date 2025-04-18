package controllers;

import entities.Event;
import entities.Sponsors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.EventService;

import java.io.IOException;
import java.util.stream.Collectors;

public class EventListController {

    @FXML
    private TableView<Event> eventTable;

    @FXML
    private TableColumn<Event, String> nameColumn;

    @FXML
    private TableColumn<Event, String> descriptionColumn;

    @FXML
    private TableColumn<Event, String> genreColumn;

    @FXML
    private TableColumn<Event, String> sponsorColumn;

    @FXML
    private TableColumn<Event, String> dateColumn;

    @FXML
    private Button btnAddEvent;

    @FXML
    private Button btnEditEvent;

    @FXML
    private Button btnDeleteEvent;

    private EventService eventService = new EventService();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("nomEvent"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateEvent().toString()));

        genreColumn.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getGenre().getNomGenre())
        );

        sponsorColumn.setCellValueFactory(cellData -> {
            String sponsorNames = cellData.getValue().getListSponsors().stream()
                    .map(Sponsors::getNomSponsor)
                    .collect(Collectors.joining(", "));
            return new SimpleStringProperty(sponsorNames);
        });

        refreshTable();
    }

    private void refreshTable() {
        ObservableList<Event> events = FXCollections.observableArrayList(eventService.listEvenets());
        eventTable.setItems(events);
    }

    @FXML
    private void handleAddEvent(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddEvent.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Add Event");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        refreshTable();
    }

    @FXML
    private void handleEditEvent(ActionEvent event) throws IOException {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an event to edit.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AddEvent.fxml"));
        Parent root = loader.load();
        EventAddController controller = loader.getController();
        //controller.setEditMode(selected);

        Stage stage = new Stage();
        stage.setTitle("Edit Event");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        refreshTable();
    }

    @FXML
    private void handleDeleteEvent(ActionEvent event) {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an event to delete.");
            return;
        }
        eventService.deleteEvent(selected.getId());
        refreshTable();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
