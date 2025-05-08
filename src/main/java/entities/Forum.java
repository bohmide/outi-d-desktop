package entities;

import java.time.LocalDate;
import java.util.List;

public class Forum {
    private int id;
    private String nom;
    private String theme;
    private LocalDate dateCreation;
    private String imageForum;
    private List<Post> posts;

    // Default constructor
    public Forum() {}

    // Constructor without ID (for creation)
    public Forum(String nom, String theme, LocalDate dateCreation, String imageForum) {
        setNom(nom);
        setTheme(theme);
        setDateCreation(dateCreation);
        setImageForum(imageForum);
    }

    // Constructor with ID (for retrieval from database)
    public Forum(int id, String nom, String theme, LocalDate dateCreation, String imageForum) {
        this(nom, theme, dateCreation, imageForum);
        setId(id);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Forum ID must be greater than 0");
        }
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Forum name cannot be empty");
        }
        if (nom.length() > 255) {
            throw new IllegalArgumentException("Forum name cannot exceed 255 characters");
        }
        this.nom = nom;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        if (theme == null || theme.trim().isEmpty()) {
            throw new IllegalArgumentException("Forum theme cannot be empty");
        }
        if (theme.length() > 10) {
            throw new IllegalArgumentException("Forum theme cannot exceed 10 characters");
        }
        this.theme = theme;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        if (dateCreation == null) {
            throw new IllegalArgumentException("Forum creation date cannot be null");
        }
        this.dateCreation = dateCreation;
    }

    public String getImageForum() {
        return imageForum;
    }

    public void setImageForum(String imageForum) {
        // Image is optional (can be null according to DB schema)
        if (imageForum != null && imageForum.length() > 255) {
            throw new IllegalArgumentException("Image path cannot exceed 255 characters");
        }
        this.imageForum = imageForum;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
    

    public void validate() {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Forum name cannot be empty");
        }
        if (nom.length() > 255) {
            throw new IllegalArgumentException("Forum name cannot exceed 255 characters");
        }
        if (theme == null || theme.trim().isEmpty()) {
            throw new IllegalArgumentException("Forum theme cannot be empty");
        }
        if (theme.length() > 255) {
            throw new IllegalArgumentException("Forum theme cannot exceed 255 characters");
        }
        if (dateCreation == null) {
            throw new IllegalArgumentException("Forum creation date cannot be null");
        }
        if (imageForum != null && imageForum.length() > 255) {
            throw new IllegalArgumentException("Image path cannot exceed 255 characters");
        }
    }

    @Override
    public String toString() {
        return "Forum{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", theme='" + theme + '\'' +
                ", dateCreation=" + dateCreation +
                ", imageForum='" + imageForum + '\'' +
                '}';
    }
}