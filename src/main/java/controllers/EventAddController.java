package controllers;

import entities.Event;
import entities.EventGenre;
import entities.Sponsors;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.EventGenreService;
import services.EventService;
import services.SponsorsService;

import java.time.LocalDate;
import java.util.List;

public class EventAddController {

    @FXML
    private TextField eventNameField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private TextField membersField;

    @FXML
    private TextField imagePathField;

    @FXML
    private DatePicker creationDatePicker;

    @FXML
    private TextField priceField;

    @FXML
    private ComboBox<EventGenre> genreComboBox;

    @FXML
    private ComboBox<Sponsors> sponsorComboBox;

    private final EventService eventService = new EventService();
    private final EventGenreService genreService = new EventGenreService();
    private final SponsorsService sponsorsService = new SponsorsService();

    @FXML
    public void initialize() {
        List<EventGenre> genres = genreService.listEventGenre();
        genreComboBox.getItems().addAll(genres);

        List<Sponsors> sponsors = sponsorsService.listSponsor();
        sponsorComboBox.getItems().addAll(sponsors);
    }

    @FXML
    private void handleAddEvent() {
        try {
            String name = eventNameField.getText();
            String description = descriptionArea.getText();
            LocalDate eventDate = eventDatePicker.getValue();
            int members = Integer.parseInt(membersField.getText());
            String imagePath = imagePathField.getText();
            LocalDate creationDate = creationDatePicker.getValue();
            float price = Float.parseFloat(priceField.getText());
            EventGenre genre = genreComboBox.getValue();
            Sponsors sponsor = sponsorComboBox.getValue();

            Event event = new Event();
            event.setNomEvent(name);
            event.setDescription(description);
            event.setDateEvent(eventDate);
            event.setNbrMemebers(members);
            event.setImagePath(imagePath);
            event.setDateCreation(creationDate);
            event.setPrix(price);
            event.setGenre(genre);

            eventService.addEvent(event, genre.getId(), sponsor.getId());

            // Clear fields after adding
            eventNameField.clear();
            descriptionArea.clear();
            eventDatePicker.setValue(null);
            membersField.clear();
            imagePathField.clear();
            creationDatePicker.setValue(null);
            priceField.clear();
            genreComboBox.getSelectionModel().clearSelection();
            sponsorComboBox.getSelectionModel().clearSelection();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Event added successfully!");
            alert.showAndWait();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding event: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
