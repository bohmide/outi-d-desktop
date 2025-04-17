package controllers;

import entities.Forum;
import entities.Post;
import services.ForumService;
import services.PostService;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for handling Forum-related operations
 */
public class ForumController {
    
    private final ForumService forumService;
    private final PostService postService;
    
    /**
     * Constructor initializing required services
     */
    public ForumController() {
        this.forumService = new ForumService();
        this.postService = new PostService();
    }
    
    /**
     * Create a new forum
     * 
     * @param name The name of the forum
     * @param theme The theme/category of the forum
     * @param imagePath Path to the forum image (optional)
     * @return The created forum object
     * @throws IllegalArgumentException if validation fails or forum with same name exists
     */
    public Forum createForum(String name, String theme, String imagePath) {
        // Check if forum with same name already exists
        Forum existingForum = forumService.findForumByName(name);
        if (existingForum != null) {
            throw new IllegalArgumentException("Error: Forum with name '" + name + "' already exists");
        }
        
        try {
            // Create new forum with current date
            // Validation happens in constructor via setters
            Forum forum = new Forum(name, theme, LocalDate.now(), imagePath);
            
            // Add forum using service
            forumService.addEntity(forum);
            
            return forum;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to create forum: " + e.getMessage());
        }
    }
    
    /**
     * Update an existing forum
     * 
     * @param id Forum ID
     * @param name New name for the forum (or null to keep current)
     * @param theme New theme for the forum (or null to keep current)
     * @param imagePath New image path for the forum (or null to keep current)
     * @return True if update successful
     * @throws IllegalArgumentException if validation fails, forum not found, or name already exists
     */
    public boolean updateForum(int id, String name, String theme, String imagePath) {
        Forum forum = forumService.findForumById(id);
        
        if (forum == null) {
            throw new IllegalArgumentException("Error: Forum with ID " + id + " not found");
        }
        
        try {
            // Check if new name is already used by another forum
            if (name != null && !name.isEmpty() && !name.equals(forum.getNom())) {
                Forum existingForum = forumService.findForumByName(name);
                if (existingForum != null && existingForum.getId() != id) {
                    throw new IllegalArgumentException("Error: Forum with name '" + name + "' already exists");
                }
                forum.setNom(name);
            }
            
            // Update only if new values provided
            if (theme != null && !theme.isEmpty()) {
                forum.setTheme(theme);
            }
            
            if (imagePath != null) {
                forum.setImageForum(imagePath);
            }
            
            // Validate the full object
            forum.validate();
            
            forumService.updateEntityById(forum);
            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to update forum: " + e.getMessage());
        }
    }
    
    /**
     * Delete a forum by its ID
     * 
     * @param id Forum ID to delete
     * @return True if deletion successful
     * @throws IllegalArgumentException if forum not found
     */
    public boolean deleteForum(int id) {
        Forum forum = forumService.findForumById(id);
        
        if (forum == null) {
            throw new IllegalArgumentException("Error: Forum with ID " + id + " not found");
        }
        
        // Get posts related to this forum
        List<Post> forumPosts = postService.findPostsByForumId(id);
        if (!forumPosts.isEmpty()) {
            System.out.println("Warning: Deleting this forum will also delete " + forumPosts.size() + " posts and all their comments");
        }
        
        forumService.deleteEntityById(forum);
        return true;
    }
    
    /**
     * Get all forums
     * 
     * @return List of all forums
     */
    public List<Forum> getAllForums() {
        return forumService.listEntity();
    }
    
    /**
     * Get a specific forum by its ID
     * 
     * @param id Forum ID
     * @return Forum object if found, null otherwise
     */
    public Forum getForumById(int id) {
        return forumService.findForumById(id);
    }
    
    /**
     * Get a specific forum by its name
     * 
     * @param name Forum name
     * @return Forum object if found, null otherwise
     */
    public Forum getForumByName(String name) {
        return forumService.findForumByName(name);
    }
    
    /**
     * Get all forums with a specific theme
     * 
     * @param theme Theme to search for
     * @return List of forums with the specified theme
     */
    public List<Forum> getForumsByTheme(String theme) {
        return forumService.findForumsByTheme(theme);
    }
}