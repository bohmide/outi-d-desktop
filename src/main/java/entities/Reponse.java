package entities;

import javafx.beans.property.*;

public class Reponse {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty reponse = new SimpleStringProperty();
    private final BooleanProperty correct = new SimpleBooleanProperty();
    private final ObjectProperty<Question> question = new SimpleObjectProperty<>();

    public Reponse() {}

    public Reponse(int id, String text, boolean correct, Question question) {
        this.id.set(id);
        this.reponse.set(text);
        this.correct.set(correct);
        this.question.set(question);
    }

    // Getters, setters and property methods
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getReponse() {
        return reponse.get();
    }

    public void setReponse(String text) {
        this.reponse.set(text);
    }

    public StringProperty textProperty() {
        return reponse;
    }

    public boolean isCorrect() {
        return correct.get();
    }

    public void setCorrect(boolean correct) {
        this.correct.set(correct);
    }

    public BooleanProperty correctProperty() {
        return correct;
    }

    public Question getParentQuestion() {
        return question.get();
    }

    public void setParentQuestion(Question question) {
        this.question.set(question);
    }

    public ObjectProperty<Question> questionParentProperty() {
        return question;
    }
}