package controllers;

import entities.Post;
import services.ForumService;
import services.PostService;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for handling Post-related operations
 */
public class PostController {
    
    private final PostService postService;
    private final ForumService forumService;
    
    /**
     * Constructor initializing required services
     */
    public PostController() {
        this.postService = new PostService();
        this.forumService = new ForumService();
    }
    
    /**
     * Add a new post to a forum
     * 
     * @param forumId The ID of the forum to add the post to
     * @param title The title of the post
     * @param content The content of the post
     * @return The created post object
     * @throws IllegalArgumentException if validation fails
     */
    public Post createPost(int forumId, String title, String content) {
        // Verify forum exists
        if (forumService.findForumById(forumId) == null) {
            throw new IllegalArgumentException("Error: Forum with ID " + forumId + " does not exist");
        }
        
        try {
            // Create new post with initial values (0 likes, 0 comments)
            // Validation happens in constructor via setters
            Post post = new Post(title, LocalDate.now(), 0, 0, forumId, content);
            
            // Add post using service
            postService.addEntity(post);
            
            return post;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to create post: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing post
     * 
     * @param id Post ID
     * @param title New title for the post (or null to keep current)
     * @param content New content for the post (or null to keep current)
     * @return True if update successful
     * @throws IllegalArgumentException if validation fails or post not found
     */
    public boolean updatePost(int id, String title, String content) {
        Post post = postService.findPostById(id);
        
        if (post == null) {
            throw new IllegalArgumentException("Error: Post with ID " + id + " not found");
        }
        
        try {
            // Update only if new values provided
            if (title != null && !title.isEmpty()) {
                post.setNom(title);
            }
            
            if (content != null && !content.isEmpty()) {
                post.setContenu(content);
            }
            
            // Validate the full object
            post.validate();
            
            postService.updateEntityById(post);
            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to update post: " + e.getMessage());
        }
    }
    
    /**
     * Delete a post by its ID
     * 
     * @param id Post ID to delete
     * @return True if deletion successful
     * @throws IllegalArgumentException if post not found
     */
    public boolean deletePost(int id) {
        Post post = postService.findPostById(id);
        
        if (post == null) {
            throw new IllegalArgumentException("Error: Post with ID " + id + " not found");
        }
        
        postService.deleteEntityById(post);
        return true;
    }
    
    /**
     * Get all posts
     * 
     * @return List of all posts
     */
    public List<Post> getAllPosts() {
        return postService.listEntity();
    }
    
    /**
     * Get a specific post by its ID
     * 
     * @param id Post ID
     * @return Post object if found, null otherwise
     */
    public Post getPostById(int id) {
        return postService.findPostById(id);
    }
    
    /**
     * Get all posts for a specific forum
     * 
     * @param forumId Forum ID
     * @return List of posts for the specified forum
     */
    public List<Post> getPostsByForumId(int forumId) {
        return postService.findPostsByForumId(forumId);
    }
    
    /**
     * Add a like to a post
     * 
     * @param id Post ID to like
     * @return True if like added successfully
     * @throws IllegalArgumentException if post not found
     */
    public boolean likePost(int id) {
        Post post = postService.findPostById(id);
        
        if (post == null) {
            throw new IllegalArgumentException("Error: Post with ID " + id + " not found");
        }
        
        postService.incrementLikes(id);
        return true;
    }
}