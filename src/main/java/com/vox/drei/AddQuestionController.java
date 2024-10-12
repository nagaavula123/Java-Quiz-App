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
    @FXML private Button saveButton;

    private Quiz currentQuiz;

    @FXML
    public void initialize() {
        questionTypeComboBox.setItems(FXCollections.observableArrayList("MULTIPLE_CHOICE", "IDENTIFICATION"));
        questionTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> updateAnswersBox(newVal));

        // Disable save button by default
        saveButton.setDisable(true);

        // Add listeners to enable/disable save button
        questionField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
        correctAnswerComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
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

                // Add listener to update correct answer options
                answerField.textProperty().addListener((obs, oldVal, newVal) -> updateCorrectAnswerOptions());
            }
            correctAnswerComboBox = new ComboBox<>();
            correctAnswerComboBox.setPromptText("Select correct answer");
            answersBox.getChildren().add(correctAnswerComboBox);

            // Add listener to validate form when correct answer is selected
            correctAnswerComboBox.valueProperty().addListener((obs, oldVal, newVal) -> validateForm());
        } else {
            TextField answerField = new TextField();
            answerField.setPromptText("Correct Answer");
            answersBox.getChildren().add(answerField);

            // Add listener to validate form when answer is entered
            answerField.textProperty().addListener((obs, oldVal, newVal) -> validateForm());
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
        validateForm();
    }

    private void validateForm() {
        boolean isValid = !questionField.getText().trim().isEmpty();

        if ("MULTIPLE_CHOICE".equals(questionTypeComboBox.getValue())) {
            int nonEmptyAnswers = (int) answersBox.getChildren().stream()
                    .filter(node -> node instanceof TextField)
                    .map(node -> ((TextField) node).getText().trim())
                    .filter(text -> !text.isEmpty())
                    .count();
            isValid = isValid && nonEmptyAnswers >= 4 && correctAnswerComboBox.getValue() != null;
        } else {
            TextField answerField = (TextField) answersBox.getChildren().get(0);
            isValid = isValid && !answerField.getText().trim().isEmpty();
        }

        saveButton.setDisable(!isValid);
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