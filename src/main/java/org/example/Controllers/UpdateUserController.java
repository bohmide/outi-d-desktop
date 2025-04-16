package org.example.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Models.User;
import org.example.Services.UserService;

import java.io.IOException;

public class UpdateUserController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField numTelField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserService userService = new UserService();
    private User userToUpdate;

    public void setUserToUpdate(User user) {
        this.userToUpdate = user;

        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        numTelField.setText(user.getNumtel());
        passwordField.setText(user.getPassword());
        roleComboBox.setValue(user.getType());
    }

    @FXML
    private void handleUpdate() {
        if (userToUpdate == null) return;

        userToUpdate.setFirstName(firstNameField.getText().trim());
        userToUpdate.setLastName(lastNameField.getText().trim());
        userToUpdate.setEmail(emailField.getText().trim());
        userToUpdate.setNumtel(numTelField.getText().trim());
        userToUpdate.setPassword(passwordField.getText().trim());
        userToUpdate.setType(roleComboBox.getValue());

        try {
            userService.update(userToUpdate);
            statusLabel.setText("✅ User updated successfully.");
            goBack();
        } catch (Exception e) {
            statusLabel.setText("❌ Update failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        goBack();
    }

    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/AdminUsers.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) roleComboBox.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}