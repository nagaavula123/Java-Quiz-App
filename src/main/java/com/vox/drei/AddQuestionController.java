package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.util.Arrays;
import java.util.List;

public class AddQuestionController {

    @FXML private TextField questionField;
    @FXML private TextField answer1Field;
    @FXML private TextField answer2Field;
    @FXML private TextField answer3Field;
    @FXML private TextField answer4Field;
    @FXML private ComboBox<String> correctAnswerComboBox;

    @FXML
    public void initialize() {
        correctAnswerComboBox.getItems().addAll("Answer 1", "Answer 2", "Answer 3", "Answer 4");
    }

    @FXML
    private void addQuestion() {
        String question = questionField.getText();
        List<String> answers = Arrays.asList(
                answer1Field.getText(),
                answer2Field.getText(),
                answer3Field.getText(),
                answer4Field.getText()
        );
        int correctAnswerIndex = correctAnswerComboBox.getSelectionModel().getSelectedIndex();

        if (question.isEmpty() || answers.contains("") || correctAnswerIndex == -1) {
            // Show error message
            return;
        }

        Question newQuestion = new Question(question, answers, correctAnswerIndex);
        QuestionDatabase.addQuestion(newQuestion);

        // Clear fields after adding
        questionField.clear();
        answer1Field.clear();
        answer2Field.clear();
        answer3Field.clear();
        answer4Field.clear();
        correctAnswerComboBox.getSelectionModel().clearSelection();
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }
}