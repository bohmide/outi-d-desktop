package controllers;

import entities.Forum;
import entities.Post;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.application.Platform;
import services.PostService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class PostViewController implements Initializable {

    @FXML
    private Button backButton;
    
    @FXML
    private Label forumNameLabel;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private ComboBox<String> sortByComboBox;
    
    @FXML
    private ComboBox<String> filterComboBox;
    
    @FXML
    private VBox postsContainer;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private Label pageIndicator;
    
    @FXML
    private Button addPostButton;
    
    private PostService postService;
    private ObservableList<Post> postsList;
    private Forum currentForum;
    private int currentPage = 1;
    private int itemsPerPage = 5;
    private int totalPages = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        postService = new PostService();
        postsList = FXCollections.observableArrayList();
        
        //setupComboBoxes();
        setupButtons();
    }
    
    public void initData(Forum forum) {
        this.currentForum = forum;
        forumNameLabel.setText(forum.getNom());
        loadPosts();
    }
    
    /*private void setupComboBoxes() {
        // Setup sort options
        sortByComboBox.getItems().addAll(
            "Newest First",
            "Oldest First",
            "Most Comments",
            "Most Likes"
        );
        sortByComboBox.setValue("Newest First");
        sortByComboBox.setOnAction(event -> loadPosts());
        
        // Setup filter options
        filterComboBox.getItems().addAll(
            "All Posts",
            "Today",
            "This Week",
            "This Month"
        );
        filterComboBox.setValue("All Posts");
        filterComboBox.setOnAction(event -> loadPosts());
    }
    */
    private void setupButtons() {
        // Back button
        backButton.setOnAction(event -> navigateBackToForums());
        
        // Search functionality
        searchButton.setOnAction(event -> {
            String searchQuery = searchField.getText().trim();
            if (!searchQuery.isEmpty()) {
                searchPosts(searchQuery);
            } else {
                loadPosts();
            }
        });
        
        // Search on enter key
        searchField.setOnAction(event -> searchButton.fire());
        
        // Pagination
        prevButton.setOnAction(event -> {
            if (currentPage > 1) {
                currentPage--;
                loadPosts();
            }
        });
        
        nextButton.setOnAction(event -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadPosts();
            }
        });
        
        // Add post button
        addPostButton.setOnAction(event -> showAddPostDialog());
    }
    
    private void navigateBackToForums() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ForumView.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not navigate back to forums", e.getMessage());
        }
    }
    
    private void loadPosts() {
        if (currentForum == null) return;
        
        // Get all posts for the current forum
        // We're filtering on the client side for now, but ideally this would be done in the repository
        List<Post> allPosts = postService.listEntity();
        List<Post> forumPosts = FXCollections.observableArrayList();
        
        // Filter posts by forum ID
        for (Post post : allPosts) {
            if (post.getForumId() == currentForum.getId()) {
                forumPosts.add(post);
            }
        }
        
        // Apply sorting
        //applySorting(forumPosts);
        
        // Apply filtering
        //applyFiltering(forumPosts);
        
        // Calculate total pages
        totalPages = (int) Math.ceil((double) forumPosts.size() / itemsPerPage);
        
        // Update page indicator
        pageIndicator.setText("Page " + currentPage + " of " + (totalPages > 0 ? totalPages : 1));
        
        // Enable/disable pagination buttons
        prevButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= totalPages || totalPages <= 1);
        
        // Get current page items
        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, forumPosts.size());
        
        // Update the UI with posts
        displayPosts(fromIndex < toIndex ? forumPosts.subList(fromIndex, toIndex) : FXCollections.observableArrayList());
    }
    
   /* private void applySorting(List<Post> posts) {
        String sortOption = sortByComboBox.getValue();
        if (sortOption == null) return;
        
        switch (sortOption) {
            case "Newest First":
                posts.sort((p1, p2) -> p2.getDateCreation().compareTo(p1.getDateCreation()));
                break;
            case "Oldest First":
                posts.sort((p1, p2) -> p1.getDateCreation().compareTo(p2.getDateCreation()));
                break;
            case "Most Comments":
                posts.sort((p1, p2) -> Integer.compare(p2.getNbComment(), p1.getNbComment()));
                break;
            case "Most Likes":
                posts.sort((p1, p2) -> Integer.compare(p2.getNbLike(), p1.getNbLike()));
                break;
        }
    }

    private void applyFiltering(List<Post> posts) {
        String filterOption = filterComboBox.getValue();
        if (filterOption == null) return;
        
        LocalDate today = LocalDate.now();
        
        switch (filterOption) {
            case "Today":
                posts.removeIf(post -> !post.getDateCreation().isEqual(today));
                break;
            case "This Week":
                LocalDate weekAgo = today.minusWeeks(1);
                posts.removeIf(post -> post.getDateCreation().isBefore(weekAgo));
                break;
            case "This Month":
                LocalDate monthAgo = today.minusMonths(1);
                posts.removeIf(post -> post.getDateCreation().isBefore(monthAgo));
                break;
        }
    }
    */
    private void displayPosts(List<Post> posts) {
        postsContainer.getChildren().clear();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        for (Post post : posts) {
            VBox postBox = new VBox(5);
            postBox.getStyleClass().add("post-box");
            
            Label titleLabel = new Label(post.getNom());
            titleLabel.getStyleClass().add("post-title");
            
            // This project doesn't track posts by user, so we'll just show the date
            Label dateLabel = new Label("Posted on: " + post.getDateCreation().format(formatter));
            dateLabel.getStyleClass().add("post-metadata");
            
            Label contentPreview = new Label(post.getContenu().length() > 100 ? 
                post.getContenu().substring(0, 100) + "..." : post.getContenu());
            contentPreview.getStyleClass().add("post-preview");
            contentPreview.setWrapText(true);
            
            Label commentsLabel = new Label("Comments: " + post.getNbComment());
            commentsLabel.getStyleClass().add("post-comments-count");
            
            Label likesLabel = new Label("Likes: " + post.getNbLike());
            likesLabel.getStyleClass().add("post-likes-count");
            
            HBox actionsBox = new HBox(10);
            
            Button viewButton = new Button("View");
            viewButton.getStyleClass().add("view-button");
            viewButton.setOnAction(e -> openPostComments(post));
            
            Button editButton = new Button("Edit");
            editButton.getStyleClass().add("edit-button");
            editButton.setOnAction(e -> editPost(post));
            
            Button deleteButton = new Button("Delete");
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(e -> deletePost(post));
            
            actionsBox.getChildren().addAll(viewButton, editButton, deleteButton);
            
            Separator separator = new Separator();
            
            postBox.getChildren().addAll(titleLabel, dateLabel, contentPreview, commentsLabel, likesLabel, actionsBox, separator);
            
            // Make the entire post clickable
            postBox.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    openPostComments(post);
                }
            });
            
            postsContainer.getChildren().add(postBox);
        }
        
        if (posts.isEmpty()) {
            Label emptyLabel = new Label("No posts found");
            emptyLabel.getStyleClass().add("empty-list-label");
            postsContainer.getChildren().add(emptyLabel);
        }
    }
    
    private void searchPosts(String query) {
        if (currentForum == null) return;
        
        List<Post> allPosts = postService.listEntity();
        ObservableList<Post> filteredPosts = FXCollections.observableArrayList();
        
        for (Post post : allPosts) {
            // Only include posts from current forum
            if (post.getForumId() == currentForum.getId()) {
                // Match on title or content
                if (post.getNom().toLowerCase().contains(query.toLowerCase()) || 
                    post.getContenu().toLowerCase().contains(query.toLowerCase())) {
                    filteredPosts.add(post);
                }
            }
        }
        
        // Reset pagination for search
        currentPage = 1;
        totalPages = (int) Math.ceil((double) filteredPosts.size() / itemsPerPage);
        pageIndicator.setText("Search Results");
        
        // Update the UI with search results
        displayPosts(filteredPosts);
    }
    
    private void openPostComments(Post post) {
        try {
            // Important: Set the forum reference in the post before navigating
            if (post.getForum() == null && currentForum != null) {
                post.setForum(currentForum);
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CommentView.fxml"));
            Parent root = loader.load();
            
            CommentViewController controller = loader.getController();
            controller.initData(post);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) postsContainer.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open comments view", e.getMessage());
        }
    }
    
    private void editPost(Post post) {
        // Ensure the post has a reference to its forum before editing
        if (post.getForum() == null && currentForum != null) {
            post.setForum(currentForum);
        }

        // Create a dialog for editing an existing post
        Dialog<Post> dialog = new Dialog<>();
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit post: " + post.getNom());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField(post.getNom());
        titleField.setPromptText("Post Title");
        
        TextArea contentArea = new TextArea(post.getContenu());
        contentArea.setPromptText("Post Content");
        contentArea.setPrefHeight(200);
        contentArea.setWrapText(true);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentArea, 1, 1);
        
        // Enable/Disable save button depending on whether required fields are filled
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        
        // Validate inputs as user types
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState(saveButton, titleField.getText(), contentArea.getText());
        });
        
        contentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState(saveButton, titleField.getText(), contentArea.getText());
        });
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the title field by default
        Platform.runLater(() -> titleField.requestFocus());
        
        // Store a reference to the forum to avoid null issues
        final Forum forum = post.getForum() != null ? post.getForum() : currentForum;
        
        // Convert the result to a Post object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Post updatedPost = new Post();
                updatedPost.setId(post.getId());
                updatedPost.setNom(titleField.getText().trim());
                updatedPost.setContenu(contentArea.getText().trim());
                updatedPost.setDateCreation(post.getDateCreation());
                updatedPost.setNbLike(post.getNbLike());
                updatedPost.setNbComment(post.getNbComment());
                updatedPost.setForumId(post.getForumId() > 0 ? post.getForumId() : (forum != null ? forum.getId() : 0));
                
                // Make sure we set the forum reference before returning
                if (forum != null) {
                    updatedPost.setForum(forum);
                }
                return updatedPost;
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<Post> result = dialog.showAndWait();
        
        result.ifPresent(updatedPost -> {
            // Update the post
            try {
                postService.updateEntityById(updatedPost);
                loadPosts(); // Refresh the list
                showAlert(Alert.AlertType.INFORMATION, "Success", "Post Updated", 
                         "Post '" + updatedPost.getNom() + "' has been updated successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Update Post", e.getMessage());
            }
        });
    }
    
    private void deletePost(Post post) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Post");
        alert.setHeaderText("Delete Post: " + post.getNom());
        alert.setContentText("Are you sure you want to delete this post? This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                postService.deleteEntityById(post);
                loadPosts();
            }
        });
    }
    
    private void showAddPostDialog() {
        if (currentForum == null) return;
        
        // Create a dialog for adding a new post
        Dialog<Post> dialog = new Dialog<>();
        dialog.setTitle("Add New Post");
        dialog.setHeaderText("Create a new post in " + currentForum.getNom());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Post Title");
        
        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Post Content");
        contentArea.setPrefHeight(200);
        contentArea.setWrapText(true);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentArea, 1, 1);
        
        // Enable/Disable save button depending on whether required fields are filled
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        
        // Validate inputs as user types
        titleField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState(saveButton, titleField.getText(), contentArea.getText());
        });
        
        contentArea.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSaveButtonState(saveButton, titleField.getText(), contentArea.getText());
        });
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the title field by default
        Platform.runLater(() -> titleField.requestFocus());
        
        // Convert the result to a Post object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Post post = new Post();
                post.setNom(titleField.getText().trim());
                post.setContenu(contentArea.getText().trim());
                post.setDateCreation(LocalDate.now());
                post.setNbLike(0);
                post.setNbComment(0);
                post.setForumId(currentForum.getId());
                post.setForum(currentForum);
                return post;
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<Post> result = dialog.showAndWait();
        
        result.ifPresent(post -> {
            // Save the new post
            try {
                postService.addEntity(post);
                loadPosts(); // Refresh the list
                showAlert(Alert.AlertType.INFORMATION, "Success", "Post Created", 
                         "Post '" + post.getNom() + "' has been created successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Create Post", e.getMessage());
            }
        });
    }
    
    private void updateSaveButtonState(Node saveButton, String title, String content) {
        saveButton.setDisable(title.trim().isEmpty() || content.trim().isEmpty());
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}