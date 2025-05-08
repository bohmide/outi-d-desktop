package services;

import entities.Comment;
import interfaces.Iservice;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentService implements Iservice<Comment> {

    @Override
    public void addEntity(Comment comment) {
        try {
            String query = "INSERT INTO comment (post_id, description, date_creation) VALUES (?, ?, ?)";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            
            prestate.setInt(1, comment.getPostId());
            prestate.setString(2, comment.getDescription());
            prestate.setDate(3, Date.valueOf(comment.getDateCreation()));
            
            prestate.executeUpdate();
            
            System.out.println("Comment added successfully");
            
            // Update comment count in post
            PostService postService = new PostService();
            postService.updateCommentCount(comment.getPostId());
            
        } catch (SQLException e) {
            System.out.println("Error adding comment: " + e.getMessage());
        }
    }

    @Override
    public void updateEntityById(Comment comment) {
        try {
            String query = "UPDATE comment SET post_id = ?, description = ?, date_creation = ? WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            
            prestate.setInt(1, comment.getPostId());
            prestate.setString(2, comment.getDescription());
            prestate.setDate(3, Date.valueOf(comment.getDateCreation()));
            prestate.setInt(4, comment.getId());
            
            int rowsUpdated = prestate.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Comment updated successfully");
            } else {
                System.out.println("No comment found with ID: " + comment.getId());
            }
            
        } catch (SQLException e) {
            System.out.println("Error updating comment: " + e.getMessage());
        }
    }

    @Override
    public void deleteEntityById(Comment comment) {
        try {
            int postId = comment.getPostId(); // Store post ID before deletion
            
            String query = "DELETE FROM comment WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            
            prestate.setInt(1, comment.getId());
            
            int rowsDeleted = prestate.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Comment deleted successfully");
                
                // Update comment count in post
                PostService postService = new PostService();
                postService.updateCommentCount(postId);
            } else {
                System.out.println("No comment found with ID: " + comment.getId());
            }
            
        } catch (SQLException e) {
            System.out.println("Error deleting comment: " + e.getMessage());
        }
    }

    @Override
    public List<Comment> listEntity() {
        List<Comment> commentList = new ArrayList<>();
        
        try {
            String query = "SELECT * FROM comment";
            
            Statement statement = MyConnection.getInstance().getCnx().createStatement();
            ResultSet result = statement.executeQuery(query);
            
            while (result.next()) {
                Comment comment = new Comment(
                    result.getInt("id"),
                    result.getInt("post_id"),
                    result.getString("description"),
                    result.getDate("date_creation").toLocalDate()
                );
                commentList.add(comment);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving comments: " + e.getMessage());
        }
        
        return commentList;
    }
    
    // Additional methods
    
    public Comment findCommentById(int id) {
        try {
            String query = "SELECT * FROM comment WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            prestate.setInt(1, id);
            
            ResultSet result = prestate.executeQuery();
            
            if (result.next()) {
                return new Comment(
                    result.getInt("id"),
                    result.getInt("post_id"),
                    result.getString("description"),
                    result.getDate("date_creation").toLocalDate()
                );
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding comment by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Comment> findCommentsByPostId(int postId) {
        List<Comment> commentList = new ArrayList<>();
        
        try {
            String query = "SELECT * FROM comment WHERE post_id = ? ORDER BY date_creation DESC";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            prestate.setInt(1, postId);
            
            ResultSet result = prestate.executeQuery();
            
            while (result.next()) {
                Comment comment = new Comment(
                    result.getInt("id"),
                    result.getInt("post_id"),
                    result.getString("description"),
                    result.getDate("date_creation").toLocalDate()
                );
                commentList.add(comment);
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding comments by post ID: " + e.getMessage());
        }
        
        return commentList;
    }
    
    public int countCommentsByPostId(int postId) {
        try {
            String query = "SELECT COUNT(*) FROM comment WHERE post_id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            prestate.setInt(1, postId);
            
            ResultSet result = prestate.executeQuery();
            
            if (result.next()) {
                return result.getInt(1);
            }
            
        } catch (SQLException e) {
            System.out.println("Error counting comments by post ID: " + e.getMessage());
        }
        
        return 0;
    }
}