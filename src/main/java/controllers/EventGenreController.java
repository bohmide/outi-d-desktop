package controllers;

import entities.EventGenre;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.EventGenreService;

import java.time.LocalDate;

public class EventGenreController {

    private EventGenreService genreService = new EventGenreService();
    private ObservableList<EventGenre> eventGenres = FXCollections.observableArrayList();

    @FXML
    private TextField nameField, nbrField, imagePathField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<EventGenre> genreTableView;
    @FXML
    private TableColumn<EventGenre, String> nameColumn, nbrColumn, imagePathColumn, dateColumn;
    @FXML
    private Button addButton, updateButton, deleteButton;

    @FXML
    public void initialize() {
        // Set up columns
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomGenre()));
        nbrColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getNbr())));
        imagePathColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getImagePath()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateCreation().toString()));

        loadEventGenres();
    }

    // Load all event genres into the TableView
    private void loadEventGenres() {
        eventGenres.clear();
        eventGenres.addAll(genreService.listEventGenre());
        genreTableView.setItems(eventGenres);
    }

    // Add a new genre
    @FXML
    private void handleAddEventGenre() {
        String name = nameField.getText();
        int nbr = Integer.parseInt(nbrField.getText());
        String imagePath = imagePathField.getText();
        LocalDate date = datePicker.getValue();

        EventGenre newGenre = new EventGenre(name, nbr, imagePath, date);
        genreService.addEventGenre(newGenre);
        loadEventGenres();
    }

    // Update an existing genre
    @FXML
    private void handleUpdateEventGenre() {
        EventGenre selectedGenre = genreTableView.getSelectionModel().getSelectedItem();
        if (selectedGenre != null) {
            selectedGenre.setNomGenre(nameField.getText());
            selectedGenre.setNbr(Integer.parseInt(nbrField.getText()));
            selectedGenre.setImagePath(imagePathField.getText());
            selectedGenre.setDateCreation(datePicker.getValue());

            genreService.updateEventGenre(selectedGenre);
            loadEventGenres();
        }
    }

    // Delete a selected genre
    @FXML
    private void handleDeleteEventGenre() {
        EventGenre selectedGenre = genreTableView.getSelectionModel().getSelectedItem();
        if (selectedGenre != null) {
            genreService.deleteEventGenre(selectedGenre);
            loadEventGenres();
        }
    }

    // Event handling to populate fields when a genre is selected from the table
    @FXML
    private void handleTableSelection() {
        EventGenre selectedGenre = genreTableView.getSelectionModel().getSelectedItem();
        if (selectedGenre != null) {
            nameField.setText(selectedGenre.getNomGenre());
            nbrField.setText(String.valueOf(selectedGenre.getNbr()));
            imagePathField.setText(selectedGenre.getImagePath());
            datePicker.setValue(selectedGenre.getDateCreation());
        }
    }
}
