package Services;

import entities.Quiz;
import entities.Chapitres;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizService {

    private Connection connection;

    public QuizService() {
        connection = MyConnection.getInstance().getCnx();
    }

    public void ajouterQuiz(Quiz quiz) throws SQLException {
        // First check if the chapter already has a quiz
        if (chapterHasQuiz(quiz.getChapitre().getId())) {
            throw new IllegalStateException("Ce chapitre a déjà un quiz associé");
        }

        String sql = "INSERT INTO quiz (titre, score, chapitre_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, quiz.getTitre());
            stmt.setInt(2, quiz.getScore());
            stmt.setInt(3, quiz.getChapitre().getId());
            stmt.executeUpdate();

            // Get the generated ID
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    quiz.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateQuiz(Quiz quiz) throws SQLException {
        // Check if we're changing the chapter association
        Quiz existingQuiz = findById(quiz.getId());
        if (existingQuiz != null &&
                existingQuiz.getChapitre().getId() != quiz.getChapitre().getId() &&
                chapterHasQuiz(quiz.getChapitre().getId())) {
            throw new IllegalStateException("Le nouveau chapitre a déjà un quiz associé");
        }

        String sql = "UPDATE quiz SET titre = ?, score = ?, chapitre_id = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, quiz.getTitre());
            stmt.setInt(2, quiz.getScore());
            stmt.setInt(3, quiz.getChapitre().getId());
            stmt.setInt(4, quiz.getId());
            stmt.executeUpdate();
        }
    }

    public void supprimerQuiz(int id) throws SQLException {
        String sql = "DELETE FROM quiz WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<Quiz> getAllQuizzes() throws SQLException {
        List<Quiz> quizzes = new ArrayList<>();
        String sql = "SELECT q.*, c.nom_chapitre FROM quiz q JOIN chapitre c ON q.chapitre_id = c.id";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setTitre(rs.getString("titre"));
                quiz.setScore(rs.getInt("score"));

                Chapitres chapitre = new Chapitres();
                chapitre.setId(rs.getInt("chapitre_id"));
                chapitre.setNomChapitre(rs.getString("nom_chapitre"));
                quiz.setChapitre(chapitre);

                quizzes.add(quiz);
            }
        }
        return quizzes;
    }

    public Quiz findById(int id) throws SQLException {
        String sql = "SELECT q.*, c.nom_chapitre FROM quiz q JOIN chapitre c ON q.chapitre_id = c.id WHERE q.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setTitre(rs.getString("titre"));
                quiz.setScore(rs.getInt("score"));

                Chapitres chapitre = new Chapitres();
                chapitre.setId(rs.getInt("chapitre_id"));
                chapitre.setNomChapitre(rs.getString("nom_chapitre"));
                quiz.setChapitre(chapitre);

                return quiz;
            }
        }
        return null;
    }

    public boolean chapterHasQuiz(int chapitreId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM quiz WHERE chapitre_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chapitreId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public Quiz findByChapterId(int chapitreId) throws SQLException {
        String sql = "SELECT * FROM quiz WHERE chapitre_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, chapitreId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getInt("id"));
                quiz.setTitre(rs.getString("titre"));
                quiz.setScore(rs.getInt("score"));

                Chapitres chapitre = new Chapitres();
                chapitre.setId(chapitreId);
                quiz.setChapitre(chapitre);

                return quiz;
            }
        }
        return null;
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