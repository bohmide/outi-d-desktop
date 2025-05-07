package org.example.Controllers.user;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.Models.user.User;
import org.example.Services.user.UserService;
import org.example.Models.user.Session;
//import org.example.Utils.GoogleLogin;

import javafx.event.ActionEvent;
import org.example.Utils.user.FaceAuthenticator;
import org.example.Utils.GoogleLogin;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;


public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Label captchaLabel;
    @FXML private TextField captchaField;
    @FXML private Button adminButton;

    private final UserService userService = new UserService();
    private String generatedCaptcha;

    private final FaceAuthenticator authenticator = new FaceAuthenticator();
    private final String pythonScriptPath = "C:/Users/beyat/Desktop/face_id_test/login.py";
    private final String dbPath = "C:/Users/beyat/Desktop/face_id_test/face_db";

    @FXML
    public void initialize() {
        generateCaptcha();
    }

    /*private void playIntroVideo() {
        try {
            AnchorPane videoPane = new AnchorPane();
            videoPane.setStyle("-fx-background-color: black;");

            // Configurer le MediaView
            String videoPath = "/videos/0303.mp4";
            Media media = new Media(getClass().getResource(videoPath).toExternalForm());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);

            mediaView.setFitWidth(1980);
            mediaView.setFitHeight(1080);
            mediaView.setPreserveRatio(false);

            Label skipLabel = new Label("Appuyez sur ÉCHAP pour passer l'introduction");
            skipLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            AnchorPane.setBottomAnchor(skipLabel, 20.0);
            AnchorPane.setRightAnchor(skipLabel, 20.0);

            videoPane.getChildren().addAll(mediaView, skipLabel);

            videoPane.setPrefWidth(1980);
            videoPane.setPrefHeight(1080);
            videoPane.setMaxWidth(1980);
            videoPane.setMaxHeight(1080);

            videoPane.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    skipVideo();
                }
            });

            videoPane.setFocusTraversable(true);
            Platform.runLater(() -> videoPane.requestFocus());

            home_page.getChildren().add(videoPane);
            mediaPlayer.play();

            // Gérer la fin de la vidéo
            mediaPlayer.setOnEndOfMedia(() -> {
                skipVideo();
            });

        } catch (Exception e) {
            e.printStackTrace();
            String nextPage;
            nextPage = "/views/CompleteParentSignup.fxml";
        }
    }

    private void skipVideo() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }
        home_page.getChildren().remove(home_page.getChildren().size() - 1);
        videoPlayed = true;
        String nextPage;
        nextPage = "/views/CompleteParentSignup.fxml";
    }*/

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String enteredCaptcha = captchaField.getText().trim();

        if (email.isEmpty() || password.isEmpty() || enteredCaptcha.isEmpty()) {
            errorLabel.setText("Please enter both email and password.");
            return;
        }

        if (!enteredCaptcha.equals(generatedCaptcha)) {
            errorLabel.setText("Incorrect Captcha. Please try again.");
            generateCaptcha(); // refresh captcha after failed attempt
            return;
        }

        User authenticatedUser = userService.authenticate(email, password);
        if (authenticatedUser != null) {
            Session.setCurrentUser(authenticatedUser);
            redirectToHome();
        } else {
            errorLabel.setText("Invalid email or password.");
            generateCaptcha();
        }
    }

    private void redirectToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user/home.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            errorLabel.setText("Login successful but failed to load home.");
            e.printStackTrace();
        }
    }

    @FXML
    private void goToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user/Signup.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAdminPanel() {
        try {
            System.out.println("Starting face authentication...");
            System.out.println("Python script path: " + pythonScriptPath);
            System.out.println("DB path: " + dbPath);
            CompletableFuture.supplyAsync(() -> {
                Webcam webcam = null;
                try {
                    webcam = Webcam.getDefault();
                    if (webcam == null) {
                        throw new RuntimeException("Aucune webcam trouvée.");
                    }

                    webcam.setViewSize(WebcamResolution.VGA.getSize());
                    webcam.open();

                    BufferedImage image = webcam.getImage();
                    if (image == null) {
                        throw new RuntimeException("Impossible de capturer l'image.");
                    }

                    File tempFile = File.createTempFile("login", ".jpg");
                    ImageIO.write(image, "JPG", tempFile);
                    System.out.println("Image capturée et sauvegardée : " + tempFile.getAbsolutePath());
                    return tempFile;
                } catch (IOException e) {
                    throw new RuntimeException("Erreur lors de la capture de l'image : " + e.getMessage(), e);
                } finally {
                    if (webcam != null && webcam.isOpen()) {
                        webcam.close();
                    }
                }
            }).thenCompose(tempFile -> {
                System.out.println("Début de l'authentification...");
                return authenticator.authenticateAsync(tempFile, pythonScriptPath, dbPath)
                        .whenComplete((result, error) -> {
                            if (tempFile != null && tempFile.exists()) {
                                tempFile.delete();
                                System.out.println("Fichier temporaire supprimé.");
                            }
                            if (error != null) {
                                System.err.println("Erreur lors de l'authentification : " + error.getMessage());
                            }
                        });
            }).thenAcceptAsync(result -> {
                if (result) {
                    System.out.println("Match !");
                    Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user/AdminUsers.fxml"));
                            Scene scene = new Scene(loader.load());
                            Stage stage = (Stage) emailField.getScene().getWindow();
                            stage.setScene(scene);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    System.out.println("Pas de match.");
                    Platform.runLater(() -> {
                        showAlert("erreur", "Pas de match", Alert.AlertType.ERROR);
                    });
                }
            }, Platform::runLater).exceptionally(ex -> {
                ex.printStackTrace();
                System.err.println("Erreur lors de la tentative de connexion : " + ex.getMessage());
                Platform.runLater(() -> {
                    showAlert("erreur", "Pas de match", Alert.AlertType.ERROR);
                });
                return null;
            });
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur inattendue dans goToAdminPanel : " + e.getMessage());
        }
    }

    private void generateCaptcha() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder captchaBuilder = new StringBuilder();
        Random rnd = new Random();
        while (captchaBuilder.length() < 5) { // 5 characters captcha
            int index = (int) (rnd.nextFloat() * characters.length());
            captchaBuilder.append(characters.charAt(index));
        }
        generatedCaptcha = captchaBuilder.toString();
        captchaLabel.setText(generatedCaptcha);
    }

    @FXML
    private void refreshCaptcha() {
        generateCaptcha();
    }
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    private void ForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/user/ForgotPassword.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            errorLabel.setText("❌ Failed to open Forgot Password page.");
            e.printStackTrace();
        }
    }


    @FXML
    private void handleGoogleLogin(ActionEvent event) {
        try {
            String googleEmail = GoogleLogin.loginWithGoogle();

            User user = userService.findByEmail(googleEmail);

            if (user != null) {
                Session.setCurrentUser(user);
                errorLabel.setText("✔ Logged in with: " + googleEmail);
                redirectToHome();
            } else {
                errorLabel.setText("❌ No user found with Google email: " + googleEmail);
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Google login failed: " + e.getMessage());
        }
    }



}

