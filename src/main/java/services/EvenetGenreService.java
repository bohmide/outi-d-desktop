package services;

import entities.EventGenre;
import interfaces.Iservice;
import utils.MyConnection;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class EvenetGenreService implements Iservice<EventGenre> {


    public EventGenre findGenreById(EventGenre entity) {
        try{
            String querry = "SELECT * FROM event_genre WHERE id = ?";

            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);
            prestate.setInt(1, entity.getId());
            ResultSet result = prestate.executeQuery();
            if (result.next()) {
                EventGenre eventGenre = new EventGenre(
                        result.getInt(1),
                        result.getNString(2),
                        result.getInt(3),
                        result.getNString(4),
                        result.getDate(5).toLocalDate()
                );
                return eventGenre;
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public EventGenre findGenreByName(String nom_genre) {
        try{
            String querry = "SELECT * FROM event_genre WHERE nom_genre = ?";

            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);
            prestate.setString(1, nom_genre);
            ResultSet result = prestate.executeQuery();
            if (result.next()) {
                EventGenre eventGenre = new EventGenre(
                        result.getInt(1),
                        result.getNString(2),
                        result.getInt(3),
                        result.getNString(4),
                        result.getDate(5).toLocalDate()
                );
                return eventGenre;
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void addEntity(EventGenre entity) {
        try{
            String querry = "insert into event_genre(nom_genre, nbr, image_path, date_creation) values(?,?,?,?)";

            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);

            prestate.setString(1, entity.getNomGenre());
            prestate.setInt(2, entity.getNbr());
            prestate.setString(3, entity.getImagePath());
            prestate.setDate(4, Date.valueOf(entity.getDateCreation()));

            prestate.executeUpdate();

            System.out.println("Evenet genre added");

        }catch (SQLException e){
            System.out.println("error: "+e.getMessage());

        }
    }

    @Override
    public void updateEntity(EventGenre entity) {
        /*try {
            String querry ="update event_genre set";

        } catch (SQLException e) {
            System.out.println("error: "+e.getMessage());
        }*/

    }

    @Override
    public void deleteEntityById(EventGenre entity) {

        EventGenre eventGenre = findGenreById(entity);
        if(eventGenre == null){
            System.out.println("Genre not found");
        }else{
            try {
                String querry = "delete from event_genre where id = ?";

                PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);

                prestate.setInt(1, eventGenre.getId());
                prestate.executeUpdate();

                System.out.println("Evenet genre deleted");

            } catch (SQLException e) {
                System.out.println("error: "+e.getMessage());
            }
        }
    }

    public void deleteEntityByName(String nom_genre) {

        EventGenre eventGenre = findGenreByName(nom_genre);
        if(eventGenre == null){
            System.out.println("Genre not found");
        }else{
            try {
                String querry = "delete from event_genre where id = ?";

                PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);

                prestate.setInt(1, eventGenre.getId());
                prestate.executeUpdate();

                System.out.println("Evenet genre deleted");

            } catch (SQLException e) {
                System.out.println("error: "+e.getMessage());
            }
        }
    }

    @Override
    public List<EventGenre> listEntity() {
        List<EventGenre> listGenre = new ArrayList<EventGenre>();

        try{
            String querry = "select * from event_genre";
            Statement statement = MyConnection.getInstance().getCnx().createStatement();
            ResultSet result = statement.executeQuery(querry);
            System.out.println("Evenet genre tlamou ");
            while (result.next()){
                EventGenre genre = new EventGenre(
                        result.getInt(1),
                        result.getNString(2),
                        result.getInt(3),
                        result.getNString(4),
                        result.getDate(5).toLocalDate()
                );
                listGenre.add(genre);
            }
            return listGenre;
        }catch (SQLException e){
            System.out.println("error: "+e.getMessage());
        }
        return listGenre;
    }


}
