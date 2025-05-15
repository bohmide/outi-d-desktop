package controllers.evenement.student;

import entities.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.EventService;

import utils.MailUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EventStudentController implements Initializable {

    @FXML private FlowPane eventGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button returnButton;

    private List<Event> allEvents;
    private final EventService eventService = new EventService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            allEvents = eventService.listEvenets();
            if (allEvents == null || allEvents.isEmpty()) {
                showErrorMessage("No events found or error loading events");
                allEvents = List.of(); // Initialize with empty list to avoid NPE
            }
            setupSearchAndSort();
            loadEventsInGrid(allEvents);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorMessage("Error initializing events: " + e.getMessage());
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupSearchAndSort() {
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());
        sortComboBox.getItems().addAll("Prix croissant", "Prix décroissant", "Date croissante", "Date décroissante");
        sortComboBox.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase();
        String sort = sortComboBox.getValue();

        // Safely filter events
        List<Event> filtered = allEvents.stream()
                .filter(e -> {
                    try {
                        return (e.getNomEvent() != null && e.getNomEvent().toLowerCase().contains(keyword))
                                || (e.getGenre() != null && e.getGenre().getNomGenre() != null && e.getGenre().getNomGenre().toLowerCase().contains(keyword))
                                || String.valueOf(e.getPrix()).contains(keyword)
                                || (e.getDateEvent() != null && e.getDateEvent().toString().contains(keyword));
                    } catch (Exception ex) {
                        System.err.println("Error filtering event: " + ex.getMessage());
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if (sort != null) {
            try {
                switch (sort) {
                    case "Prix croissant": filtered.sort(Comparator.comparingDouble(Event::getPrix)); break;
                    case "Prix décroissant": filtered.sort(Comparator.comparingDouble(Event::getPrix).reversed()); break;
                    case "Date croissante": filtered.sort(Comparator.comparing(Event::getDateEvent,
                            (d1, d2) -> {
                                if (d1 == null && d2 == null) return 0;
                                if (d1 == null) return -1;
                                if (d2 == null) return 1;
                                return d1.compareTo(d2);
                            }));
                        break;
                    case "Date décroissante": filtered.sort(Comparator.comparing(Event::getDateEvent,
                            (d1, d2) -> {
                                if (d1 == null && d2 == null) return 0;
                                if (d1 == null) return 1;
                                if (d2 == null) return -1;
                                return d2.compareTo(d1);
                            }));
                        break;
                }
            } catch (Exception e) {
                System.err.println("Error sorting events: " + e.getMessage());
            }
        }
        loadEventsInGrid(filtered);
    }

    private void loadEventsInGrid(List<Event> events) {
        eventGrid.getChildren().clear();
        events.forEach(event -> {
            try {
                eventGrid.getChildren().add(createEventCard(event));
            } catch (Exception e) {
                System.err.println("Error creating card for event " + event.getId() + ": " + e.getMessage());
            }
        });
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color:white; -fx-border-radius:10; -fx-background-radius:10; -fx-padding:15; -fx-effect:dropshadow(gaussian,rgba(0,0,0,0.1),6,0,0,4);");

        ImageView img = new ImageView();
        img.setFitWidth(270);
        img.setFitHeight(150);
        img.setPreserveRatio(false);

        try {
            String path = event.getImagePath();
            if (path != null && !path.isEmpty()) {
                InputStream is = getClass().getResourceAsStream(path);
                img.setImage(is != null ? new Image(is) : new Image("file:src/main/resources"+path));
            }
        } catch (Exception e) {
            System.err.println("Error loading image for event " + event.getId() + ": " + e.getMessage());
            // Set a default image
            try {
                img.setImage(new Image(getClass().getResourceAsStream("/images/default-event.png")));
            } catch (Exception ex) {
                // If default image fails, just continue without an image
            }
        }

        Label title = new Label(event.getNomEvent() != null ? event.getNomEvent() : "No Title");
        title.setStyle("-fx-font-size:18px; -fx-text-fill:#007BFF; -fx-font-weight:bold;");

        HBox row1 = new HBox(10);
        if (event.getGenre() != null && event.getGenre().getNomGenre() != null) {
            row1.getChildren().add(new Label("🎭 " + event.getGenre().getNomGenre()));
        } else {
            row1.getChildren().add(new Label("🎭 Unknown Genre"));
        }
        row1.getChildren().add(new Label("💰 " + event.getPrix() + " TND"));

        HBox row2 = new HBox(10);
        if (event.getDateEvent() != null) {
            row2.getChildren().add(new Label("📅 " + event.getDateEvent()));
        } else {
            row2.getChildren().add(new Label("📅 No Date"));
        }

        Button detailsBtn = new Button("Voir Détails");
        detailsBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");
        detailsBtn.setOnAction(e -> showEventDetails(event));

        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");
        reserverButton.setOnAction(e -> {
            try {
                String toEmail = "tonemaildestinataire@example.com";
                String fromEmail = "noreply@example.com";
                MailUtil.reserveEvent(fromEmail, toEmail,
                        event.getNomEvent() != null ? event.getNomEvent() : "Unknown Event",
                        event.getDateEvent() != null ? event.getDateEvent().toString() : "No Date");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Réservation");
                alert.setHeaderText(null);
                alert.setContentText("Votre réservation a été envoyée par email !");
                alert.showAndWait();
            } catch (Exception ex) {
                showErrorMessage("Error sending reservation: " + ex.getMessage());
            }
        });

        card.getChildren().addAll(img, title, row1, row2, new HBox(10, detailsBtn), new HBox(10, reserverButton));
        return card;
    }

    private void showEventDetails(Event event) {
        try {
            Stage popupStage = new Stage();
            popupStage.setTitle("Détails de l'Événement");

            VBox layout = new VBox(15);
            layout.setStyle("-fx-padding: 20; -fx-background-color: #f4f9ff; " +
                    "-fx-border-color: #007BFF; -fx-border-radius: 10; -fx-background-radius: 10;");
            layout.setPrefWidth(500);

            // Image de l'événement en grand
            ImageView imageView = new ImageView();
            imageView.setFitWidth(460);
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(false);
            imageView.setStyle("-fx-background-radius: 10;");

            try {
                String imagePath = event.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    InputStream stream = getClass().getResourceAsStream(imagePath);
                    if (stream != null) {
                        Image image = new Image(stream);
                        imageView.setImage(image);
                    } else {
                        // Fallback: essayer de charger depuis le système de fichiers
                        try {
                            String filePath = "src/main/resources" + imagePath;
                            Image fileImage = new Image("file:" + filePath);
                            imageView.setImage(fileImage);
                        } catch (Exception e) {
                            System.err.println("Failed to load image from file: " + imagePath);
                            // Image par défaut si aucune image n'est trouvée
                            try {
                                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-event.png"));
                                imageView.setImage(defaultImage);
                            } catch (Exception ex) {
                                // If default image fails, just continue without an image
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Titre et détails
            Label title = new Label(event.getNomEvent() != null ? event.getNomEvent() : "No Title");
            title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #007BFF;");

            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(20);
            detailsGrid.setVgap(10);

            detailsGrid.add(new Label("Genre:"), 0, 0);
            detailsGrid.add(new Label(event.getGenre() != null && event.getGenre().getNomGenre() != null ?
                    event.getGenre().getNomGenre() : "Unknown"), 1, 0);

            detailsGrid.add(new Label("Prix:"), 0, 1);
            detailsGrid.add(new Label(event.getPrix() + " TND"), 1, 1);

            detailsGrid.add(new Label("Date:"), 0, 2);
            detailsGrid.add(new Label(event.getDateEvent() != null ?
                    event.getDateEvent().toString() : "No Date"), 1, 2);

            detailsGrid.add(new Label("Participants max:"), 0, 3);
            detailsGrid.add(new Label(String.valueOf(event.getNbrMemebers())), 1, 3);

            // Description
            Label descriptionLabel = new Label("Description:");
            descriptionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #007BFF;");

            Label description = new Label(event.getDescription() != null ? event.getDescription() : "No description available");
            description.setWrapText(true);
            description.setStyle("-fx-padding: 0 0 10 0;");

            // Bouton Fermer
            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; " +
                    "-fx-padding: 8 20; -fx-background-radius: 5;");
            closeButton.setOnAction(e -> popupStage.close());

            layout.getChildren().addAll(imageView, title, detailsGrid, descriptionLabel, description, closeButton);

            Scene scene = new Scene(layout);
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();
        } catch (Exception e) {
            showErrorMessage("Error showing event details: " + e.getMessage());
        }
    }

    @FXML
    private void returnToMainMenu() {
        try {
            // Load the MainMenu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/MainMenu.fxml"));
            Pane mainMenu = loader.load();

            // Get the current stage from the returnButton
            Stage stage = (Stage) returnButton.getScene().getWindow();

            // Create a new scene with the loaded main menu
            Scene mainMenuScene = new Scene(mainMenu, 800, 600); // Adjust size as needed
            mainMenuScene.getStylesheets().add(getClass().getResource("/styles/mainF.css").toExternalForm());

            // Set the new scene on the stage
            stage.setScene(mainMenuScene);
            stage.setTitle("Main Menu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}