package controllers;

import entities.Competition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.io.File;


public class CompetitionCardController {
    @FXML private Label nomLabel;
    @FXML private Label organisationLabel;
    @FXML private Label dateLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label fichierLabel; // Maintenant dans le FXML
    @FXML private HBox fichierContainer; // Conteneur pour la section fichier
    @FXML private HBox actionMenu;
    @FXML private TextField localisationField;




    private Competition competition;
    private CompetitionController parent;

    public void setCompetition(Competition comp, CompetitionController parentController) {
        this.competition = comp;
        this.parent = parentController;

        nomLabel.setText(comp.getNomComp());
        organisationLabel.setText(comp.getNomOrganisation());
        dateLabel.setText("Du " + comp.getDateDebut() + " au " + comp.getDateFin());
        descriptionLabel.setText(comp.getDescription());

        // Gestion conditionnelle du fichier
        if (comp.getFichier() != null && !comp.getFichier().isEmpty()) {
            fichierLabel.setText(comp.getFichier());
            fichierContainer.setVisible(true);
        } else {
            fichierContainer.setVisible(false);
        }
    }

    @FXML
    private void modifier() {
        parent.modifierCompetition(competition);
    }

    @FXML
    private void supprimer() {
        parent.supprimerCompetition(competition);
    }

    @FXML
    private void reserver() {
        parent.reserverEquipe(competition);
    }
    @FXML
    private void toggleMenu() {
        actionMenu.setVisible(!actionMenu.isVisible());
    }
    @FXML
    private void showDetailsPage() {
        try {
            // Sauvegarder la scène actuelle
            Stage currentStage = (Stage) nomLabel.getScene().getWindow();
            double currentWidth = currentStage.getWidth(); // Largeur actuelle
            double currentHeight = currentStage.getHeight(); // Hauteur actuelle

            // Charger la nouvelle vue
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/CompetitionDetailsView.fxml"));
            Parent root = loader.load();

            // Configurer le contrôleur
            CompetitionDetailsController controller = loader.getController();
            controller.setStage(currentStage);
            controller.setPreviousScene(nomLabel.getScene());

            File fichier = null;
            if (competition.getFichier() != null && !competition.getFichier().isEmpty()) {
                fichier = new File(competition.getFichier());
            }


            controller.setCompetitionDetails(
                    competition,
                    competition.getNomComp(),
                    competition.getNomOrganisation(),
                    "Du " + competition.getDateDebut() + " au " + competition.getDateFin(),
                    competition.getDescription(),
                    competition.getFichier() != null ? new File(competition.getFichier()) : null
            );

            // Créer la nouvelle scène avec la même taille
            Scene detailsScene = new Scene(root, currentWidth, currentHeight);
            detailsScene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            // Appliquer la nouvelle scène sans redimensionnement
            currentStage.setScene(detailsScene);

            // (Optionnel) Désactiver le redimensionnement si nécessaire
            // currentStage.setResizable(false);
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), currentStage.getScene().getRoot());
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                currentStage.setScene(detailsScene);
                FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
            });
            fadeOut.play();
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur (par exemple, afficher une alerte)
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement de la page de détails.").show();
        }
    }
}