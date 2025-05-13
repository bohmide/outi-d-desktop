package services;

import entities.Chapitres;
import entities.Cours;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChapitreService {

    private Connection connection;

    // Constructeur qui ouvre la connexion une seule fois
    public ChapitreService() {
        connection = MyConnection.getInstance().getCnx();
    }

    public void ajouter(Chapitres chapitre) {
        String sql = "INSERT INTO chapitre (nom_chapitre, contenu_text, cours_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, chapitre.getNomChapitre());
            stmt.setString(2, chapitre.getContenuText());
            stmt.setInt(3, chapitre.getCours().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifier(Chapitres chapitre) {
        String sql = "UPDATE chapitre SET nom_chapitre = ?, contenu_text = ?, cours_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, chapitre.getNomChapitre());
            stmt.setString(2, chapitre.getContenuText());
            stmt.setInt(3, chapitre.getCours().getId());
            stmt.setInt(4, chapitre.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM chapitre WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Chapitres> recuperer(Cours cours) {
        List<Chapitres> list = new ArrayList<>();
        String sql = "SELECT * FROM chapitre WHERE cours_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cours.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Chapitres ch = new Chapitres();
                ch.setId(rs.getInt("id"));
                ch.setNomChapitre(rs.getString("nom_chapitre"));
                ch.setContenuText(rs.getString("contenu_text"));
                ch.setCours(cours);
                list.add(ch);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Chapitres getById(int id) {
        String sql = "SELECT * FROM chapitre WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Chapitres ch = new Chapitres();
                ch.setId(rs.getInt("id"));
                ch.setNomChapitre(rs.getString("nom_chapitre"));
                ch.setContenuText(rs.getString("contenu_text"));
                CoursService coursService = new CoursService();
                ch.setCours(coursService.getById(rs.getInt("cours_id")));
                return ch;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean existeDeja(String nomChapitre, Cours cours) {
        String sql = "SELECT COUNT(*) FROM chapitre WHERE nom_chapitre = ? AND cours_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nomChapitre);
            stmt.setInt(2, cours.getId());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
