package entities;

import javafx.beans.property.*;

public class Quiz {
    private IntegerProperty id = new SimpleIntegerProperty();
    private IntegerProperty score = new SimpleIntegerProperty();
    private String titre ;
    private ObjectProperty<Chapitres> chapitre = new SimpleObjectProperty<>();

    // Constructeurs
    public Quiz() {}

    public Quiz(int score, Chapitres chapitre) {
        this.score.set(score);
        this.chapitre.set(chapitre);
    }

    // Getters et Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public Chapitres getChapitre() {
        return chapitre.get();
    }

    public void setChapitre(Chapitres chapitre) {
        if (this.chapitre != null && this.chapitre.get() != null
                && !this.chapitre.get().equals(chapitre)) {
            // Retirer ce quiz de l'ancien chapitre
            this.chapitre.get().setQuiz(null);
        }
        this.chapitre.set(chapitre);
        // Maintenir la cohérence bidirectionnelle
        if (chapitre != null && chapitre.getQuiz() != this) {
            chapitre.setQuiz(this);
        }
    }
    public ObjectProperty<Chapitres> chapitreProperty() {
        return chapitre;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    @Override
    public String toString() {
        return "Quiz ID: " + getId() + ", Score: " + getScore();
    }
}
