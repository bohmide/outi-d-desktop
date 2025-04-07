package org.example.App;

import java.sql.Connection;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Test the database connection BEFORE launching the GUI
        Connection connection = DatabaseConnection.getInstance().getCnx();

        if (connection != null) {
            System.out.println("✅ Database connection successful!");
        } else {
            System.err.println("❌ Database connection failed!");
        }

        // Load the login UI
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
            Scene scene = new Scene(loader.load());
            primaryStage.setTitle("Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("❌ Failed to load Login.fxml");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}