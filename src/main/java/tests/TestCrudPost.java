package tests;

import entities.Forum;
import entities.Post;
import services.ForumService;
import services.PostService;

import java.time.LocalDate;
import java.util.List;

public class TestCrudPost {

    public static void main(String[] args) {
        try {
            PostService postService = new PostService();
            ForumService forumService = new ForumService();

            System.out.println("========== POST CRUD TESTING ==========");

            // Ensure we have a forum to work with
            System.out.println("\n----- Checking for Forum -----");
            String forumName = "Testing Forum";
            Forum existingForum = forumService.findForumByName(forumName);
            Forum forum;
            
            if (existingForum != null) {
                System.out.println("Forum with name " + forumName + " already exists with ID: " + existingForum.getId());
                forum = existingForum;
            } else {
                forum = new Forum();
                forum.setNom(forumName);
                forum.setTheme("Testing");
                forum.setDateCreation(LocalDate.now());
                forum.setImageForum("test.png");
                forumService.addEntite(forum);
                // Retrieve the forum to get its ID
                forum = forumService.findForumByName(forumName);
                System.out.println("Created forum with ID: " + (forum != null ? forum.getId() : "unknown"));
            }

            if (forum == null) {
                throw new RuntimeException("Failed to create or find a forum for testing");
            }

            // Create first post
            System.out.println("\n----- Creating First Post -----");
            Post post1 = new Post();
            post1.setNom("First Test Post");
            post1.setContenu("This is the content of the first test post.");
            post1.setDateCreation(LocalDate.now());
            post1.setNbLike(0);
            post1.setNbComment(0);
            post1.setForumId(forum.getId());
            postService.addEntite(post1);
            System.out.println("Post 1 created");
            
            // Finding the post by name would require an additional method in the service
            // For now, we'll list all posts and find the matching one
            List<Post> postsByForum = postService.findPostsByForumId(forum.getId());
            for (Post p : postsByForum) {
                if (p.getNom().equals("First Test Post")) {
                    post1 = p;
                    System.out.println("Retrieved Post 1 with ID: " + post1.getId());
                    break;
                }
            }

            // Create second post
            System.out.println("\n----- Creating Second Post -----");
            Post post2 = new Post();
            post2.setNom("Second Test Post");
            post2.setContenu("This is the content of the second test post.");
            post2.setDateCreation(LocalDate.now());
            post2.setNbLike(0);
            post2.setNbComment(0);
            post2.setForumId(forum.getId());
            postService.addEntite(post2);
            System.out.println("Post 2 created");
            
            // Find post2 by listing all posts again
            postsByForum = postService.findPostsByForumId(forum.getId());
            for (Post p : postsByForum) {
                if (p.getNom().equals("Second Test Post")) {
                    post2 = p;
                    System.out.println("Retrieved Post 2 with ID: " + post2.getId());
                    break;
                }
            }

            // Verify all posts were added
            System.out.println("\n----- All Posts After Creation -----");
            List<Post> allPosts = postService.listEntite();
            System.out.println("Total posts: " + allPosts.size());
            for (Post post : allPosts) {
                System.out.println("Post: " + post.getId() + " - " + post.getNom() + " - Forum ID: " + post.getForumId());
            }

            // Update first post
            System.out.println("\n----- Updating First Post -----");
            post1.setNom("Updated First Post");
            post1.setContenu("This content has been updated.");
            postService.updateEntite(post1);
            System.out.println("Post 1 updated");

            // Increment likes on first post
            System.out.println("\n----- Incrementing Likes on First Post -----");
            postService.incrementLikes(post1.getId());
            System.out.println("Post 1 likes incremented");

            // Delete second post
            System.out.println("\n----- Deleting Second Post -----");
            postService.deleteEntite(post2);
            System.out.println("Post 2 deleted");

            // Verify post1 exists and post2 is deleted
            System.out.println("\n----- Final Posts List -----");
            List<Post> finalPosts = postService.listEntite();
            System.out.println("Total posts: " + finalPosts.size());
            for (Post post : finalPosts) {
                System.out.println("Post: " + post.getId() + " - " + post.getNom() + " - Forum ID: " + post.getForumId());
            }

            // Verify post1 details
            Post updatedPost1 = postService.findPostById(post1.getId());
            if (updatedPost1 != null) {
                System.out.println("\nPost 1 details after update:");
                System.out.println("Name: " + updatedPost1.getNom());
                System.out.println("Content: " + updatedPost1.getContenu());
                System.out.println("Likes: " + updatedPost1.getNbLike());
                System.out.println("Creation Date: " + updatedPost1.getDateCreation());
            }

            // Verify post2 no longer exists
            Post deletedPost2 = postService.findPostById(post2.getId());
            if (deletedPost2 == null) {
                System.out.println("\nPost 2 was successfully deleted");
            } else {
                System.out.println("\nPost 2 was not deleted correctly");
            }

            System.out.println("\n========== POST CRUD TESTING COMPLETED ==========");

        } catch (Exception e) {
            System.err.println("Error occurred during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}