package dao;

import entities.Badge;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BadgeDAO {
    private final Connection conn = MyConnection.getInstance().getCnx();

    public void add(Badge badge) throws SQLException {
        String sql = "INSERT INTO badge (name, required_score, icon) VALUES (?, ?, ?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, badge.getName());
        ps.setInt(2, badge.getRequiredScore());
        ps.setString(3, badge.getIcon());
        ps.executeUpdate();
    }

    public List<Badge> getAll() throws SQLException {
        List<Badge> list = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM badge");

        while (rs.next()) {
            list.add(new Badge(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("required_score"),
                    rs.getString("icon")
            ));
        }
        return list;
    }

    public void delete(int id) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("DELETE FROM badge WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
    }

    public void update(Badge badge) throws SQLException {
        String sql = "UPDATE badge SET name = ?, required_score = ?, icon = ? WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, badge.getName());
        ps.setInt(2, badge.getRequiredScore());
        ps.setString(3, badge.getIcon());
        ps.setInt(4, badge.getId());
        ps.executeUpdate();
    }
    public Badge getById(int id) throws SQLException {
        String sql = "SELECT * FROM badge WHERE id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Badge(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("required_score"),
                    rs.getString("icon")
            );
        }
        return null;
    }

    public Badge getByName(String name) throws SQLException {
        String sql = "SELECT * FROM badge WHERE name = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new Badge(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("required_score"),
                    rs.getString("icon")
            );
        }
        return null;
    }
}
