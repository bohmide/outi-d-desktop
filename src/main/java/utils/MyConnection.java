package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    private final String url = "jdbc:mysql://localhost:3306/outi-d";
    private final String login = "root";
    private final String pwd = "";

    private static MyConnection instance;
    private Connection cnx;

    private MyConnection() {
        try {
            cnx = DriverManager.getConnection(url, login, pwd);
            System.out.println("Connexion établie !");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur de connexion : " + e.getMessage());
        }
    }

    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                cnx = DriverManager.getConnection(url, login, pwd);
                System.out.println("Connexion rétablie !");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cnx;
    }
}
