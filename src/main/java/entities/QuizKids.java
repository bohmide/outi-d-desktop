package entities;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;

public class QuizKids {

    private final IntegerProperty id;
    private final StringProperty question;
    private final ListProperty<String> options;
    private final StringProperty correctAnswer;
    private final StringProperty mediaPath;
    private final StringProperty level;
    private final IntegerProperty score;
    private final StringProperty genre;
    private final StringProperty country;

    public QuizKids() {
        this.id = new SimpleIntegerProperty();
        this.question = new SimpleStringProperty();
        this.options = new SimpleListProperty<>();
        this.correctAnswer = new SimpleStringProperty();
        this.mediaPath = new SimpleStringProperty();
        this.level = new SimpleStringProperty();
        this.score = new SimpleIntegerProperty();
        this.genre = new SimpleStringProperty();
        this.country = new SimpleStringProperty();
    }

    public QuizKids(String question, List<String> options, String correctAnswer,
                    String mediaPath, String level, int score,
                    String genre, String country) {
        this();
        this.question.set(question);
        this.options.set(FXCollections.observableArrayList(options));
        this.correctAnswer.set(correctAnswer);
        this.mediaPath.set(mediaPath);
        this.level.set(level);
        this.score.set(score);
        this.genre.set(genre);
        this.country.set(country);
    }

    // Getters and Setters for Properties
    public IntegerProperty idProperty() {
        return id;
    }

    public StringProperty questionProperty() {
        return question;
    }

    public ListProperty<String> optionsProperty() {
        return options;
    }

    public StringProperty correctAnswerProperty() {
        return correctAnswer;
    }

    public StringProperty mediaPathProperty() {
        return mediaPath;
    }

    public StringProperty levelProperty() {
        return level;
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public StringProperty genreProperty() {
        return genre;
    }

    public StringProperty countryProperty() {
        return country;
    }

    // Regular Getters and Setters (for non-property access)
    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getQuestion() {
        return question.get();
    }

    public void setQuestion(String question) {
        this.question.set(question);
    }

    public List<String> getOptions() {
        return options.get();
    }

    public void setOptions(List<String> options) {
        this.options.set(FXCollections.observableArrayList(options));
    }

    public String getCorrectAnswer() {
        return correctAnswer.get();
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer.set(correctAnswer);
    }

    public String getMediaPath() {
        return mediaPath.get();
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath.set(mediaPath);
    }

    public String getLevel() {
        return level.get();
    }

    public void setLevel(String level) {
        this.level.set(level);
    }

    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public String getGenre() {
        return genre.get();
    }

    public void setGenre(String genre) {
        this.genre.set(genre);
    }

    public String getCountry() {
        return country.get();
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    @Override
    public String toString() {
        return "QuizKids{" +
                "question='" + question.get() + '\'' +
                ", options=" + options.get() +
                ", correctAnswer='" + correctAnswer.get() + '\'' +
                ", mediaPath='" + mediaPath.get() + '\'' +
                ", level='" + level.get() + '\'' +
                ", score=" + score.get() +
                ", genre='" + genre.get() + '\'' +
                ", country='" + country.get() + '\'' +
                '}';
    }
    public List<String> getCleanOptions() {
        List<String> cleanedOptions = new ArrayList<>();
        for (String option : this.options) {
            // Supprime tous les caractères non désirables
            String cleanOption = option.replaceAll("[\\[\\]\"]", "").trim();
            cleanedOptions.add(cleanOption);
        }
        return cleanedOptions;
    }
}
