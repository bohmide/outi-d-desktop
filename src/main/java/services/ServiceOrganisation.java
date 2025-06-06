package services;

import entities.Organisation;
import interfaces.IServiceOrganisation;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceOrganisation implements IServiceOrganisation {
    private Connection cnx = MyConnection.getInstance().getCnx();

    @Override
    public void ajouterOrganisation(Organisation org) {
        String sql = "INSERT INTO organisation(nom_organisation, domaine) VALUES (?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, org.getNomOrganisation());
            ps.setString(2, org.getDomaine());
            ps.executeUpdate();
            System.out.println("Organisation ajoutée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Organisation> afficherOrganisations() {
        List<Organisation> list = new ArrayList<>();
        String sql = "SELECT * FROM organisation";
        try (Statement stmt = cnx.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Organisation o = new Organisation(
                        rs.getInt("id"),
                        rs.getString("nom_organisation"),
                        rs.getString("domaine")
                );
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    @Override
    public boolean supprimerOrganisation(int id) {
        String deleteCompetitions = "DELETE FROM competition WHERE organisation_id = ?";
        String deleteOrganisation = "DELETE FROM organisation WHERE id = ?";

        try (PreparedStatement ps1 = cnx.prepareStatement(deleteCompetitions);
             PreparedStatement ps2 = cnx.prepareStatement(deleteOrganisation))
        {
            // Supprimer les compétitions liées à l'organisation
            ps1.setInt(1, id);
            ps1.executeUpdate();

            // Supprimer l'organisation
            ps2.setInt(1, id);
            int rowsAffected = ps2.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // En cas d'erreur
        }
    }


    @Override
    public void modifierOrganisation(Organisation org) {
        String sql = "UPDATE organisation SET nom_organisation=?, domaine=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, org.getNomOrganisation());
            ps.setString(2, org.getDomaine());
            ps.setInt(3, org.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
