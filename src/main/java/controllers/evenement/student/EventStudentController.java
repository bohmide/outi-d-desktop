package controllers.evenement.student;

import entities.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.EventService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class EventStudentController implements Initializable {

    @FXML
    private FlowPane eventGrid;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> sortComboBox;

    private List<Event> allEvents; // pour stocker tous les événements
    private final EventService eventService = new EventService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allEvents = eventService.listEvenets();
        setupSearchAndSort();
        loadEventsInGrid(allEvents);
    }

    private void loadEventsInGrid(List<Event> events) {
        eventGrid.getChildren().clear();
        for (Event event : events) {
            VBox card = createEventCard(event);
            eventGrid.getChildren().add(card);
        }
    }


    private void setupSearchAndSort() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        sortComboBox.getItems().addAll("Prix croissant", "Prix décroissant", "Date croissante", "Date décroissante");
        sortComboBox.setOnAction(e -> applyFilters());
    }



    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase();
        String sortOption = sortComboBox.getValue();

        List<Event> filtered = allEvents.stream()
                .filter(e ->
                        e.getNomEvent().toLowerCase().contains(keyword) ||
                                e.getGenre().getNomGenre().toLowerCase().contains(keyword) ||
                                String.valueOf(e.getPrix()).contains(keyword) ||  // Filtrer par prix
                                e.getDateEvent().toString().contains(keyword))    // Filtrer par date
                .collect(Collectors.toList());

        if (sortOption != null) {
            switch (sortOption) {
                case "Prix croissant":
                    filtered = filtered.stream()
                            .sorted(Comparator.comparingDouble(Event::getPrix))
                            .collect(Collectors.toList());
                    break;
                case "Prix décroissant":
                    filtered = filtered.stream()
                            .sorted(Comparator.comparingDouble(Event::getPrix).reversed())
                            .collect(Collectors.toList());
                    break;
                case "Date croissante":
                    filtered = filtered.stream()
                            .sorted(Comparator.comparing(Event::getDateEvent))
                            .collect(Collectors.toList());
                    break;
                case "Date décroissante":
                    filtered = filtered.stream()
                            .sorted(Comparator.comparing(Event::getDateEvent).reversed())
                            .collect(Collectors.toList());
                    break;
            }
        }

        loadEventsInGrid(filtered);
    }




    private VBox createEventCard(Event event) {
        VBox card = new VBox(10);
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10; " +
                "-fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 4);");

        // Image de l'événement
        ImageView imageView = new ImageView();
        imageView.setFitWidth(270);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(false);
        imageView.setStyle("-fx-background-radius: 10 10 0 0;");

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
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-event.png"));
                        imageView.setImage(defaultImage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Titre de l'événement
        Label title = new Label(event.getNomEvent());
        title.setStyle("-fx-font-size: 18px; -fx-text-fill: #007BFF; -fx-font-weight: bold;");

        // Détails de l'événement
        HBox detailsRow1 = new HBox(10);
        Label genre = new Label("🎭 " + event.getGenre().getNomGenre());
        genre.setStyle("-fx-text-fill: #555;");
        Label prix = new Label("💰 " + event.getPrix() + " TND");
        prix.setStyle("-fx-text-fill: #333;");
        detailsRow1.getChildren().addAll(genre, prix);

        HBox detailsRow2 = new HBox(10);
        Label date = new Label("📅 " + event.getDateEvent());
        date.setStyle("-fx-text-fill: #444;");
        detailsRow2.getChildren().add(date);

        // Bouton Voir Détails
        Button detailsButton = new Button("Voir Détails");
        detailsButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white; " +
                "-fx-background-radius: 5; -fx-padding: 5 10;");
        detailsButton.setOnAction(e -> showEventDetails(event));

        card.getChildren().addAll(imageView, title, detailsRow1, detailsRow2, detailsButton);
        return card;
    }

    private void showEventDetails(Event event) {
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
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-event.png"));
                        imageView.setImage(defaultImage);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Titre et détails
        Label title = new Label(event.getNomEvent());
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #007BFF;");

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
    }
}