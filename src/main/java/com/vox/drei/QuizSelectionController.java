package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;

import java.util.List;

public class QuizSelectionController {

    @FXML private ListView<Quiz> quizListView;

    private List<Quiz> quizzes;

    @FXML
    public void initialize() {
        loadQuizzes();
    }

    private void loadQuizzes() {
        quizzes = QuestionDatabase.loadQuizzes();
        quizListView.setItems(FXCollections.observableArrayList(quizzes));
        quizListView.setCellFactory(param -> new javafx.scene.control.ListCell<Quiz>() {
            @Override
            protected void updateItem(Quiz item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
    }

    @FXML
    private void startSelectedQuiz() throws Exception {
        Quiz selectedQuiz = quizListView.getSelectionModel().getSelectedItem();
        if (selectedQuiz != null) {
            QuizGameController.setCurrentQuiz(selectedQuiz);
            DreiMain.showView("QuizGameView.fxml");
        }
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }
}