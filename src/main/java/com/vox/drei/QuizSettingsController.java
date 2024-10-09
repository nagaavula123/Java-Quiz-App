package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.prefs.Preferences;

public class QuizSettingsController {

    @FXML private TextField numQuestionsField;
    @FXML private TextField timePerQuestionField;
    @FXML private CheckBox timerEnabledCheckBox;
    @FXML private Label notificationLabel;

    private Preferences prefs = Preferences.userNodeForPackage(QuizSettingsController.class);

    @FXML
    public void initialize() {
        numQuestionsField.setText(String.valueOf(prefs.getInt("numQuestions", 5)));
        timePerQuestionField.setText(String.valueOf(prefs.getInt("timePerQuestion", 15)));
        timerEnabledCheckBox.setSelected(prefs.getBoolean("timerEnabled", true));
    }

    @FXML
    private void saveSettings() {
        try {
            int numQuestions = Integer.parseInt(numQuestionsField.getText());
            int timePerQuestion = Integer.parseInt(timePerQuestionField.getText());
            boolean timerEnabled = timerEnabledCheckBox.isSelected();

            prefs.putInt("numQuestions", numQuestions);
            prefs.putInt("timePerQuestion", timePerQuestion);
            prefs.putBoolean("timerEnabled", timerEnabled);

            notificationLabel.setText("Settings saved successfully!");
        } catch (NumberFormatException e) {
            notificationLabel.setText("Invalid input. Please enter numbers only.");
        }
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }
}