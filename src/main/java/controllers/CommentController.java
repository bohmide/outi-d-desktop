package controllers;

import entities.Comment;
import services.CommentService;
import services.PostService;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for handling Comment-related operations
 */
public class CommentController {
    
    private final CommentService commentService;
    private final PostService postService;
    
    /**
     * Constructor initializing required services
     */
    public CommentController() {
        this.commentService = new CommentService();
        this.postService = new PostService();
    }
    
    /**
     * Add a new comment to a post
     * 
     * @param postId The ID of the post to comment on
     * @param description The content of the comment
     * @return The created comment object
     * @throws IllegalArgumentException if validation fails
     */
    public Comment addComment(int postId, String description) {
        // Verify post exists
        if (postService.findPostById(postId) == null) {
            throw new IllegalArgumentException("Error: Post with ID " + postId + " does not exist");
        }
        
        try {
            // Create new comment (validation happens in constructor)
            Comment comment = new Comment(postId, description, LocalDate.now());
            
            // Add comment using service
            commentService.addEntity(comment);
            
            return comment;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to add comment: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing comment
     * 
     * @param id Comment ID
     * @param description New content for the comment
     * @return True if update successful, false otherwise
     * @throws IllegalArgumentException if validation fails or comment not found
     */
    public boolean updateComment(int id, String description) {
        Comment comment = commentService.findCommentById(id);
        
        if (comment == null) {
            throw new IllegalArgumentException("Error: Comment with ID " + id + " not found");
        }
        
        try {
            comment.setDescription(description);
            // Validate the full object
            comment.validate();
            commentService.updateEntityById(comment);
            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to update comment: " + e.getMessage());
        }
    }
    
    /**
     * Delete a comment by its ID
     * 
     * @param id Comment ID to delete
     * @return True if deletion successful
     * @throws IllegalArgumentException if comment not found
     */
    public boolean deleteComment(int id) {
        Comment comment = commentService.findCommentById(id);
        
        if (comment == null) {
            throw new IllegalArgumentException("Error: Comment with ID " + id + " not found");
        }
        
        commentService.deleteEntityById(comment);
        return true;
    }
    
    /**
     * Get all comments
     * 
     * @return List of all comments
     */
    public List<Comment> getAllComments() {
        return commentService.listEntity();
    }
    
    /**
     * Get a specific comment by its ID
     * 
     * @param id Comment ID
     * @return Comment object if found, null otherwise
     */
    public Comment getCommentById(int id) {
        return commentService.findCommentById(id);
    }
    
    /**
     * Get all comments for a specific post
     * 
     * @param postId Post ID
     * @return List of comments for the specified post
     */
    public List<Comment> getCommentsByPostId(int postId) {
        return commentService.findCommentsByPostId(postId);
    }
    
    /**
     * Get comment count for a specific post
     * 
     * @param postId Post ID
     * @return Number of comments on the post
     */
    public int getCommentsCountForPost(int postId) {
        return commentService.countCommentsByPostId(postId);
    }
}