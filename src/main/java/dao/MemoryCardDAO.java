package dao;

import entities.Games;
import entities.MemoryCard;
import org.json.JSONArray;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MemoryCardDAO {
    private final Connection conn;

         public MemoryCardDAO() {
        conn = MyConnection.getInstance().getCnx();
    }

    public void add(MemoryCard memoryCard) throws SQLException {
        // Liste d'images provenant de memoryCard
        List<String> imagePaths = memoryCard.getImages();

        // Vérifier si la liste d'images est vide
        if (imagePaths == null || imagePaths.isEmpty()) {
            throw new SQLException("La liste des images ne peut pas être vide.");
        }

        // Créer un JSONArray pour les images
        JSONArray imagesArray = new JSONArray();
        for (String imagePath : imagePaths) {
            if (imagePath != null && !imagePath.isEmpty()) {
                imagesArray.put(imagePath);  // Ajouter chaque image au tableau JSON
            }
        }

        // Convertir le tableau en chaîne JSON
        String jsonImages = imagesArray.toString();

        // Requête SQL pour insérer une MemoryCard
        String query = "INSERT INTO memory_card (game_id, images) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, memoryCard.getGameId());  // Assurez-vous que gameId est dans l'objet memoryCard
            stmt.setString(2, jsonImages);  // Insérer la chaîne JSON dans la colonne images
            stmt.executeUpdate();
        }
        System.out.println("JSON complet des images : " + jsonImages);
        System.out.println("Longueur JSON : " + jsonImages.length());

    }


    public List<MemoryCard> getAll() throws SQLException {
        List<MemoryCard> list = new ArrayList<>();
        String sql = "SELECT * FROM memory_card";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        GamesDAO gamesDAO = new GamesDAO(); // Pour récupérer le nom du jeu

        while (rs.next()) {
            int id = rs.getInt("id");
            int gameId = rs.getInt("game_id");

            String jsonImages = rs.getString("images");
            List<String> images = new ArrayList<>();

            if (jsonImages != null && !jsonImages.isEmpty()) {
                JSONArray jsonArray = new JSONArray(jsonImages);
                for (int i = 0; i < jsonArray.length(); i++) {
                    images.add(jsonArray.getString(i));
                }
            }

            MemoryCard mc = new MemoryCard(id, gameId, images);

            // Récupérer le nom du jeu
            Games game = gamesDAO.getById(gameId);
            if (game != null) {
                mc.setGameName(game.getName());
            }

            list.add(mc);
        }

        return list;
    }




    public void update(MemoryCard mc) throws SQLException {
        String sql = "UPDATE memory_card SET game_id = ?, images = ? WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, mc.getGameId());
        JSONArray imagesArray = new JSONArray(mc.getImages());
        stmt.setString(2, imagesArray.toString());

        stmt.setInt(3, mc.getId());
        stmt.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM memory_card WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        stmt.executeUpdate();
    }
}
