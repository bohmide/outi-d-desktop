package controllers;

import dao.CoursDAO;
import entities.Cours;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

public class CoursControllerProf {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Cours> tableCours;
    @FXML
    private TableColumn<Cours, Integer> colId;
    @FXML
    private TableColumn<Cours, String> colNom;
    @FXML
    private TableColumn<Cours, String> colDate;
    @FXML
    private TableColumn<Cours, String> colEtat;
    @FXML
    private TextField txtNom;
    @FXML
    private ComboBox<String> comboEtat;
    @FXML
    private DatePicker datePicker;

    private final ObservableList<Cours> coursList = FXCollections.observableArrayList();
    private final CoursDAO coursDAO = new CoursDAO();
    private Cours coursSelectionne = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colDate.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateCreation() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate localDate = cellData.getValue().getDateCreation().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                return new SimpleStringProperty(formatter.format(localDate));
            } else {
                return new SimpleStringProperty("N/A");
            }
        });
        colEtat.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEtat()));

        tableCours.setOnMouseClicked(event -> selectionnerCours());

        // 👉 Date par défaut = aujourd'hui
        datePicker.setValue(LocalDate.now());

        chargerCoursDepuisBDD();
    }

    @FXML
    private void chargerCoursDepuisBDD() {
        coursList.clear();
        var coursRecuperes = coursDAO.getAllCours();
        System.out.println("Cours récupérés depuis la BDD : " + coursRecuperes.size());
        coursList.addAll(coursRecuperes);
        tableCours.setItems(coursList);
    }

    private void selectionnerCours() {
        coursSelectionne = tableCours.getSelectionModel().getSelectedItem();
        if (coursSelectionne != null) {
            txtNom.setText(coursSelectionne.getNom());
            comboEtat.setValue(coursSelectionne.getEtat());

            // conversion Date -> LocalDate
            if (coursSelectionne.getDateCreation() != null) {
                LocalDate localDate = coursSelectionne.getDateCreation().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                datePicker.setValue(localDate);
            } else {
                datePicker.setValue(LocalDate.now());
            }
        }
    }

    @FXML
    private void ajouterCours() {
        String nom = txtNom.getText();
        String etat = comboEtat.getValue();
        LocalDate localDate = datePicker.getValue();

        if (nom.isEmpty() || etat == null || localDate == null) {
            showAlert("Erreur", "Tous les champs doivent être remplis.", Alert.AlertType.ERROR);
            return;
        }

        Date dateCreation = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        Cours cours = new Cours(nom, dateCreation, etat);
        coursDAO.ajouterCours(cours);
        System.out.println("Cours ajouté : " + cours);
        chargerCoursDepuisBDD();
        clearFields();
        showAlert("Succès", "Cours ajouté avec succès !", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void modifierCours() {
        if (coursSelectionne != null) {
            try {
                coursSelectionne.setNom(txtNom.getText().trim());
                coursSelectionne.setEtat(comboEtat.getValue());
                LocalDate localDate = datePicker.getValue();
                coursSelectionne.setDateCreation(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

                coursDAO.updateCours(coursSelectionne);
                System.out.println("Cours modifié : " + coursSelectionne);
                chargerCoursDepuisBDD();
                clearFields();
                showAlert("Succès", "Cours modifié avec succès.", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Problème lors de la modification du cours.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un cours à modifier.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void supprimerCours() {
        if (coursSelectionne != null) {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirmation de suppression");
            confirmation.setHeaderText("Supprimer le cours");
            confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le cours \"" + coursSelectionne.getNom() + "\" ?");

            Optional<ButtonType> result = confirmation.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    coursDAO.deleteCours(coursSelectionne.getId());
                    chargerCoursDepuisBDD();
                    clearFields();
                    showAlert("Suppression réussie", "Le cours a été supprimé avec succès.", Alert.AlertType.INFORMATION);
                } catch (Exception e) {
                    showAlert("Erreur", "Problème lors de la suppression du cours.", Alert.AlertType.ERROR);
                }
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un cours à supprimer.", Alert.AlertType.WARNING);
        }
    }

    private void clearFields() {
        txtNom.clear();
        comboEtat.setValue(null);
        datePicker.setValue(LocalDate.now());
        tableCours.getSelectionModel().clearSelection();
        coursSelectionne = null;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
