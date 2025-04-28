package controllers;

import entities.Post;
import services.ForumService;
import services.PostService;

import java.time.LocalDate;
import java.util.List;

public class PostController {
    
    private final PostService postService;
    private final ForumService forumService;

    public PostController() {
        this.postService = new PostService();
        this.forumService = new ForumService();
    }

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
    public void updateLike(int postId) {
        try {
            // Call the PostService to update the like count
            postService.updateLikeCount(postId);
        } catch (Exception e) {
            System.out.println("Error updating like count: " + e.getMessage());
        }
    }

    public boolean deletePost(int id) {
        Post post = postService.findPostById(id);
        
        if (post == null) {
            throw new IllegalArgumentException("Error: Post with ID " + id + " not found");
        }
        postService.deleteEntityById(post);
        return true;
    }

    public List<Post> getAllPosts() {
        return postService.listEntity();
    }
    

    public Post getPostById(int id) {
        return postService.findPostById(id);
    }

    public List<Post> getPostsByForumId(int forumId) {
        return postService.findPostsByForumId(forumId);
    }

    public boolean likePost(int id) {
        Post post = postService.findPostById(id);
        
        if (post == null) {
            throw new IllegalArgumentException("Error: Post with ID " + id + " not found");
        }
        
        postService.incrementLikes(id);
        return true;
    }
}