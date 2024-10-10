package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ManageQuestionsController {

    @FXML private TableView<Question> questionsTable;
    @FXML private TableColumn<Question, String> questionColumn;
    @FXML private TableColumn<Question, String> typeColumn;
    @FXML private TableColumn<Question, Void> actionsColumn;

    private Quiz currentQuiz;
    private List<Question> questions;

    public void setQuiz(Quiz quiz) {
        this.currentQuiz = quiz;
        loadQuestions();
    }

    private void loadQuestions() {
        questions = QuestionDatabase.getQuestionsForQuiz(currentQuiz.getId());
        questionsTable.setItems(FXCollections.observableArrayList(questions));
    }

    @FXML
    public void initialize() {
        setupTable();
    }

    private void setupTable() {
        questionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getQuestion()));
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> editQuestion(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> deleteQuestion(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new VBox(5, editButton, deleteButton));
                }
            }
        });
    }

    @FXML
    private void addNewQuestion() {
        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Add New Question");
        dialog.setHeaderText("Enter the new question details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField questionField = new TextField();
        ComboBox<String> typeComboBox = new ComboBox<>(FXCollections.observableArrayList("MULTIPLE_CHOICE", "IDENTIFICATION"));
        VBox answersBox = new VBox(5);

        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Answers:"), 0, 2);
        grid.add(answersBox, 1, 2);

        typeComboBox.setOnAction(e -> updateAnswersBox(answersBox, typeComboBox.getValue(), null));

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String questionText = questionField.getText();
                String type = typeComboBox.getValue();
                List<String> answers;
                int correctAnswerIndex;

                if (type.equals("MULTIPLE_CHOICE")) {
                    answers = answersBox.getChildren().stream()
                            .filter(node -> node instanceof TextField)
                            .map(node -> ((TextField) node).getText())
                            .collect(Collectors.toList());
                    ComboBox<Integer> correctAnswerComboBox = (ComboBox<Integer>) answersBox.getChildren().get(4);
                    correctAnswerIndex = correctAnswerComboBox.getValue();
                } else {
                    answers = List.of(((TextField) answersBox.getChildren().get(0)).getText());
                    correctAnswerIndex = 0;
                }

                return new Question(questionText, answers, correctAnswerIndex, type);
            }
            return null;
        });

        Optional<Question> result = dialog.showAndWait();
        result.ifPresent(question -> {
            QuestionDatabase.addQuestion(question, currentQuiz.getId());
            loadQuestions();
        });
    }

    private void editQuestion(Question question) {
        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Edit Question");
        dialog.setHeaderText("Edit the question details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField questionField = new TextField(question.getQuestion());
        ComboBox<String> typeComboBox = new ComboBox<>(FXCollections.observableArrayList("MULTIPLE_CHOICE", "IDENTIFICATION"));
        typeComboBox.setValue(question.getType());
        VBox answersBox = new VBox(5);

        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(typeComboBox, 1, 1);
        grid.add(new Label("Answers:"), 0, 2);
        grid.add(answersBox, 1, 2);

        typeComboBox.setOnAction(e -> updateAnswersBox(answersBox, typeComboBox.getValue(), question));
        updateAnswersBox(answersBox, question.getType(), question);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                question.setQuestion(questionField.getText());
                question.setType(typeComboBox.getValue());

                if (question.getType().equals("MULTIPLE_CHOICE")) {
                    List<String> answers = answersBox.getChildren().stream()
                            .filter(node -> node instanceof TextField)
                            .map(node -> ((TextField) node).getText())
                            .collect(Collectors.toList());
                    question.setAnswers(answers);
                    ComboBox<Integer> correctAnswerComboBox = (ComboBox<Integer>) answersBox.getChildren().get(4);
                    question.setCorrectAnswerIndex(correctAnswerComboBox.getValue());
                } else {
                    question.setAnswers(List.of(((TextField) answersBox.getChildren().get(0)).getText()));
                    question.setCorrectAnswerIndex(0);
                }

                return question;
            }
            return null;
        });

        Optional<Question> result = dialog.showAndWait();
        result.ifPresent(updatedQuestion -> {
            QuestionDatabase.updateQuestion(updatedQuestion);
            loadQuestions();
        });
    }

    private void deleteQuestion(Question question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Question");
        alert.setHeaderText("Are you sure you want to delete this question?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            QuestionDatabase.deleteQuestion(question.getId());
            loadQuestions();
        }
    }

    private void updateAnswersBox(VBox answersBox, String type, Question question) {
        answersBox.getChildren().clear();
        if (type.equals("MULTIPLE_CHOICE")) {
            for (int i = 0; i < 4; i++) {
                TextField answerField = new TextField();
                answerField.setPromptText("Answer " + (i + 1));
                if (question != null && i < question.getAnswers().size()) {
                    answerField.setText(question.getAnswers().get(i));
                }
                answersBox.getChildren().add(answerField);
            }
            ComboBox<Integer> correctAnswerComboBox = new ComboBox<>(FXCollections.observableArrayList(0, 1, 2, 3));
            if (question != null) {
                correctAnswerComboBox.setValue(question.getCorrectAnswerIndex());
            }
            answersBox.getChildren().add(correctAnswerComboBox);
        } else {
            TextField answerField = new TextField();
            answerField.setPromptText("Correct Answer");
            if (question != null && !question.getAnswers().isEmpty()) {
                answerField.setText(question.getAnswers().get(0));
            }
            answersBox.getChildren().add(answerField);
        }
    }

    @FXML
    private void backToManageQuizzes() {
        Stage stage = (Stage) questionsTable.getScene().getWindow();
        stage.close();
    }
}