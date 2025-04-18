package controllers;

import Services.CoursService;
import entities.Cours;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.util.Callback;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

public class CoursControllerProf {

    @FXML
    private TableView<Cours> tableCours;
    @FXML
    private TableColumn<Cours, Number> colId;
    @FXML
    private TableColumn<Cours, String> colNom;
    @FXML
    private TableColumn<Cours, Date> colDate;
    @FXML
    private TableColumn<Cours, String> colEtat;

    @FXML
    private TextField tfNom;
    @FXML
    private DatePicker dpDate;
    @FXML
    private ComboBox<String> comboEtat;

    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnModifier;
    @FXML
    private Button btnSupprimer;
    @FXML
    private TableColumn<Cours, Void> colAction;

    private final CoursService coursService = new CoursService();
    private ObservableList<Cours> coursList;

    private Cours selectedCours;

    @FXML
    public void initialize() {
        comboEtat.setItems(FXCollections.observableArrayList("Facile", "Moyen", "Avancé"));

//        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("dateCreation"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        addButtonToTable();

        loadCours();
        tableCours.setOnMouseClicked(this::handleRowClick);
    }

    private void loadCours() {
        coursList = FXCollections.observableArrayList(coursService.recuperer());
        tableCours.setItems(coursList);
    }

    private void clearForm() {
        tfNom.clear();
        dpDate.setValue(null);
        comboEtat.setValue(null);
        selectedCours = null;
    }

    @FXML
    private void ajouterCours() {
        String nom = tfNom.getText().trim();
        String etat = comboEtat.getValue();
        LocalDate date = dpDate.getValue();
        // Contrôle
        if (nom.isEmpty() || date == null || etat == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }
        //controle
        if (nom.length() < 5 || nom.length() > 20) {
            showAlert("Le nom du cours doit contenir entre 5 et 20 caractères.");
            return;
        }

        // Contrôle
        if (!comboEtat.getItems().contains(etat)) {
            showAlert("État invalide. Veuillez choisir une valeur parmi : Facile, Moyen, Avancé.");
            return;
        }

        // Contrôle
        boolean coursExiste = coursList.stream().anyMatch(c -> c.getNom().equalsIgnoreCase(nom));
        if (coursExiste) {
            showAlert("Un cours avec ce nom existe déjà !");
            return;
        }

        if (tfNom.getText().isEmpty() || dpDate.getValue() == null || comboEtat.getValue() == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        Cours c = new Cours(
                tfNom.getText(),
                java.sql.Date.valueOf(dpDate.getValue()),
                comboEtat.getValue()
        );
        coursService.ajouter(c);
        loadCours();
        clearForm();
    }

    @FXML
    private void modifierCours() {
        if (selectedCours == null) {
            showAlert("Veuillez sélectionner un cours à modifier.");
            return;
        }
        String nom = tfNom.getText().trim();
        String etat = comboEtat.getValue();
        LocalDate date = dpDate.getValue();

        // Contrôle : Champs obligatoires
        if (nom.isEmpty() || date == null || etat == null) {
            showAlert("Veuillez remplir tous les champs.");
            return;
        }

        // Contrôle : Longueur du nom
        if (nom.length() < 5 || nom.length() > 20) {
            showAlert("Le nom du cours doit contenir entre 5 et 20 caractères.");
            return;
        }

        // Contrôle : État valide
        if (!comboEtat.getItems().contains(etat)) {
            showAlert("État invalide. Veuillez choisir une valeur parmi : Facile, Moyen, Avancé.");
            return;
        }

        // Contrôle : Nom déjà utilisé par un autre cours
        boolean nomUtilise = coursList.stream()
                .anyMatch(c -> c.getNom().equalsIgnoreCase(nom) && c.getId() != selectedCours.getId());
        if (nomUtilise) {
            showAlert("Un autre cours avec ce nom existe déjà !");
            return;
        }
        selectedCours.setNom(tfNom.getText());
        selectedCours.setDateCreation(java.sql.Date.valueOf(dpDate.getValue()));
        selectedCours.setEtat(comboEtat.getValue());

        coursService.modifier(selectedCours);
        loadCours();
        clearForm();
    }

    @FXML
    private void supprimerCours() {
        if (selectedCours == null) {
            showAlert("Veuillez sélectionner un cours à supprimer.");
            return;
        }

        coursService.supprimer(selectedCours.getId());
        loadCours();
        clearForm();
    }

    private void handleRowClick(MouseEvent event) {
        selectedCours = tableCours.getSelectionModel().getSelectedItem();
        if (selectedCours != null) {
            tfNom.setText(selectedCours.getNom());
            dpDate.setValue(new java.sql.Date(selectedCours.getDateCreation().getTime()).toLocalDate());
            comboEtat.setValue(selectedCours.getEtat());
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Attention");
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void addButtonToTable() {
        Callback<TableColumn<Cours, Void>, TableCell<Cours, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Cours, Void> call(final TableColumn<Cours, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Voir Chapitres");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Cours cours = getTableView().getItems().get(getIndex());
                            navigateToChapitres(cours);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        };
        colAction.setCellFactory(cellFactory);
    }
    private void navigateToChapitres(Cours cours) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ChapitreView.fxml"));
            Parent root = loader.load();

            // Passez le cours sélectionné au contrôleur des chapitres
            ChapitreControllerProf controller = loader.getController();
            controller.initData(cours);

            // Obtenez la scène actuelle et changez sa racine
            Scene scene = tableCours.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur lors du chargement de la vue des chapitres");
        }
    }
}