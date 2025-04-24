package services;

import interfaces.IServiceEquipe;
import entities.Equipe;
import entities.Competition_Equipe;
import utils.MyConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import entities.Competition;  // Ajoutez cette ligne


public class ServiceEquipe implements IServiceEquipe {
    private Connection cnx = MyConnection.getInstance().getCnx();

    @Override
    public int ajouterEquipe(Equipe equipe) {
        PreparedStatement pst = null;
        ResultSet rs = null;
        String query = "INSERT INTO equipe (travail, nom_equipe, ambassadeur, membres) VALUES (?, ?, ?, ?)";

        try {
            pst = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, equipe.getTravail());
            pst.setString(2, equipe.getNomEquipe());
            pst.setString(3, equipe.getAmbassadeur());
            pst.setString(4, equipe.getMembres() == null || equipe.getMembres().trim().isEmpty()
                    ? "Membres non spécifiés"
                    : equipe.getMembres());

            if (pst.executeUpdate() == 0) {
                return -1;
            }

            rs = pst.getGeneratedKeys();
            return rs.next() ? rs.getInt(1) : -1;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return -1;
        } finally {
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
        String query =  "SELECT e.*, GROUP_CONCAT(c.nom_comp SEPARATOR ', ') as competitions " +
                "FROM equipe e " +
                "LEFT JOIN competition_equipe ce ON e.id = ce.equipe_id " +
                "LEFT JOIN competition c ON ce.competition_id = c.id " +
                "GROUP BY e.id";


        try (PreparedStatement ps = cnx.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setId(rs.getInt("id"));
                equipe.setNomEquipe(rs.getString("nom_equipe"));
                equipe.setAmbassadeur(rs.getString("ambassadeur"));
                equipe.setMembres(rs.getString("membres"));
                equipe.setTravail(rs.getString("travail"));
                equipe.setCompetitionNom(rs.getString("competitions"));
                equipes.add(equipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipes;
    }

    @Override
    public boolean ajouterParticipation(Competition_Equipe ce) {
        String query = "INSERT INTO competition_equipe (competition_id, equipe_id) VALUES (?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, ce.getCompetitionId());
            pst.setInt(2, ce.getEquipeId());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur participation: " + e.getMessage());
            return false;
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
                Equipe equipe = new Equipe(
                        rs.getInt("id"),
                        rs.getString("travail"),
                        rs.getString("nom_equipe"),
                        rs.getString("ambassadeur"),
                        rs.getString("membres"),
                        ""
                );
                equipes.add(equipe);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des équipes: " + e.getMessage());
        }
        return equipes;
    }

    @Override
    public void supprimerEquipe(int id) {
        try {
            cnx.setAutoCommit(false);

            // D'abord supprimer les participations
            try (PreparedStatement pst = cnx.prepareStatement(
                    "DELETE FROM competition_equipe WHERE equipe_id = ?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
            }

            // Puis supprimer l'équipe
            try (PreparedStatement pst = cnx.prepareStatement(
                    "DELETE FROM equipe WHERE id = ?")) {
                pst.setInt(1, id);
                if (pst.executeUpdate() == 0) {
                    throw new SQLException("Échec de la suppression, aucune équipe trouvée avec l'ID: " + id);
                }
            }
            cnx.commit();
            System.out.println("🗑️ Équipe supprimée avec succès !");

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
    public List<Competition> getCompetitionsByEquipe(int equipeId) {
        List<Competition> competitions = new ArrayList<>();
        String query = "SELECT c.* FROM competition c " +
                "JOIN competition_equipe ce ON c.id = ce.competition_id " +
                "WHERE ce.equipe_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, equipeId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Competition competition = new Competition(
                        rs.getInt("id"),
                        rs.getString("nom_comp"),
                        rs.getString("nom_entreprise"),
                        rs.getDate("date_debut") != null ? rs.getDate("date_debut").toLocalDate() : null,
                        rs.getDate("date_fin") != null ? rs.getDate("date_fin").toLocalDate() : null,
                        rs.getString("description"),
                        rs.getString("fichier"),
                        null // Organisation non chargée ici
                );
                competitions.add(competition);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des compétitions: " + e.getMessage());
        }
        return competitions;
    }
}