package services;

import entities.Forum;
import interfaces.Iservice;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForumService implements Iservice<Forum> {

    @Override
    public void addEntite(Forum forum) {
        try {
            String query = "INSERT INTO forum (nom, theme, date_creation, image_forum) VALUES (?, ?, ?, ?)";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            
            prestate.setString(1, forum.getNom());
            prestate.setString(2, forum.getTheme());
            prestate.setDate(3, Date.valueOf(forum.getDateCreation()));
            prestate.setString(4, forum.getImageForum());
            
            prestate.executeUpdate();
            
            System.out.println("Forum added successfully");
            
        } catch (SQLException e) {
            System.out.println("Error adding forum: " + e.getMessage());
        }
    }

    @Override
    public void updateEntite(Forum forum) {
        try {
            String query = "UPDATE forum SET nom = ?, theme = ?, date_creation = ?, image_forum = ? WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            
            prestate.setString(1, forum.getNom());
            prestate.setString(2, forum.getTheme());
            prestate.setDate(3, Date.valueOf(forum.getDateCreation()));
            prestate.setString(4, forum.getImageForum());
            prestate.setInt(5, forum.getId());
            
            int rowsUpdated = prestate.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Forum updated successfully");
            } else {
                System.out.println("No forum found with ID: " + forum.getId());
            }
            
        } catch (SQLException e) {
            System.out.println("Error updating forum: " + e.getMessage());
        }
    }

    @Override
    public void deleteEntite(Forum forum) {
        try {
            String query = "DELETE FROM forum WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            
            prestate.setInt(1, forum.getId());
            
            int rowsDeleted = prestate.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Forum deleted successfully");
            } else {
                System.out.println("No forum found with ID: " + forum.getId());
            }
            
        } catch (SQLException e) {
            System.out.println("Error deleting forum: " + e.getMessage());
        }
    }

    @Override
    public List<Forum> listEntite() {
        List<Forum> forumList = new ArrayList<>();
        
        try {
            String query = "SELECT * FROM forum";
            
            Statement statement = MyConnection.getInstance().getCnx().createStatement();
            ResultSet result = statement.executeQuery(query);
            
            while (result.next()) {
                Forum forum = new Forum(
                    result.getInt("id"),
                    result.getString("nom"),
                    result.getString("theme"),
                    result.getDate("date_creation").toLocalDate(),
                    result.getString("image_forum")
                );
                forumList.add(forum);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving forums: " + e.getMessage());
        }
        
        return forumList;
    }
    
    // Additional methods
    
    public Forum findForumById(int id) {
        try {
            String query = "SELECT * FROM forum WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            prestate.setInt(1, id);
            
            ResultSet result = prestate.executeQuery();
            
            if (result.next()) {
                return new Forum(
                    result.getInt("id"),
                    result.getString("nom"),
                    result.getString("theme"),
                    result.getDate("date_creation").toLocalDate(),
                    result.getString("image_forum")
                );
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding forum by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public Forum findForumByName(String name) {
        try {
            String query = "SELECT * FROM forum WHERE nom = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            prestate.setString(1, name);
            
            ResultSet result = prestate.executeQuery();
            
            if (result.next()) {
                return new Forum(
                    result.getInt("id"),
                    result.getString("nom"),
                    result.getString("theme"),
                    result.getDate("date_creation").toLocalDate(),
                    result.getString("image_forum")
                );
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding forum by name: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Forum> findForumsByTheme(String theme) {
        List<Forum> forumList = new ArrayList<>();
        
        try {
            String query = "SELECT * FROM forum WHERE theme = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            prestate.setString(1, theme);
            
            ResultSet result = prestate.executeQuery();
            
            while (result.next()) {
                Forum forum = new Forum(
                    result.getInt("id"),
                    result.getString("nom"),
                    result.getString("theme"),
                    result.getDate("date_creation").toLocalDate(),
                    result.getString("image_forum")
                );
                forumList.add(forum);
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding forums by theme: " + e.getMessage());
        }
        
        return forumList;
    }
}