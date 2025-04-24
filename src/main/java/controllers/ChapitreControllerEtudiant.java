package controllers;

import Services.ChapitreService;
import entities.Chapitres;
import entities.Cours;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

public class ChapitreControllerEtudiant {

    @FXML
    private Label lblCoursTitle;
    @FXML
    private TableView<Chapitres> tableChapitres;
    @FXML
    private TableColumn<Chapitres, String> colNom;
    @FXML
    private TableColumn<Chapitres, String> colContenuText;
    @FXML
    private TableColumn<Chapitres, Void> colAction;

    @FXML
    private Button btnRetour;

    private final ChapitreService chapitreService = new ChapitreService();
    private Cours currentCours;

    public void initData(Cours cours) {
        this.currentCours = cours;
        lblCoursTitle.setText("Chapitres du cours : " + cours.getNom());
        loadChapitres();
    }

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomChapitre"));
        colContenuText.setCellValueFactory(new PropertyValueFactory<>("contenuText"));
        addQuizButtonToTable();
    }

    private void loadChapitres() {
        if (currentCours != null) {
            ObservableList<Chapitres> chapitres = FXCollections.observableArrayList(chapitreService.recuperer(currentCours));
            tableChapitres.setItems(chapitres);
        }
    }

    private void addQuizButtonToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Voir Quiz");

            {
                btn.setOnAction(event -> {
                    Chapitres chapitre = getTableView().getItems().get(getIndex());
                    navigateToQuiz(chapitre);
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

    private void navigateToQuiz(Chapitres chapitre) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuizViewEtudiant.fxml"));
            Parent root = loader.load();

            QuizControllerEtudiant controller = loader.getController();
            controller.initData(chapitre, currentCours);

            Scene scene = tableChapitres.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement du quiz.");
        }
    }

    @FXML
    private void retourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CoursViewEtudiant.fxml"));
            Parent root = loader.load();
            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
