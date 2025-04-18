package entities;

import javafx.beans.property.*;

public class Reponse {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty reponse = new SimpleStringProperty();
    private BooleanProperty isCorrect = new SimpleBooleanProperty();
    private ObjectProperty<Question> question = new SimpleObjectProperty<>();

    public Reponse() {}

    public Reponse(int id, String reponse, boolean isCorrect, Question question) {
        this.id.set(id);
        this.reponse.set(reponse);
        this.isCorrect.set(isCorrect);
        this.question.set(question);
    }

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

    public void setReponse(String reponse) {
        this.reponse.set(reponse);
    }

    public StringProperty reponseProperty() {
        return reponse;
    }

    public boolean isCorrect() {
        return isCorrect.get();
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect.set(isCorrect);
    }

    public BooleanProperty isCorrectProperty() {
        return isCorrect;
    }

    public Question getQuestion() {
        return question.get();
    }

    public void setQuestion(Question question) {
        this.question.set(question);
    }

    public ObjectProperty<Question> questionProperty() {
        return question;
    }
}
