package com.vox.drei;

import javafx.fxml.FXML;

public class DreiController {

    @FXML
    private void startQuiz() throws Exception {
        DreiMain.showView("QuizGameView.fxml");
    }

    @FXML
    private void openSettings() throws Exception {
        DreiMain.showView("QuizSettingsView.fxml");
    }

    @FXML
    private void manageQuestions() throws Exception {
        DreiMain.showView("ManageQuestionsView.fxml");
    }

    @FXML
    private void exitGame() {
        System.exit(0);
    }
}