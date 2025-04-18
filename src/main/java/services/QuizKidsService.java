package services;

import entities.QuizKids;

import java.util.ArrayList;
import java.util.List;

public class QuizKidsService {
    private final List<QuizKids> quizList = new ArrayList<>();

    public void addQuiz(QuizKids quiz) {
        quizList.add(quiz);
    }

    public void updateQuiz(int index, QuizKids updatedQuiz) {
        quizList.set(index, updatedQuiz);
    }

    public void deleteQuiz(int index) {
        quizList.remove(index);
    }

    public List<QuizKids> getAllQuizzes() {
        return quizList;
    }
}

