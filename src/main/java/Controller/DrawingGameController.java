package Controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.Dialog;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Stage;
import org.json.JSONObject;
import utils.VoiceAssistant;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public class DrawingGameController {

    @FXML
    private Canvas canvas;
    private double lastX, lastY;

    @FXML
    public void initialize() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(3);

        canvas.setOnMousePressed(this::startDrawing);
        canvas.setOnMouseDragged(this::draw);
    }

    private void startDrawing(MouseEvent event) {
        lastX = event.getX();
        lastY = event.getY();
    }

    private void draw(MouseEvent event) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double x = event.getX();
        double y = event.getY();
        gc.strokeLine(lastX, lastY, x, y);
        lastX = x;
        lastY = y;
    }
    @FXML
    private void handleSaveDrawing() {
        try {
            int targetSize = 255;

            // Capture du canvas JavaFX
            WritableImage fxImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, fxImage);

            // Conversion JavaFX -> BufferedImage (noir/blanc)
            BufferedImage rawImage = new BufferedImage((int) canvas.getWidth(), (int) canvas.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < rawImage.getHeight(); y++) {
                for (int x = 0; x < rawImage.getWidth(); x++) {
                    javafx.scene.paint.Color fxColor = fxImage.getPixelReader().getColor(x, y);
                    int gray = (int) (fxColor.getRed() * 255);
                    int rgb = (gray > 30) ? 0xFFFFFF : 0x000000; // noir ou blanc
                    rawImage.setRGB(x, y, rgb);
                }
            }

            // Trouver la bounding box du dessin (partie noire)
            int minX = rawImage.getWidth(), minY = rawImage.getHeight(), maxX = 0, maxY = 0;
            for (int y = 0; y < rawImage.getHeight(); y++) {
                for (int x = 0; x < rawImage.getWidth(); x++) {
                    if (rawImage.getRGB(x, y) == 0xFF000000) { // pixel noir
                        if (x < minX) minX = x;
                        if (x > maxX) maxX = x;
                        if (y < minY) minY = y;
                        if (y > maxY) maxY = y;
                    }
                }
            }

            // Vérifie s’il y a bien un dessin
            if (minX > maxX || minY > maxY) {
                showError("Aucun dessin détecté !");
                return;
            }

            // Rogner l'image autour du dessin
            BufferedImage croppedImage = rawImage.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);

            // Redimensionner à 255x255
            Image tmp = croppedImage.getScaledInstance(targetSize, targetSize, Image.SCALE_SMOOTH);
            BufferedImage finalImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g2d = finalImage.createGraphics();
            g2d.drawImage(tmp, 0, 0, null);
            g2d.dispose();

            // Sauvegarde pour debug
            ImageIO.write(finalImage, "png", new File("final_debug.png"));

            // Encodage base64 et envoi
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(finalImage, "png", baos);
            String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/predict"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{\"image\":\"" + base64Image + "\"}"))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::handlePredictionResponse);

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de la capture du dessin : " + e.getMessage());
        }
    }




    private void handlePredictionResponse(String responseBody) {
        try {
            JSONObject json = new JSONObject(responseBody);

            if (json.has("success") && json.getBoolean("success")) {
                String prediction = json.getString("prediction");
                String suggestion = json.getString("suggestion");

                javafx.application.Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Résultat de l'IA");
                    alert.setHeaderText("Interprétation du dessin");

                    String content = "Prédiction : " + prediction + "\n\n" +
                            "Suggestion d'amélioration :\n" + suggestion;

                    alert.setContentText(content);

                    // Appliquer le style enfantin
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
                    dialogPane.getStyleClass().add("result-alert");

                    alert.showAndWait();
                });

            } else {
                String error = json.optString("error", "Erreur inconnue");
                showError("Erreur IA : " + error);
            }

        } catch (Exception e) {
            showError("Réponse serveur invalide : " + responseBody);
        }
    }





    // Fonction utilitaire pour mettre une majuscule à la première lettre
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }


    private void showError(String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }









    @FXML
    private void handleClearCanvas() {
        // Effacer tout le canvas
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE); // Couleur de fond
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setStroke(Color.BLACK); // Réinitialiser la couleur du trait
        gc.setLineWidth(3);
    }

    @FXML
    private void handleChangeColor() {
        // Créer un ColorPicker
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue((Color) canvas.getGraphicsContext2D().getStroke());

        // Configurer la boîte de dialogue
        Dialog<Color> dialog = new Dialog<>();
        dialog.setTitle("Choisir une couleur");
        dialog.getDialogPane().setContent(colorPicker);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Gérer le résultat
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return colorPicker.getValue();
            }
            return null;
        });

        // Afficher et appliquer
        Optional<Color> result = dialog.showAndWait();
        result.ifPresent(newColor -> {
            canvas.getGraphicsContext2D().setStroke(newColor);
        });
    }
    @FXML
    private void handleReturnToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainGamification_View.fxml"));
            Parent root = loader.load();

            // Charger la scène du menu principal
            Stage stage = (Stage) canvas.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styleKids.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

            // Optionnel : Ajouter un message vocal
            VoiceAssistant.speak("Retour au menu principal. Choisissez une nouvelle aventure!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
