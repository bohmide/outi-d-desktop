package entities;
import jakarta.persistence.*;
@Entity
@Table(name = "chapitre")
public class Chapitres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nomChapitre;

    @Lob
    private String contenuText;

    @Column(nullable = false)
    private String contenuFile; // Stocke le nom du fichier ou son chemin

    @ManyToOne
    @JoinColumn(name = "cours_id", nullable = false)
    private Cours cours;

    // Constructeurs
    public Chapitres() {}

    public Chapitres(String nomChapitre, String contenuText, String contenuFile, Cours cours) {
        this.nomChapitre = nomChapitre;
        this.contenuText = contenuText;
        this.contenuFile = contenuFile;
        this.cours = cours;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomChapitre() {
        return nomChapitre;
    }

    public void setNomChapitre(String nomChapitre) {
        this.nomChapitre = nomChapitre;
    }

    public String getContenuText() {
        return contenuText;
    }

    public void setContenuText(String contenuText) {
        this.contenuText = contenuText;
    }

    public String getContenuFile() {
        return contenuFile;
    }

    public void setContenuFile(String contenuFile) {
        this.contenuFile = contenuFile;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }
}
