package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class AddQuestionController {

    @FXML private TextField questionField;
    @FXML private VBox answersBox;
    @FXML private ComboBox<String> correctAnswerComboBox;
    @FXML private ComboBox<String> questionTypeComboBox;

    private Quiz currentQuiz;

    @FXML
    public void initialize() {
        questionTypeComboBox.setItems(FXCollections.observableArrayList("MULTIPLE_CHOICE", "IDENTIFICATION"));
        questionTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateAnswersBox(newVal));

        correctAnswerComboBox.setItems(FXCollections.observableArrayList(
                IntStream.rangeClosed(1, 4)
                        .mapToObj(i -> "Answer " + i)
                        .collect(Collectors.toList())
        ));
    }

    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
    }

    private void updateAnswersBox(String questionType) {
        answersBox.getChildren().clear();
        if ("MULTIPLE_CHOICE".equals(questionType)) {
            for (int i = 1; i <= 4; i++) {
                TextField answerField = new TextField();
                answerField.setPromptText("Answer " + i);
                answersBox.getChildren().add(answerField);
            }
            answersBox.getChildren().add(correctAnswerComboBox);
        } else {
            TextField answerField = new TextField();
            answerField.setPromptText("Correct Answer");
            answersBox.getChildren().add(answerField);
        }
    }

    @FXML
    private void addQuestion() {
        String questionText = questionField.getText();
        String questionType = questionTypeComboBox.getValue();
        List<String> answers;
        String correctAnswer;

        if ("MULTIPLE_CHOICE".equals(questionType)) {
            answers = answersBox.getChildren().stream()
                    .filter(node -> node instanceof TextField)
                    .map(node -> ((TextField) node).getText())
                    .collect(Collectors.toList());
            correctAnswer = answers.get(Integer.parseInt(correctAnswerComboBox.getValue().split(" ")[1]) - 1);
        } else {
            TextField answerField = (TextField) answersBox.getChildren().get(0);
            correctAnswer = answerField.getText();
            answers = List.of(correctAnswer);
        }

        Question newQuestion = new Question(questionText, answers, correctAnswer, questionType);
        QuestionDatabase.addQuestion(newQuestion, currentQuiz.getId());

        Stage stage = (Stage) questionField.getScene().getWindow();
        stage.close();
    }
}