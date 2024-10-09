package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ManageQuestionsController {

    @FXML private TableView<Question> questionsTable;
    @FXML private TableColumn<Question, String> questionColumn;
    @FXML private TableColumn<Question, Void> actionsColumn;

    private List<Question> questions;

    @FXML
    public void initialize() {
        loadQuestions();
        setupTable();
    }

    private void loadQuestions() {
        questions = QuestionDatabase.loadQuestions();
        questionsTable.setItems(FXCollections.observableArrayList(questions));
    }

    private void setupTable() {
        questionColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getQuestion()));

        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.setOnAction(event -> editQuestion(getIndex()));
                deleteButton.setOnAction(event -> deleteQuestion(getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void editQuestion(int index) {
        Question question = questions.get(index);
        boolean edited = openEditDialog(question);
        if (edited) {
            QuestionDatabase.updateQuestion(question);
            loadQuestions(); // Refresh the table
        }
    }

    private boolean openEditDialog(Question question) {
        Dialog<Question> dialog = new Dialog<>();
        dialog.setTitle("Edit Question");
        dialog.setHeaderText("Edit the question and its answers");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField questionField = new TextField(question.getQuestion());
        List<TextField> answerFields = question.getAnswers().stream()
                .map(TextField::new)
                .collect(Collectors.toList());
        ComboBox<Integer> correctAnswerComboBox = new ComboBox<>(FXCollections.observableArrayList(
                IntStream.range(0, question.getAnswers().size()).boxed().collect(Collectors.toList())
        ));
        correctAnswerComboBox.getSelectionModel().select(question.getCorrectAnswerIndex());

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
                question.setQuestion(questionField.getText());
                for (int i = 0; i < answerFields.size(); i++) {
                    question.getAnswers().set(i, answerFields.get(i).getText());
                }
                question.setCorrectAnswerIndex(correctAnswerComboBox.getValue());
                return question;
            }
            return null;
        });

        Optional<Question> result = dialog.showAndWait();
        return result.isPresent();
    }

    private void deleteQuestion(int index) {
        Question question = questions.get(index);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Question");
        alert.setHeaderText("Are you sure you want to delete this question?");
        alert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            QuestionDatabase.deleteQuestion(question.getId());
            loadQuestions(); // Refresh the table
        }
    }

    @FXML
    private void addNewQuestion() {
        Question newQuestion = new Question("New Question", List.of("Answer 1", "Answer 2", "Answer 3", "Answer 4"), 0);
        boolean edited = openEditDialog(newQuestion);
        if (edited) {
            QuestionDatabase.addQuestion(newQuestion);
            loadQuestions(); // Refresh the table
        }
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }
}