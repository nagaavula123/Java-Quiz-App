package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ScoreController {

    @FXML private Label quizNameLabel;
    @FXML private Label scoreLabel;
    @FXML private TableView<Question> answersTable;
    @FXML private TableColumn<Question, String> questionColumn;
    @FXML private TableColumn<Question, String> userAnswerColumn;
    @FXML private TableColumn<Question, String> correctAnswerColumn;

    private List<Question> questions;
    private String quizName;

    public void setScore(int score, int totalQuestions) {
        scoreLabel.setText(String.format("Your Score: %d out of %d", score, totalQuestions));
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        populateAnswersTable();
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
        quizNameLabel.setText("Quiz: " + quizName);
    }

    private void populateAnswersTable() {
        questionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getQuestion()));
        userAnswerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getUserAnswer(cellData.getValue())));
        correctAnswerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getCorrectAnswer(cellData.getValue())));

        answersTable.getItems().addAll(questions);
    }

    private String getUserAnswer(Question question) {
        // This method should be implemented to return the user's answer for each question
        // You'll need to store the user's answers during the quiz
        return "User Answer"; // Placeholder
    }

    private String getCorrectAnswer(Question question) {
        return question.getAnswers().get(question.getCorrectAnswerIndex());
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }

    @FXML
    private void viewAnswers() {
        // Logic for viewing answers goes here
        // You can show the TableView (answersTable) or any other UI element with the answers
        answersTable.setVisible(true);  // If hidden by default, make it visible here
    }

}