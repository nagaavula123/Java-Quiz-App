package com.vox.drei;

import javafx.fxml.FXML;

public class DreiController {

    @FXML
    private void startQuiz() throws Exception {
        DreiMain.showView("QuizSelectionView.fxml");
    }

    @FXML
    private void manageQuizzes() throws Exception {
        DreiMain.showView("ManageQuizzesView.fxml");
    }

    @FXML
    private void openSettings() throws Exception {
        DreiMain.showView("QuizSettingsView.fxml");
    }

    @FXML
    private void exitGame() {
        System.exit(0);
    }
}