package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.util.List;

public class ScoreController {
    @FXML private VBox rootVBox;
    @FXML private Label quizNameLabel;
    @FXML private Label scoreLabel;
    @FXML private TableView<Question> answersTable;
    @FXML private TableColumn<Question, String> questionColumn;
    @FXML private TableColumn<Question, String> userAnswerColumn;
    @FXML private TableColumn<Question, String> correctAnswerColumn;

    private List<Question> questions;
    private String quizName;

    @FXML
    public void initialize() {
        answersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        questionColumn.setMaxWidth(1f * Integer.MAX_VALUE * 40);
        userAnswerColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        correctAnswerColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30);

        answersTable.setVisible(false);
    }

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
        userAnswerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserAnswer()));
        correctAnswerColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCorrectAnswer()));

        answersTable.getItems().setAll(questions);
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }

    @FXML
    private void viewAnswers() {
        answersTable.setVisible(true);

        Stage stage = (Stage) rootVBox.getScene().getWindow();
        double newHeight = 600; // Set a larger height to accommodate the table
        double newWidth = 800;  // Set a larger width for better readability

        stage.setHeight(newHeight);
        stage.setWidth(newWidth);

        // Center the stage on the screen
        stage.centerOnScreen();
    }
}