package controllers.evenement;

import com.google.zxing.BarcodeFormat;
import com.itextpdf.text.pdf.qrcode.BitMatrix;
import com.itextpdf.text.pdf.qrcode.QRCodeWriter;
import entities.Event;
import entities.EventGenre;
import entities.Sponsors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.EventService;
import com.google.zxing.client.j2se.*;

import utils.CurrencyUtil;

import javafx.stage.FileChooser;
import utils.TelegraphUploader;

import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.nio.file.StandardCopyOption;
import java.util.Currency;
import java.util.stream.Collectors;
import java.util.Locale;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class EventController implements Initializable {

    @FXML private TableView<Event> eventTable;
    @FXML private TableColumn<Event, String> nomEvent;
    @FXML private TableColumn<Event, String> description;
    @FXML private TableColumn<Event, String> imageColumn;
    @FXML private TableColumn<Event, String> genre;
    @FXML private TableColumn<Event, String> sponsor;
    @FXML private TableColumn<Event, Integer> nbrMemeber;
    @FXML private TableColumn<Event, Number> prix;
    @FXML private TableColumn<Event, LocalDate> dateCreation;
    @FXML private TableColumn<Event, LocalDate> eventDate;
    @FXML private TableColumn<Event, Void> actionColumn;
    @FXML private TextField searchField;
    @FXML
    private Button exportButton;
    @FXML private Label deviseLabel;

    @FXML private HBox loadingBox;
    @FXML private ProgressIndicator loadingIndicator;

    private final EventService eventService = new EventService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCurrency();
        configureTableColumns();
        loadEvents();
    }

    private void setCurrency() {
        Currency currency = CurrencyUtil.getCurrencyFromIP();
        String currencySymbol = currency.getSymbol();
        deviseLabel.setText("Devise: " + currencySymbol + " (1 TND = " +
                String.format("%.2f", CurrencyUtil.convertFromTND(1.0, currency)) + " " +
                currency.getCurrencyCode() + ")");

        if (eventTable != null) {
            eventTable.refresh();
        }
    }



    private Image loadImageWithRetry(String imagePath) {
        // Essai 1: Chargement normal depuis les ressources
        try {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("First try failed for: " + imagePath);
        }

        // Essai 2: Chargement direct depuis le système de fichiers
        try {
            Path path = Paths.get("src/main/resources" + imagePath);
            if (Files.exists(path)) {
                return new Image(path.toUri().toString());
            }
        } catch (Exception e) {
            System.err.println("Second try failed for: " + imagePath);
        }

        return null;
    }

    private void configureTableColumns() {
        nomEvent.setCellValueFactory(new PropertyValueFactory<>("nomEvent"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        nbrMemeber.setCellValueFactory(new PropertyValueFactory<>("nbrMemebers"));

        // Get the currency symbol from the label
        String currencySymbol = deviseLabel.getText().replace("Devise: ", "");

        // Configure the price column to show value with currency
        prix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prix.setCellFactory(column -> new TableCell<Event, Number>() {
            @Override
            protected void updateItem(Number price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    // Get the currency symbol from the label
                    String currencySymbol = deviseLabel.getText().replace("Devise: ", "");
                    setText(String.format("%.2f %s", CurrencyUtil.convertFromTND(price.doubleValue(), CurrencyUtil.getCurrencyFromIP()), ""));
                }
            }
        });

        dateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        eventDate.setCellValueFactory(new PropertyValueFactory<>("dateEvent"));
        genre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGenre().getNomGenre()));
        sponsor.setCellValueFactory(cellData -> {
            List<Sponsors> sponsors = cellData.getValue().getListSponsors();
            return new SimpleStringProperty(sponsors != null ?
                    sponsors.stream().map(Sponsors::getNomSponsor).collect(Collectors.joining(", ")) : "");
        });

        // Rest of your column configurations...
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageColumn.setCellFactory(column -> new TableCell<Event, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitWidth(80);
                imageView.setFitHeight(60);
                imageView.setPreserveRatio(true);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    Image image = loadImageWithRetry(imagePath);
                    if (image != null) {
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } else {
                        System.err.println("Failed to load image: " + imagePath);
                        setGraphic(null);
                    }
                }
            }
        });

        actionColumn.setCellFactory(createActionCellFactory());
    }

    private Callback<TableColumn<Event, Void>, TableCell<Event, Void>> createActionCellFactory() {
        actionColumn.setPrefWidth(300);
        return param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button qrCodeButton = new Button("QR Code");

            {


                editButton.setOnAction(event -> {
                    Event selectedEvent = getTableView().getItems().get(getIndex());
                    openEditDialog(selectedEvent);
                });

                deleteButton.setOnAction(event -> {
                    Event selectedEvent = getTableView().getItems().get(getIndex());
                    deleteEvent(selectedEvent);
                });

                qrCodeButton.setOnAction(event -> {
                    Event selectedEvent = getTableView().getItems().get(getIndex());
                    //exportEventAsQRCode(selectedEvent);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(20, editButton, deleteButton, qrCodeButton);
                    buttons.setAlignment(Pos.CENTER); // Optional: center align the buttons
                    setGraphic(buttons);
                }
            }
        };
    }




    /*private void exportEventAsQRCode(Event event) {
        try {
            // Combine the event's name and description into a single string
            String eventInfo = "Description: " + event.getDescription() + "\nDate: " + event.getDateEvent();
            String url = TelegraphUploader.createTelegraphPage(event.getNomEvent(), eventInfo);
            System.out.println("Event URL: " + url);

            // Generate QR code
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Show the QR code in the UI
            // Convert BufferedImage to JavaFX Image
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", os);
            ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
            Image fxImage = new Image(is);

            ImageView qrCodeImageView = new ImageView(fxImage);
            qrCodeImageView.setFitWidth(200);
            qrCodeImageView.setFitHeight(200);

            // Download button
            Button downloadButton = new Button("Télécharger");
            downloadButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Sauvegarder le QR Code");
                fileChooser.setInitialFileName("EventQRCode_" + event.getId() + ".png");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image PNG", "*.png"));
                File selectedFile = fileChooser.showSaveDialog(null);
                if (selectedFile != null) {
                    try {
                        ImageIO.write(bufferedImage, "PNG", selectedFile);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "QR Code téléchargé avec succès !");
                        alert.show();
                        openFileLocation(selectedFile.getAbsolutePath());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });


            VBox vbox = new VBox(10, qrCodeImageView, downloadButton);
            vbox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(vbox, 250, 300);

            Stage qrCodeStage = new Stage();
            qrCodeStage.setTitle("QR Code de l'Événement");
            qrCodeStage.setScene(scene);
            qrCodeStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/




    public void openFileLocation(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                // Open the folder and highlight the file (Windows only)
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    new ProcessBuilder("explorer.exe", "/select,", file.getAbsolutePath()).start();
                }
                // For macOS
                else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    new ProcessBuilder("open", "-R", file.getAbsolutePath()).start();
                }
                // For Linux (open the folder only)
                else {
                    new ProcessBuilder("xdg-open", file.getParent()).start();
                }
            } else {
                System.err.println("File does not exist: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    private void loadEvents() {
        List<Event> events = eventService.listEvenets();
        ObservableList<Event> observableList = FXCollections.observableArrayList(events);

        // Create the FilteredList and set the default predicate
        FilteredList<Event> filteredList = new FilteredList<>(observableList, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // If the search field is empty, show all events
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Check all fields for the search term
                boolean matches = false;

                // Match based on event name
                matches |= event.getNomEvent().toLowerCase().contains(lowerCaseFilter);

                // Match based on description
                matches |= event.getDescription().toLowerCase().contains(lowerCaseFilter);

                // Match based on genre
                matches |= event.getGenre().getNomGenre().toLowerCase().contains(lowerCaseFilter);

                // Match based on sponsors
                matches |= event.getListSponsors().stream()
                        .map(Sponsors::getNomSponsor)
                        .collect(Collectors.joining(", "))
                        .toLowerCase()
                        .contains(lowerCaseFilter);

                // Match based on number of members (convert to string)
                matches |= String.valueOf(event.getNbrMemebers()).contains(lowerCaseFilter);

                // Match based on price (convert to string)
                matches |= String.valueOf(event.getPrix()).contains(lowerCaseFilter);

                // Match based on creation date (convert to string)
                matches |= String.valueOf(event.getDateCreation()).contains(lowerCaseFilter);

                // Match based on event date (convert to string)
                matches |= String.valueOf(event.getDateEvent()).contains(lowerCaseFilter);

                return matches; // Return whether the event matches any condition
            });
        });

        // Create a SortedList to manage sorting
        SortedList<Event> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(eventTable.comparatorProperty());

        // Apply the sorted list to the table
        eventTable.setItems(sortedList);
        eventTable.refresh(); // Refresh the table view
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/evenement/addEventView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Événement");
            stage.initModality(Modality.APPLICATION_MODAL); // Block interactions with other windows
            stage.showAndWait();

            loadEvents(); // Reload events after adding
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToSponsors() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sponsor/sponsorView.fxml"));
        Parent root = loader.load();
        searchField.getScene().setRoot(root);
    }

    @FXML
    private void navigateToGenres() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/genre/genreView.fxml"));
        Parent root = loader.load();
        searchField.getScene().setRoot(root);
    }


    @FXML
    private void handleExport() {
        // Create a simple Choice Dialog
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Excel", "Excel", "PDF");
        dialog.setTitle("Exporter les Événements");
        dialog.setHeaderText("Choisissez un format d'exportation");
        dialog.setContentText("Format:");

        dialog.showAndWait().ifPresent(choice -> {
            if (choice.equals("Excel")) {
                exportToExcel();
            } else if (choice.equals("PDF")) {
                exportToPDF();
            }
        });
    }

    private void exportToPDF() {
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();


            java.io.File file = new java.io.File(System.getProperty("user.home") + "/Documents/evenements.pdf");

            // Create PDF writer instance
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
            document.open();

            // Title
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Liste des Événements");
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph(" ")); // Empty line

            // Table setup
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(8);
            table.setWidthPercentage(100);

            // Table header
            table.addCell("Nom");
            table.addCell("Description");
            table.addCell("Genre");
            table.addCell("Sponsors");
            table.addCell("Places");
            table.addCell("Prix");
            table.addCell("Date Création");
            table.addCell("Date Événement");

            // Data rows
            for (Event event : eventTable.getItems()) {
                table.addCell(event.getNomEvent());
                table.addCell(event.getDescription());
                table.addCell(event.getGenre().getNomGenre());
                table.addCell(event.getListSponsors().stream().map(Sponsors::getNomSponsor).collect(Collectors.joining(", ")));
                table.addCell(String.valueOf(event.getNbrMemebers()));
                table.addCell(String.valueOf(event.getPrix()));
                table.addCell(String.valueOf(event.getDateCreation()));
                table.addCell(String.valueOf(event.getDateEvent()));
            }

            document.add(table);
            document.close();



            System.out.println("PDF file has been saved to: " + file.getAbsolutePath());
            openFileLocation(file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportToExcel() {
        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Events");

            // Header
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nom");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Genre");
            header.createCell(3).setCellValue("Sponsors");
            header.createCell(4).setCellValue("Places");
            header.createCell(5).setCellValue("Prix");
            header.createCell(6).setCellValue("Date Création");
            header.createCell(7).setCellValue("Date Événement");

            // Data
            List<Event> events = eventTable.getItems();
            for (int i = 0; i < events.size(); i++) {
                Event event = events.get(i);
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(event.getNomEvent());
                row.createCell(1).setCellValue(event.getDescription());
                row.createCell(2).setCellValue(event.getGenre().getNomGenre());
                row.createCell(3).setCellValue(
                        event.getListSponsors().stream().map(Sponsors::getNomSponsor).collect(Collectors.joining(", "))
                );
                row.createCell(4).setCellValue(event.getNbrMemebers());
                row.createCell(5).setCellValue(event.getPrix());
                row.createCell(6).setCellValue(event.getDateCreation().toString());
                row.createCell(7).setCellValue(event.getDateEvent().toString());
            }

            // Define a path for the file to be saved (e.g., "Documents/evenements.xlsx")
            java.io.File file = new java.io.File(System.getProperty("user.home") + "/Documents/evenements.xlsx");

            // Save to file
            try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file)) {
                workbook.write(outputStream);
            }

            System.out.println("Excel file has been saved to: " + file.getAbsolutePath());
            openFileLocation(file.getAbsolutePath());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void openEditDialog(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/evenement/addEventView.fxml"));
            Parent root = loader.load();

            AddEventController editController = loader.getController();
            editController.setEventForEdit(event); // Pass the selected event to the edit controller

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Événement");
            stage.initModality(Modality.APPLICATION_MODAL); // Block interactions with other windows
            stage.showAndWait();

            loadEvents(); // Reload events after editing
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteEvent(Event event) {
        // Implémenter la logique pour supprimer l'événement
        eventService.deleteEvent(event.getId());
        loadEvents(); // Reload events after deletion
    }
}
