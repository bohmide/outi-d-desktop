package controllers.evenement;

import entities.Event;
import entities.EventGenre;
import entities.Sponsors;
import services.EventGenreService;
import services.EventService;
import services.SponsorsService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class AddEventController {

    @FXML
    private TextField nomEvent;

    @FXML
    private TextArea description;

    @FXML
    private DatePicker eventDate;

    @FXML
    private TextField nbrMember;

    @FXML
    private TextField imagePath;

    @FXML
    private TextField priw;

    @FXML
    private ComboBox<EventGenre> genreComboBox;

    @FXML
    private ComboBox<Sponsors> sponsorComboBox;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final EventService eventService = new EventService();
    private final EventGenreService genreService = new EventGenreService();
    private final SponsorsService sponsorsService = new SponsorsService();

    private Event eventToEdit;

    @FXML
    public void initialize() {
        List<EventGenre> genres = genreService.listEventGenre();
        genreComboBox.setItems(FXCollections.observableArrayList(genres));

        List<Sponsors> sponsors = sponsorsService.listSponsor();
        sponsorComboBox.setItems(FXCollections.observableArrayList(sponsors));
    }

    // Set event for editing
    public void setEventForEdit(Event event) {
        this.eventToEdit = event;
        nomEvent.setText(event.getNomEvent());
        description.setText(event.getDescription());
        eventDate.setValue(event.getDateEvent());
        nbrMember.setText(String.valueOf(event.getNbrMemebers()));
        imagePath.setText(event.getImagePath());
        priw.setText(String.valueOf(event.getPrix()));

        genreComboBox.setValue(event.getGenre());
        sponsorComboBox.setValue(event.getListSponsors().get(0));  // Assuming one sponsor
        saveButton.setText("Modifier");
    }

    @FXML
    public void handleSave(javafx.event.ActionEvent actionEvent) {
        String name = nomEvent.getText();
        String desc = description.getText();
        LocalDate dateEvent = eventDate.getValue();
        int nbr = Integer.parseInt(nbrMember.getText());
        String image = imagePath.getText();
        float prix = Float.parseFloat(priw.getText());
        LocalDate creationDate = LocalDate.now();

        EventGenre selectedGenre = genreComboBox.getValue();
        Sponsors selectedSponsor = sponsorComboBox.getValue();

        try {
            if (eventToEdit != null) {
                // Update existing event
                eventToEdit.setNomEvent(name);
                eventToEdit.setDescription(desc);
                eventToEdit.setDateEvent(dateEvent);
                eventToEdit.setNbrMemebers(nbr);
                eventToEdit.setImagePath(image);
                eventToEdit.setPrix(prix);
                eventToEdit.setDateCreation(creationDate);
                eventToEdit.setGenre(selectedGenre);
                eventToEdit.setListSponsors(List.of(selectedSponsor)); // assuming single sponsor

                eventService.updateEvent(eventToEdit);
            } else {
                // Add new event
                Event event = new Event();
                event.setNomEvent(name);
                event.setDescription(desc);
                event.setDateEvent(dateEvent);
                event.setNbrMemebers(nbr);
                event.setImagePath(image);
                event.setPrix(prix);
                event.setDateCreation(creationDate);
                event.setGenre(selectedGenre);
                event.setListSponsors(List.of(selectedSponsor)); // assuming single sponsor

                eventService.addEvent(event);
            }

            // Close popup
            Stage stage = (Stage) saveButton.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez remplir tous les champs correctement.");
        }
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        // Close the window without saving
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
