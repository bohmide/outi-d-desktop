package org.example.Service;

import org.example.Model.Parents;
import org.example.App.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParentsService {
    private final Connection cnx;

    public ParentsService() {
        cnx = DatabaseConnection.getInstance().getCnx();
    }

    public void ajouter(Parents parent) {
        String req = "INSERT INTO parents (birthday_child, first_name_child, last_name_child, sexe_child, learning_difficulties_child) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setDate(1, parent.getBirthdayChild());
            stm.setString(2, parent.getFirstNameChild());
            stm.setString(3, parent.getLastNameChild());
            stm.setString(4, parent.getSexeChild());
            stm.setString(5, parent.getLearningDifficultiesChild());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding parent to the database.", e);
        }
    }

    public void update(Parents parent) {
        if (parent.getId() == null) {
            throw new RuntimeException("Cannot update parent with null ID.");
        }

        String req = "UPDATE parents SET birthday_child = ?, first_name_child = ?, last_name_child = ?, sexe_child = ?, learning_difficulties_child = ? WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setDate(1, parent.getBirthdayChild());
            stm.setString(2, parent.getFirstNameChild());
            stm.setString(3, parent.getLastNameChild());
            stm.setString(4, parent.getSexeChild());
            stm.setString(5, parent.getLearningDifficultiesChild());
            stm.setLong(6, parent.getId());

            int rowsUpdated = stm.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("⚠️ No parent updated. Check if the ID exists.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating parent in the database.", e);
        }
    }

    public List<Parents> getAllParents() {
        List<Parents> parents = new ArrayList<>();
        String req = "SELECT * FROM parents";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                Parents parent = new Parents(
                        rs.getDate("birthday_child"),
                        rs.getString("first_name_child"),
                        rs.getString("last_name_child"),
                        rs.getString("sexe_child"),
                        rs.getString("learning_difficulties_child"),
                        rs.getString("first_name_child"),
                        rs.getString("last_name_child")
                );
                parent.setId(rs.getLong("id"));
                parents.add(parent);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving parents from the database.", e);
        }
        return parents;
    }

    public Parents getParentById(Long parentId) {
        Parents parent = null;
        String req = "SELECT * FROM parents WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, parentId);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                parent = new Parents(
                        rs.getDate("birthday_child"),
                        rs.getString("first_name_child"),
                        rs.getString("last_name_child"),
                        rs.getString("sexe_child"),
                        rs.getString("learning_difficulties_child"),
                        rs.getString("first_name_child"),
                        rs.getString("last_name_child")
                );
                parent.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving parent with ID: " + parentId, e);
        }
        return parent;
    }

    public void supprimer(Long id) {
        String req = "DELETE FROM parents WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting parent with ID: " + id, e);
        }
    }
}

