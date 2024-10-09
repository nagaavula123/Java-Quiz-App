package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

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
        nameColumn.setCellValueFactory(cellData -> new  javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));

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
        QuestionDatabase.deleteQuiz(quiz.getId());
        loadQuizzes(); // Refresh the table
    }

    private void manageQuizQuestions(int index) {
        Quiz quiz = quizzes.get(index);
        // Open a new dialog to manage questions for this quiz
        openManageQuestionsDialog(quiz);
    }

    private void openManageQuestionsDialog(Quiz quiz) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Manage Quiz Questions");
        dialog.setHeaderText("Manage questions for quiz: " + quiz.getName());

        ButtonType closeButtonType = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButtonType);

        VBox content = new VBox(10);
        content.setPadding(new Insets(20, 20, 10, 20));

        List<Question> allQuestions = QuestionDatabase.loadQuestions();
        List<Question> quizQuestions = QuestionDatabase.getQuestionsForQuiz(quiz.getId());

        ListView<Question> questionListView = new ListView<>(FXCollections.observableArrayList(allQuestions));
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

        content.getChildren().add(questionListView);

        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }

    @FXML
    private void addNewQuiz() {
        Quiz newQuiz = new Quiz("New Quiz");
        QuestionDatabase.addQuiz(newQuiz);
        loadQuizzes(); // Refresh the table
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }
}