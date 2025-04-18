package controllers;

import entities.Sponsors;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import services.SponsorsService;

import java.time.LocalDate;

public class AddSponsorController {

    @FXML private TextField idField, nameField, descriptionField, imagePathField;
    @FXML private DatePicker datePicker;

    private final SponsorsService service = new SponsorsService();

    @FXML
    public void handleAddSponsor() {
        try {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String description = descriptionField.getText();
            String imagePath = imagePathField.getText();
            LocalDate date = datePicker.getValue();

            Sponsors sponsor = new Sponsors(id, name, description, imagePath, date);
            service.addSponsor(sponsor);
            clearFields();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        descriptionField.clear();
        imagePathField.clear();
        datePicker.setValue(null);
    }
}
