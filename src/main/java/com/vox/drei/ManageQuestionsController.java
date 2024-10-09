package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;

import java.util.List;

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
        // Open edit dialog
        boolean edited = openEditDialog(question);
        if (edited) {
            QuestionDatabase.saveQuestions(questions);
            loadQuestions(); // Refresh the table
        }
    }

    private boolean openEditDialog(Question question) {
        // Implement edit dialog logic here
        // Return true if the question was edited, false otherwise
        return false; // Placeholder
    }

    private void deleteQuestion(int index) {
        questions.remove(index);
        QuestionDatabase.saveQuestions(questions);
        loadQuestions(); // Refresh the table
    }

    @FXML
    private void addNewQuestion() throws Exception {
        DreiMain.showView("AddQuestionView.fxml");
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }
}