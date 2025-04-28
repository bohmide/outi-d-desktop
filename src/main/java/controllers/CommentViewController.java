package controllers;

import entities.Comment;
import entities.Post;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.CommentService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class CommentViewController implements Initializable {

    @FXML
    private Button backButton;
    
    @FXML
    private Label postTitleLabel;
    
    @FXML
    private Label postAuthorLabel;
    
    @FXML
    private Label postDateLabel;
    
    @FXML
    private TextArea postContentArea;
    
    @FXML
    private VBox postContentPane;
    
    @FXML
    private ComboBox<String> sortCommentsComboBox;
    
    @FXML
    private VBox commentsContainer;
    
    @FXML
    private TextArea newCommentArea;
    
    @FXML
    private Button submitCommentButton;
    
    private Post currentPost;
    private CommentService commentService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        commentService = new CommentService();
        
        setupSortComboBox();
        setupButtons();
    }
    
    public void initData(Post post) {
        this.currentPost = post;
        
        // Set post details
        postTitleLabel.setText(post.getNom());
        postAuthorLabel.setText("Post ID: " + post.getId()); // No user info in this model
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        postDateLabel.setText("Posted on: " + post.getDateCreation().format(formatter));
        
        postContentArea.setText(post.getContenu());
        
        loadComments();
    }
    
    private void setupSortComboBox() {
        sortCommentsComboBox.getItems().addAll(
            "Newest First",
            "Oldest First"
        );
        sortCommentsComboBox.setValue("Newest First");
        sortCommentsComboBox.setOnAction(event -> loadComments());
    }
    
    private void setupButtons() {
        // Back button
        backButton.setOnAction(event -> navigateBackToPosts());
        
        // Submit comment button
        submitCommentButton.setOnAction(event -> submitComment());
    }
    
    private void navigateBackToPosts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostView.fxml"));
            Parent root = loader.load();
            
            PostViewController controller = loader.getController();
            // The key issue is here - we need to ensure we have access to the Forum object
            if (currentPost != null && currentPost.getForum() != null) {
                controller.initData(currentPost.getForum());
                
                Scene scene = new Scene(root);
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                        "Could not navigate back to posts", 
                        "The forum information is missing for this post.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not navigate back to posts", e.getMessage());
        }
    }
    
    private void loadComments() {
        if (currentPost == null) return;
        
        // Get all comments
        List<Comment> allComments = commentService.listEntity();
        
        // Filter comments for the current post
        List<Comment> postComments = allComments.stream()
            .filter(comment -> comment.getPostId() == currentPost.getId())
            .collect(Collectors.toList());
        
        // Apply sorting
        applySorting(postComments);
        
        displayComments(postComments);
    }
    
    private void applySorting(List<Comment> comments) {
        String sortOption = sortCommentsComboBox.getValue();
        if (sortOption == null) return;
        
        switch (sortOption) {
            case "Newest First":
                comments.sort((c1, c2) -> c2.getDateCreation().compareTo(c1.getDateCreation()));
                break;
            case "Oldest First":
                comments.sort((c1, c2) -> c1.getDateCreation().compareTo(c2.getDateCreation()));
                break;
        }
    }
    
    private void displayComments(List<Comment> comments) {
        commentsContainer.getChildren().clear();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        for (Comment comment : comments) {
            VBox commentBox = new VBox(5);
            commentBox.getStyleClass().add("comment-box");
            
            HBox headerBox = new HBox(10);
            
            Label commentIdLabel = new Label("Comment #" + comment.getId());
            commentIdLabel.getStyleClass().add("comment-author");
            
            Label dateLabel = new Label(comment.getDateCreation().format(formatter));
            dateLabel.getStyleClass().add("comment-date");
            
            headerBox.getChildren().addAll(commentIdLabel, dateLabel);
            
            TextArea contentArea = new TextArea(comment.getDescription());
            contentArea.setEditable(false);
            contentArea.setWrapText(true);
            contentArea.getStyleClass().add("comment-content");
            
            HBox actionsBox = new HBox(10);
            
            Button editButton = new Button();
            editButton.setGraphic(new FontAwesomeIconView(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.EDIT));
            editButton.getStyleClass().add("edit-button");
            editButton.setOnAction(e -> editComment(comment));
            
            Button deleteButton = new Button();
            deleteButton.setGraphic(new FontAwesomeIconView(de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon.TRASH));
            deleteButton.getStyleClass().add("delete-button");
            deleteButton.setOnAction(e -> deleteComment(comment));
            
            actionsBox.getChildren().addAll(editButton, deleteButton);
            
            Separator separator = new Separator();
            
            commentBox.getChildren().addAll(headerBox, contentArea, actionsBox, separator);
            commentsContainer.getChildren().add(commentBox);
        }
        
        if (comments.isEmpty()) {
            Label emptyLabel = new Label("No comments yet. Be the first to comment!");
            emptyLabel.getStyleClass().add("empty-list-label");
            commentsContainer.getChildren().add(emptyLabel);
        }
    }
    
    private void submitComment() {
        String content = newCommentArea.getText().trim();
        
        if (content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", "Empty Comment", 
                     "Comment content cannot be empty.");
            return;
        }
        
        if (currentPost == null) return;
        
        // Create a new comment
        Comment comment = new Comment();
        comment.setPostId(currentPost.getId());
        comment.setDescription(content);
        comment.setDateCreation(LocalDate.now());
        
        // Save comment
        commentService.addEntity(comment);
        
        // Update post comment count
        currentPost.setNbComment(currentPost.getNbComment() + 1);
        
        // Clear the textarea
        newCommentArea.clear();
        
        // Reload comments
        loadComments();
    }
    
    private void editComment(Comment comment) {
        TextInputDialog dialog = new TextInputDialog(comment.getDescription());
        dialog.setTitle("Edit Comment");
        dialog.setHeaderText("Edit your comment");
        dialog.setContentText("Comment:");
        
        dialog.showAndWait().ifPresent(result -> {
            if (!result.trim().isEmpty()) {
                comment.setDescription(result);
                commentService.updateEntityById(comment);
                loadComments();
            }
        });
    }
    
    private void deleteComment(Comment comment) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Comment");
        alert.setHeaderText("Delete Comment");
        alert.setContentText("Are you sure you want to delete this comment? This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                commentService.deleteEntityById(comment);
                
                // Update post comment count
                if (currentPost.getNbComment() > 0) {
                    currentPost.setNbComment(currentPost.getNbComment() - 1);
                }
                loadComments();
            }
        });
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}