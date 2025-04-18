package entities;

import java.time.LocalDate;

public class Comment {
    private int id;
    private int postId;
    private String description;
    private LocalDate dateCreation;
    private Post post; // Reference to the parent post
    
    // Default constructor
    public Comment() {}
    
    // Constructor without ID (for creation)
    public Comment(int postId, String description, LocalDate dateCreation) {
        setPostId(postId);
        setDescription(description);
        setDateCreation(dateCreation);
    }
    
    // Constructor with ID (for retrieval from database)
    public Comment(int id, int postId, String description, LocalDate dateCreation) {
        this(postId, description, dateCreation);
        setId(id);
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Comment ID must be greater than 0");
        }
        this.id = id;
    }
    
    public int getPostId() {
        return postId;
    }
    
    public void setPostId(int postId) {
        if (postId <= 0) {
            throw new IllegalArgumentException("Post ID must be greater than 0");
        }
        this.postId = postId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment description cannot be empty");
        }
        if (description.length() > 255) {
            throw new IllegalArgumentException("Comment description cannot exceed 255 characters");
        }
        this.description = description;
    }
    
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDate dateCreation) {
        if (dateCreation == null) {
            throw new IllegalArgumentException("Comment creation date cannot be null");
        }
        this.dateCreation = dateCreation;
    }
    
    public Post getPost() {
        return post;
    }
    
    public void setPost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Parent post cannot be null");
        }
        this.post = post;
        this.postId = post.getId();
    }
    
    /**
     * Validates all the data in this comment object
     * @throws IllegalArgumentException if any validation fails
     */
    public void validate() {
        if (postId <= 0) {
            throw new IllegalArgumentException("Post ID must be greater than 0");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment description cannot be empty");
        }
        if (description.length() > 255) {
            throw new IllegalArgumentException("Comment description cannot exceed 255 characters");
        }
        if (dateCreation == null) {
            throw new IllegalArgumentException("Comment creation date cannot be null");
        }
    }
    
    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postId=" + postId +
                ", description='" + description + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}