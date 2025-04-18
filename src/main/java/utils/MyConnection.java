package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    String url = "jdbc:mysql://localhost:3306/outi-d";
    String login = "root";
    String pwd = "";
    public static MyConnection instance;

    private Connection cnx;

    private MyConnection() {
        try {
            cnx = DriverManager.getConnection(url, login, pwd);
            System.out.println("Connection established");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Connection failed", e);
        }
    }

    public Connection getCnx() {
        try {
            // Check if the connection is closed or null, then reopen it
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(url, login, pwd);
                System.out.println("Reopened connection");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnx;
    }

    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    // Optionally, you can add a method to explicitly close the connection
    public void closeConnection() {
        try {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
