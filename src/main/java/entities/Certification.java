package entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "certification")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String nom_certification;

    @OneToOne
    @JoinColumn(name = "cours_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @OnDelete(action = OnDeleteAction.CASCADE) // Active la suppression en cascade
    private Cours cours;


    // Constructeurs
    public Certification() {}

    public Certification(String nomCertification, Cours cours) {
        this.nom_certification = nomCertification;
        this.cours = cours;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom_certification() {
        return nom_certification;
    }

    public void setNom_certification(String nom_certification) {
        this.nom_certification = nom_certification;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }
}

