package org.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Model.User;
import org.example.Service.UserService;
import org.example.Model.Session;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password.");
            return;
        }

        User authenticatedUser = userService.authenticate(email, password);
        if (authenticatedUser != null) {
            Session.setCurrentUser(authenticatedUser);
            redirectToDashboard(authenticatedUser);
        } else {
            errorLabel.setText("Invalid email or password.");
        }
    }

    private void redirectToDashboard(User user) {
        try {
            String fxmlFile = switch (user.getType().toLowerCase()) {
                case "student" -> "/org/example/Main/View/Frontoffice/StudentDashboard.fxml";
                case "prof" -> "/org/example/Main/View/Frontoffice/ProfDashboard.fxml";
                case "parent" -> "/org/example/Main/View/Frontoffice/ParentDashboard.fxml";
                default -> "/org/example/Main/View/Frontoffice/Home.fxml";
            };

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            errorLabel.setText("Login successful but failed to load dashboard.");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Signup.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}