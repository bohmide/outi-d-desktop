package services;

import entities.Competition;
import entities.Organisation;
import interfaces.IServiceCompetition;
import utils.MyConnection;
import entities.Equipe;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections; // Ajoutez cette ligne


public class ServiceCompetition implements IServiceCompetition {
    private Connection cnx = MyConnection.getInstance().getCnx();

    @Override
    public void ajouterCompetition(Competition comp) {
        String sql = "INSERT INTO competition (nom_comp, nom_entreprise, date_debut, date_fin, description, fichier, organisation_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, comp.getNomComp());
            ps.setString(2, comp.getNomEntreprise());
            // Conversion de LocalDate à java.sql.Date uniquement ici
            ps.setDate(3, comp.getDateDebut() != null ? Date.valueOf(comp.getDateDebut()) : null);
            ps.setDate(4, comp.getDateFin() != null ? Date.valueOf(comp.getDateFin()) : null);
            ps.setString(5, comp.getDescription());
            ps.setString(6, comp.getFichier());
            ps.setInt(7, comp.getOrganisation().getId());

            ps.executeUpdate();
            System.out.println("✅ Compétition ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Competition> afficherCompetitions() {
        List<Competition> list = new ArrayList<>();
        String sql = "SELECT c.*, o.nom_organisation, o.domaine FROM competition c JOIN organisation o ON c.organisation_id = o.id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Organisation org = new Organisation(
                        rs.getInt("organisation_id"),
                        rs.getString("nom_organisation"),
                        rs.getString("domaine")
                );

                // Conversion de java.sql.Date à LocalDate lors de la lecture
                Date dateDebut = rs.getDate("date_debut");
                Date dateFin = rs.getDate("date_fin");

                Competition comp = new Competition(
                        rs.getInt("id"),
                        rs.getString("nom_comp"),
                        rs.getString("nom_entreprise"),
                        dateDebut != null ? dateDebut.toLocalDate() : null,
                        dateFin != null ? dateFin.toLocalDate() : null,
                        rs.getString("description"),
                        rs.getString("fichier"),
                        org

                );
                // Charger les équipes associées
                List<Equipe> equipes = getEquipesByCompetition(comp.getId());
                comp.setEquipes(FXCollections.observableArrayList(equipes));
                list.add(comp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void supprimerCompetition(int id) {
        try {
            cnx.setAutoCommit(false);

            // 1. Supprimer d'abord les équipes associées
            String deleteEquipesSql = "DELETE FROM equipe WHERE id IN " +
                    "(SELECT equipe_id FROM competition_equipe WHERE competition_id = ?)";
            try (PreparedStatement ps = cnx.prepareStatement(deleteEquipesSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 2. Supprimer les associations
            String deleteAssociationsSql = "DELETE FROM competition_equipe WHERE competition_id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(deleteAssociationsSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 3. Supprimer la compétition
            String deleteCompetitionSql = "DELETE FROM competition WHERE id = ?";
            try (PreparedStatement ps = cnx.prepareStatement(deleteCompetitionSql)) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            cnx.commit();
            System.out.println("🗑️ Compétition et équipes associées supprimées !");
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


    @Override
    public void modifierCompetition(Competition comp) {
        String sql = "UPDATE competition SET nom_comp=?, nom_entreprise=?, date_debut=?, date_fin=?, description=?, fichier=?, organisation_id=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, comp.getNomComp());
            ps.setString(2, comp.getNomEntreprise());
            // Conversion de LocalDate à java.sql.Date uniquement ici
            ps.setDate(3, comp.getDateDebut() != null ? Date.valueOf(comp.getDateDebut()) : null);
            ps.setDate(4, comp.getDateFin() != null ? Date.valueOf(comp.getDateFin()) : null);
            ps.setString(5, comp.getDescription());
            ps.setString(6, comp.getFichier());
            ps.setInt(7, comp.getOrganisation().getId());
            ps.setInt(8, comp.getId());

            ps.executeUpdate();
            System.out.println("📝 Compétition modifiée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Equipe> getEquipesByCompetition(int idCompetition) {
        List<Equipe> equipes = new ArrayList<>();
        String query =  "SELECT e.* FROM equipe e " +
                "JOIN competition_equipe ce ON e.id = ce.equipe_id " +
                "WHERE ce.competition_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, idCompetition);
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

    public void ajouterEquipeACompetition(int competitionId, int equipeId) throws SQLException {
        String sql = "INSERT INTO competition_equipe (competition_id, equipe_id) VALUES (?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, competitionId);
            ps.setInt(2, equipeId);
            ps.executeUpdate();
        }
    }
    public void supprimerEquipeDeCompetition(int competitionId, int equipeId) throws SQLException {
        String sql = "DELETE FROM competition_equipe WHERE competition_id = ? AND equipe_id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, competitionId);
            ps.setInt(2, equipeId);
            ps.executeUpdate();
        }
    }

}