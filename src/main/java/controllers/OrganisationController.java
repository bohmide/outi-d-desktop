package controllers;


import entities.Organisation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // <-- Import manquant
import javafx.scene.Parent;   // <-- Import souvent nécessaire avec FXMLLoader
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import services.ServiceOrganisation;
import java.io.IOException;

public class OrganisationController {

    @FXML private TableView<Organisation> tableOrgs;
    @FXML private TableColumn<Organisation, Integer> colOrgId;
    @FXML private TableColumn<Organisation, String> colOrgNom;
    @FXML private TableColumn<Organisation, String> colOrgDomaine;

    private ServiceOrganisation so = new ServiceOrganisation();
    private ObservableList<Organisation> orgList;
    private CompetitionController competitionController;
    @FXML
    public void initialize() {
        colOrgId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colOrgNom.setCellValueFactory(new PropertyValueFactory<>("nomOrganisation"));
        colOrgDomaine.setCellValueFactory(new PropertyValueFactory<>("domaine"));

        orgList = FXCollections.observableArrayList();
        tableOrgs.setItems(orgList);
        rafraichirListeOrganisations();
    }
    public void setCompetitionController(CompetitionController competitionController) {
        this.competitionController = competitionController;
    }
    private void rafraichirListeOrganisations() {
        orgList.setAll(so.afficherOrganisations());
    }

    @FXML
    public void ajouterOrganisation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addOrganisationForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Organisation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            rafraichirListeOrganisations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void modifierOrganisation() {
        Organisation selected = tableOrgs.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addOrganisationForm.fxml"));
                Parent root = loader.load();

                addOrganisationFormController controller = loader.getController();
                controller.setOrganisationData(selected);

                Stage stage = new Stage();
                stage.setTitle("Modifier Organisation");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                rafraichirListeOrganisations();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Sélection nécessaire", "Veuillez sélectionner une organisation");
        }
    }
    @FXML
    public void supprimerOrganisation() {
        Organisation selectedOrg = tableOrgs.getSelectionModel().getSelectedItem();
        if (selectedOrg != null) {
            so.supprimerOrganisation(selectedOrg.getId());
            orgList.setAll(so.afficherOrganisations());
            rafraichirListeOrganisations();

            // Rafraîchit aussi la liste des compétitions si le contrôleur est défini
            if (competitionController != null) {
                competitionController.rafraichirListeCompetitions();
            }

        } else {
            showAlert("Sélection nécessaire", "Veuillez sélectionner une organisation à supprimer.");
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
