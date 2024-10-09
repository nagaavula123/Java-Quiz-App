package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.stage.Modality;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ManageQuizzesController {

    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, String> nameColumn;
    @FXML private TableColumn<Quiz, Void> actionsColumn;

    private List<Quiz> quizzes;

    @FXML
    public void initialize() {
        loadQuizzes();
        setupTable();
    }

    private void loadQuizzes() {
        quizzes = QuestionDatabase.loadQuizzes();
        quizzesTable.setItems(FXCollections.observableArrayList(quizzes));
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button manageQuestionsButton = new Button("Manage Questions");

            {
                editButton.setOnAction(event -> editQuiz(getIndex()));
                deleteButton.setOnAction(event -> deleteQuiz(getIndex()));
                manageQuestionsButton.setOnAction(event -> manageQuizQuestions(getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton, manageQuestionsButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void editQuiz(int index) {
        Quiz quiz = quizzes.get(index);
        boolean edited = openEditDialog(quiz);
        if (edited) {
            QuestionDatabase.updateQuiz(quiz);
            loadQuizzes(); // Refresh the table
        }
    }

    private boolean openEditDialog(Quiz quiz) {
        Dialog<Quiz> dialog = new Dialog<>();
        dialog.setTitle("Edit Quiz");
        dialog.setHeaderText("Edit the quiz name");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(quiz.getName());

        grid.add(new Label("Quiz Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                quiz.setName(nameField.getText());
                return quiz;
            }
            return null;
        });

        Optional<Quiz> result = dialog.showAndWait();
        return result.isPresent();
    }

    private void deleteQuiz(int index) {
        Quiz quiz = quizzes.get(index);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Quiz");
        alert.setHeaderText("Are you sure you want to delete this quiz?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            QuestionDatabase.deleteQuiz(quiz.getId());
            loadQuizzes(); // Refresh the table
        }
    }

    private void manageQuizQuestions(int index) {
        Quiz quiz = quizzes.get(index);
        Stage questionStage = new Stage();
        questionStage.initModality(Modality.APPLICATION_MODAL);
        questionStage.setTitle("Manage Quiz Questions");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        Label titleLabel = new Label("Manage questions for quiz: " + quiz.getName());
        titleLabel.getStyleClass().add("subtitle");

        ListView<Question> questionListView = new ListView<>();
        questionListView.setPrefHeight(300);

        List<Question> allQuestions = QuestionDatabase.loadQuestions();
        List<Question> quizQuestions = QuestionDatabase.getQuestionsForQuiz(quiz.getId());

        questionListView.setItems(FXCollections.observableArrayList(allQuestions));
        questionListView.setCellFactory(lv -> new CheckBoxListCell<>(item -> {
            javafx.beans.property.BooleanProperty observable = new javafx.beans.property.SimpleBooleanProperty(quizQuestions.contains(item));
            observable.addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    quiz.addQuestionId(item.getId());
                } else {
                    quiz.removeQuestionId(item.getId());
                }
                QuestionDatabase.updateQuiz(quiz);
            });
            return observable;
        }));

        Button addQuestionButton = new Button("Add New Question");
        addQuestionButton.setOnAction(e -> addNewQuestion(quiz, questionListView));

        Button editQuestionButton = new Button("Edit Selected Question");
        editQuestionButton.setOnAction(e -> editSelectedQuestion(questionListView));

        Button deleteQuestionButton = new Button("Delete Selected Question");
        deleteQuestionButton.setOnAction(e -> deleteSelectedQuestion(questionListView));

        HBox buttonBox = new HBox(10, addQuestionButton, editQuestionButton, deleteQuestionButton);

        layout.getChildren().addAll(titleLabel, questionListView, buttonBox);

        Scene scene = new Scene(layout, 600, 400);
        questionStage.setScene(scene);
        questionStage.showAndWait();
    }

    private void addNewQuestion(Quiz quiz, ListView<Question> questionListView) {
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
        TextField answer1Field = new TextField();
        TextField answer2Field = new TextField();
        TextField answer3Field = new TextField();
        TextField answer4Field = new TextField();
        ComboBox<Integer> correctAnswerComboBox = new ComboBox<>(FXCollections.observableArrayList(0, 1, 2, 3));

        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        grid.add(new Label("Answer 1:"), 0, 1);
        grid.add(answer1Field, 1, 1);
        grid.add(new Label("Answer 2:"), 0, 2);
        grid.add(answer2Field, 1, 2);
        grid.add(new Label("Answer 3:"), 0, 3);
        grid.add(answer3Field, 1, 3);
        grid.add(new Label("Answer 4:"), 0, 4);
        grid.add(answer4Field, 1, 4);
        grid.add(new Label("Correct Answer:"), 0, 5);
        grid.add(correctAnswerComboBox, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                List<String> answers = List.of(answer1Field.getText(), answer2Field.getText(), answer3Field.getText(), answer4Field.getText());
                return new Question(questionField.getText(), answers, correctAnswerComboBox.getValue());
            }
            return null;
        });

        Optional<Question> result = dialog.showAndWait();
        result.ifPresent(question -> {
            QuestionDatabase.addQuestion(question);
            quiz.addQuestionId(question.getId());
            QuestionDatabase.updateQuiz(quiz);
            questionListView.getItems().add(question);
        });
    }

    private void editSelectedQuestion(ListView<Question> questionListView) {
        Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion == null) {
            showAlert("No Question Selected", "Please select a question to edit.");
            return;
        }

        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Edit Question");
        dialog.setHeaderText("Edit the question details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField questionField = new TextField(selectedQuestion.getQuestion());
        List<TextField> answerFields = selectedQuestion.getAnswers().stream()
                .map(TextField::new)
                .collect(Collectors.toList());
        ComboBox<Integer> correctAnswerComboBox = new ComboBox<>(FXCollections.observableArrayList(0, 1, 2, 3));
        correctAnswerComboBox.getSelectionModel().select(selectedQuestion.getCorrectAnswerIndex());

        grid.add(new Label("Question:"), 0, 0);
        grid.add(questionField, 1, 0);
        for (int i = 0; i < answerFields.size(); i++) {
            grid.add(new Label("Answer " + (i + 1) + ":"), 0, i + 1);
            grid.add(answerFields.get(i), 1, i + 1);
        }
        grid.add(new Label("Correct Answer:"), 0, answerFields.size() + 1);
        grid.add(correctAnswerComboBox, 1, answerFields.size() + 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                selectedQuestion.setQuestion(questionField.getText());
                for (int i = 0; i < answerFields.size(); i++) {
                    selectedQuestion.getAnswers().set(i, answerFields.get(i).getText());
                }
                selectedQuestion.setCorrectAnswerIndex(correctAnswerComboBox.getValue());
                return selectedQuestion;
            }
            return null;
        });

        Optional<Question> result = dialog.showAndWait();
        result.ifPresent(question -> {
            QuestionDatabase.updateQuestion(question);
            questionListView.refresh();
        });
    }

    private void deleteSelectedQuestion(ListView<Question> questionListView) {
        Question selectedQuestion = questionListView.getSelectionModel().getSelectedItem();
        if (selectedQuestion == null) {
            showAlert("No Question Selected", "Please select a question to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Question");
        alert.setHeaderText("Are you sure you want to delete this question?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            QuestionDatabase.deleteQuestion(selectedQuestion.getId());
            questionListView.getItems().remove(selectedQuestion);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void addNewQuiz() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New Quiz");
        dialog.setHeaderText("Enter the name for the new quiz");
        dialog.setContentText("Quiz Name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Quiz newQuiz = new Quiz(name);
            QuestionDatabase.addQuiz(newQuiz);
            loadQuizzes(); // Refresh the table
        });
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }
}