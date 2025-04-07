package org.example.Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Model.User;
import org.example.Service.UserService;

import java.util.Collections;
import java.util.List;

public class SignupController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        typeComboBox.getItems().addAll("student", "prof", "parent");
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

            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || type == null) {
                messageLabel.setText("Please fill all fields.");
                return;
            }

            System.out.println("Creating user...");
            String jsonRoles = "[\"ROLE_" + type.toUpperCase() + "\"]";
            User user = new User(email, password, List.of(jsonRoles), firstName, lastName, type);
            userService.ajouter(user);  // 💥 this is the line most likely to fail

            System.out.println("User created!");

            messageLabel.setText("Signup successful! Redirecting to login...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);

        } catch (Exception e) {
            e.printStackTrace();  // this will now show the REAL error!
            messageLabel.setText("❌ Signup error: " + e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}