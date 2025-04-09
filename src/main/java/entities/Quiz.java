package entities;

import jakarta.persistence.*;

@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private int score;

    @OneToOne
    @JoinColumn(name = "chapitre_id", nullable = false)
    private Chapitres chapitre;

    // Constructeurs
    public Quiz() {}

    public Quiz(int score, Chapitres chapitre) {
        this.score = score;
        this.chapitre = chapitre;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Chapitres getChapitre() {
        return chapitre;
    }

    public void setChapitre(Chapitres chapitre) {
        this.chapitre = chapitre;
    }
}

