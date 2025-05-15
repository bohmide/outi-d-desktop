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
import javafx.scene.control.TextField;
import javafx.collections.transformation.FilteredList;
import javafx.stage.StageStyle; // <-- Ajoutez cette ligne

public class CompetitionController {

    @FXML private TilePane cardsContainer;
    @FXML private TextField searchField;

    private final ServiceCompetition sc = new ServiceCompetition();
    private final ServiceOrganisation serviceOrganisation = new ServiceOrganisation();
    private ObservableList<Competition> compList;
    private EquipeController equipeController;
    private FilteredList<Competition> filteredComps;


    @FXML
    public void initialize() {
        compList = FXCollections.observableArrayList();
        filteredComps = new FilteredList<>(compList);

        // Configuration de la recherche
        setupSearchFilter();
        rafraichirListeCompetitions();
    }
    private void setupSearchFilter() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredComps.setPredicate(competition -> {
                if (newVal == null || newVal.isEmpty()) return true;

                String lowerCaseFilter = newVal.toLowerCase();

                return matchesField(competition.getNomComp(), lowerCaseFilter) ||
                        matchesField(competition.getNomOrganisation(), lowerCaseFilter) ||
                        matchesField(competition.getDescription(), lowerCaseFilter) ||
                        matchesDate(competition.getDateDebut(), lowerCaseFilter) ||
                        matchesDate(competition.getDateFin(), lowerCaseFilter) ||
                        matchesField(competition.getFichier(), lowerCaseFilter);
            });
            updateCompetitionCards();
        });
    }

    private boolean matchesField(String value, String filter) {
        return value != null && value.toLowerCase().contains(filter);
    }

    private boolean matchesDate(LocalDate date, String filter) {
        return date != null && date.toString().contains(filter);
    }


    public void rafraichirListeCompetitions() {
        try {
            List<Competition> competitions = sc.afficherCompetitions();
            compList.setAll(competitions); // Mise à jour de la liste observable
            updateCompetitionCards(); // Appel unique pour la mise à jour des cartes
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les compétitions");
            e.printStackTrace();
        }
    }

    private void updateCompetitionCards() {
        cardsContainer.getChildren().clear();
        for (Competition comp : filteredComps) {
            try {
                //FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CompetitionCard.fxml"));
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CompetitionCardEtudiant.fxml"));

                VBox card = loader.load();
                CompetitionCardController cardController = loader.getController();
                cardController.setCompetition(comp, this);
                card.getStyleClass().add("competition-card");
                card.setMaxWidth(300);
                cardsContainer.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors du chargement d'une carte");
            }
        }
    }


    @FXML
    private void ajouterCompetition() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addCompetitionForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une compétition");
            Scene scene = new Scene(root, 900, 700); // Augmentez 600 et réduisez 400 selon vos besoins
            stage.setScene(scene);
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

    public void showTopOrganisations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/TopOrganisationsView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Statistiques des organisations");
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.UTILITY);
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'afficher les statistiques");
        }
    }

    @FXML
    private void showCompetitionCalendar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CalendarView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Météo des Compétitions");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le calendrier");
        }
    }

}