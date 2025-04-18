package services;

import interfaces.IServiceEquipe;
import entities.Equipe;
import entities.Competition_Equipe;
import utils.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.MyConnection;

public class ServiceEquipe implements IServiceEquipe {
    private Connection cnx = MyConnection.getInstance().getCnx();

    @Override
    public int ajouterEquipe(Equipe equipe) {
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            String query = "INSERT INTO equipe (travail, nom_equipe, ambassadeur, membres) VALUES (?, ?, ?, ?)";
            pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            pst.setString(1, equipe.getTravail());
            pst.setString(2, equipe.getNomEquipe());
            pst.setString(3, equipe.getAmbassadeur());

            // Gestion robuste des membres
            String membres = equipe.getMembres();
            if (membres == null || membres.trim().isEmpty()) {
                pst.setString(4, "Membres non spécifiés");
            } else {
                pst.setString(4, membres.substring(0, Math.min(membres.length(), 255)));
            }

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected == 0) {
                return -1; // Aucune ligne affectée
            }

            rs = pst.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Retourne l'ID généré
            }

            return -1; // Cas où aucun ID n'est généré

        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1; // Retour en cas d'exception
        } finally {
            // Fermeture des ressources
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<Equipe> afficherToutesEquipes() {
        List<Equipe> equipes = new ArrayList<>();
        String query = "SELECT * FROM equipe";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            while (rs.next()) {
                equipes.add(new Equipe(
                        rs.getInt("id"),
                        rs.getString("travail"),
                        rs.getString("nom_equipe"),
                        rs.getString("ambassadeur"),
                        rs.getString("membres")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Erreur lors de la lecture: " + e.getMessage());
        }
        return equipes;
    }

    @Override
    public boolean ajouterParticipation(Competition_Equipe ce) {
        String query = "INSERT INTO competition_equipe (competition_id, equipe_id) VALUES (?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, ce.getCompetitionId());
            pst.setInt(2, ce.getEquipeId());
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0; // Retourne true si au moins une ligne a été affectée
        } catch (SQLException e) {
            System.out.println("Erreur participation: " + e.getMessage());
            return false; // Retourne false en cas d'erreur
        }
    }

    @Override
    public List<Equipe> getEquipesParCompetition(int competitionId) {
        List<Equipe> equipes = new ArrayList<>();
        String query = "SELECT e.* FROM equipe e " +
                "JOIN competition_equipe ce ON e.id = ce.equipe_id " +
                "WHERE ce.competition_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, competitionId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                equipes.add(new Equipe(
                        rs.getInt("id"),
                        rs.getString("travail"),
                        rs.getString("nom_equipe"),
                        rs.getString("ambassadeur"),
                        rs.getString("membres")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des équipes: " + e.getMessage());
        }
        return equipes;
    }

    @Override
    public void supprimerEquipe(int id) {
        String deleteParticipationQuery = "DELETE FROM competition_equipe WHERE equipe_id = ?";
        String deleteEquipeQuery = "DELETE FROM equipe WHERE id = ?";

        try {
            cnx.setAutoCommit(false);

            try (PreparedStatement pst = cnx.prepareStatement(deleteParticipationQuery)) {
                pst.setInt(1, id);
                pst.executeUpdate();
            }

            try (PreparedStatement pst = cnx.prepareStatement(deleteEquipeQuery)) {
                pst.setInt(1, id);
                int affectedRows = pst.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Échec de la suppression, aucune équipe trouvée avec l'ID: " + id);
                }
            }

            cnx.commit();
            System.out.println("Équipe supprimée avec succès !");

        } catch (SQLException e) {
            try {
                cnx.rollback();
                System.err.println("Erreur lors de la suppression: " + e.getMessage());
            } catch (SQLException ex) {
                System.err.println("Erreur lors du rollback: " + ex.getMessage());
            }
        } finally {
            try {
                cnx.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erreur lors du rétablissement autoCommit: " + e.getMessage());
            }
        }
    }

}