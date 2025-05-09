package services;

import entities.Forum;
import entities.Post;
import interfaces.Iservice;
import utils.MyConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostService implements Iservice<Post> {

    @Override
    public void addEntite(Post post) {
        try {
            String query = "INSERT INTO post (nom, date_creation, nb_like, nb_comment, forum_id, contenu) VALUES (?, ?, ?, ?, ?, ?)";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            
            prestate.setString(1, post.getNom());
            prestate.setDate(2, Date.valueOf(post.getDateCreation()));
            prestate.setInt(3, post.getNbLike());
            prestate.setInt(4, post.getNbComment());
            prestate.setInt(5, post.getForumId());
            prestate.setString(6, post.getContenu());
            
            prestate.executeUpdate();
            
            System.out.println("Post added successfully");
            
        } catch (SQLException e) {
            System.out.println("Error adding post: " + e.getMessage());
        }
    }

    @Override
    public void updateEntite(Post post) {
        try {
            String query = "UPDATE post SET nom = ?, date_creation = ?, nb_like = ?, nb_comment = ?, forum_id = ?, contenu = ? WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            
            prestate.setString(1, post.getNom());
            prestate.setDate(2, Date.valueOf(post.getDateCreation()));
            prestate.setInt(3, post.getNbLike());
            prestate.setInt(4, post.getNbComment());
            prestate.setInt(5, post.getForumId());
            prestate.setString(6, post.getContenu());
            prestate.setInt(7, post.getId());
            
            int rowsUpdated = prestate.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Post updated successfully");
            } else {
                System.out.println("No post found with ID: " + post.getId());
            }
            
        } catch (SQLException e) {
            System.out.println("Error updating post: " + e.getMessage());
        }
    }

    @Override
    public void deleteEntite(Post post) {
        try {
            String query = "DELETE FROM post WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            
            prestate.setInt(1, post.getId());
            
            int rowsDeleted = prestate.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Post deleted successfully");
            } else {
                System.out.println("No post found with ID: " + post.getId());
            }
            
        } catch (SQLException e) {
            System.out.println("Error deleting post: " + e.getMessage());
        }
    }

    @Override
    public List<Post> listEntite() {
        List<Post> postList = new ArrayList<>();
        
        try {
            String query = "SELECT * FROM post";
            
            Statement statement = MyConnection.getInstance().getCnx().createStatement();
            ResultSet result = statement.executeQuery(query);
            
            while (result.next()) {
                Post post = new Post(
                    result.getInt("id"),
                    result.getString("nom"),
                    result.getDate("date_creation").toLocalDate(),
                    result.getInt("nb_like"),
                    result.getInt("nb_comment"),
                    result.getInt("forum_id"),
                    result.getString("contenu")
                );
                postList.add(post);
            }
            
        } catch (SQLException e) {
            System.out.println("Error retrieving posts: " + e.getMessage());
        }
        
        return postList;
    }
    
    // Additional methods
    
    public Post findPostById(int id) {
        try {
            String query = "SELECT * FROM post WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            prestate.setInt(1, id);
            
            ResultSet result = prestate.executeQuery();
            
            if (result.next()) {
                return new Post(
                    result.getInt("id"),
                    result.getString("nom"),
                    result.getDate("date_creation").toLocalDate(),
                    result.getInt("nb_like"),
                    result.getInt("nb_comment"),
                    result.getInt("forum_id"),
                    result.getString("contenu")
                );
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding post by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Post> findPostsByForumId(int forumId) {
        List<Post> postList = new ArrayList<>();
        
        try {
            String query = "SELECT * FROM post WHERE forum_id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            prestate.setInt(1, forumId);
            
            ResultSet result = prestate.executeQuery();
            
            while (result.next()) {
                Post post = new Post(
                    result.getInt("id"),
                    result.getString("nom"),
                    result.getDate("date_creation").toLocalDate(),
                    result.getInt("nb_like"),
                    result.getInt("nb_comment"),
                    result.getInt("forum_id"),
                    result.getString("contenu")
                );
                postList.add(post);
            }
            
        } catch (SQLException e) {
            System.out.println("Error finding posts by forum ID: " + e.getMessage());
        }
        
        return postList;
    }
    
    public void incrementLikes(int postId) {
        try {
            String query = "UPDATE post SET nb_like = nb_like + 1 WHERE id = ?";
            
            PreparedStatement prestate = MyConnection.getInstance().getCnx().prepareStatement(query);
            prestate.setInt(1, postId);
            
            int rowsUpdated = prestate.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Post likes incremented successfully");
            } else {
                System.out.println("No post found with ID: " + postId);
            }
            
        } catch (SQLException e) {
            System.out.println("Error incrementing post likes: " + e.getMessage());
        }
    }
    
    public void updateCommentCount(int postId) {
        try {
            // Count comments for this post
            String countQuery = "SELECT COUNT(*) FROM comment WHERE post_id = ?";
            PreparedStatement countStmt = MyConnection.getInstance().getCnx().prepareStatement(countQuery);
            countStmt.setInt(1, postId);
            ResultSet countResult = countStmt.executeQuery();
            
            if (countResult.next()) {
                int commentCount = countResult.getInt(1);
                
                // Update the post with the new comment count
                String updateQuery = "UPDATE post SET nb_comment = ? WHERE id = ?";
                PreparedStatement updateStmt = MyConnection.getInstance().getCnx().prepareStatement(updateQuery);
                updateStmt.setInt(1, commentCount);
                updateStmt.setInt(2, postId);
                
                updateStmt.executeUpdate();
                
                System.out.println("Post comment count updated to: " + commentCount);
            }
            
        } catch (SQLException e) {
            System.out.println("Error updating comment count: " + e.getMessage());
        }
    }
        public void updateLikeCount(int postId) {
            try {
                String updateQuery = "UPDATE post SET nb_like = nb_like + 1 WHERE id = ?";
                PreparedStatement updateStmt = MyConnection.getInstance().getCnx().prepareStatement(updateQuery);
                updateStmt.setInt(1, postId);

                int rowsUpdated = updateStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("Like count updated in database for post ID: " + postId);
                } else {
                    System.out.println("No rows updated, check if the post exists.");
                }
            } catch (SQLException e) {
                System.out.println("Error updating like count: " + e.getMessage());
            }
        }
}