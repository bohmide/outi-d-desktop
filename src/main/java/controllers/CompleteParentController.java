package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Parents;
import models.Session;
import models.User;
import services.ParentsService;

import java.sql.Date;
import java.time.LocalDate;

public class CompleteParentController {

    @FXML private DatePicker birthdayPicker;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private ComboBox<String> sexeComboBox;
    @FXML private ComboBox<String> learningDiffComboBox;
    @FXML private Label messageLabel;

    private final ParentsService parentService = new ParentsService();

    @FXML
    public void initialize() {
        sexeComboBox.getItems().addAll("Male", "Female", "Other");
        learningDiffComboBox.getItems().addAll("yes", "no");
    }

    @FXML
    private void handleFinishSignup() {
        try {
            User user = Session.getCurrentUser();
            if (user == null) {
                messageLabel.setText("Session expired. Please sign up again.");
                return;
            }

            // Collect and validate input
            if (birthdayPicker.getValue() == null ||
                    firstNameField.getText().isEmpty() ||
                    lastNameField.getText().isEmpty() ||
                    sexeComboBox.getValue() == null ||
                    learningDiffComboBox.getValue() == null) {
                messageLabel.setText("❗ Please fill all fields.");
                return;
            }

            Date birthday = Date.valueOf(birthdayPicker.getValue());
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String sexe = sexeComboBox.getValue();
            String learningDiff = learningDiffComboBox.getValue();
            LocalDate birthDate = birthdayPicker.getValue();

            // 🔎 Contrôle de longueur prénom
            if (firstName.length() < 3 || firstName.length() > 20) {
                messageLabel.setText("❗ First name must be between 3 and 20 characters.");
                return;
            }

            // 🔎 Contrôle de longueur nom
            if (lastName.length() < 3 || lastName.length() > 20) {
                messageLabel.setText("❗ Last name must be between 3 and 20 characters.");
                return;
            }

            // 🔎 Contrôle d'âge entre 5.5 et 12 ans
            LocalDate minDate = LocalDate.now().minusYears(12); // Né il y a 12 ans (âge max)
            LocalDate maxDate = LocalDate.now().minusYears(5).minusMonths(6); // Né il y a 5 ans et demi (âge min)

            if (birthDate.isBefore(minDate) || birthDate.isAfter(maxDate)) {
                messageLabel.setText("❗ Age must be between 5.5 and 12 years.");
                return;
            }

            // Create and populate the Parents object
            Parents parent = new Parents(birthday, firstName, lastName, sexe, learningDiff);
            parent.setId(user.getId()); // FK from session

            parentService.ajouter(parent); // Save to DB

            messageLabel.setText("✅ Parent info saved! Redirecting...");

            // Redirect to login
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
