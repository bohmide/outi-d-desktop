package services;

import entities.EventGenre;
import utils.MyConnection;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class EventGenreService {


    public EventGenre findGenre(EventGenre genre) {
        try{
            String querry = "SELECT * FROM event_genre WHERE id = ?";

            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);
            prestate.setInt(1, genre.getId());
            ResultSet result = prestate.executeQuery();
            if (result.next()) {
                return new EventGenre(
                        result.getInt(1),
                        result.getNString(2),
                        result.getInt(3),
                        result.getNString(4),
                        result.getDate(5).toLocalDate()
                );
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public EventGenre findGenreById(int genreId) {
        try{
            String querry = "SELECT * FROM event_genre WHERE id = ?";

            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);
            prestate.setInt(1, genreId);
            ResultSet result = prestate.executeQuery();
            if (result.next()) {
                return new EventGenre(
                        result.getInt(1),
                        result.getNString(2),
                        result.getInt(3),
                        result.getNString(4),
                        result.getDate(5).toLocalDate()
                );
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
                return new EventGenre(
                        result.getInt(1),
                        result.getNString(2),
                        result.getInt(3),
                        result.getNString(4),
                        result.getDate(5).toLocalDate()
                );
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<EventGenre> listEventGenre() {
        List<EventGenre> listGenre = new ArrayList<>();

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

    public void addEventGenre(EventGenre genre) {
        try{
            String querry = "insert into event_genre(nom_genre, nbr, image_path, date_creation) values(?,?,?,?)";

            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);

            prestate.setString(1, genre.getNomGenre());
            prestate.setInt(2, genre.getNbr());
            prestate.setString(3, genre.getImagePath());
            prestate.setDate(4, Date.valueOf(genre.getDateCreation()));

            prestate.executeUpdate();

            System.out.println("Evenet genre added");

        }catch (SQLException e){
            System.out.println("error: "+e.getMessage());

        }
    }

    public void updateEventGenre(EventGenre genre) {
        EventGenre eventGenre = findGenre(genre);
        if (eventGenre == null) {
            System.out.println("Evenet genre not found");
        }else{
            try {
                String querry ="update event_genre set nom_genre = ? and nbr = ? and image_path = ? and date_creation = ? where id = ?";

                PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);

                prestate.setString(1, genre.getNomGenre());
                prestate.setInt(2, genre.getNbr());
                prestate.setString(3, genre.getImagePath());
                prestate.setDate(4, Date.valueOf(genre.getDateCreation()));
                prestate.setInt(5, eventGenre.getId());

                prestate.executeUpdate();

                System.out.println("Evenet genre updated");

            } catch (SQLException e) {
                System.out.println("error: "+e.getMessage());
            }
        }

    }

    public void deleteEventGenre(EventGenre genre) {

        EventGenre eventGenre = findGenre(genre);
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

    public void deleteEventGenreById(int genreId) {

        EventGenre eventGenre = findGenreById(genreId);
        if(eventGenre == null){
            System.out.println("Genre not found");
        }else{
            try {
                String querry = "delete from event_genre where id = ?";

                PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(querry);

                prestate.setInt(1, genreId);
                prestate.executeUpdate();

                System.out.println("Evenet genre deleted");

            } catch (SQLException e) {
                System.out.println("error: "+e.getMessage());
            }
        }
    }

    public void deleteEventGenreByName(String nom_genre) {

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
}
