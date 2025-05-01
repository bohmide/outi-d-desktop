package controllers;

import entities.Organisation;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;

public class OrganisationCardController {

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

    @FXML
    private Label nomLabel;
    @FXML
    private Label domaineLabel;


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
        private void modifier(ActionEvent event) {
            parentController.modifierOrganisation(organisation);

        }

        @FXML
        private void supprimer(ActionEvent event) {
            parentController.supprimerOrganisation(organisation);

        }
    }