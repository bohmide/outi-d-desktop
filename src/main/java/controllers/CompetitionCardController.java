package controllers;

import entities.Competition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class CompetitionCardController {
    @FXML private Label nomLabel;
    @FXML private Label organisationLabel;
    @FXML private Label dateLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label fichierLabel; // Maintenant dans le FXML
    @FXML private HBox fichierContainer; // Conteneur pour la section fichier
    @FXML
    private HBox actionMenu;



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
}