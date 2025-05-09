package controllers;

import services.ChapitreService;
import services.CoursService;
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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class ChapitreControllerProf {

    @FXML
    private Label lblCoursTitle;

    @FXML
    private TableView<Chapitres> tableChapitres;
    @FXML
    private TableColumn<Chapitres, Number> colId;
    @FXML
    private TableColumn<Chapitres, String> colNom;
    @FXML
    private TableColumn<Chapitres, String> colContenuText;
    @FXML
    private TableColumn<Chapitres, String> colContenuFile;
    @FXML
    private TableColumn<Chapitres, Void> colAction;

    @FXML
    private TextField tfNom;
    @FXML
    private TextArea taContenuText;
    @FXML
    private TextField tfContenuFile;

    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private Button btnRetour;

    private final ChapitreService chapitreService = new ChapitreService();
    private ObservableList<Chapitres> chapitreList;
    private Chapitres selectedChapitre;
    private Cours currentCours;

    public void initData(Cours cours) {
        this.currentCours = cours;
        lblCoursTitle.setText("Gestion des chapitres - Cours: " + cours.getNom());
        loadChapitres();
    }

    @FXML
    public void initialize() {
        // Configuration des colonnes
//        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nomChapitre"));
        colContenuText.setCellValueFactory(new PropertyValueFactory<>("contenuText"));
//        colContenuFile.setCellValueFactory(new PropertyValueFactory<>("contenuFile"));

        // Ajout du bouton d'action pour les quiz
        addQuizButtonToTable();

        // Gestion du clic sur une ligne
        tableChapitres.setOnMouseClicked(this::handleRowClick);
    }

    private void loadChapitres() {
        if (currentCours != null) {
            chapitreList = FXCollections.observableArrayList(chapitreService.recuperer(currentCours));
            tableChapitres.setItems(chapitreList);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/QuizProfView.fxml"));
            Parent root = loader.load();

            QuizControllerProf controller = loader.getController();
            controller.initData(chapitre, currentCours);

            Scene scene = tableChapitres.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement de la vue des quiz");
        }
    }

    @FXML
    private void ajouterChapitre() {
        String nom = tfNom.getText().trim();
        String contenuText = taContenuText.getText().trim();

        if (nom.isEmpty() || nom.length() < 5 || nom.length() > 30) {
            showAlert("Le nom du chapitre doit contenir entre 5 et 30 caractères.");
            return;
        }

        if (contenuText.isEmpty()) {
            showAlert("Le contenu texte ne peut pas être vide.");
            return;
        }
        if (chapitreService.existeDeja(nom, currentCours)) {
            showAlert("Ce chapitre existe déjà dans ce cours.");
            return;
        }

        if (tfNom.getText().isEmpty()) {
            showAlert("Veuillez saisir un nom pour le chapitre");
            return;
        }

        Chapitres chapitre = new Chapitres();
        chapitre.setNomChapitre(tfNom.getText());
        chapitre.setContenuText(taContenuText.getText());
//        chapitre.setContenuFile(tfContenuFile.getText());
        chapitre.setCours(currentCours);

        chapitreService.ajouter(chapitre);
        loadChapitres();
        clearForm();
    }

    @FXML
    private void modifierChapitre() {
        if (selectedChapitre == null) {
            showAlert("Veuillez sélectionner un chapitre à modifier");
            return;
        }
        String nom = tfNom.getText().trim();
        String contenuText = taContenuText.getText().trim();

        if (nom.isEmpty() || nom.length() < 5 || nom.length() > 30) {
            showAlert("Le nom du chapitre doit contenir entre 5 et 30 caractères.");
            return;
        }
        if (!selectedChapitre.getNomChapitre().equals(nom)
                && chapitreService.existeDeja(nom, currentCours)) {
            showAlert("Un autre chapitre avec ce nom existe déjà dans ce cours.");
            return;
        }

        if (contenuText.isEmpty()) {
            showAlert("Le contenu texte ne peut pas être vide.");
            return;
        }


        selectedChapitre.setNomChapitre(tfNom.getText());
        selectedChapitre.setContenuText(taContenuText.getText());
//        selectedChapitre.setContenuFile(tfContenuFile.getText());

        chapitreService.modifier(selectedChapitre);
        loadChapitres();
        clearForm();
    }

    @FXML
    private void supprimerChapitre() {
        if (selectedChapitre == null) {
            showAlert("Veuillez sélectionner un chapitre à supprimer");
            return;
        }

        chapitreService.supprimer(selectedChapitre.getId());
        loadChapitres();
        clearForm();
    }

    @FXML
    private void retourALaListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CoursProfView.fxml"));
            Parent root = loader.load();

            Scene scene = btnRetour.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRowClick(MouseEvent event) {
        selectedChapitre = tableChapitres.getSelectionModel().getSelectedItem();
        if (selectedChapitre != null) {
            tfNom.setText(selectedChapitre.getNomChapitre());
            taContenuText.setText(selectedChapitre.getContenuText());
//            tfContenuFile.setText(selectedChapitre.getContenuFile());
        }
    }

    private void clearForm() {
        tfNom.clear();
        taContenuText.clear();
//        tfContenuFile.clear();
        selectedChapitre = null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
