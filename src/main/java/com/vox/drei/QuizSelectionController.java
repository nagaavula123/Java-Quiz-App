package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;

import java.util.List;

public class QuizSelectionController {

    @FXML private TableView<Quiz> quizTableView;
    @FXML private TableColumn<Quiz, String> nameColumn;
    @FXML private TableColumn<Quiz, String> categoryColumn;
    @FXML private TableColumn<Quiz, Integer> questionCountColumn;

    private List<Quiz> quizzes;

    @FXML
    public void initialize() {
        loadQuizzes();
        setupTable();
    }

    private void loadQuizzes() {
        quizzes = QuestionDatabase.loadQuizzes();
        quizTableView.setItems(FXCollections.observableArrayList(quizzes));
    }

    private void setupTable() {
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        categoryColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategory()));
        questionCountColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuestions().size()).asObject());
    }

    @FXML
    private void startSelectedQuiz() throws Exception {
        Quiz selectedQuiz = quizTableView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            QuizGameController.setCurrentQuiz(selectedQuiz);
            DreiMain.showQuizGameView();
        } else {
            showAlert("No Quiz Selected", "Please select a quiz to start.");
        }
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