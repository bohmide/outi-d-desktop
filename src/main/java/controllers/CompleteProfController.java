package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.Prof;
import entities.Session;
import entities.User;
import services.ProfService;

import java.sql.Date;
import java.time.LocalDate;

public class CompleteProfController {

    @FXML private DatePicker interDatePicker;
    @FXML private ComboBox<String> interModeComboBox;
    @FXML private Label messageLabel;

    private final ProfService profService = new ProfService();

    @FXML
    public void initialize() {
        interModeComboBox.getItems().addAll("online", "face to face");
    }

    @FXML
    private void handleFinishSignup() {
        try {
            User user = Session.getCurrentUser();
            if (user == null) {
                messageLabel.setText("Session expired. Please sign up again.");
                return;
            }

            Date interDate = Date.valueOf(interDatePicker.getValue());
            String interMode = interModeComboBox.getValue();

            if (interDate == null || interMode == null || interMode.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            LocalDate interLocalDate = interDate.toLocalDate(); // Conversion en LocalDate
            LocalDate today = LocalDate.now();
            LocalDate minimumDate = today.plusDays(2);

            if (!interLocalDate.isAfter(today.plusDays(1))) {
                messageLabel.setText("The date must be at least 2 days from today.");
                return;
            }

            Prof prof = new Prof(interDate, interMode);
            prof.setId(user.getId()); // FK to User

            profService.ajouter(prof);

            messageLabel.setText("✅ Prof info saved! Redirecting...");

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
