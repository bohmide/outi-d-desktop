package entities;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Question {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty questionText = new SimpleStringProperty();
    private final StringProperty type = new SimpleStringProperty();
    private final ObjectProperty<Quiz> quiz = new SimpleObjectProperty<>();
    private final ObservableList<Reponse> reponses = FXCollections.observableArrayList();

    public Question() {}

    public Question(int id, String questionText, String type, Quiz quiz) {
        this.id.set(id);
        this.questionText.set(questionText);
        this.type.set(type);
        this.quiz.set(quiz);
    }

    // Add methods to manage answers
    public ObservableList<Reponse> getReponses() {
        return reponses;
    }

    public void addReponse(Reponse reponse) {
        reponses.add(reponse);
    }

    public void removeReponse(Reponse reponse) {
        reponses.remove(reponse);
    }

    // Existing getters/setters/property methods...
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getQuestion() {
        return questionText.get();
    }

    public void setQuestion(String questionText) {
        this.questionText.set(questionText);
    }

    public StringProperty questionProperty() {
        return questionText;
    }

    public String getType() {
        return type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return type;
    }

    public Quiz getQuiz() {
        return quiz.get();
    }

    public void setQuiz(Quiz quiz) {
        this.quiz.set(quiz);
    }

    public ObjectProperty<Quiz> quizProperty() {
        return quiz;
    }
}