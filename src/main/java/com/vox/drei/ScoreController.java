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
        populateAnswersTable(); // Call to populate the table with questions
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
        quizNameLabel.setText("Quiz: " + quizName);
    }

    private void populateAnswersTable() {
        questionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getQuestion()));
        userAnswerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserAnswer())); // Actual user answer here
        correctAnswerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(getCorrectAnswer(cellData.getValue())));

        // Populate the table with the question data
        answersTable.getItems().setAll(questions); // Use setAll to refresh table contents
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
        answersTable.setVisible(true);  // Make the answers table visible when the button is pressed
    }

}
