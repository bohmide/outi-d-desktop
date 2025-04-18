package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Session;
import models.Student;
import models.User;
import services.StudentService;

import java.sql.Date;
import java.time.LocalDate;

public class CompleteStudentController {

    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<String> sexeComboBox;
    @FXML private Label messageLabel;

    private final StudentService studentService = new StudentService();

    @FXML
    public void initialize() {
        sexeComboBox.getItems().addAll("Homme", "Femme");
    }

    @FXML
    private void handleFinishSignup() {
        try {
            User user = Session.getCurrentUser();
            if (user == null) {
                messageLabel.setText("Session expired. Please sign up again.");
                return;
            }

            if (birthDatePicker.getValue() == null || sexeComboBox.getValue() == null || sexeComboBox.getValue().isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            LocalDate birthDate = birthDatePicker.getValue();
            LocalDate minDate = LocalDate.now().minusYears(25); // né il y a 25 ans (âge max)
            LocalDate maxDate = LocalDate.now().minusYears(12); // né il y a 12 ans (âge min)

            if (birthDate.isBefore(minDate) || birthDate.isAfter(maxDate)) {
                messageLabel.setText("Age must be between 12 and 25 years.");
                return;
            }

            Date dateBirth = Date.valueOf(birthDatePicker.getValue());
            String sexe = sexeComboBox.getValue();

            Student student = new Student(dateBirth, sexe);
            student.setId(user.getId()); // ✅ Very important: foreign key to User

            System.out.println("✅ Saving student with ID: " + student.getId());

            studentService.ajouter(student);

            messageLabel.setText("✅ Student info saved! Redirecting...");

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