package tests;

import entities.Forum;
import services.ForumService;

import java.time.LocalDate;
import java.util.List;

public class TestCrudForum {

    public static void main(String[] args) {
        try {
            ForumService forumService = new ForumService();

            System.out.println("========== FORUM CRUD TESTING ==========");

            // Check and create first dummy forum
            System.out.println("\n----- Creating First Forum -----");
            String forumName1 = "Gaming Discussion";
            Forum existingForum1 = forumService.findForumByName(forumName1);
            Forum forum1;
            
            if (existingForum1 != null) {
                System.out.println("Forum with name " + forumName1 + " already exists with ID: " + existingForum1.getId());
                forum1 = existingForum1;
            } else {
                forum1 = new Forum();
                forum1.setNom(forumName1);
                forum1.setTheme("Video Games");
                forum1.setDateCreation(LocalDate.now());
                forum1.setImageForum("gaming.png");
                forumService.addEntity(forum1);
                // Retrieve the forum to get its ID
                forum1 = forumService.findForumByName(forumName1);
                System.out.println("Forum 1 created with ID: " + (forum1 != null ? forum1.getId() : "unknown"));
            }

            // Check and create second dummy forum
            System.out.println("\n----- Creating Second Forum -----");
            String forumName2 = "Tech Support";
            Forum existingForum2 = forumService.findForumByName(forumName2);
            Forum forum2;
            
            if (existingForum2 != null) {
                System.out.println("Forum with name " + forumName2 + " already exists with ID: " + existingForum2.getId());
                forum2 = existingForum2;
            } else {
                forum2 = new Forum();
                forum2.setNom(forumName2);
                forum2.setTheme("Technology");
                forum2.setDateCreation(LocalDate.now());
                forum2.setImageForum("tech.png");
                forumService.addEntity(forum2);
                // Retrieve the forum to get its ID
                forum2 = forumService.findForumByName(forumName2);
                System.out.println("Forum 2 created with ID: " + (forum2 != null ? forum2.getId() : "unknown"));
            }

            // Verify all forums were added
            System.out.println("\n----- All Forums After Creation -----");
            List<Forum> allForums = forumService.listEntity();
            System.out.println("Total forums: " + allForums.size());
            for (Forum forum : allForums) {
                System.out.println("Forum: " + forum.getId() + " - " + forum.getNom() + " - " + forum.getTheme());
            }

            // Update first forum
            System.out.println("\n----- Updating First Forum -----");
            if (forum1 != null) {
                forum1.setNom("Gaming & eSports Discussion");
                forum1.setTheme("Gaming & eSports");
                forumService.updateEntityById(forum1);
                System.out.println("Forum 1 updated");
            }

            // Delete second forum
            System.out.println("\n----- Deleting Second Forum -----");
            if (forum2 != null) {
                forumService.deleteEntityById(forum2);
                System.out.println("Forum 2 deleted");
            }

            // Verify forum1 exists and forum2 is deleted
            System.out.println("\n----- Final Forums List -----");
            List<Forum> finalForums = forumService.listEntity();
            System.out.println("Total forums: " + finalForums.size());
            for (Forum forum : finalForums) {
                System.out.println("Forum: " + forum.getId() + " - " + forum.getNom() + " - " + forum.getTheme());
            }

            // Verify forum1 details
            if (forum1 != null) {
                Forum updatedForum1 = forumService.findForumById(forum1.getId());
                if (updatedForum1 != null) {
                    System.out.println("\nForum 1 details after update:");
                    System.out.println("Name: " + updatedForum1.getNom());
                    System.out.println("Theme: " + updatedForum1.getTheme());
                    System.out.println("Creation Date: " + updatedForum1.getDateCreation());
                }
            }

            // Verify forum2 no longer exists
            if (forum2 != null) {
                Forum deletedForum2 = forumService.findForumById(forum2.getId());
                if (deletedForum2 == null) {
                    System.out.println("\nForum 2 was successfully deleted");
                } else {
                    System.out.println("\nForum 2 was not deleted correctly");
                }
            }

            System.out.println("\n========== FORUM CRUD TESTING COMPLETED ==========");

        } catch (Exception e) {
            System.err.println("Error occurred during testing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}