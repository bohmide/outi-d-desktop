package controllers;

import entities.Competition;
import entities.Organisation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ServiceCompetition;
import services.ServiceOrganisation;
import javafx.scene.layout.AnchorPane;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.layout.TilePane; // Ajoutez cet import


public class CompetitionController {

    @FXML private TilePane cardsContainer;

    private final ServiceCompetition sc = new ServiceCompetition();
    private final ServiceOrganisation serviceOrganisation = new ServiceOrganisation();
    private ObservableList<Competition> compList;
    private EquipeController equipeController;

    @FXML
    public void initialize() {
        compList = FXCollections.observableArrayList();
        rafraichirListeCompetitions();
    }

    public void rafraichirListeCompetitions() {
        try {
            List<Competition> competitions = sc.afficherCompetitions();
            cardsContainer.getChildren().clear();

            // Dans la méthode rafraichirListeCompetitions()
            for (Competition comp : competitions) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CompetitionCard.fxml"));
                VBox card = loader.load();
                CompetitionCardController cardController = loader.getController();
                cardController.setCompetition(comp, this);

                // Appliquez le style CSS directement ou via une classe
                card.getStyleClass().add("competition-card");
                card.setMaxWidth(300); // Limitez la largeur maximale
                cardsContainer.getChildren().add(card);
            }
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger les cartes de compétition");
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouterCompetition() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addCompetitionForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une compétition");
            stage.setScene(new Scene(root));
            stage.setOnHiding(event -> rafraichirListeCompetitions());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le formulaire: " + e.getMessage());
        }
    }

    public void modifierCompetition(Competition selectedComp) {
        if (selectedComp != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addCompetitionForm.fxml"));
                Parent root = loader.load();

                addCompetitionFormController formController = loader.getController();
                formController.setCompetitionData(selectedComp);

                Stage stage = new Stage();
                stage.setTitle("Modifier Compétition");
                stage.setScene(new Scene(root));
                stage.setOnHiding(event -> rafraichirListeCompetitions());
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir le formulaire de modification");
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une compétition à modifier");
        }
    }

    public void supprimerCompetition(Competition comp) {
        if (comp != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer la compétition et toutes ses équipes ?");
            confirm.setContentText("Cette action est irréversible. Continuer ?");

            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        sc.supprimerCompetition(comp.getId());
                        rafraichirListeCompetitions();
                        showAlert("Succès", "Compétition supprimée avec succès");
                        if (equipeController != null) {
                            equipeController.refreshEquipeList();
                        }
                    } catch (Exception e) {
                        showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                    }
                }
            });
        } else {
            showAlert("Erreur", "Compétition non définie");
        }
    }

    public void reserverEquipe(Competition comp) {
        if (comp == null) {
            showAlert("Erreur", "Veuillez sélectionner une compétition");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReserverEquipeView.fxml"));
            Parent root = loader.load();

            ReserverEquipeController controller = loader.getController();
            controller.setCompetition(comp);
            controller.setOnReservationComplete(this::rafraichirListeCompetitions);

            Stage stage = new Stage();
            stage.setTitle("Réserver pour: " + comp.getNomComp());
            stage.setScene(new Scene(root));
            stage.setOnHidden(e -> rafraichirListeCompetitions());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la réservation");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setEquipeController(EquipeController equipeController) {
        this.equipeController = equipeController;
    }
}