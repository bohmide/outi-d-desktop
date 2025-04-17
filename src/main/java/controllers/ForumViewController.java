package controllers;

import entities.Forum;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.ForumService;

import java.io.File;
import javafx.stage.FileChooser;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.TilePane;
import javafx.scene.Node;
import javafx.application.Platform;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;

public class ForumViewController implements Initializable {

    @FXML
    private TextField searchField;
    
    @FXML
    private Button searchButton;
    
    @FXML
    private TilePane forumsGridPane;
    
    @FXML
    private Button prevButton;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private Label pageIndicator;
    
    @FXML
    private Button addForumButton;
    
    private ForumService forumService;
    private ObservableList<Forum> forumsList;
    private int currentPage = 1;
    private int itemsPerPage = 9; // Changed to 9 for a 3x3 grid
    private int totalPages = 1;
    private final String UPLOAD_DIR = "src/main/resources/uploads/forums/";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        forumService = new ForumService();
        forumsList = FXCollections.observableArrayList();
        
        // Make sure upload directory exists
        createUploadDirectoryIfNotExists();
        
        setupButtons();
        loadForums();
    }
    
    private void createUploadDirectoryIfNotExists() {
        try {
            Path path = Paths.get(UPLOAD_DIR);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create upload directory", e.getMessage());
        }
    }
    
    private void setupButtons() {
        // Search functionality
        searchButton.setOnAction(event -> {
            String searchQuery = searchField.getText().trim();
            if (!searchQuery.isEmpty()) {
                searchForums(searchQuery);
            } else {
                loadForums();
            }
        });
        
        // Search on enter key
        searchField.setOnAction(event -> searchButton.fire());
        
        // Pagination
        prevButton.setOnAction(event -> {
            if (currentPage > 1) {
                currentPage--;
                loadForums();
            }
        });
        
        nextButton.setOnAction(event -> {
            if (currentPage < totalPages) {
                currentPage++;
                loadForums();
            }
        });
        
        // Add forum button
        addForumButton.setOnAction(event -> showAddForumDialog());
    }
    
    private void loadForums() {
        List<Forum> allForums = forumService.listEntity();
        
        // Calculate total pages
        totalPages = (int) Math.ceil((double) allForums.size() / itemsPerPage);
        
        // Update page indicator
        pageIndicator.setText("Page " + currentPage + " of " + Math.max(1, totalPages));
        
        // Enable/disable pagination buttons
        prevButton.setDisable(currentPage <= 1);
        nextButton.setDisable(currentPage >= totalPages);
        
        // Get current page items
        int fromIndex = (currentPage - 1) * itemsPerPage;
        int toIndex = Math.min(fromIndex + itemsPerPage, allForums.size());
        
        // Update the list
        forumsList.clear();
        if (fromIndex < toIndex) {
            forumsList.addAll(allForums.subList(fromIndex, toIndex));
        }
        
        // Display forums in grid
        displayForumsInGrid();
    }
    
    private void displayForumsInGrid() {
        forumsGridPane.getChildren().clear();
        
        for (Forum forum : forumsList) {
            VBox forumCard = createForumCard(forum);
            forumsGridPane.getChildren().add(forumCard);
        }
    }
    
    private VBox createForumCard(Forum forum) {
        VBox card = new VBox(10);
        card.getStyleClass().add("forum-card");
        card.setPadding(new Insets(15));
        card.setMinWidth(200);
        card.setMaxWidth(250);
        
        // Add forum image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        String imagePath = forum.getImageForum();
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                } else {
                    // Default image if file doesn't exist
                    Image defaultImage = new Image("/assets/default/forum-icon.png");
                    imageView.setImage(defaultImage);
                }
            } catch (Exception e) {
                // Fall back to default image on error
                Image defaultImage = new Image("/assets/default/forum-icon.png");
                imageView.setImage(defaultImage);
            }
        } else {
            // Default image if no image path
            Image defaultImage = new Image("/assets/default/forum-icon.png");
            imageView.setImage(defaultImage);
        }
        
        // Create title label
        Label nameLabel = new Label(forum.getNom());
        nameLabel.getStyleClass().add("forum-name");
        nameLabel.setWrapText(true);
        
        // Create theme label
        Label themeLabel = new Label("Theme: " + forum.getTheme());
        themeLabel.getStyleClass().add("forum-theme");
        themeLabel.setWrapText(true);
        
        // Create date label
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        Label dateLabel = new Label("Created: " + forum.getDateCreation().format(formatter));
        dateLabel.getStyleClass().add("forum-date");
        
        // Create action buttons
        Button viewButton = new Button("View Posts");
        viewButton.getStyleClass().add("view-button");
        viewButton.setMaxWidth(Double.MAX_VALUE);
        viewButton.setOnAction(e -> openForumPosts(forum));
        
        HBox actionButtons = new HBox(5);
        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("edit-button");
        editButton.setOnAction(e -> editForum(forum));
        
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().add("delete-button");
        deleteButton.setOnAction(e -> deleteForum(forum));
        
        actionButtons.getChildren().addAll(editButton, deleteButton);
        
        // Add all elements to the card
        card.getChildren().addAll(imageView, nameLabel, themeLabel, dateLabel, viewButton, actionButtons);
        
        // Add click handler for the card
        card.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                openForumPosts(forum);
            }
        });
        
        return card;
    }
    
    private void searchForums(String query) {
        // Implement search functionality
        List<Forum> allForums = forumService.listEntity();
        
        forumsList.clear();
        for (Forum forum : allForums) {
            if (forum.getNom().toLowerCase().contains(query.toLowerCase()) || 
                forum.getTheme().toLowerCase().contains(query.toLowerCase())) {
                forumsList.add(forum);
            }
        }
        
        // Display forums in grid
        displayForumsInGrid();
        
        // Reset pagination for search results
        currentPage = 1;
        totalPages = 1;
        pageIndicator.setText("Search Results");
        prevButton.setDisable(true);
        nextButton.setDisable(true);
    }
    
    private void openForumPosts(Forum forum) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PostView.fxml"));
            Parent root = loader.load();
            
            PostViewController controller = loader.getController();
            controller.initData(forum);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) forumsGridPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not open posts view", e.getMessage());
        }
    }
    
    private void editForum(Forum forum) {
        // Create a dialog for editing an existing forum
        Dialog<Forum> dialog = new Dialog<>();
        dialog.setTitle("Edit Forum");
        dialog.setHeaderText("Edit forum: " + forum.getNom());
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(forum.getNom());
        TextField themeField = new TextField(forum.getTheme());
        
        // Add image selection field
        TextField imageField = new TextField();
        imageField.setPromptText("Choose image...");
        imageField.setEditable(false);
        
        if (forum.getImageForum() != null && !forum.getImageForum().isEmpty()) {
            imageField.setText(forum.getImageForum());
        }
        
        Button browseButton = new Button("Browse");
        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Forum Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                imageField.setText(selectedFile.getAbsolutePath());
            }
        });
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Theme:"), 0, 1);
        grid.add(themeField, 1, 1);
        grid.add(new Label("Image:"), 0, 2);
        grid.add(imageField, 1, 2);
        grid.add(browseButton, 2, 2);
        
        // Enable/Disable save button depending on whether a name was entered
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        
        // Validate input as user types
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the name field by default
        Platform.runLater(() -> nameField.requestFocus());
        
        // Convert the result to a Forum object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String imagePath = imageField.getText().trim();
                
                // If a new image is selected, copy it to uploads folder
                if (!imagePath.isEmpty() && !imagePath.equals(forum.getImageForum())) {
                    imagePath = copyImageToUploadsFolder(imagePath);
                } else {
                    // Keep existing image path
                    imagePath = forum.getImageForum();
                }
                
                Forum updatedForum = new Forum();
                updatedForum.setId(forum.getId());
                updatedForum.setNom(nameField.getText().trim());
                updatedForum.setTheme(themeField.getText().trim());
                updatedForum.setImageForum(imagePath);
                updatedForum.setDateCreation(forum.getDateCreation());
                return updatedForum;
            }
            return null;
        });
        
        // Show the dialog and process the result
        Optional<Forum> result = dialog.showAndWait();
        
        result.ifPresent(updatedForum -> {
            // Update the forum
            try {
                forumService.updateEntityById(updatedForum);
                loadForums(); // Refresh the list
                showAlert(Alert.AlertType.INFORMATION, "Success", "Forum Updated", 
                         "Forum '" + updatedForum.getNom() + "' has been updated successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Update Forum", e.getMessage());
            }
        });
    }
    
    private void deleteForum(Forum forum) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Forum");
        alert.setHeaderText("Delete Forum: " + forum.getNom());
        alert.setContentText("Are you sure you want to delete this forum? This action cannot be undone.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                forumService.deleteEntityById(forum);
                loadForums();
            }
        });
    }

    private void showAddForumDialog() {
        // Create a dialog for adding a new forum
        Dialog<Forum> dialog = new Dialog<>();
        dialog.setTitle("Add New Forum");
        dialog.setHeaderText("Create a new forum");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Forum Name");

        TextField themeField = new TextField();
        themeField.setPromptText("Forum Theme");

        TextField imageField = new TextField();
        imageField.setPromptText("Choose image...");
        imageField.setEditable(false);

        Button browseButton = new Button("Browse");

        browseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Forum Image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog.getOwner());
            if (selectedFile != null) {
                imageField.setText(selectedFile.getAbsolutePath());
            }
        });

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Theme:"), 0, 1);
        grid.add(themeField, 1, 1);

        grid.add(new Label("Image:"), 0, 2);
        grid.add(imageField, 1, 2);
        grid.add(browseButton, 2, 2);

        // Enable/Disable save button depending on whether a name was entered
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the name field by default
        Platform.runLater(() -> nameField.requestFocus());

        // Convert the result to a Forum object when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String imagePath = imageField.getText().trim();
                
                // If an image is selected, copy it to uploads folder
                if (!imagePath.isEmpty()) {
                    imagePath = copyImageToUploadsFolder(imagePath);
                }
                
                Forum forum = new Forum();
                forum.setNom(nameField.getText().trim());
                forum.setTheme(themeField.getText().trim());
                forum.setImageForum(imagePath);
                forum.setDateCreation(LocalDate.now());
                return forum;
            }
            return null;
        });

        // Show the dialog and process the result
        Optional<Forum> result = dialog.showAndWait();

        result.ifPresent(forum -> {
            try {
                forumService.addEntity(forum);
                loadForums(); // Refresh the list
                showAlert(Alert.AlertType.INFORMATION, "Success", "Forum Created",
                        "Forum '" + forum.getNom() + "' has been created successfully.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to Create Forum", e.getMessage());
            }
        });
    }
    
    private String copyImageToUploadsFolder(String sourcePath) {
        try {
            // Generate a unique filename to avoid collisions
            String originalFileName = new File(sourcePath).getName();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            
            // Create full destination path
            Path destinationPath = Paths.get(UPLOAD_DIR, uniqueFileName);
            
            // Create directory if it doesn't exist
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            // Copy the file
            Files.copy(Paths.get(sourcePath), destinationPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return the relative path for storage
            return destinationPath.toString();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to Copy Image", e.getMessage());
            return ""; // Return empty on failure
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}