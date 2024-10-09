package com.vox.drei;

import com.fasterxml.jackson.core.type.TypeReference;
import  com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QuestionDatabase {
    private static final String QUESTIONS_FILE = "questions.json";
    private static final String QUIZZES_FILE = "quizzes.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Question> loadQuestions() {
        try {
            return objectMapper.readValue(new File(QUESTIONS_FILE), new TypeReference<List<Question>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveQuestions(List<Question> questions) {
        try {
            objectMapper.writeValue(new File(QUESTIONS_FILE), questions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addQuestion(Question question) {
        List<Question> questions = loadQuestions();
        questions.add(question);
        saveQuestions(questions);
    }

    public static void updateQuestion(Question updatedQuestion) {
        List<Question> questions = loadQuestions();
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getId().equals(updatedQuestion.getId())) {
                questions.set(i, updatedQuestion);
                break;
            }
        }
        saveQuestions(questions);
    }

    public static void deleteQuestion(String questionId) {
        List<Question> questions = loadQuestions();
        questions.removeIf(q -> q.getId().equals(questionId));
        saveQuestions(questions);

        // Remove the question from all quizzes
        List<Quiz> quizzes = loadQuizzes();
        for (Quiz quiz : quizzes) {
            quiz.removeQuestionId(questionId);
        }
        saveQuizzes(quizzes);
    }

    public static List<Quiz> loadQuizzes() {
        try {
            return objectMapper.readValue(new File(QUIZZES_FILE), new TypeReference<List<Quiz>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveQuizzes(List<Quiz> quizzes) {
        try {
            objectMapper.writeValue(new File(QUIZZES_FILE), quizzes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addQuiz(Quiz quiz) {
        List<Quiz> quizzes = loadQuizzes();
        quizzes.add(quiz);
        saveQuizzes(quizzes);
    }

    public static void updateQuiz(Quiz updatedQuiz) {
        List<Quiz> quizzes = loadQuizzes();
        for (int i = 0; i < quizzes.size(); i++) {
            if (quizzes.get(i).getId().equals(updatedQuiz.getId())) {
                quizzes.set(i, updatedQuiz);
                break;
            }
        }
        saveQuizzes(quizzes);
    }

    public static void deleteQuiz(String quizId) {
        List<Quiz> quizzes = loadQuizzes();
        quizzes.removeIf(q -> q.getId().equals(quizId));
        saveQuizzes(quizzes);
    }

    public static List<Question> getQuestionsForQuiz(String quizId) {
        List<Quiz> quizzes = loadQuizzes();
        List<Question> allQuestions = loadQuestions();
        Quiz quiz = quizzes.stream().filter(q -> q.getId().equals(quizId)).findFirst().orElse(null);
        if (quiz == null) return new ArrayList<>();
        return allQuestions.stream().filter(q -> quiz.getQuestionIds().contains(q.getId())).collect(Collectors.toList());
    }
}