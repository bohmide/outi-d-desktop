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
import javafx.scene.layout.TilePane; // Ajoutez cet import
import java.util.List;
import controllers.addOrganisationFormController; // ou le bon chemin selon votre package

public class OrganisationController {


    @FXML private TilePane cardsContainer;

    private ServiceOrganisation so = new ServiceOrganisation();
  //  private ObservableList<Organisation> orgList;
    private CompetitionController competitionController;
    private EquipeController equipeController;

    @FXML
    public void initialize() {




        rafraichirListeOrganisations();
    }
    public void setCompetitionController(CompetitionController competitionController) {
        this.competitionController = competitionController;
    }
    public void rafraichirListeOrganisations() {
        cardsContainer.getChildren().clear();
        List<Organisation> organisations = so.afficherOrganisations();

        try {
            for (Organisation organisation : organisations) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/OrganisationCard.fxml"));
                Parent card = loader.load();
                OrganisationCardController controller = loader.getController();
                controller.setOrganisationData(organisation);
                controller.setParentController(this);
                cardsContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void ajouterOrganisation() {
        System.out.println("orgqnisqtion");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addOrganisationForm.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter Organisation");
            stage.setScene(new Scene(root,400,400));

            stage.showAndWait();
            rafraichirListeOrganisations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void modifierOrganisation(Organisation organisation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addOrganisationForm.fxml"));
            Parent root = loader.load();

            addOrganisationFormController controller = loader.getController();
            controller.setOrganisationData(organisation);

            Stage stage = new Stage();
            stage.setTitle("Modifier Organisation");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            rafraichirListeOrganisations();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void supprimerOrganisation(Organisation organisation) {
        so.supprimerOrganisation(organisation.getId());
        rafraichirListeOrganisations();

        // Rafraîchit aussi la liste des compétitions si le contrôleur est défini
        if (competitionController != null) {
            competitionController.rafraichirListeCompetitions();
        }
        if (equipeController != null) {
            equipeController.refreshEquipeList();
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
