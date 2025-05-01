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
import utils.EmailService; // Cet import est crucial
import javax.mail.MessagingException;

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

            // Normaliser les membres pour la base de données (séparés par virgule)
            String membresNormalises = String.join(", ", membresArray);
            if (membresNormalises.length() > 255) {
                membresNormalises = membresNormalises.substring(0, 255);
            }

            // Création et enregistrement
            Equipe equipe = new Equipe();
            equipe.setNomEquipe(nomEquipe);
            equipe.setAmbassadeur(ambassadeur);
            equipe.setMembres(membresNormalises);
            equipe.setTravail("Réservation formulaire");

            IServiceEquipe service = new ServiceEquipe();
            int equipeId = service.ajouterEquipe(equipe);

            if (equipeId <= 0) throw new RuntimeException("Échec de l'insertion de l'équipe.");

            Competition_Equipe participation = new Competition_Equipe(competition.getId(), equipeId);
            boolean participationAjoutee = service.ajouterParticipation(participation);

            if (!participationAjoutee) throw new RuntimeException("Échec de l'ajout de la participation.");

            showAlert("Succès", "Réservation effectuée avec succès !");

            // Envoi de l'email
            String staticEmail = "salmaachour2015@gmail.com"; // Mail statique
            String emailBody = generateTeamEmailBody(equipe);

// Utilisation d'un thread séparé pour éviter de bloquer l'UI
            new Thread(() -> {
                try {
                    EmailService.sendEmail(staticEmail, "Création d'équipe - " + equipe.getNomEquipe(), emailBody);
                } catch (MessagingException e) {
                    Platform.runLater(() -> {
                        showAlert("Erreur Email", "Erreur lors de l'envoi du mail de confirmation");
                    });
                    e.printStackTrace();
                }
            }).start();

            // Exécuter callback si défini
            if (onReservationComplete != null) {
                onReservationComplete.run();
            }

            // Fermer la fenêtre proprement
            Platform.runLater(() -> {
                Stage stage = (Stage) nomEquipeField.getScene().getWindow();
                stage.close();
            });

        } catch (Exception e) {
            showAlert("Erreur Critique", "Erreur lors de la réservation : " + e.getMessage());
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
    //email
    private String generateTeamEmailBody(Equipe equipe) {
        return "Nouvelle équipe créée :\n"
                + "Nom: " + equipe.getNomEquipe() + "\n"
                + "Ambassadeur: " + equipe.getAmbassadeur() + "\n"
                + "Membres: " + equipe.getMembres() + "\n"
                + "Compétition: " + competition.getNomComp();
    }
}
