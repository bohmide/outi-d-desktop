package controllers.evenement;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import services.EventService;
import utils.CurrencyUtil;
import utils.TelegraphUploader;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    @FXML private Button exportButton;
    @FXML private Label deviseLabel;

    private final EventService eventService = new EventService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCurrency();
        configureTableColumns();
        loadEvents();
    }

    private void setCurrency() {
        Currency currency = CurrencyUtil.getCurrencyFromIP();
        deviseLabel.setText("Devise: " + currency.getSymbol() + " (1 TND = " +
                String.format("%.2f", CurrencyUtil.convertFromTND(1.0, currency)) + " " +
                currency.getCurrencyCode() + ")");
        eventTable.refresh();
    }

    private Image loadImageWithRetry(String imagePath) {
        // Try loading from resources
        try {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("Failed to load from resources: " + imagePath);
        }

        // Try loading from file system
        try {
            Path path = Paths.get("src/main/resources" + imagePath);
            if (Files.exists(path)) {
                return new Image(path.toUri().toString());
            }
        } catch (Exception e) {
            System.err.println("Failed to load from file system: " + imagePath);
        }

        return null;
    }

    private void configureTableColumns() {
        nomEvent.setCellValueFactory(new PropertyValueFactory<>("nomEvent"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        nbrMemeber.setCellValueFactory(new PropertyValueFactory<>("nbrMemebers"));
        prix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prix.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    String currencySymbol = deviseLabel.getText().split(" ")[1];
                    setText(String.format("%.2f %s", CurrencyUtil.convertFromTND(price.doubleValue(), CurrencyUtil.getCurrencyFromIP()), currencySymbol));
                }
            }
        });
        dateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        eventDate.setCellValueFactory(new PropertyValueFactory<>("dateEvent"));
        genre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGenre().getNomGenre()));
        sponsor.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getListSponsors().stream()
                        .map(Sponsors::getNomSponsor)
                        .collect(Collectors.joining(", "))
        ));

        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageColumn.setCellFactory(column -> new TableCell<>() {
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
                    imageView.setImage(image);
                    setGraphic(image != null ? imageView : null);
                }
            }
        });

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button qrCodeButton = new Button("QR Code");

            {
                editButton.setOnAction(e -> openEditDialog(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> deleteEvent(getTableView().getItems().get(getIndex())));
                qrCodeButton.setOnAction(e -> exportEventAsQRCode(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(20, editButton, deleteButton, qrCodeButton));
            }
        });
    }

    private void exportEventAsQRCode(Event event) {
        try {
            String eventInfo = "Description: " + event.getDescription() + "\nDate: " + event.getDateEvent();
            String url = TelegraphUploader.createTelegraphPage(event.getNomEvent(), eventInfo);

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 200, 200);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", os);
            Image fxImage = new Image(new ByteArrayInputStream(os.toByteArray()));

            ImageView qrCodeImageView = new ImageView(fxImage);
            qrCodeImageView.setFitWidth(200);
            qrCodeImageView.setFitHeight(200);

            Button downloadButton = new Button("Télécharger");
            downloadButton.setOnAction(e -> {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Sauvegarder le QR Code");
                fileChooser.setInitialFileName("EventQRCode_" + event.getId() + ".png");
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image PNG", "*.png"));
                File file = fileChooser.showSaveDialog(null);
                if (file != null) {
                    try {
                        ImageIO.write(MatrixToImageWriter.toBufferedImage(bitMatrix), "PNG", file);
                        new Alert(Alert.AlertType.INFORMATION, "QR Code téléchargé avec succès !").show();
                        openFileLocation(file.getAbsolutePath());
                    } catch (IOException ex) {
                        new Alert(Alert.AlertType.ERROR, "Erreur lors du téléchargement: " + ex.getMessage()).show();
                    }
                }
            });

            VBox vbox = new VBox(10, qrCodeImageView, downloadButton);
            vbox.setAlignment(Pos.CENTER);
            Scene scene = new Scene(vbox, 250, 300);
            Stage stage = new Stage();
            stage.setTitle("QR Code de l'Événement");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la génération du QR Code: " + e.getMessage()).show();
        }
    }

    private void openFileLocation(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    new ProcessBuilder("explorer.exe", "/select,", file.getAbsolutePath()).start();
                } else if (os.contains("mac")) {
                    new ProcessBuilder("open", "-R", file.getAbsolutePath()).start();
                } else {
                    new ProcessBuilder("xdg-open", file.getParent()).start();
                }
            }
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du dossier: " + e.getMessage()).show();
        }
    }

    private void loadEvents() {
        List<Event> events = eventService.listEvenets();
        if (events == null) {
            events = new ArrayList<>();
        }
        ObservableList<Event> observableList = FXCollections.observableArrayList(events);
        FilteredList<Event> filteredList = new FilteredList<>(observableList, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(event -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return event.getNomEvent().toLowerCase().contains(lowerCaseFilter) ||
                        event.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                        event.getGenre().getNomGenre().toLowerCase().contains(lowerCaseFilter) ||
                        event.getListSponsors().stream()
                                .map(Sponsors::getNomSponsor)
                                .collect(Collectors.joining(", "))
                                .toLowerCase().contains(lowerCaseFilter) ||
                        String.valueOf(event.getNbrMemebers()).contains(lowerCaseFilter) ||
                        String.valueOf(event.getPrix()).contains(lowerCaseFilter) ||
                        String.valueOf(event.getDateCreation()).contains(lowerCaseFilter) ||
                        String.valueOf(event.getDateEvent()).contains(lowerCaseFilter);
            });
        });

        SortedList<Event> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(eventTable.comparatorProperty());
        eventTable.setItems(sortedList);
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/evenement/addEventView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Événement");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadEvents();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire: " + e.getMessage()).show();
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
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Excel", "Excel", "PDF");
        dialog.setTitle("Exporter les Événements");
        dialog.setHeaderText("Choisissez un format d'exportation");
        dialog.setContentText("Format:");
        dialog.showAndWait().ifPresent(choice -> {
            if (choice.equals("Excel")) {
                exportToExcel();
            } else {
                exportToPDF();
            }
        });
    }

    private void exportToPDF() {
        try {
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            File file = new File(System.getProperty("user.home") + "/Documents/evenements.pdf");
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
            document.open();

            document.add(new com.itextpdf.text.Paragraph("Liste des Événements",
                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD)));
            document.add(new com.itextpdf.text.Paragraph(" "));

            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(8);
            table.setWidthPercentage(100);
            table.addCell("Nom");
            table.addCell("Description");
            table.addCell("Genre");
            table.addCell("Sponsors");
            table.addCell("Places");
            table.addCell("Prix");
            table.addCell("Date Création");
            table.addCell("Date Événement");

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
            openFileLocation(file.getAbsolutePath());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'exportation PDF: " + e.getMessage()).show();
        }
    }

    private void exportToExcel() {
        try (org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook()) {
            org.apache.poi.xssf.usermodel.XSSFSheet sheet = workbook.createSheet("Events");
            org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nom");
            header.createCell(1).setCellValue("Description");
            header.createCell(2).setCellValue("Genre");
            header.createCell(3).setCellValue("Sponsors");
            header.createCell(4).setCellValue("Places");
            header.createCell(5).setCellValue("Prix");
            header.createCell(6).setCellValue("Date Création");
            header.createCell(7).setCellValue("Date Événement");

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

            File file = new File(System.getProperty("user.home") + "/Documents/evenements.xlsx");
            try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            openFileLocation(file.getAbsolutePath());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'exportation Excel: " + e.getMessage()).show();
        }
    }

    private void openEditDialog(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/evenement/addEventView.fxml"));
            Parent root = loader.load();
            AddEventController editController = loader.getController();
            editController.setEventForEdit(event);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Événement");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadEvents();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire: " + e.getMessage()).show();
        }
    }

    private void deleteEvent(Event event) {
        try {
            eventService.deleteEvent(event.getId());
            loadEvents();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de la suppression: " + e.getMessage()).show();
        }
    }
}