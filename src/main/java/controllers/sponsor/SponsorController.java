package controllers.sponsor;

import controllers.tests.AddSponsorController;
import entities.Sponsors;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.SponsorsService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class SponsorController implements Initializable {

    @FXML private TableView<Sponsors> sponsorTable;
    @FXML private TableColumn<Sponsors, String> sponsorName;
    @FXML private TableColumn<Sponsors, String> description;
    @FXML private TableColumn<Sponsors, String> imagePath;
    @FXML private TableColumn<Sponsors, LocalDate> dateCreation;
    @FXML private TableColumn<Sponsors, Void> actionColumn; // <--- uniquement pour le cellFactory
    @FXML private Button addGenre;

    private SponsorsService sponsorService = new SponsorsService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sponsorName.setCellValueFactory(new PropertyValueFactory<>("nomSponsor"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        imagePath.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        dateCreation.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));

        // Construction de la colonne d’actions
        actionColumn.setCellFactory(col -> new TableCell<Sponsors,Void>() {
            private final Button btnMod = new Button("Modifier");
            private final Button btnSup = new Button("Supprimer");
            {
                btnMod.setOnAction(e -> openModifier(getTableView().getItems().get(getIndex())));
                btnSup.setOnAction(e -> supprimer(getTableView().getItems().get(getIndex())));
                // Style ou espacement si besoin :
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

    private void loadSponsors() {
        List<Sponsors> list = sponsorService.listSponsor();
        sponsorTable.setItems(FXCollections.observableArrayList(list));
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sponsor/addSponsorView.fxml"));
            Parent root = loader.load();
            // Aucun setSponsor, on est en mode “ajout”
            Stage st = new Stage();
            st.setTitle("Ajouter un Sponsor");
            st.initModality(Modality.APPLICATION_MODAL);
            st.setScene(new Scene(root));
            st.showAndWait();
            loadSponsors();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openModifier(Sponsors sp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/sponsor/addSponsorView.fxml"));
            Parent root = loader.load();
            AddSponsor ctrl = loader.getController();
            ctrl.setSponsor(sp);  // pré-remplir le formulaire
            Stage st = new Stage();
            st.setTitle("Modifier Sponsor");
            st.initModality(Modality.APPLICATION_MODAL);
            st.setScene(new Scene(root));
            st.showAndWait();
            loadSponsors();
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
