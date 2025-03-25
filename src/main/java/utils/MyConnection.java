package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyConnection {
    String url = "jdbc:mysql://localhost:3306/outi-d";
    String login ="root";
    String pwd="";
    public static MyConnection instance ;

    public Connection getCnx() {
        return cnx;
    }

    Connection cnx;

    private MyConnection() {
        try {
            cnx = DriverManager.getConnection(url,login,pwd);
            System.out.println("connection estaplished");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static MyConnection getInstance() {
        if (instance == null) {
            instance = new MyConnection();
        }
        return instance;
    }
}
