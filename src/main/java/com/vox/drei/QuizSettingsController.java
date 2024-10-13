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
    @FXML private CheckBox animationEnabledCheckBox;
    @FXML private CheckBox immediateAnswerCheckBox; // New checkbox for immediate answer feature

    private final Preferences prefs = Preferences.userNodeForPackage(QuizSettingsController.class);

    @FXML
    public void initialize() {
        numQuestionsField.setText(String.valueOf(prefs.getInt("numQuestions", 5)));
        timePerQuestionField.setText(String.valueOf(prefs.getInt("timePerQuestion", 15)));
        timerEnabledCheckBox.setSelected(prefs.getBoolean("timerEnabled", true));
        animationEnabledCheckBox.setSelected(prefs.getBoolean("animationEnabled", true));
        immediateAnswerCheckBox.setSelected(prefs.getBoolean("immediateAnswerEnabled", false)); // Initialize the new checkbox
    }

    @FXML
    private void saveSettings() {
        try {
            int numQuestions = Integer.parseInt(numQuestionsField.getText());
            int timePerQuestion = Integer.parseInt(timePerQuestionField.getText());
            boolean timerEnabled = timerEnabledCheckBox.isSelected();
            boolean animationEnabled = animationEnabledCheckBox.isSelected();
            boolean immediateAnswerEnabled = immediateAnswerCheckBox.isSelected(); // Get the state of the new checkbox

            prefs.putBoolean("animationEnabled", animationEnabled);
            prefs.putInt("numQuestions", numQuestions);
            prefs.putInt("timePerQuestion", timePerQuestion);
            prefs.putBoolean("timerEnabled", timerEnabled);
            prefs.putBoolean("immediateAnswerEnabled", immediateAnswerEnabled); // Save the state of the new feature

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