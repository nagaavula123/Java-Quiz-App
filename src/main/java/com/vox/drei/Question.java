package com.vox.drei;

import java.util.List;
import java.util.UUID;

public class Question {
    private String id;
    private String question;
    private List<String> answers;
    private int correctAnswerIndex;

    public Question() {
        this.id = UUID.randomUUID().toString();
    }

    public Question(String question, List<String> answers, int correctAnswerIndex) {
        this();
        this.question = question;
        this.answers = answers;
        this.correctAnswerIndex = correctAnswerIndex;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public List<String> getAnswers() { return answers; }
    public void setAnswers(List<String> answers) { this.answers = answers; }
    public int getCorrectAnswerIndex() { return correctAnswerIndex; }
    public void setCorrectAnswerIndex(int correctAnswerIndex) { this.correctAnswerIndex = correctAnswerIndex; }

    public boolean isCorrectAnswer(int index) {
        return index == correctAnswerIndex;
    }
}