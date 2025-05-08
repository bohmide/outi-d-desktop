package controllers;

import entities.Organisation;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.ServiceOrganisation;
import javafx.application.Platform;

public class addOrganisationFormController {

    @FXML private Label errorLabel;
    @FXML private TextField nomField;
    @FXML private TextField domaineField;
    @FXML private Button actionButton;

    private final ServiceOrganisation service = new ServiceOrganisation();
    private Integer currentOrganisationId = null;

    public void setOrganisationData(Organisation org) {
        currentOrganisationId = org.getId();
        nomField.setText(org.getNomOrganisation());
        domaineField.setText(org.getDomaine());
        actionButton.setText("Modifier");
    }

    @FXML
    private void handleAction() {
        String nom = nomField.getText().trim();
        String domaine = domaineField.getText().trim();

        // Réinitialisation des erreurs
        errorLabel.setVisible(false);
        nomField.setStyle("");
        domaineField.setStyle("");

        if (nom.isEmpty() || domaine.isEmpty()) {
            errorLabel.setText("Tous les champs sont obligatoires.");
            errorLabel.setVisible(true);

            if (nom.isEmpty()) nomField.setStyle("-fx-border-color: red;");
            if (domaine.isEmpty()) domaineField.setStyle("-fx-border-color: red;");
            return;
        }

        Organisation org = new Organisation();
        org.setNomOrganisation(nom);
        org.setDomaine(domaine);

        if (currentOrganisationId != null) {
            org.setId(currentOrganisationId);
            service.modifierOrganisation(org);
            showAlert("Succès", "Organisation modifiée !");
        } else {
            service.ajouterOrganisation(org);
            showAlert("Succès", "Organisation ajoutée !");
        }

        closeWindow();
    }


    private void closeWindow() {
        Stage stage = (Stage) actionButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    public void initialize() {
        // Solution simplifiée
        nomField.requestFocus();
    }
}