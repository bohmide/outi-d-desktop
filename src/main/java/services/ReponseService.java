package services;

import entities.Reponse;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReponseService {

    private Connection connection;

    // Constructeur qui ouvre la connexion une seule fois
    public ReponseService() {
        connection = MyConnection.getInstance().getCnx();
    }

    public void ajouterReponse(Reponse reponse) {
        String sql = "INSERT INTO reponse (reponse, is_correct, question_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reponse.getReponse());
            stmt.setBoolean(2, reponse.isCorrect());
            stmt.setInt(3, reponse.getParentQuestion().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateReponse(Reponse reponse) {
        String sql = "UPDATE reponse SET reponse = ?, is_correct = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reponse.getReponse());
            stmt.setBoolean(2, reponse.isCorrect());
            stmt.setInt(3, reponse.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reponse> getReponsesByQuestion(int questionId) {
        List<Reponse> reponses = new ArrayList<>();
        String sql = "SELECT * FROM reponse WHERE question_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, questionId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Reponse r = new Reponse();
                r.setId(rs.getInt("id"));
                r.setReponse(rs.getString("reponse"));
                r.setCorrect(rs.getBoolean("is_correct"));
                reponses.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reponses;
    }

    public void supprimerReponse(int id) {
        String sql = "DELETE FROM reponse WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fermer la connexion à la fin de l'utilisation (si nécessaire, mais elle peut être partagée).
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
