package entities;

import javafx.beans.property.*;

public class Question {

    private IntegerProperty id = new SimpleIntegerProperty();
    private StringProperty question = new SimpleStringProperty();
    private StringProperty type = new SimpleStringProperty();
    private ObjectProperty<Quiz> quiz = new SimpleObjectProperty<>();

    public Question() {}

    public Question(int id, String question, String type, Quiz quiz) {
        this.id.set(id);
        this.question.set(question);
        this.type.set(type);
        this.quiz.set(quiz);
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

    public String getQuestion() {
        return question.get();
    }

    public void setQuestion(String question) {
        this.question.set(question);
    }

    public StringProperty questionProperty() {
        return question;
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
