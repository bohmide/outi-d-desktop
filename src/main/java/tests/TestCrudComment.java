package tests;

import entities.Comment;
import entities.Forum;
import entities.Post;
import services.CommentService;
import services.ForumService;
import services.PostService;

import java.time.LocalDate;
import java.util.List;

public class TestCrudComment {

    public static void main(String[] args) {
        try {
            CommentService commentService = new CommentService();
            PostService postService = new PostService();
            ForumService forumService = new ForumService();

            System.out.println("========== COMMENT CRUD TESTING ==========");

            // Ensure we have a forum to work with
            System.out.println("\n----- Checking for Forum -----");
            String forumName = "Comment Test Forum";
            Forum existingForum = forumService.findForumByName(forumName);
            Forum forum;
            
            if (existingForum != null) {
                System.out.println("Forum with name " + forumName + " already exists with ID: " + existingForum.getId());
                forum = existingForum;
            } else {
                forum = new Forum();
                forum.setNom(forumName);
                forum.setTheme("Comments");
                forum.setDateCreation(LocalDate.now());
                forum.setImageForum("comments.png");
                forumService.addEntite(forum);
                // Retrieve the forum to get its ID
                forum = forumService.findForumByName(forumName);
                System.out.println("Created forum with ID: " + (forum != null ? forum.getId() : "unknown"));
            }

            if (forum == null) {
                throw new RuntimeException("Failed to create or find a forum for testing");
            }

            // Ensure we have a post to comment on
            System.out.println("\n----- Checking for Post -----");
            Post post = null;
            List<Post> postsByForum = postService.findPostsByForumId(forum.getId());
            for (Post p : postsByForum) {
                if (p.getNom().equals("Comment Test Post")) {
                    post = p;
                    System.out.println("Found existing post with ID: " + post.getId());
                    break;
                }
            }

            if (post == null) {
                System.out.println("Creating new post for comments");
                post = new Post();
                post.setNom("Comment Test Post");
                post.setContenu("This is a post for testing comments.");
                post.setDateCreation(LocalDate.now());
                post.setNbLike(0);
                post.setNbComment(0);
                post.setForumId(forum.getId());
                postService.addEntite(post);
                
                // Find the post by listing all posts
                postsByForum = postService.findPostsByForumId(forum.getId());
                for (Post p : postsByForum) {
                    if (p.getNom().equals("Comment Test Post")) {
                        post = p;
                        System.out.println("Retrieved post with ID: " + post.getId());
                        break;
                    }
                }
            }

            if (post == null) {
                throw new RuntimeException("Failed to create or find a post for testing");
            }

            // Create first comment
            System.out.println("\n----- Creating First Comment -----");
            Comment comment1 = new Comment();
            comment1.setPostId(post.getId());
            comment1.setDescription("This is the first test comment.");
            comment1.setDateCreation(LocalDate.now());
            commentService.addEntite(comment1);
            System.out.println("Comment 1 created");
            
            // Finding the comment by description would require an additional method in the service
            // For now, we'll list all comments and find the matching one
            List<Comment> commentsByPost = commentService.findCommentsByPostId(post.getId());
            for (Comment c : commentsByPost) {
                if (c.getDescription().equals("This is the first test comment.")) {
                    comment1 = c;
                    System.out.println("Retrieved Comment 1 with ID: " + comment1.getId());
                    break;
                }
            }

            // Create second comment
            System.out.println("\n----- Creating Second Comment -----");
            Comment comment2 = new Comment();
            comment2.setPostId(post.getId());
            comment2.setDescription("This is the second test comment.");
            comment2.setDateCreation(LocalDate.now());
            commentService.addEntite(comment2);
            System.out.println("Comment 2 created");
            
            // Find comment2 by listing all comments again
            commentsByPost = commentService.findCommentsByPostId(post.getId());
            for (Comment c : commentsByPost) {
                if (c.getDescription().equals("This is the second test comment.")) {
                    comment2 = c;
                    System.out.println("Retrieved Comment 2 with ID: " + comment2.getId());
                    break;
                }
            }

            // Verify all comments were added
            System.out.println("\n----- All Comments After Creation -----");
            List<Comment> allComments = commentService.listEntite();
            System.out.println("Total comments: " + allComments.size());
            for (Comment comment : allComments) {
                System.out.println("Comment: " + comment.getId() + " - " + comment.getDescription() + " - Post ID: " + comment.getPostId());
            }

            // Verify post comment count was updated
            System.out.println("\n----- Checking Post Comment Count -----");
            Post updatedPost = postService.findPostById(post.getId());
            System.out.println("Post comment count: " + updatedPost.getNbComment());

            // Update first comment
            System.out.println("\n----- Updating First Comment -----");
            comment1.setDescription("This comment has been updated.");
            commentService.updateEntite(comment1);
            System.out.println("Comment 1 updated");

            // Delete second comment
            System.out.println("\n----- Deleting Second Comment -----");
            commentService.deleteEntite(comment2);
            System.out.println("Comment 2 deleted");

            // Verify comment1 exists and comment2 is deleted
            System.out.println("\n----- Final Comments List -----");
            List<Comment> finalComments = commentService.listEntite();
            System.out.println("Total comments: " + finalComments.size());
            for (Comment comment : finalComments) {
                System.out.println("Comment: " + comment.getId() + " - " + comment.getDescription() + " - Post ID: " + comment.getPostId());
            }

            // Verify comment1 details
            Comment updatedComment1 = commentService.findCommentById(comment1.getId());
            if (updatedComment1 != null) {
                System.out.println("\nComment 1 details after update:");
                System.out.println("Description: " + updatedComment1.getDescription());
                System.out.println("Creation Date: " + updatedComment1.getDateCreation());
                System.out.println("Post ID: " + updatedComment1.getPostId());
            }

            // Verify comment2 no longer exists
            Comment deletedComment2 = commentService.findCommentById(comment2.getId());
            if (deletedComment2 == null) {
                System.out.println("\nComment 2 was successfully deleted");
            } else {
                System.out.println("\nComment 2 was not deleted correctly");
            }

            // Verify post comment count was updated after deletion
            System.out.println("\n----- Checking Post Comment Count After Deletion -----");
            Post finalPost = postService.findPostById(post.getId());
            System.out.println("Post comment count: " + finalPost.getNbComment());

            System.out.println("\n========== COMMENT CRUD TESTING COMPLETED ==========");

        } catch (Exception e) {
            System.err.println("Error occurred during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}