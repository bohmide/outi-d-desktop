package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

public class MainController {
    @FXML private Tab tabCompetitions;
    @FXML private Tab tabOrganisations;
    @FXML private Tab tabEquipe;

    // Les AnchorPane doivent correspondre exactement aux fx:id
    @FXML private AnchorPane compPane;
    @FXML private AnchorPane orgPane;
    @FXML private AnchorPane equipePane;

    @FXML
    public void initialize() {


        try {
            // Charger CompetitionView
            FXMLLoader compLoader = new FXMLLoader(getClass().getResource("/views/CompetitionView.fxml"));
            AnchorPane compContent = compLoader.load();
            compPane.getChildren().add(compContent);
            CompetitionController competitionController = compLoader.getController();

            // Charger CompetitionView
            FXMLLoader orgLoader = new FXMLLoader(getClass().getResource("/views/OrganisationView.fxml"));
            AnchorPane orgContent = orgLoader.load();
            orgPane.getChildren().add(orgContent);
            OrganisationController organisationController = orgLoader.getController();
            // Charger EquipeView
            FXMLLoader equipeLoader = new FXMLLoader(getClass().getResource("/views/EquipeView.fxml"));
            AnchorPane equipeContent = equipeLoader.load();
            equipePane.getChildren().add(equipeContent);

            organisationController.setCompetitionController(competitionController);

        } catch (IOException e) {
            e.printStackTrace();
        }
}}