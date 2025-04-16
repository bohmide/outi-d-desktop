package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Models.Session;
import org.example.Models.User;
import org.example.Services.UserService;

import java.util.List;

public class SignupController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField numTelField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("student", "prof", "parent","collaborator");
    }

    @FXML
    private void handleSignup() {
        try {
            System.out.println("Clicked Create Account ✅");

            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String type = typeComboBox.getValue();
            String numTel = numTelField.getText().trim();

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty()
                    || lastName.isEmpty() || type == null || numTel.isEmpty()) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            firstName = firstName.trim();
            lastName = lastName.trim();

            // ✅ Vérifier la taille du prénom
            if (firstName.length() < 3 || firstName.length() > 20) {
                messageLabel.setText("❗ First name must be between 3 and 20 characters.");
                return;
            }

            // ✅ Vérifier la taille du nom
            if (lastName.length() < 3 || lastName.length() > 20) {
                messageLabel.setText("❗ Last name must be between 3 and 20 characters.");
                return;
            }

            // ✅ Vérifier le format de l'email
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                messageLabel.setText("❗ Please enter a valid email address.");
                return;
            }

            // ✅ Vérifier la longueur du mot de passe
            if (password.length() < 10) {
                messageLabel.setText("❗ Password must be at least 10 characters long.");
                return;
            }

            // ✅ Validation du numéro de téléphone (8 chiffres)
            if (!numTel.matches("\\d{8}")) {
                messageLabel.setText("❌ Le numéro de téléphone doit contenir exactement 8 chiffres.");
                return;
            }

            // ✅ Vérifier que le mot de passe est différent du nom + prénom
            String fullName = (firstName + lastName).toLowerCase();
            if (password.toLowerCase().contains(fullName)) {
                messageLabel.setText("❗ Password must not contain your full name.");
                return;
            }

            String jsonRoles = "[\"ROLE_" + type.toUpperCase() + "\"]";
            User user = new User(email, password, List.of(jsonRoles), firstName, lastName, type, numTel);
            userService.ajouter(user);

            System.out.println("✅ User created: " + user.getEmail());
            Session.setCurrentUser(user);

            // Set redirection page based on role
            String nextPage;
            switch (type.toLowerCase()) {
                case "parent":
                    nextPage = "/views/CompleteParentSignup.fxml";
                    break;
                case "student":
                    nextPage = "/views/CompleteStudentSignup.fxml";
                    break;
                case "prof":
                    nextPage = "/views/CompleteProfSignup.fxml";
                    break;
                case "collaborator":
                    nextPage = "/views/CompleteCollaboratorSignup.fxml";
                    break;
                default:
                    messageLabel.setText("Unknown user type.");
                    return;
            }

            System.out.println("Redirecting to: " + nextPage);

            // Load and set scene with null check
            FXMLLoader loader = new FXMLLoader();
            var location = getClass().getResource(nextPage);

            if (location == null) {
                throw new IllegalStateException("❌ FXML path not found: " + nextPage);
            }

            loader.setLocation(location);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Signup error: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

