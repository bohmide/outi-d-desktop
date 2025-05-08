package controllers.evenement;

import entities.Event;
import entities.EventGenre;
import entities.Sponsors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.EventGenreService;
import services.EventService;
import services.SponsorsService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class AddEventController {

    @FXML private TextField nomEvent;
    @FXML private TextArea description;
    @FXML private DatePicker eventDate;
    @FXML private TextField nbrMember;
    @FXML private TextField imagePath;
    @FXML private TextField priw;
    @FXML private ComboBox<EventGenre> genreComboBox;
    @FXML private ComboBox<Sponsors> sponsorComboBox;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button browseButton;

    private final EventService eventService = new EventService();
    private final EventGenreService genreService = new EventGenreService();
    private final SponsorsService sponsorsService = new SponsorsService();

    private Event eventToEdit;

    @FXML
    public void initialize() {
        // Charger les genres et sponsors
        genreComboBox.setItems(FXCollections.observableArrayList(genreService.listEventGenre()));
        sponsorComboBox.setItems(FXCollections.observableArrayList(sponsorsService.listSponsor()));
    }

    public void setEventForEdit(Event event) {
        this.eventToEdit = event;
        nomEvent.setText(event.getNomEvent());
        description.setText(event.getDescription());
        eventDate.setValue(event.getDateEvent());
        nbrMember.setText(String.valueOf(event.getNbrMemebers()));
        imagePath.setText(event.getImagePath());
        priw.setText(String.valueOf(event.getPrix()));
        genreComboBox.setValue(event.getGenre());
        if (!event.getListSponsors().isEmpty()) {
            sponsorComboBox.setValue(event.getListSponsors().get(0));
        }
        saveButton.setText("Modifier");
    }

    @FXML
    private void handleBrowseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(browseButton.getScene().getWindow());
        if (selectedFile != null) {
            try {
                // Créer le dossier images s'il n'existe pas
                Path destDir = Paths.get("src/main/resources/images");
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }

                // Générer un nom unique pour le fichier
                String uniqueName = UUID.randomUUID() + "_" + selectedFile.getName();
                Path destination = destDir.resolve(uniqueName);

                // Copier le fichier
                Files.copy(selectedFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le champ avec le chemin relatif
                imagePath.setText("/images/" + uniqueName);
            } catch (IOException e) {
                showAlert("Erreur", "Impossible de copier l'image: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Validation des données
            if (nomEvent.getText().isEmpty() || description.getText().isEmpty() ||
                    eventDate.getValue() == null || nbrMember.getText().isEmpty() ||
                    imagePath.getText().isEmpty() || priw.getText().isEmpty() ||
                    genreComboBox.getValue() == null || sponsorComboBox.getValue() == null) {
                showAlert("Erreur", "Tous les champs doivent être remplis");
                return;
            }

            // Création/mise à jour de l'événement
            Event event = eventToEdit != null ? eventToEdit : new Event();
            event.setNomEvent(nomEvent.getText());
            event.setDescription(description.getText());
            event.setDateEvent(eventDate.getValue());
            event.setNbrMemebers(Integer.parseInt(nbrMember.getText()));
            event.setImagePath(imagePath.getText());
            event.setPrix(Float.parseFloat(priw.getText()));
            event.setDateCreation(LocalDate.now());
            event.setGenre(genreComboBox.getValue());
            event.setListSponsors(List.of(sponsorComboBox.getValue()));

            if (eventToEdit != null) {
                eventService.updateEvent(event);
            } else {
                eventService.addEvent(event);
            }

            // Fermer la fenêtre
            ((Stage) saveButton.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer des valeurs numériques valides");
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}