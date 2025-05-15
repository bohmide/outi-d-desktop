package controllers.evenement;

import entities.Event;
import entities.Sponsors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.EventService;
import utils.CurrencyUtil;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import services.EventService;

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
    @FXML private HBox loadingBox;
    @FXML private ProgressIndicator loadingIndicator;

    private final EventService eventService = new EventService();
    @FXML private Button returnButton;


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
        try {
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("First try failed for: " + imagePath);
        }
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
        prix.setCellValueFactory(new PropertyValueFactory<>("prix"));
        prix.setCellFactory(column -> new TableCell<Event, Number>() {
            @Override
            protected void updateItem(Number price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    String currencySymbol = deviseLabel.getText().split(" ")[0].replace("Devise:", "");
                    setText(String.format("%.2f %s",
                            CurrencyUtil.convertFromTND(price.doubleValue(), CurrencyUtil.getCurrencyFromIP()),
                            currencySymbol));
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
                    imageView.setImage(image);
                    setGraphic(image != null ? imageView : null);
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
                    // exportEventAsQRCode(selectedEvent);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(20, editButton, deleteButton, qrCodeButton);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        };
    }

    private void loadEvents() {
        try {
            List<Event> events = eventService.listEvenets();
            System.out.println("Events retrieved: " + (events != null ? events.size() : "null"));
            if (events == null) {
                events = Collections.emptyList();
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
                                    .anyMatch(s -> s.toLowerCase().contains(lowerCaseFilter)) ||
                            String.valueOf(event.getNbrMemebers()).contains(lowerCaseFilter) ||
                            String.valueOf(event.getPrix()).contains(lowerCaseFilter) ||
                            String.valueOf(event.getDateCreation()).contains(lowerCaseFilter) ||
                            String.valueOf(event.getDateEvent()).contains(lowerCaseFilter);
                });
            });

            SortedList<Event> sortedList = new SortedList<>(filteredList);
            sortedList.comparatorProperty().bind(eventTable.comparatorProperty());
            eventTable.setItems(sortedList);
            eventTable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to load events: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addEventView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Événement");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            loadEvents();
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
            File file = new File(System.getProperty("user.home") + "/Documents/evenements.pdf");
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Liste des Événements");
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
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
            System.out.println("PDF file saved to: " + file.getAbsolutePath());
            openFileLocation(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
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
                        event.getListSponsors().stream().map(Sponsors::getNomSponsor).collect(Collectors.joining(", ")));
                row.createCell(4).setCellValue(event.getNbrMemebers());
                row.createCell(5).setCellValue(event.getPrix());
                row.createCell(6).setCellValue(event.getDateCreation().toString());
                row.createCell(7).setCellValue(event.getDateEvent().toString());
            }
            File file = new File(System.getProperty("user.home") + "/Documents/evenements.xlsx");
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                workbook.write(outputStream);
            }
            System.out.println("Excel file saved to: " + file.getAbsolutePath());
            openFileLocation(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openEditDialog(Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addEventView.fxml"));
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
            e.printStackTrace();
        }
    }

    private void deleteEvent(Event event) {
        eventService.deleteEvent(event.getId());
        loadEvents();
    }

    public void openFileLocation(String path) {
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
            } else {
                System.err.println("File does not exist: " + path);
            }
        } catch (IOException e) {
            e.printStackTrace();
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