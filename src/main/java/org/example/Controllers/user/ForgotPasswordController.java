package org.example.Controllers.user;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.Models.user.User;
import org.example.Services.user.UserService;
import org.example.Utils.user.EmailService;
import org.example.Utils.user.PasswordHasher;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ForgotPasswordController {
    @FXML private TextField emailField;
    @FXML private TextField codeField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label statusLabel;

    private final UserService userService = new UserService();
    private final Map<String, String> emailToCode = new HashMap<>();

    @FXML
    private void sendResetCode() {
        String email = emailField.getText().trim();
        if (!userService.checkEmailExists(email)) {
            statusLabel.setText("Email not found.");
            return;
        }

        String code = generateCode();
        emailToCode.put(email, code);

        try {
            EmailService.sendResetCode(email, code);
            statusLabel.setText("Code sent to your email.");
        } catch (MessagingException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to send email.");
        }
    }

    @FXML
    private void resetPassword() {
        String email = emailField.getText().trim();
        String enteredCode = codeField.getText().trim();
        String newPassword = newPasswordField.getText().trim();

        if (!emailToCode.containsKey(email) || !emailToCode.get(email).equals(enteredCode)) {
            statusLabel.setText("Invalid code.");
            return;
        }

        String hashedPassword = PasswordHasher.hashPassword(newPassword);
        User user = userService.findByEmail(email);
        userService.updatePasswordOnly(user.getEmail(), hashedPassword);
        statusLabel.setText("✅ Password updated.");
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(90000) + 10000);
    }
}
