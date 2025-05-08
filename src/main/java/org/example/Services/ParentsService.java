package org.example.Services;

import org.example.entities.Parents;
import org.example.utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParentsService {
    private final Connection cnx;

    public ParentsService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    // ➕ Ajouter un parent (enfant)
    public void ajouter(Parents parent) {
        String sql = "INSERT INTO parents (id, birthday_child, first_name_child, last_name_child, sexe_child, learning_difficulties_child) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(sql)) {
            stm.setLong(1, parent.getId());
            stm.setDate(2, parent.getBirthdayChild());
            stm.setString(3, parent.getFirstNameChild());
            stm.setString(4, parent.getLastNameChild());
            stm.setString(5, parent.getSexeChild());
            stm.setString(6, parent.getLearningDifficultiesChild());

            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout du parent.", e);
        }
    }

    // 🔁 Mettre à jour un parent
    public void update(Parents parent) {
        if (parent.getId() == null) {
            throw new RuntimeException("Impossible de mettre à jour un parent sans ID.");
        }

        String req = "UPDATE parents SET birthday_child = ?, first_name_child = ?, last_name_child = ?, sexe_child = ?, learning_difficulties_child = ? " +
                "WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setDate(1, parent.getBirthdayChild());
            stm.setString(2, parent.getFirstNameChild());
            stm.setString(3, parent.getLastNameChild());
            stm.setString(4, parent.getSexeChild());
            stm.setString(5, parent.getLearningDifficultiesChild());
            stm.setLong(6, parent.getId());

            int rowsUpdated = stm.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("⚠️ Aucun parent mis à jour. Vérifie l'ID.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du parent.", e);
        }
    }

    // 📥 Récupérer tous les parents
    public List<Parents> getAllParents() {
        List<Parents> parentsList = new ArrayList<>();
        String req = "SELECT * FROM parents";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {

            while (rs.next()) {
                Parents parent = new Parents(
                        rs.getDate("birthday_child"),
                        rs.getString("first_name_child"),
                        rs.getString("last_name_child"),
                        rs.getString("sexe_child"),
                        rs.getString("learning_difficulties_child")
                );
                parent.setId(rs.getLong("id"));
                parentsList.add(parent);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des parents.", e);
        }
        return parentsList;
    }

    // 🔍 Récupérer un parent par ID
    public Parents getParentById(Long id) {
        Parents parent = null;
        String req = "SELECT * FROM parents WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                parent = new Parents(
                        rs.getDate("birthday_child"),
                        rs.getString("first_name_child"),
                        rs.getString("last_name_child"),
                        rs.getString("sexe_child"),
                        rs.getString("learning_difficulties_child")
                );
                parent.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du parent avec l'ID : " + id, e);
        }
        return parent;
    }

    // ❌ Supprimer un parent
    public void supprimer(Long id) {
        String req = "DELETE FROM parents WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du parent avec l'ID : " + id, e);
        }
    }
}
