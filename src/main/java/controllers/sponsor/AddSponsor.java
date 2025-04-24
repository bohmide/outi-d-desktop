package controllers.sponsor;

import entities.Sponsors;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.SponsorsService;

import java.time.LocalDate;

public class AddSponsor {
    @FXML
    private TextField nameField;

    @FXML
    private TextField descriptionField;

    @FXML
    private TextField imageField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private SponsorsService sponsorsService = new SponsorsService();

    private Sponsors currentSponsor;  // Sponsor à modifier

    // Méthode pour initialiser les champs avec les données du sponsor sélectionné
    public void setSponsor(Sponsors sponsor) {
        this.currentSponsor = sponsor;
        nameField.setText(sponsor.getNomSponsor());
        descriptionField.setText(sponsor.getDescription());
        imageField.setText(sponsor.getImagePath());
    }

    @FXML
    private void handleSave() {
        String name = nameField.getText().trim();
        String desc = descriptionField.getText().trim();
        String img  = imageField.getText().trim();
        SponsorsService service = new SponsorsService();

        if (name.isEmpty() || desc.isEmpty() || img.isEmpty()) {
            showAlert("error", "Tous les champs sont obligatoires.");
            return;
        }

        try {
            if (currentSponsor == null) {
                // Mode ajout
                Sponsors sp = new Sponsors(name, desc, img, LocalDate.now());

                service.addSponsor(sp);
                System.out.println("Ajout effectué, nouveau ID : " + sp.getId());
            } else {
                // Mode mise à jour
                currentSponsor.setNomSponsor(name);
                currentSponsor.setDescription(desc);
                currentSponsor.setImagePath(img);
                service.updateSponsor(currentSponsor);
                System.out.println("Mise à jour effectuée pour ID : " + currentSponsor.getId());
            }
            ((Stage) saveButton.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();  // Affiche la cause dans la console
            showAlert("error","Une erreur est survenue : " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel(javafx.event.ActionEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
