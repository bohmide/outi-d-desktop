package dao;

import entities.Games;
import entities.Puzzle;
import org.json.JSONArray;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PuzzleDAO {
    private final Connection conn;

    public PuzzleDAO() {
        conn = MyConnection.getInstance().getCnx();
    }

    public void add(Puzzle puzzle) throws SQLException {
        String sql = "INSERT INTO puzzle (game_id, final_image, pieces) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, puzzle.getGameId());
            stmt.setString(2, puzzle.getFinalImage());
            stmt.setString(3, new JSONArray(puzzle.getPieces()).toString());
            stmt.executeUpdate();
        }
    }

    public List<Puzzle> getAll() throws SQLException {
        List<Puzzle> list = new ArrayList<>();
        String sql = "SELECT * FROM puzzle";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        GamesDAO gamesDAO = new GamesDAO();

        while (rs.next()) {
            int id = rs.getInt("id");
            int gameId = rs.getInt("game_id");
            String finalImage = rs.getString("final_image");
            String piecesJson = rs.getString("pieces");

            List<String> pieces = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(piecesJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                pieces.add(jsonArray.getString(i));
            }

            Puzzle puzzle = new Puzzle(id, gameId, finalImage, pieces);
            Games game = gamesDAO.getById(gameId);
            if (game != null) puzzle.setGameName(game.getName());

            list.add(puzzle);
        }

        return list;
    }

    public void update(Puzzle puzzle) throws SQLException {
        String sql = "UPDATE puzzle SET game_id = ?, final_image = ?, pieces = ? WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, puzzle.getGameId());
            stmt.setString(2, puzzle.getFinalImage());
            stmt.setString(3, new JSONArray(puzzle.getPieces()).toString());
            stmt.setInt(4, puzzle.getId());
            stmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM puzzle WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
