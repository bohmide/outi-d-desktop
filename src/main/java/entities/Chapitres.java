package entities;

import javafx.beans.property.*;

public class Chapitres {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty nomChapitre = new SimpleStringProperty();
    private StringProperty contenuText = new SimpleStringProperty();
//    private StringProperty contenuFile = new SimpleStringProperty();

    private Cours cours;
    private Quiz quiz;

    // Constructeurs
    public Chapitres() {}

    public Chapitres(String nomChapitre, String contenuText, String contenuFile, Cours cours) {
        this.nomChapitre.set(nomChapitre);
        this.contenuText.set(contenuText);
//        this.contenuFile.set(contenuFile);
        this.cours = cours;
    }

    // Getters & Setters
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNomChapitre() {
        return nomChapitre.get();
    }

    public void setNomChapitre(String nomChapitre) {
        this.nomChapitre.set(nomChapitre);
    }

    public StringProperty nomChapitreProperty() {
        return nomChapitre;
    }

    public String getContenuText() {
        return contenuText.get();
    }

    public void setContenuText(String contenuText) {
        this.contenuText.set(contenuText);
    }

    public StringProperty contenuTextProperty() {
        return contenuText;
    }

//    public String getContenuFile() {
//        return contenuFile.get();
//    }

//    public void setContenuFile(String contenuFile) {
//        this.contenuFile.set(contenuFile);
//    }

//    public StringProperty contenuFileProperty() {
//        return contenuFile;
//    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    @Override
    public String toString() {
        return getNomChapitre();
    }
}
