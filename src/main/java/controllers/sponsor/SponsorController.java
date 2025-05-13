package controllers.sponsor;


import entities.Sponsors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;



import services.SponsorsService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class SponsorController implements Initializable {

    @FXML private TableView<Sponsors> sponsorTable;
    @FXML private TableColumn<Sponsors, String> sponsorName;
    @FXML private TableColumn<Sponsors, String> description;
    @FXML private TableColumn<Sponsors, String> imageColumn;
    @FXML private TableColumn<Sponsors, LocalDate> dateCreation;
    @FXML private TableColumn<Sponsors, Void> actionColumn;
    @FXML private Button addGenre;
    @FXML private TextField searchField;

    private FilteredList<Sponsors> filteredSponsors;


    private SponsorsService sponsorService = new SponsorsService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sponsorName.setCellValueFactory(new PropertyValueFactory<>("nomSponsor"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        dateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        // Configuration de la colonne image
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageColumn.setCellFactory(col -> new TableCell<Sponsors, String>() {
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

        // Construction de la colonne d'actions
        actionColumn.setCellFactory(col -> new TableCell<Sponsors,Void>() {
            private final Button btnMod = new Button("Modifier");
            private final Button btnSup = new Button("Supprimer");
            {
                btnMod.setOnAction(e -> openModifier(getTableView().getItems().get(getIndex())));
                btnSup.setOnAction(e -> supprimer(getTableView().getItems().get(getIndex())));
                btnMod.setStyle("-fx-background-radius:5");
                btnSup.setStyle("-fx-background-radius:5");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox h = new HBox(5, btnMod, btnSup);
                    setGraphic(h);
                }
            }
        });

        loadSponsors();
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

        // Essai 2: Chargement direct depuis le système de fichiers (pour le développement)
        try {
            Path path = Paths.get("src/main/resources" + imagePath);
            if (Files.exists(path)) {
                return new Image(path.toUri().toString());
            }
        } catch (Exception e) {
            System.err.println("Second try failed for: " + imagePath);
        }

        // Essai 3: Attendre et réessayer (pour les systèmes lents)
        try {
            Thread.sleep(200);
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("Third try failed for: " + imagePath);
        }

        return null;
    }

    private void loadSponsors() {
        List<Sponsors> list = sponsorService.listSponsor();
        filteredSponsors = new FilteredList<>(FXCollections.observableArrayList(list), p -> true);

        // Lier la recherche dynamique
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredSponsors.setPredicate(sponsor -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();

                return sponsor.getNomSponsor().toLowerCase().contains(lower)
                        || sponsor.getDescription().toLowerCase().contains(lower);
            });
        });

        SortedList<Sponsors> sortedData = new SortedList<>(filteredSponsors);
        sortedData.comparatorProperty().bind(sponsorTable.comparatorProperty());
        sponsorTable.setItems(sortedData);
        sponsorTable.refresh();
    }


    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sponsor/addSponsorView.fxml"));
            Parent root = loader.load();

            Stage st = new Stage();
            st.setTitle("Ajouter un Sponsor");
            st.initModality(Modality.APPLICATION_MODAL);
            st.setScene(new Scene(root));

            st.setOnHidden(e -> loadSponsors());
            st.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleRetour() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/eventView.fxml"));
        Parent root = loader.load();
        searchField.getScene().setRoot(root);
    }


    private void openModifier(Sponsors sp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sponsor/addSponsorView.fxml"));
            Parent root = loader.load();

            AddSponsor ctrl = loader.getController();
            ctrl.setSponsor(sp);

            Stage st = new Stage();
            st.setTitle("Modifier Sponsor");
            st.initModality(Modality.APPLICATION_MODAL);
            st.setScene(new Scene(root));

            st.setOnHidden(e -> loadSponsors());
            st.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void supprimer(Sponsors sp) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
                "Cette action est irréversible.", ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText("Supprimer le sponsor \""+sp.getNomSponsor()+"\" ?");
        a.showAndWait().ifPresent(bt -> {
            if (bt==ButtonType.OK) {
                sponsorService.deleteSponsor(sp);
                loadSponsors();
            }
        });
    }
}