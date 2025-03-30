package services;

import entities.Sponsors;
import utils.MyConnection;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class SponsorsService {

    Sponsors findSponsorById(Sponsors sponsor) {
        try{
            String querry = "SELECT * FROM SPONSORS WHERE id = ?";

            PreparedStatement preparedStatement = MyConnection.getInstance().getCnx().prepareStatement(querry);
            preparedStatement.setInt(1, sponsor.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                return new Sponsors(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5).toLocalDate()
                );
            }

        }catch (SQLException e){
            System.out.printf("Error in findSponsorById"+ e.getMessage());
        }
        return null;
    }

    public Sponsors findSponsorByName(String nom_sponsor){
        try{
            String querry = "SELECT * FROM SPONSORS WHERE nom_sponsor = ?";

            PreparedStatement preparedStatement = MyConnection.getInstance().getCnx().prepareStatement(querry);
            preparedStatement.setString(1, nom_sponsor);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                return new Sponsors(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5).toLocalDate()
                );
            }

        }catch (SQLException e){
            System.out.printf("Error in findSponsorById"+ e.getMessage());
        }
        return null;
    }

    List<Sponsors> listSponsor(){

        ArrayList<Sponsors> listSponsor = new ArrayList<>();
        try{
            String querry = "SELECT * FROM SPONSORS";

            Statement statement = MyConnection.getInstance().getCnx().createStatement();

            ResultSet resultSet = statement.executeQuery(querry);
            while(resultSet.next()){
                Sponsors newSponsor = new Sponsors(
                        resultSet.getInt(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4),
                        resultSet.getDate(5).toLocalDate()
                );
                listSponsor.add(newSponsor);
            }
            return listSponsor;
        }catch (SQLException e){
            System.out.printf("Error in findSponsorById"+ e.getMessage());
        }
        return null;
    }

    public void addSponsor(Sponsors sponsor){
        try{
            String querry = "INSERT INTO SPONSORS(id, nom_sponsor, description, image_path, date_creation) VALUES(?,?,?,?,?)";

            PreparedStatement preparedStatement = MyConnection.getInstance().getCnx().prepareStatement(querry);

            preparedStatement.setInt(1, sponsor.getId());
            preparedStatement.setString(2, sponsor.getNomSponsor());
            preparedStatement.setString(3, sponsor.getDescription());
            preparedStatement.setString(4, sponsor.getImagePath());
            preparedStatement.setDate(5, Date.valueOf(sponsor.getDateCreation()));

            preparedStatement.executeUpdate();
            System.out.println("Sponsor added");

        }catch (SQLException e){
            System.out.printf("Error in findSponsorById"+ e.getMessage());
        }
    }

    public void updateSponsor(Sponsors sponsor){

        try{
            String querry = "UPDATE sponsors SET nom_sponsor = ?, description = ?, image_path = ?, date_creation = ? WHERE id = ?";

            PreparedStatement preparedStatement = MyConnection.getInstance().getCnx().prepareStatement(querry);

            preparedStatement.setInt(5, sponsor.getId());
            preparedStatement.setString(1, sponsor.getNomSponsor());
            preparedStatement.setString(2, sponsor.getDescription());
            preparedStatement.setString(3, sponsor.getImagePath());
            preparedStatement.setDate(4, Date.valueOf(sponsor.getDateCreation()));

            preparedStatement.executeUpdate();
            System.out.println("Sponsor UPDATED");

        }catch (SQLException e){
            System.out.printf("Error in findSponsorById"+ e.getMessage());
        }
    }

    public void deleteSponsor(Sponsors sponsor){

        try{
            String querry = "DELETE FROM SPONSORS WHERE id = ?";

            PreparedStatement preparedStatement = MyConnection.getInstance().getCnx().prepareStatement(querry);

            preparedStatement.setInt(1, sponsor.getId());

            preparedStatement.executeUpdate();
            System.out.println("Sponsor Deleted");

        }catch (SQLException e){
            System.out.printf("Error"+ e.getMessage());
        }
    }



}
