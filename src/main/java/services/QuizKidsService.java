package services;

import dao.QuizKidsDAO;
import entities.QuizKids;

import java.util.ArrayList;
import java.util.List;

public class QuizKidsService {
    private List<QuizKids> quizList = new ArrayList<>();

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
        QuizKidsDAO quizKidsDAO = new QuizKidsDAO();
        return quizList = quizKidsDAO.getAll();
    }
}

