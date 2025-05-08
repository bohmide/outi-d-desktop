package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import entities.User;
import Services.UserService;
import utils.user.PDFGenerator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class AdminUsersController {

    @FXML private VBox userListContainer;
    @FXML private VBox statistiquesPane;
    @FXML private ComboBox<String> comboStatut;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        loadUsers();
        afficherStatistiquesUtilisateursParType(); // <== Important: call it here
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Viewss/UpdateUser.fxml"));
            AnchorPane updatePane = loader.load();

            UpdateUserController controller = loader.getController();
            controller.setUserToUpdate(user);

            Stage stage = (Stage) userListContainer.getScene().getWindow();
            stage.setScene(new Scene(updatePane));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exportUsersToPDF() {
        List<User> users = userService.getAllUsers();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (.pdf)", ".pdf"));
        fileChooser.setInitialFileName("user-list.pdf");

        File selectedFile = fileChooser.showSaveDialog(userListContainer.getScene().getWindow());

        if (selectedFile != null) {
            PDFGenerator.generateUserListPDF(users, selectedFile);
        }
    }

    @FXML
    private void afficherStatistiquesUtilisateursParType() {
        List<User> allUsers = userService.getAll(); // Correct
        Map<String, Integer> stats = userService.getStatistiquesUtilisateursParType(allUsers);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre d'utilisateurs par type");

        for (Map.Entry<String, Integer> entry : stats.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Type");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Nombre");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Statistiques des utilisateurs par type");
        barChart.getData().add(series);

        statistiquesPane.getChildren().clear();
        statistiquesPane.getChildren().add(barChart);
    }
}