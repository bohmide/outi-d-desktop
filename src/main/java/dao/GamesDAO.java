package dao;

import entities.Games;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GamesDAO {

    private final Connection conn;

    public GamesDAO() {
        conn = MyConnection.getInstance().getCnx();
    }

    public void add(Games game) throws SQLException {
        String sql = "INSERT INTO games (name) VALUES (?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, game.getName());
        ps.executeUpdate();
    }

    public List<Games> getAll() throws SQLException {
        List<Games> gamesList = new ArrayList<>();
        String sql = "SELECT * FROM games";
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Games g = new Games(rs.getInt("id"), rs.getString("name"));
            gamesList.add(g);
        }

        return gamesList;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM games WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public void update(Games game) throws SQLException {
        String sql = "UPDATE games SET name = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, game.getName());
        ps.setInt(2, game.getId());
        ps.executeUpdate();
    }
    public Games getByName(String name) throws SQLException {
        String query = "SELECT * FROM games WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Games(
                            rs.getInt("id"),
                            rs.getString("name")
                    );
                }
            }
        }
        return null; // Retourne null si aucun jeu n'est trouvé
    }
    public Games getById(int id) throws SQLException {
        String sql = "SELECT * FROM games WHERE id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            String name = rs.getString("name");
            return new Games(id, name);
        }

        return null;
    }

}
