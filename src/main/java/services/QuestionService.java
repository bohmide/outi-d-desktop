package services;

import entities.Question;
import entities.Quiz;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestionService {

    private Connection connection;

    // Constructeur qui ouvre la connexion une seule fois
    public QuestionService() {
        connection = MyConnection.getInstance().getCnx();
    }

    public void ajouterQuestion(Question question) {
        String sql = "INSERT INTO question (question, type, quiz_id) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, question.getQuestion());
            stmt.setString(2, question.getType());
            stmt.setInt(3, question.getQuiz().getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Question> getQuestionsByQuiz(Quiz quiz) {
        List<Question> questions = new ArrayList<>();
        String sql = "SELECT * FROM question WHERE quiz_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quiz.getId());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Question q = new Question();
                q.setId(rs.getInt("id"));
                q.setQuestion(rs.getString("question"));
                q.setType(rs.getString("type"));
                q.setQuiz(quiz);
                questions.add(q);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return questions;
    }

    public void supprimerQuestion(int id) {
        String sql = "DELETE FROM question WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateQuestion(Question question) {
        String sql = "UPDATE question SET question = ?, type = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, question.getQuestion());
            stmt.setString(2, question.getType());
            stmt.setInt(3, question.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
