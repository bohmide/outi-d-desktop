package Services;

import entities.Quiz;
import entities.Chapitres;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService {

    private Connection connection;

    // Constructeur qui ouvre la connexion une seule fois
    public QuizService() {
        connection = MyConnection.getInstance().getCnx();
    }

    public void ajouterQuiz(Quiz quiz) {
        String sql = "INSERT INTO quiz (titre, score, chapitre_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, quiz.getTitre());
            stmt.setInt(2, quiz.getScore());
            stmt.setInt(3, quiz.getChapitre().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateQuiz(Quiz quiz) {
        String sql = "UPDATE quiz SET titre = ?, chapitre_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, quiz.getTitre());
            stmt.setInt(2, quiz.getChapitre().getId());
            stmt.setInt(3, quiz.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerQuiz(int id) {
        String sql = "DELETE FROM quiz WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quiz";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setTitre(rs.getString("titre"));
                quiz.setScore(rs.getInt("score"));

                int chapitreId = rs.getInt("chapitre_id");
                ChapitreService chapitreService = new ChapitreService();
                quiz.setChapitre(chapitreService.getById(chapitreId));

                quizzes.add(quiz);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quizzes;
    }

    public Quiz findById(int id) {
        String sql = "SELECT * FROM quiz WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setScore(rs.getInt("score"));
                return quiz;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
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
