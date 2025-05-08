package controllers;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import entities.Equipe;
import interfaces.IServiceEquipe;
import services.ServiceCompetition;
import services.ServiceEquipe;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;  // Ajoutez cette ligne


public class EquipeController {
    @FXML private TextField nomEquipeField;
    @FXML private TextField ambassadeurField;
    @FXML private TextArea membresField;
    @FXML private ListView<Equipe> equipeListView;

    @FXML private TableView<Equipe> equipeTableView;
    @FXML private TableColumn<Equipe, String> nomCol;
    @FXML private TableColumn<Equipe, String> ambassadeurCol;
    @FXML private TableColumn<Equipe, String> membresCol;
    @FXML private TableColumn<Equipe, String> travailCol;
    @FXML private TableColumn<Equipe, String> competitionCol;


    private ServiceCompetition sc = new ServiceCompetition();
    private IServiceEquipe service = new ServiceEquipe();

    // Déclare une ObservableList pour les équipes
    private ObservableList<Equipe> equipesList;

    @FXML
    public void initialize() {
        equipesList = FXCollections.observableArrayList();
        equipeTableView.setItems(equipesList);

        // Lier les colonnes aux propriétés
        nomCol.setCellValueFactory(cellData -> cellData.getValue().nomEquipeProperty());
        ambassadeurCol.setCellValueFactory(cellData -> cellData.getValue().ambassadeurProperty());
        membresCol.setCellValueFactory(cellData -> cellData.getValue().membresProperty());
        travailCol.setCellValueFactory(cellData -> cellData.getValue().travailProperty());
        competitionCol.setCellValueFactory(cellData -> cellData.getValue().competitionNomProperty());

        refreshEquipeList();
    }


    @FXML
    private void handleAjouterEquipe() {
        // Validation des champs
        String nomEquipe = nomEquipeField.getText().trim();
        String ambassadeur = ambassadeurField.getText().trim();
        String membres = membresField.getText().trim();

        if (nomEquipe.isEmpty() || ambassadeur.isEmpty()) {
            showAlert("Erreur", "Le nom de l'équipe et l'ambassadeur sont obligatoires");
            return;
        }

        // Création de l'équipe
        Equipe equipe = new Equipe();
        equipe.setNomEquipe(nomEquipe);
        equipe.setAmbassadeur(ambassadeur);
        equipe.setMembres(membres.isEmpty() ? "Aucun membre spécifié" : membres);
        equipe.setTravail("Non spécifié"); // Valeur par défaut comme dans ReserverEquipeController

        // Ajout de l'équiper
        int equipeId = service.ajouterEquipe(equipe);

        if (equipeId > 0) {
            showAlert("Succès", "Équipe ajoutée avec succès !");
            refreshEquipeList();
            clearFields();
        } else {
            showAlert("Erreur", "Échec de l'ajout de l'équipe");
        }
    }

    @FXML
    private void handleSupprimerEquipe() {
        Equipe selected = equipeTableView.getSelectionModel().getSelectedItem();

        if (selected != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation");
            confirmation.setHeaderText("Supprimer l'équipe");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer '" + selected.getNomEquipe() + "' ?");

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    service.supprimerEquipe(selected.getId());
                    refreshEquipeList();
                }
            });
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une équipe à supprimer");
        }
    }

    public void refreshEquipeList() {
        equipesList.setAll(service.afficherToutesEquipes());
        equipeTableView.refresh();

    }

    private void clearFields() {
        nomEquipeField.clear();
        ambassadeurField.clear();
        membresField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
