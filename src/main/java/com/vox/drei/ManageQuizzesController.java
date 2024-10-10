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

public class ManageQuizzesController {

    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, String> nameColumn;
    @FXML private TableColumn<Quiz, String> categoryColumn;
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
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));

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
        dialog.setHeaderText("Edit the quiz details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(quiz.getName());
        TextField categoryField = new TextField(quiz.getCategory());

        grid.add(new Label("Quiz Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                quiz.setName(nameField.getText());
                quiz.setCategory(categoryField.getText());
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
        try {
            DreiMain.showManageQuestionsView(quiz);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to open Manage Questions view.");
        }
    }

    @FXML
    private void addNewQuiz() {
        Dialog<Quiz> dialog = new Dialog<>();
        dialog.setTitle("Add New Quiz");
        dialog.setHeaderText("Enter the details for the new quiz");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField categoryField = new TextField();

        grid.add(new Label("Quiz Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Quiz(nameField.getText(), categoryField.getText());
            }
            return null;
        });

        Optional<Quiz> result = dialog.showAndWait();
        result.ifPresent(quiz -> {
            QuestionDatabase.addQuiz(quiz);
            loadQuizzes(); // Refresh the table
        });
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}