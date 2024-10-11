package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ManageQuestionsController {
    @FXML private VBox rootVBox;
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

        // Set column resize policy
        questionsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Set column widths
        questionColumn.setMaxWidth(1f * Integer.MAX_VALUE * 50); // 50% width
        typeColumn.setMaxWidth(1f * Integer.MAX_VALUE * 20); // 20% width
        actionsColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30); // 30% width

        // Set grow priority
        VBox.setVgrow(questionsTable, Priority.ALWAYS);
    }

    private void setupTable() {
        questionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getQuestion()));
        typeColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox buttonsBox = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> editQuestion(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> deleteQuestion(getTableView().getItems().get(getIndex())));
                buttonsBox.setPadding(new Insets(2));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonsBox);
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

        // Create a ScrollPane to ensure all content is accessible
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400);

        // Create a VBox to hold all the form elements
        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 20, 10, 20));

        TextField questionField = new TextField();
        questionField.setPromptText("Enter your question");

        ComboBox<String> typeComboBox = new ComboBox<>(FXCollections.observableArrayList("MULTIPLE_CHOICE", "IDENTIFICATION"));
        typeComboBox.setPromptText("Select question type");

        VBox answersBox = new VBox(5);
        Label answersLabel = new Label("Answers:");

        content.getChildren().addAll(
                new Label("Question:"),
                questionField,
                new Label("Type:"),
                typeComboBox,
                answersLabel,
                answersBox
        );

        // Update the answers box based on the selected question type
        typeComboBox.setOnAction(e -> updateAnswersBox(answersBox, typeComboBox.getValue(), null));

        scrollPane.setContent(content);
        dialog.getDialogPane().setContent(scrollPane);

        // Enable/Disable save button depending on whether a question is entered.
        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Do some validation (using an anonymous inner-class for brevity)
        questionField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String questionText = questionField.getText();
                String type = typeComboBox.getValue();
                List<String> answers = new ArrayList<>();
                int correctAnswerIndex = 0;

                if (type.equals("MULTIPLE_CHOICE")) {
                    answers = answersBox.getChildren().stream()
                            .filter(node -> node instanceof TextField)
                            .map(node -> ((TextField) node).getText())
                            .collect(Collectors.toList());
                    ComboBox<Integer> correctAnswerComboBox = (ComboBox<Integer>) answersBox.getChildren().get(4);
                    correctAnswerIndex = correctAnswerComboBox.getValue();
                } else {
                    answers.add(((TextField) answersBox.getChildren().get(0)).getText());
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

        ScrollPane scrollPane = new ScrollPane();
        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 150, 10, 10));

        TextField questionField = new TextField(question.getQuestion());
        questionField.setPromptText("Question");
        ComboBox<String> typeComboBox = new ComboBox<>(FXCollections.observableArrayList("MULTIPLE_CHOICE", "IDENTIFICATION"));
        typeComboBox.setValue(question.getType());
        VBox answersBox = new VBox(5);

        content.getChildren().addAll(
                new Label("Question:"),
                questionField,
                new Label("Type:"),
                typeComboBox,
                new Label("Answers:"),
                answersBox
        );

        typeComboBox.setOnAction(e -> updateAnswersBox(answersBox, typeComboBox.getValue(), question));
        updateAnswersBox(answersBox, question.getType(), question);

        scrollPane.setContent(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400);

        dialog.getDialogPane().setContent(scrollPane);

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
            correctAnswerComboBox.setPromptText("Select correct answer");
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

    @FXML
    private void backToManageQuizzes() {
        Stage stage = (Stage) questionsTable.getScene().getWindow();
        stage.close();
    }
}