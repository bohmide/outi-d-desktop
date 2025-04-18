package controllers;

import entities.Comment;
import services.CommentService;
import services.PostService;

import java.time.LocalDate;
import java.util.List;

public class CommentController {
    
    private final CommentService commentService;
    private final PostService postService;

    public CommentController() {
        this.commentService = new CommentService();
        this.postService = new PostService();
    }

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

    public boolean deleteComment(int id) {
        Comment comment = commentService.findCommentById(id);
        
        if (comment == null) {
            throw new IllegalArgumentException("Error: Comment with ID " + id + " not found");
        }
        
        commentService.deleteEntityById(comment);
        return true;
    }
    

    public List<Comment> getAllComments() {
        return commentService.listEntity();
    }

    public Comment getCommentById(int id) {
        return commentService.findCommentById(id);
    }

    public List<Comment> getCommentsByPostId(int postId) {
        return commentService.findCommentsByPostId(postId);
    }

    public int getCommentsCountForPost(int postId) {
        return commentService.countCommentsByPostId(postId);
    }
}