package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
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
        userAnswerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getUserAnswerDisplay(cellData.getValue())));
        correctAnswerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getCorrectAnswer(cellData.getValue())));

        answersTable.getItems().setAll(questions);
    }

    private String getUserAnswerDisplay(Question question) {
        if (question.getType().equals("MULTIPLE_CHOICE")) {
            int userAnswerIndex = Integer.parseInt(question.getUserAnswer());
            return question.getAnswers().get(userAnswerIndex);
        } else {
            return question.getUserAnswer();
        }
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
        answersTable.setVisible(true);
    }
}