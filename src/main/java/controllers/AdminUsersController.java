package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.User;
import services.UserService;

import java.io.IOException;
import java.util.List;

public class AdminUsersController {

    @FXML
    private VBox userListContainer;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        loadUsers();
    }

    private void loadUsers() {
        userListContainer.getChildren().clear();
        List<User> users = userService.getAllUsers();

        for (User user : users) {
            userListContainer.getChildren().add(createUserCard(user));
        }
    }

    private VBox createUserCard(User user) {
        VBox card = new VBox(8);
        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 8;
                -fx-padding: 18;
                -fx-border-color: #d0d0d0;
                -fx-border-radius: 8;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 6, 0.1, 0, 2);
                -fx-pref-width: 600;
            """);

        Label name = new Label("👤 " + user.getFirstName() + " " + user.getLastName());
        Label email = new Label("📧 " + user.getEmail());
        Label role = new Label("🎓 " + user.getType());
        Label phone = new Label("📱 " + user.getNumtel());

        for (Label label : List.of(name, email, role, phone)) {
            label.setStyle("-fx-font-size: 14px;");
        }

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
        updateBtn.setOnAction(e -> openUpdatePage(user));

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
        deleteBtn.setOnAction(e -> {
            userService.supprimer(user.getId());
            loadUsers();
        });

        buttons.getChildren().addAll(updateBtn, deleteBtn);
        card.getChildren().addAll(name, email, role, phone, buttons);

        return card;
    }

    private void openUpdatePage(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/UpdateUser.fxml"));
            AnchorPane updatePane = loader.load(); // FXML loads and initializes controller

            UpdateUserController controller = loader.getController();
            controller.setUserToUpdate(user); // Now the fields exist

            Stage stage = (Stage) userListContainer.getScene().getWindow();
            stage.setScene(new Scene(updatePane));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}