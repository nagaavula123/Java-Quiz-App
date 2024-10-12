package com.vox.drei;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class QuizSelectionController {

    @FXML private TableView<Quiz> quizTableView;
    @FXML private TableColumn<Quiz, String> nameColumn;
    @FXML private TableColumn<Quiz, String> categoryColumn;
    @FXML private TableColumn<Quiz, Integer> questionCountColumn;
    @FXML private TextField searchField;

    private List<Quiz> quizzes;
    private ObservableList<Quiz> observableQuizzes;
    private FilteredList<Quiz> filteredQuizzes;

    @FXML
    public void initialize() {
        loadQuizzes();
        setupTable();

        quizTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        nameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        categoryColumn.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        questionCountColumn.setMaxWidth(1f * Integer.MAX_VALUE * 40);

        VBox.setVgrow(quizTableView, Priority.ALWAYS);

        setupSearch();
    }

    private void setupSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredQuizzes.setPredicate(quiz -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (quiz.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (quiz.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    private void loadQuizzes() {
        quizzes = QuestionDatabase.loadQuizzes();
        observableQuizzes = FXCollections.observableArrayList(quizzes);
        filteredQuizzes = new FilteredList<>(observableQuizzes, p -> true);
        quizTableView.setItems(filteredQuizzes);
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