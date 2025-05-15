package services;

import entities.Cours;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursService {

    private Connection connection;

    public CoursService() {
        connection = MyConnection.getInstance().getCnx();
    }

    public void ajouter(Cours cours) {
        String sql = "INSERT INTO cours (nom, date_creation, etat) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cours.getNom());
            stmt.setDate(2, java.sql.Date.valueOf(cours.getDateCreation()));
            stmt.setString(3, cours.getEtat());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifier(Cours cours) {
        String sql = "UPDATE cours SET nom = ?, date_creation = ?, etat = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cours.getNom());
            stmt.setDate(2, java.sql.Date.valueOf(cours.getDateCreation()));
            stmt.setString(3, cours.getEtat());
            stmt.setInt(4, cours.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM cours WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Cours> recuperer() {
        List<Cours> coursList = new ArrayList<>();
        String sql = "SELECT * FROM cours";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            // Print all column names from ResultSet metadata
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("Available columns:");
            for (int i = 1; i <= columnCount; i++) {
                System.out.println(metaData.getColumnName(i));
            }
            while (rs.next()) {
                Cours c = new Cours();
                c.setId(rs.getInt("id"));
                c.setNom(rs.getString("nom"));
                c.setDateCreation(rs.getDate("date_creation"));
                c.setEtat(rs.getString("etat"));
                coursList.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return coursList;
    }

    public Cours getById(int id) {
        String sql = "SELECT * FROM cours WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cours c = new Cours();
                c.setId(rs.getInt("id"));
                c.setNom(rs.getString("nom"));
                c.setDateCreation(rs.getDate("dateCreation"));
                c.setEtat(rs.getString("etat"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
