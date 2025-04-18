package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class EventController {

    @FXML
    private Button addEventButton;

    @FXML
    private Button listEventButton;



    @FXML
    public void initialize() {
        addEventButton.setOnAction(event -> {
            openAddEventWindow();
        });

        listEventButton.setOnAction(event -> {
            openListEventWindow();
        });

    }

    // Method to open a new window for adding an event
    private void openAddEventWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EventAdd.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Add Event");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to open a new window to list events
    private void openListEventWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/EventList.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("List Events");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to delete an event
    private void deleteEvent() {
        // Display a confirmation dialog before deletion
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Event");
        alert.setHeaderText("Are you sure you want to delete this event?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Event deleted!");
                // Add logic to delete the event from the database or list
            }
        });
    }

    // Method to modify an event
    private void modifyEvent() {
        // You can open a modification window or a form here
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ModifyEvent.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Modify Event");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
