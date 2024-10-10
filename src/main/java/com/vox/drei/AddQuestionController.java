package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.util.Arrays;
import java.util.List;

public class AddQuestionController {

    @FXML private TextField questionField;
    @FXML private TextArea answersField;
    @FXML private ComboBox<Integer> correctAnswerComboBox;
    @FXML private ComboBox<String> questionTypeComboBox;

    private Quiz currentQuiz;

    @FXML
    public void initialize() {
        correctAnswerComboBox.setItems(FXCollections.observableArrayList(0, 1, 2, 3));
        questionTypeComboBox.setItems(FXCollections.observableArrayList("MULTIPLE_CHOICE", "IDENTIFICATION"));
    }

    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
    }

    @FXML
    private void addQuestion() {
        String questionText = questionField.getText();
        String answersText = answersField.getText();
        List<String> answers = Arrays.asList(answersText.split("\n"));
        int correctAnswerIndex = correctAnswerComboBox.getValue();
        String questionType = questionTypeComboBox.getValue();

        Question newQuestion = new Question(questionText, answers, correctAnswerIndex, questionType);
        QuestionDatabase.addQuestion(newQuestion, currentQuiz.getId());

        Stage stage = (Stage) questionField.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void cancel() {
        Stage stage = (Stage) questionField.getScene().getWindow();
        stage.close();
    }
}