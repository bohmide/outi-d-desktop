package dao;

import entities.QuizKids;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.gson.Gson;
public class QuizKidsDAO {

    // Méthode pour insérer un quiz dans la base de données
    public void insert(QuizKids quiz) {
        // La requête SQL pour l'insertion
        String sql = "INSERT INTO quiz_kids (question, options, correct_answer, media, level, score, genre, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = MyConnection.getInstance().getCnx()) {
            // Vérification de la connexion
            if (conn == null) {
                System.err.println("❌ La connexion à la base de données a échoué !");
                return;
            }

            // Préparation de la requête
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Remplir les paramètres de la requête
                stmt.setString(1, quiz.getQuestion());
                Gson gson = new Gson();
                String optionsJson = gson.toJson(quiz.getOptions());  // Convertir la liste en JSON
                stmt.setString(2, optionsJson); // Insérer le JSON dans la colonne 'options'
                stmt.setString(3, quiz.getCorrectAnswer());
                stmt.setString(4, quiz.getMediaPath());
                stmt.setString(5, quiz.getLevel());
                stmt.setInt(6, quiz.getScore());
                stmt.setString(7, quiz.getGenre());
                stmt.setString(8, quiz.getCountry());

                // Log de la requête avant exécution (utile pour déboguer)
                System.out.println("SQL: " + stmt.toString());

                // Exécution de l'insertion
                int rowsAffected = stmt.executeUpdate();

                // Si l'insertion a réussi
                if (rowsAffected > 0) {
                    System.out.println("✅ Quiz ajouté avec succès !");
                } else {
                    System.err.println("❌ Aucun quiz n'a été ajouté !");
                }

            } catch (SQLException e) {
                // Gestion des erreurs lors de l'exécution de la requête
                System.err.println("❌ Erreur d'insertion : " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            // Gestion des erreurs lors de la connexion
            System.err.println("❌ Erreur de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public void delete(QuizKids quiz) {
        String sql = "DELETE FROM quiz_kids WHERE question = ?"; // Ajuste selon ton critère de suppression

        try (Connection conn = MyConnection.getInstance().getCnx()) {
            if (conn == null) {
                System.err.println("❌ La connexion à la base de données a échoué !");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, quiz.getQuestion());  // Utilise un critère unique pour la suppression

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("✅ Quiz supprimé avec succès !");
                } else {
                    System.err.println("❌ Aucun quiz trouvé pour supprimer !");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur de suppression : " + e.getMessage());
            e.printStackTrace();
        }
    }
    public List<QuizKids> getAll() {
        List<QuizKids> quizzes = new ArrayList<>();
        String sql = "SELECT * FROM quiz_kids";

        try (Connection conn = MyConnection.getInstance().getCnx();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                QuizKids quiz = new QuizKids();
                quiz.setId(rs.getInt("id"));  // Make sure you have this column in your DB
                quiz.setQuestion(rs.getString("question"));
                quiz.setOptions(Arrays.asList(rs.getString("options").split(","))); // assumes CSV
                quiz.setCorrectAnswer(rs.getString("correct_answer"));
                quiz.setMediaPath(rs.getString("media"));
                quiz.setLevel(rs.getString("level"));
                quiz.setScore(rs.getInt("score"));
                quiz.setGenre(rs.getString("genre"));
                quiz.setCountry(rs.getString("country"));

                quizzes.add(quiz);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du chargement des quiz : " + e.getMessage());
            e.printStackTrace();
        }

        return quizzes;
    }

    public void update(QuizKids quiz) {
        String sql = "UPDATE quiz_kids SET question = ?, options = ?, correct_answer = ?, media = ?, level = ?, score = ?, genre = ?, country = ? WHERE id = ?";

        try (Connection conn = MyConnection.getInstance().getCnx();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, quiz.getQuestion());
            pstmt.setString(2, String.join(",", quiz.getOptions())); // Convert list to CSV
            pstmt.setString(3, quiz.getCorrectAnswer());
            pstmt.setString(4, quiz.getMediaPath());
            pstmt.setString(5, quiz.getLevel());
            pstmt.setInt(6, quiz.getScore());
            pstmt.setString(7, quiz.getGenre());
            pstmt.setString(8, quiz.getCountry());
            pstmt.setInt(9, quiz.getId()); // ID in WHERE clause

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ QuizKids updated successfully.");
            } else {
                System.out.println("⚠️ No QuizKids found with ID: " + quiz.getId());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating QuizKids: " + e.getMessage());
        }
    }



}
