package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ScoreController {

    @FXML private Label quizNameLabel;
    @FXML private Label scoreLabel;
    private List<Question> questions;
    private String quizName;

    public void setScore(int score, int totalQuestions) {
        scoreLabel.setText(String.format("Your Score: %d out of %d", score, totalQuestions));
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setQuizName(String quizName) {
        this.quizName = quizName;
        quizNameLabel.setText("Quiz: " + quizName);
    }

    @FXML
    private void viewAnswers() {
        Stage answerStage = new Stage();
        VBox answerBox = new VBox(10);
        answerBox.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        for (Question question : questions) {
            Label questionLabel = new Label(question.getQuestion());
            questionLabel.getStyleClass().add("question-label");

            ListView<String> answerList = new ListView<>();
            answerList.getItems().addAll(question.getAnswers());
            answerList.getStyleClass().add("answer-list");

            Label correctAnswerLabel = new Label("Correct Answer: " + question.getAnswers().get(question.getCorrectAnswerIndex()));
            correctAnswerLabel.getStyleClass().add("correct-answer-label");

            answerBox.getChildren().addAll(questionLabel, answerList, correctAnswerLabel);
        }

        Scene answerScene = new Scene(answerBox, 400, 600);
        answerStage.setScene(answerScene);
        answerStage.setTitle("Quiz Answers - " + quizName);
        answerStage.show();
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }
}