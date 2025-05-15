package services.user;

import entities.Collaborator;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CollaboratorService {
    private final Connection cnx;

    public CollaboratorService() {
        cnx = MyConnection.getInstance().getCnx();
    }

    public void ajouter(Collaborator collaborator) {
        String sql = "INSERT INTO collaborator (id, nomcol) VALUES (?, ?)";
        try (PreparedStatement stm = cnx.prepareStatement(sql)) {
            stm.setLong(1, collaborator.getId());           // FK to user.id
            stm.setString(2, collaborator.getNomcol());     // Must not be null

            stm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding collaborator to the database.", e);
        }
    }

    public void update(Collaborator collaborator) {
        if (collaborator.getId() == null) {
            throw new RuntimeException("Cannot update collaborator with null ID.");
        }

        String req = "UPDATE collaborator SET nomcol = ? WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setString(1, collaborator.getNomcol());
            stm.setLong(2, collaborator.getId());

            int rowsUpdated = stm.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("⚠️ No collaborator updated. Check if the ID exists.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating collaborator in the database.", e);
        }
    }

    public List<Collaborator> getAllCollaborators() {
        List<Collaborator> collaborators = new ArrayList<>();
        String req = "SELECT * FROM collaborator";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(req)) {
            while (rs.next()) {
                Collaborator collaborator = new Collaborator(
                        rs.getString("nomcol")
                );
                collaborator.setId(rs.getLong("id"));
                collaborators.add(collaborator);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving collaborators from the database.", e);
        }
        return collaborators;
    }

    public Collaborator getCollaboratorById(Long id) {
        Collaborator collaborator = null;
        String req = "SELECT * FROM collaborator WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, id);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                collaborator = new Collaborator(
                        rs.getString("nomcol")
                );
                collaborator.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving collaborator with ID: " + id, e);
        }
        return collaborator;
    }

    public void supprimer(Long id) {
        String req = "DELETE FROM collaborator WHERE id = ?";
        try (PreparedStatement stm = cnx.prepareStatement(req)) {
            stm.setLong(1, id);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting collaborator with ID: " + id, e);
        }
    }
}
