package entities;

import java.time.LocalDate;
import java.util.List;

public class Post {
    private int id;
    private String nom;
    private LocalDate dateCreation;
    private int nbLike;
    private int nbComment;
    private int forumId;
    private String contenu;
    private Forum forum;
    private List<Comment> comments;
    
    // Default constructor
    public Post() {}
    
    // Constructor without ID (for creation)
    public Post(String nom, LocalDate dateCreation, int nbLike, int nbComment, int forumId, String contenu) {
        setNom(nom);
        setDateCreation(dateCreation);
        setNbLike(nbLike);
        setNbComment(nbComment);
        setForumId(forumId);
        setContenu(contenu);
    }
    
    // Constructor with ID (for retrieval from database)
    public Post(int id, String nom, LocalDate dateCreation, int nbLike, int nbComment, int forumId, String contenu) {
        this(nom, dateCreation, nbLike, nbComment, forumId, contenu);
        setId(id);
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Post ID must be greater than 0");
        }
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be empty");
        }
        if (nom.length() > 255) {
            throw new IllegalArgumentException("Post title cannot exceed 255 characters");
        }
        this.nom = nom;
    }
    
    public LocalDate getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDate dateCreation) {
        if (dateCreation == null) {
            throw new IllegalArgumentException("Post creation date cannot be null");
        }
        this.dateCreation = dateCreation;
    }
    
    public int getNbLike() {
        return nbLike;
    }
    
    public void setNbLike(int nbLike) {
        if (nbLike < 0) {
            throw new IllegalArgumentException("Number of likes cannot be negative");
        }
        this.nbLike = nbLike;
    }
    
    public int getNbComment() {
        return nbComment;
    }
    
    public void setNbComment(int nbComment) {
        if (nbComment < 0) {
            throw new IllegalArgumentException("Number of comments cannot be negative");
        }
        this.nbComment = nbComment;
    }
    
    public int getForumId() {
        return forumId;
    }
    
    public void setForumId(int forumId) {
        if (forumId <= 0) {
            throw new IllegalArgumentException("Forum ID must be greater than 0");
        }
        this.forumId = forumId;
    }
    
    public String getContenu() {
        return contenu;
    }
    
    public void setContenu(String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        if (contenu.length() > 255) {
            throw new IllegalArgumentException("Post content cannot exceed 255 characters");
        }
        this.contenu = contenu;
    }
    
    public Forum getForum() {
        return forum;
    }
    
    public void setForum(Forum forum) {
        if (forum == null) {
            throw new IllegalArgumentException("Parent forum cannot be null");
        }
        this.forum = forum;
        this.forumId = forum.getId();
    }
    
    public List<Comment> getComments() {
        return comments;
    }
    
    public void setComments(List<Comment> comments) {
        this.comments = comments;
        if (comments != null) {
            this.nbComment = comments.size();
        }
    }
    

    public void validate() {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Post title cannot be empty");
        }
        if (nom.length() > 10) {
            throw new IllegalArgumentException("Post title cannot exceed 10 characters");
        }
        if (dateCreation == null) {
            throw new IllegalArgumentException("Post creation date cannot be null");
        }
        if (nbLike < 0) {
            throw new IllegalArgumentException("Number of likes cannot be negative");
        }
        if (nbComment < 0) {
            throw new IllegalArgumentException("Number of comments cannot be negative");
        }
        if (forumId <= 0) {
            throw new IllegalArgumentException("Forum ID must be greater than 0");
        }
        if (contenu == null || contenu.trim().isEmpty()) {
            throw new IllegalArgumentException("Post content cannot be empty");
        }
        if (contenu.length() > 255) {
            throw new IllegalArgumentException("Post content cannot exceed 255 characters");
        }
    }
    
    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", dateCreation=" + dateCreation +
                ", nbLike=" + nbLike +
                ", nbComment=" + nbComment +
                ", forumId=" + forumId +
                ", contenu='" + contenu + '\'' +
                '}';
    }
}