package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import entities.User;
import services.UserService;

import java.io.IOException;

public class UpdateUserController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField numTelField;
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
    }

    @FXML
    private void handleUpdate() {
        if (userToUpdate == null) return;

        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String numTel = numTelField.getText().trim();
        String password = passwordField.getText().trim();

        // Validation du prénom et nom
        if (firstName.length() < 3 || firstName.length() > 20) {
            statusLabel.setText("❌ Le prénom doit contenir entre 3 et 20 caractères.");
            return;
        }
        if (lastName.length() < 3 || lastName.length() > 20) {
            statusLabel.setText("❌ Le nom doit contenir entre 3 et 20 caractères.");
            return;
        }

        // ✅ Validation numéro de téléphone
        if (!numTel.matches("\\d{8}")) {
            statusLabel.setText("❌ Le numéro de téléphone doit contenir exactement 8 chiffres.");
            return;
        }

        // Validation de l'email avec une regex simple
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            statusLabel.setText("❌ Email invalide.");
            return;
        }

        // Validation du mot de passe
        if (password.length() < 10 || password.length() > 20) {
            statusLabel.setText("❌ Le mot de passe doit contenir entre 10 et 20 caractères.");
            return;
        }
        String lowerPass = password.toLowerCase();
        if (lowerPass.contains(firstName.toLowerCase()) || lowerPass.contains(lastName.toLowerCase())) {
            statusLabel.setText("❌ Le mot de passe ne doit pas contenir le prénom ou le nom.");
            return;
        }

        // Mise à jour de l'objet
        userToUpdate.setFirstName(firstName);
        userToUpdate.setLastName(lastName);
        userToUpdate.setEmail(email);
        userToUpdate.setNumtel(numTel);
        userToUpdate.setPassword(password);

        try {
            userService.update(userToUpdate);
            statusLabel.setText("✅ Utilisateur mis à jour avec succès.");
            goBack();
        } catch (Exception e) {
            statusLabel.setText("❌ Échec de la mise à jour : " + e.getMessage());
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
            Stage stage = (Stage) firstNameField.getScene().getWindow(); // 👈 use a valid node from this scene
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
