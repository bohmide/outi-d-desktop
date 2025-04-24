package controllers;

import entities.Cours;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.TableCell;
import Services.CoursService;

import java.io.IOException;

public class CoursControllerEtudiant {

    @FXML
    private TableView<Cours> tableCours;

    @FXML
    private TableColumn<Cours, String> colNom;

    @FXML
    private TableColumn<Cours, String> colDescription;

    @FXML
    private TableColumn<Cours, Void> colAction;

    private final CoursService coursService = new CoursService();
    private ObservableList<Cours> coursList;

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Charger les données
        loadCours();

        // Ajouter le bouton "Voir chapitres" dans chaque ligne
        ajouterColonneAction();
    }

    private void loadCours() {
        coursList = FXCollections.observableArrayList(coursService.recuperer());
        tableCours.setItems(coursList);
    }

    private void ajouterColonneAction() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Voir les chapitres");

            {
                btn.setOnAction(event -> {
                    Cours cours = getTableView().getItems().get(getIndex());
                    navigateToChapitres(cours);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void navigateToChapitres(Cours cours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChapitreEtudiantView.fxml"));
            Parent root = loader.load();

            ChapitreControllerEtudiant controller = loader.getController();
            controller.initData(cours);

            Scene scene = tableCours.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement des chapitres.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void retourAccueil(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("views/MainAppAccueil.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 400, 200));
            stage.setTitle("OUTI-D - Choisissez votre rôle");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
