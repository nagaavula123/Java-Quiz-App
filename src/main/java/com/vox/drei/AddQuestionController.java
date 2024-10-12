package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
            correctAnswerComboBox = new ComboBox<>();
            correctAnswerComboBox.setPromptText("Select correct answer");
            answersBox.getChildren().add(correctAnswerComboBox);

            // Update correct answer options when answer fields change
            for (int i = 0; i < 4; i++) {
                final int index = i;
                TextField field = (TextField) answersBox.getChildren().get(i);
                field.textProperty().addListener((obs, oldVal, newVal) -> updateCorrectAnswerOptions());
            }
        } else {
            TextField answerField = new TextField();
            answerField.setPromptText("Correct Answer");
            answersBox.getChildren().add(answerField);
        }
    }

    private void updateCorrectAnswerOptions() {
        List<String> options = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            TextField field = (TextField) answersBox.getChildren().get(i);
            String text = field.getText().trim();
            if (!text.isEmpty()) {
                options.add("Answer " + (i + 1));
            }
        }
        correctAnswerComboBox.setItems(FXCollections.observableArrayList(options));
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
                    .filter(text -> !text.trim().isEmpty())
                    .collect(Collectors.toList());
            String selectedAnswer = correctAnswerComboBox.getValue();
            if (selectedAnswer != null) {
                int correctAnswerIndex = Integer.parseInt(selectedAnswer.split(" ")[1]) - 1;
                correctAnswer = answers.get(correctAnswerIndex);
            } else {
                // Handle the case where no correct answer is selected
                showAlert("Error", "Please select a correct answer.");
                return;
            }
        } else {
            TextField answerField = (TextField) answersBox.getChildren().get(0);
            correctAnswer = answerField.getText();
            answers = List.of(correctAnswer);
        }

        if (answers.isEmpty()) {
            showAlert("Error", "Please provide at least one answer.");
            return;
        }

        Question newQuestion = new Question(questionText, answers, correctAnswer, questionType);
        QuestionDatabase.addQuestion(newQuestion, currentQuiz.getId());

        Stage stage = (Stage) questionField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}