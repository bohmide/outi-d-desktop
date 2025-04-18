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

                list.add(comp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public void supprimerCompetition(int id) {
        // Pas de changement nécessaire pour la suppression
        String sql = "DELETE FROM competition WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("🗑️ Compétition supprimée !");
        } catch (SQLException e) {
            e.printStackTrace();
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
        String query = "SELECT e.id, e.nom_equipe FROM equipe e " +
                "JOIN competition_equipe ce ON e.id = ce.equipe_id " +
                "WHERE ce.competition_id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setInt(1, idCompetition);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Equipe equipe = new Equipe();
                equipe.setId(rs.getInt("id"));
                equipe.setNomEquipe(rs.getString("nom_equipe")); // Maintenant cette méthode existe
                equipes.add(equipe);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des équipes: " + e.getMessage());
        }
        return equipes;
    }
}