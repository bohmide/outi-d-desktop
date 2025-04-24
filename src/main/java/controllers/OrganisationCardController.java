package controllers;

import entities.Organisation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class OrganisationCardController {

    @FXML
    private Label nomLabel;
    @FXML
    private Label domaineLabel;
    @FXML
    private HBox actionMenu;

    private Organisation organisation;
    private OrganisationController parentController;

    public void setOrganisationData(Organisation organisation) {
        this.organisation = organisation;
        nomLabel.setText(organisation.getNomOrganisation());
        domaineLabel.setText(organisation.getDomaine());
    }

    public void setParentController(OrganisationController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void toggleMenu() {
        actionMenu.setVisible(!actionMenu.isVisible());
    }


        @FXML
        private void modifier() {
            parentController.modifierOrganisation(organisation);
            actionMenu.setVisible(false);
        }

        @FXML
        private void supprimer() {
            parentController.supprimerOrganisation(organisation);
            actionMenu.setVisible(false);
        }
    }