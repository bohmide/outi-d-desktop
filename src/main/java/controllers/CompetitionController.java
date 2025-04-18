package controllers;
import entities.Organisation;
import javafx.scene.Parent;
import entities.Competition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import services.ServiceCompetition;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import java.io.IOException;
import java.time.LocalDate;
import javafx.application.Platform;
import services.ServiceOrganisation;
import services.ServiceOrganisation;
import java.io.File;
import java.awt.Desktop;

public class CompetitionController {

    @FXML private TableView<Competition> tableComps;
    @FXML private TableColumn<Competition, Integer> colId;
    @FXML private TableColumn<Competition, String> colNom;
    @FXML private TableColumn<Competition, String> colEntreprise;
    @FXML private TableColumn<Competition, LocalDate> colDebut;
    @FXML private TableColumn<Competition, LocalDate> colFin;
    @FXML private TableColumn<Competition, String> colDescription;
    @FXML private TableColumn<Competition, String> colFichier;
    @FXML private TableColumn<Competition, String> colOrganisation;


    private final ServiceOrganisation serviceOrganisation = new ServiceOrganisation();
    private ServiceCompetition sc = new ServiceCompetition();
    private ObservableList<Competition> compList;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomComp"));
        colEntreprise.setCellValueFactory(new PropertyValueFactory<>("nomEntreprise"));
        colDebut.setCellValueFactory(new PropertyValueFactory<>("dateDebut"));
        colFin.setCellValueFactory(new PropertyValueFactory<>("dateFin"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colFichier.setCellValueFactory(new PropertyValueFactory<>("fichier"));
        TableColumn<Competition, Void> actionsCol = new TableColumn<>("Actions");
        colOrganisation.setCellValueFactory(new PropertyValueFactory<>("Organisation"));

        compList = FXCollections.observableArrayList();
        tableComps.setItems(compList);
        rafraichirListeCompetitions();

        tableComps.setRowFactory(tv -> {
            TableRow<Competition> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Competition comp = row.getItem();
                    if (comp.getFichier() != null && !comp.getFichier().isEmpty()) {
                        openFile(new File("uploads/" + comp.getFichier()));
                    }
                }
            });
            return row;
        });
        actionsCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Réserver");

            {
                btn.setOnAction(event -> {
                    // Sélectionne la compétition correspondante à la ligne
                    Competition comp = getTableView().getItems().get(getIndex());
                    tableComps.getSelectionModel().select(comp);
                    // Appelle la méthode unique
                    reserverEquipe();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tableComps.getColumns().add(actionsCol);
    }
    private void openFile(File file) {
        try {
            if (!file.exists()) {
                showAlert("Erreur", "Fichier introuvable");
                return;
            }

            if (file.getName().toLowerCase().endsWith(".pdf")) {
                // Logique spécifique pour PDF
            }
            else if (file.getName().toLowerCase().endsWith(".png")) {
                // Logique spécifique pour PNG
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception e) {
            showAlert("Erreur", "Format non supporté ou application manquante");
        }
    }


    public void rafraichirListeCompetitions() {
        compList.setAll(sc.afficherCompetitions());
    }

    @FXML
    private void ajouterCompetition() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/addCompetitionForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter une compétition");
            stage.setScene(new Scene(root));
            //ki tetsakr l fenetre  on rafraîchit la liste
            stage.setOnHiding(event -> rafraichirListeCompetitions());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le formulaire: " + e.getMessage());
        }
    }

    @FXML
    private void modifierCompetition() {
        Competition selectedComp = tableComps.getSelectionModel().getSelectedItem();
        if (selectedComp != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/addCompetitionForm.fxml"));
                Parent root = loader.load();

                addCompetitionFormController formController = loader.getController();
                formController.setCompetitionData(selectedComp); // Cette ligne changera le texte du bouton

                Stage stage = new Stage();
                stage.setTitle("Modifier Compétition");
                stage.setScene(new Scene(root));
                stage.setOnHiding(event -> rafraichirListeCompetitions());
                stage.show();


            }  catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ouvrir le formulaire de modification");
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une compétition à modifier");
        }
    }

    @FXML
    public void supprimerCompetition() {
        Competition selectedComp = tableComps.getSelectionModel().getSelectedItem();
        if (selectedComp != null) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText("Supprimer la compétition ?");
            confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette compétition ?");
            confirm.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    sc.supprimerCompetition(selectedComp.getId());
                    rafraichirListeCompetitions();

                }
            });
        } else {
            showAlert("Sélection nécessaire", "Veuillez sélectionner une compétition à supprimer.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void reserverEquipe() {
        Competition competition = tableComps.getSelectionModel().getSelectedItem();

        if (competition == null) {
            showAlert("Erreur", "Veuillez sélectionner une compétition");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ReserverEquipeView.fxml"));
            Parent root = loader.load();

            ReserverEquipeController controller = loader.getController();
            controller.setCompetition(competition);

            Stage stage = new Stage();
            stage.setTitle("Réservation pour: " + competition.getNomComp());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de réservation");
        }
    }

}
