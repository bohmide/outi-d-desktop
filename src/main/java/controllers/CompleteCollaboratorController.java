package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Collaborator;
import models.Session;
import models.User;
import services.CollaboratorService;

public class CompleteCollaboratorController {

    @FXML private TextField nomColField;
    @FXML private Label messageLabel;

    private final CollaboratorService collaboratorService = new CollaboratorService();

    @FXML
    private void handleFinishSignup() {
        try {
            User user = Session.getCurrentUser();
            if (user == null) {
                messageLabel.setText("Session expired. Please sign up again.");
                return;
            }

            String nomcol = nomColField.getText();

            if (nomcol == null || nomcol.trim().isEmpty()) {
                messageLabel.setText("Please enter the collaborator's name.");
                return;
            }

            nomcol = nomcol.trim(); // Pour éviter les espaces au début/fin

            if (nomcol.length() < 4 || nomcol.length() > 20) {
                messageLabel.setText("The name must be between 4 and 20 characters.");
                return;
            }

            Collaborator collaborator = new Collaborator(nomcol.trim());
            collaborator.setId(user.getId()); // FK to User

            collaboratorService.ajouter(collaborator);

            messageLabel.setText("✅ Collaborator info saved! Redirecting...");

            // Clear session and go to login
            Session.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) messageLabel.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Error: " + e.getMessage());
        }
    }
}
