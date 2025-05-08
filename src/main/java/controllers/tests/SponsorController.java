package controllers.tests;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SponsorController {

    @FXML
    private Button addSponsorButton;

    @FXML
    private Button listSponsorButton;



    @FXML
    public void initialize() {
        addSponsorButton.setOnAction(event -> {
            openAddSponsorWindow();
        });

        listSponsorButton.setOnAction(event -> {
            openListSponsorWindow();
        });


    }

    private void openAddSponsorWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/test/AddSponsor.fxml")); // Update path as needed
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add Sponsor");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openListSponsorWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/test/ListSponsors.fxml")); // Update path as needed
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("List Sponsors");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDeleteSponsorWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DeleteSponsor.fxml")); // Update path as needed
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Delete Sponsor");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openModifySponsorWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ModifySponsor.fxml")); // Update path as needed
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modify Sponsor");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
