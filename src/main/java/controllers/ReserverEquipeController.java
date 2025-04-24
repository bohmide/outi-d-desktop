package controllers;

import entities.Competition;
import entities.Equipe;
import entities.Competition_Equipe;
import interfaces.IServiceEquipe;
import services.ServiceEquipe;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;


public class ReserverEquipeController {

    @FXML private Label errorLabel;
    @FXML private Label competitionLabel;
    @FXML private TextField nomEquipeField;
    @FXML private TextField nomAmbassadeurField;
    @FXML private TextArea membresField;

    private Competition competition;
    private Runnable onReservationComplete;
    public void setCompetition(Competition competition) {
        this.competition = competition;
        competitionLabel.setText("Réservation pour : " + competition.getNomComp());
    }

    public void setOnReservationComplete(Runnable onReservationComplete) {
        this.onReservationComplete = onReservationComplete;
    }
    public void handleReservation() {
        try {
            // Récupération des données
            String nomEquipe = nomEquipeField.getText().trim();
            String ambassadeur = nomAmbassadeurField.getText().trim();
            String membres = membresField.getText().trim();

            // Réinitialisation UI
            errorLabel.setVisible(false);
            nomEquipeField.setStyle("");
            nomAmbassadeurField.setStyle("");
            membresField.setStyle("");

            // Vérification des champs obligatoires
            if (nomEquipe.isEmpty() || ambassadeur.isEmpty() || membres.isEmpty()) {
                errorLabel.setText("Tous les champs sont obligatoires.");
                errorLabel.setVisible(true);
                if (nomEquipe.isEmpty()) nomEquipeField.setStyle("-fx-border-color: red;");
                if (ambassadeur.isEmpty()) nomAmbassadeurField.setStyle("-fx-border-color: red;");
                if (membres.isEmpty()) membresField.setStyle("-fx-border-color: red;");
                return;
            }

            // Split les membres (par ligne) et validation des emails
            String[] membresArray = membres.split("\\r?\\n");
            if (membresArray.length < 4) {
                errorLabel.setText("L'équipe doit contenir au moins 4 membres.");
                errorLabel.setVisible(true);
                membresField.setStyle("-fx-border-color: red;");
                return;
            }

            for (String email : membresArray) {
                if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                    errorLabel.setText("Email invalide détecté : " + email);
                    errorLabel.setVisible(true);
                    membresField.setStyle("-fx-border-color: red;");
                    return;
                }
            }

            // Limiter le texte si trop long
            membres = membres.length() > 255 ? membres.substring(0, 255) : membres;

            // Création et enregistrement
            Equipe equipe = new Equipe();
            equipe.setNomEquipe(nomEquipe);
            equipe.setAmbassadeur(ambassadeur);
            equipe.setMembres(membres);
            equipe.setTravail("Réservation formulaire");

            IServiceEquipe service = new ServiceEquipe();
            int equipeId = service.ajouterEquipe(equipe);

            if (equipeId <= 0) throw new RuntimeException("Échec de l'insertion de l'équipe");

            Competition_Equipe participation = new Competition_Equipe(competition.getId(), equipeId);
            boolean participationAjoutee = service.ajouterParticipation(participation);

            if (!participationAjoutee) throw new RuntimeException("Échec de l'ajout de la participation");

            showAlert("Succès", "Réservation effectuée avec succès !");


            if (onReservationComplete != null) {
                onReservationComplete.run();
            }


            ((Stage) nomEquipeField.getScene().getWindow()).close();
// Fermer la fenêtre APRÈS le rafraîchissement
            Platform.runLater(() -> {
                ((Stage) nomEquipeField.getScene().getWindow()).close();
            });

        } catch (Exception e) {
            showAlert("Erreur Critique", "Erreur lors de la réservation: " + e.getMessage());
            e.printStackTrace();
        }

    }



    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
