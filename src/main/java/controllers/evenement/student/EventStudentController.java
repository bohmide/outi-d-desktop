package controllers.evenement.student;

import entities.Event;
import entities.EventGenre;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.EventService;
import utils.MailUtil;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EventStudentController implements Initializable {

    @FXML private FlowPane eventGrid;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;

    private List<Event> allEvents;
    private final EventService eventService = new EventService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize events, handling null case
        allEvents = eventService.listEvenets();
        if (allEvents == null) {
            allEvents = new ArrayList<>();
        }
        setupSearchAndSort();
        loadEventsInGrid(allEvents);
    }

    private void setupSearchAndSort() {
        // Set up search listener
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        // Set up sort options
        sortComboBox.getItems().addAll(
                "Prix croissant", "Prix décroissant",
                "Date croissante", "Date décroissante"
        );
        sortComboBox.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase();
        String sort = sortComboBox.getValue();

        // Filter events based on search keyword
        List<Event> filtered = allEvents.stream()
                .filter(e -> e.getNomEvent().toLowerCase().contains(keyword) ||
                        e.getGenre().getNomGenre().toLowerCase().contains(keyword) ||
                        String.valueOf(e.getPrix()).contains(keyword) ||
                        e.getDateEvent().toString().contains(keyword))
                .collect(Collectors.toList());

        // Sort events based on selected option
        if (sort != null) {
            switch (sort) {
                case "Prix croissant":
                    filtered.sort(Comparator.comparingDouble(Event::getPrix));
                    break;
                case "Prix décroissant":
                    filtered.sort(Comparator.comparingDouble(Event::getPrix).reversed());
                    break;
                case "Date croissante":
                    filtered.sort(Comparator.comparing(Event::getDateEvent));
                    break;
                case "Date décroissante":
                    filtered.sort(Comparator.comparing(Event::getDateEvent).reversed());
                    break;
            }
        }

        loadEventsInGrid(filtered);
    }

    private void loadEventsInGrid(List<Event> events) {
        eventGrid.getChildren().clear();
        if (events != null) {
            events.forEach(event -> eventGrid.getChildren().add(createEventCard(event)));
        }
    }

    private VBox createEventCard(Event event) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 4);");

        // Image
        ImageView img = new ImageView();
        img.setFitWidth(270);
        img.setFitHeight(150);
        img.setPreserveRatio(false);
        loadEventImage(event.getImagePath(), img);

        // Title
        Label title = new Label(event.getNomEvent());
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: #007BFF; -fx-font-weight: bold;");

        // Details
        HBox row1 = new HBox(10);
        row1.getChildren().addAll(
                new Label("🎭 " + event.getGenre().getNomGenre()),
                new Label("💰 " + event.getPrix() + " TND")
        );
        HBox row2 = new HBox(10);
        row2.getChildren().add(new Label("📅 " + event.getDateEvent()));

        // Details button
        Button detailsBtn = new Button("Voir Détails");
        detailsBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");
        detailsBtn.setOnAction(e -> showEventDetails(event));

        // Reserve button
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");
        reserverButton.setOnAction(e -> handleReservation(event));

        card.getChildren().addAll(img, title, row1, row2, new HBox(10, detailsBtn), new HBox(10, reserverButton));
        return card;
    }

    private void loadEventImage(String imagePath, ImageView imageView) {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                // Try loading from resources
                InputStream stream = getClass().getResourceAsStream(imagePath);
                if (stream != null) {
                    imageView.setImage(new Image(stream));
                    return;
                }
                // Try loading from file system
                String filePath = Paths.get("src/main/resources" + imagePath).toUri().toString();
                Image fileImage = new Image(filePath);
                if (!fileImage.isError()) {
                    imageView.setImage(fileImage);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + imagePath);
        }
        // Fallback to default image
        imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-event.png")));
    }

    private void handleReservation(Event event) {
        try {
            // TODO: Replace with dynamic user email input or configuration
            String toEmail = "student@example.com"; // Replace with actual user email
            String fromEmail = "noreply@example.com"; // Replace with configured sender email
            MailUtil.reserveEvent(fromEmail, toEmail, event.getNomEvent(), event.getDateEvent().toString());
            new Alert(Alert.AlertType.INFORMATION, "Votre réservation a été envoyée par email !").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'envoi de la réservation: " + e.getMessage()).showAndWait();
        }
    }

    private void showEventDetails(Event event) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Détails de l'Événement");
        popupStage.initModality(Modality.APPLICATION_MODAL);

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #f4f9ff; -fx-border-color: #007BFF; -fx-border-radius: 10; -fx-background-radius: 10;");
        layout.setPrefWidth(500);

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(460);
        imageView.setFitHeight(250);
        imageView.setPreserveRatio(false);
        imageView.setStyle("-fx-background-radius: 10;");
        loadEventImage(event.getImagePath(), imageView);

        // Title
        Label title = new Label(event.getNomEvent());
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #007BFF;");

        // Details grid
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(20);
        detailsGrid.setVgap(10);
        detailsGrid.add(new Label("Genre:"), 0, 0);
        detailsGrid.add(new Label(event.getGenre().getNomGenre()), 1, 0);
        detailsGrid.add(new Label("Prix:"), 0, 1);
        detailsGrid.add(new Label(event.getPrix() + " TND"), 1, 1);
        detailsGrid.add(new Label("Date:"), 0, 2);
        detailsGrid.add(new Label(event.getDateEvent().toString()), 1, 2);
        detailsGrid.add(new Label("Participants max:"), 0, 3);
        detailsGrid.add(new Label(String.valueOf(event.getNbrMemebers())), 1, 3);

        // Description
        Label descriptionLabel = new Label("Description:");
        descriptionLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #007BFF;");
        Label description = new Label(event.getDescription());
        description.setWrapText(true);
        description.setStyle("-fx-padding: 0 0 10 0;");

        // Close button
        Button closeButton = new Button("Fermer");
        closeButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");
        closeButton.setOnAction(e -> popupStage.close());

        layout.getChildren().addAll(imageView, title, detailsGrid, descriptionLabel, description, closeButton);

        Scene scene = new Scene(layout);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }
}