package controllers.evenement.student;

import entities.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.EventService;

import utils.MailUtil;

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

    private List<Event> allEvents;
    private final EventService eventService = new EventService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        allEvents = eventService.listEvenets();
        setupSearchAndSort();
        loadEventsInGrid(allEvents);
    }

    private void setupSearchAndSort() {
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());
        sortComboBox.getItems().addAll("Prix croissant", "Prix décroissant", "Date croissante", "Date décroissante");
        sortComboBox.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase();
        String sort = sortComboBox.getValue();
        List<Event> filtered = allEvents.stream()
                .filter(e -> e.getNomEvent().toLowerCase().contains(keyword)
                        || e.getGenre().getNomGenre().toLowerCase().contains(keyword)
                        || String.valueOf(e.getPrix()).contains(keyword)
                        || e.getDateEvent().toString().contains(keyword))
                .collect(Collectors.toList());
        if (sort != null) {
            switch (sort) {
                case "Prix croissant": filtered.sort(Comparator.comparingDouble(Event::getPrix)); break;
                case "Prix décroissant": filtered.sort(Comparator.comparingDouble(Event::getPrix).reversed()); break;
                case "Date croissante": filtered.sort(Comparator.comparing(Event::getDateEvent)); break;
                case "Date décroissante": filtered.sort(Comparator.comparing(Event::getDateEvent).reversed()); break;
            }
        }
        loadEventsInGrid(filtered);
    }

    private void loadEventsInGrid(List<Event> events) {
        eventGrid.getChildren().clear();
        events.forEach(event -> eventGrid.getChildren().add(createEventCard(event)));
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
        } catch (Exception ignored) {}

        Label title = new Label(event.getNomEvent());
        title.setStyle("-fx-font-size:18px; -fx-text-fill:#007BFF; -fx-font-weight:bold;");

        HBox row1 = new HBox(10, new Label("🎭 "+event.getGenre().getNomGenre()), new Label("💰 "+event.getPrix()+" TND"));
        HBox row2 = new HBox(10, new Label("📅 "+event.getDateEvent()));

        Button detailsBtn = new Button("Voir Détails");
        detailsBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");
        detailsBtn.setOnAction(e -> showEventDetails(event));

        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 5;");
        reserverButton.setOnAction(e -> {
            String toEmail = "tonemaildestinataire@example.com";
            String fromEmail = "noreply@example.com";
            MailUtil.reserveEvent(fromEmail, toEmail, event.getNomEvent(), event.getDateEvent().toString());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Réservation");
            alert.setHeaderText(null);
            alert.setContentText("Votre réservation a été envoyée par email !");
            alert.showAndWait();
        });


        card.getChildren().addAll(img, title, row1, row2, new HBox(10, detailsBtn), new HBox(10, reserverButton));
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