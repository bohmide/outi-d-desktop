package org.example.Services.user;

import org.example.Models.user.Prof;
import org.example.Utils.user.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfService {
    private final Connection cnx;

    public ProfService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public void ajouter(Prof prof) {
        String sql = "INSERT INTO prof (id, interdate, intermode) VALUES (?, ?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(sql)) {
            stm.setLong(1, prof.getId());                  // FK to user.id
            stm.setDate(2, prof.getInterDate());           // MUST NOT be null
            stm.setString(3, prof.getInterMode());         // MUST NOT be null

            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();  // 🧠 This is what will show the true SQL error!
            throw new RuntimeException("Error adding professor to the database.", e);
        }
    }

    // Update an existing Prof
    public void update(Prof prof) {
        if (prof.getId() == null) {
            throw new RuntimeException("Cannot update professor with null ID.");
        }

        String req = "UPDATE prof SET inter_date = ?, inter_mode = ? WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setDate(1, prof.getInterDate());
            stm.setString(2, prof.getInterMode());
            stm.setLong(3, prof.getId());
            int rowsUpdated = stm.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("⚠️ No professor updated. Check if the ID exists.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating professor in the database.", e);
        }
    }

    // Get all professors
    public List<Prof> getAllProfs() {
        List<Prof> profs = new ArrayList<>();
        String req = "SELECT * FROM prof";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                Prof prof = new Prof(
                        rs.getDate("inter_date"),
                        rs.getString("inter_mode")
                );
                prof.setId(rs.getLong("id"));
                profs.add(prof);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving professors from the database.", e);
        }
        return profs;
    }

    // Get a specific professor by ID
    public Prof getProfById(Long profId) {
        Prof prof = null;
        String req = "SELECT * FROM prof WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, profId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                prof = new Prof(
                        rs.getDate("inter_date"),
                        rs.getString("inter_mode")
                );
                prof.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving professor with ID: " + profId, e);
        }
        return prof;
    }

    // Delete a professor by ID
    public void supprimer(Long id) {
        String req = "DELETE FROM prof WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting professor with ID: " + id, e);
        }
    }
}

