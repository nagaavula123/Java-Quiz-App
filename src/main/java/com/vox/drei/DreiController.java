package com.vox.drei;

import javafx.fxml.FXML;

public class DreiController {

    @FXML
    private void startQuiz() throws Exception {
        DreiMain.showQuizSelectionView();
    }

    @FXML
    private void manageQuizzes() throws Exception {
        DreiMain.showManageQuizzesView();
    }

    @FXML
    private void openSettings() throws Exception {
        // If you have a QuizSettingsView, you need to add a method for it in DreiMain
        // For now, let's assume it doesn't exist and we'll just print a message
        System.out.println("Settings view not implemented yet");
    }

    @FXML
    private void exitGame() {
        System.exit(0);
    }
}