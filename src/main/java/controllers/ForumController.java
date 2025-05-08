package controllers;

import entities.Forum;
import entities.Post;
import services.ForumService;
import services.PostService;

import java.time.LocalDate;
import java.util.List;

public class ForumController {
    
    private final ForumService forumService;
    private final PostService postService;

    public ForumController() {
        this.forumService = new ForumService();
        this.postService = new PostService();
    }

    public Forum createForum(String name, String theme, String imagePath) {
        Forum existingForum = forumService.findForumByName(name);
        if (existingForum != null) {
            throw new IllegalArgumentException("Error: Forum with name '" + name + "' already exists");
        }
        try {
            Forum forum = new Forum(name, theme, LocalDate.now(), imagePath);
            forumService.addEntity(forum);
            
            return forum;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to create forum: " + e.getMessage());
        }
    }

    public boolean updateForum(int id, String name, String theme, String imagePath) {
        Forum forum = forumService.findForumById(id);
        
        if (forum == null) {
            throw new IllegalArgumentException("Error: Forum with ID " + id + " not found");
        }
        
        try {
            if (name != null && !name.isEmpty() && !name.equals(forum.getNom())) {
                Forum existingForum = forumService.findForumByName(name);
                if (existingForum != null && existingForum.getId() != id) {
                    throw new IllegalArgumentException("Error: Forum with name '" + name + "' already exists");
                }
                forum.setNom(name);
            }

            if (theme != null && !theme.isEmpty()) {
                forum.setTheme(theme);
            }
            
            if (imagePath != null) {
                forum.setImageForum(imagePath);
            }

            forum.validate();
            
            forumService.updateEntityById(forum);
            return true;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Failed to update forum: " + e.getMessage());
        }
    }

    public boolean deleteForum(int id) {
        Forum forum = forumService.findForumById(id);
        
        if (forum == null) {
            throw new IllegalArgumentException("Error: Forum with ID " + id + " not found");
        }

        List<Post> forumPosts = postService.findPostsByForumId(id);
        if (!forumPosts.isEmpty()) {
            System.out.println("Warning: Deleting this forum will also delete " + forumPosts.size() + " posts and all their comments");
        }
        
        forumService.deleteEntityById(forum);
        return true;
    }

    public List<Forum> getAllForums() {
        return forumService.listEntity();
    }

    public Forum getForumById(int id) {
        return forumService.findForumById(id);
    }

    public Forum getForumByName(String name) {
        return forumService.findForumByName(name);
    }

    public List<Forum> getForumsByTheme(String theme) {
        return forumService.findForumsByTheme(theme);
    }
}