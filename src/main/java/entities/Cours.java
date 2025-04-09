package entities;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "cours")
public class Cours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nom;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation = new Date();

    @Column(nullable = false)
    private String etat;

    @OneToMany(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapitres> chapitres;

    @OneToOne(mappedBy = "cours", cascade = CascadeType.ALL, orphanRemoval = true)
    private Certification certification;

    // Constructeurs
    public Cours() {}

    public Cours(String nom, Date dateCreation, String etat) {
        this.nom = nom;
        this.dateCreation = dateCreation;
        this.etat = etat;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public List<Chapitres> getChapitres() {
        return chapitres;
    }

    public void setChapitres(List<Chapitres> chapitres) {
        this.chapitres = chapitres;
    }

    public Certification getCertification() {
        return certification;
    }

    public void setCertification(Certification certification) {
        this.certification = certification;
    }
}



