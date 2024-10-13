package com.vox.drei;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

public class QuizGameController {

    @FXML private Label quizNameLabel;
    @FXML private ProgressBar quizProgressBar;
    @FXML private Label questionNumberLabel;
    @FXML private Label questionLabel;
    @FXML private GridPane answerGrid;
    @FXML private Circle timerCircle;
    @FXML private Label timerLabel;
    @FXML private Label notificationLabel;
    @FXML private Label correctAnswerLabel;
    @FXML private Button submitAnswerButton;
    @FXML private Button nextQuestionButton;
    @FXML private Button toggleTimerButton;
    @FXML private Button exitQuizButton;
    @FXML private HBox buttonBox;

    private static Quiz currentQuiz;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Preferences prefs = Preferences.userNodeForPackage(QuizSettingsController.class);
    private boolean answerSubmitted = false;
    private boolean immediateAnswerEnabled;

    private Instant startTime;
    private java.time.Duration elapsedTime = java.time.Duration.ZERO;
    private java.time.Duration remainingTime;
    private Timeline timer;
    private boolean timerRunning = false;

    public static void setCurrentQuiz(Quiz quiz) {
        currentQuiz = quiz;
    }

    @FXML
    public void initialize() {
        if (currentQuiz == null) {
            // Handle error: No quiz selected
            return;
        }

        quizNameLabel.setText(currentQuiz.getName());
        loadQuestions();
        initializeTimer();
        immediateAnswerEnabled = prefs.getBoolean("immediateAnswerEnabled", false);
        updateButtonVisibility();
        displayQuestion();

        if (!prefs.getBoolean("timerEnabled", true)) {
            timerCircle.setVisible(false);
            timerLabel.setVisible(false);
            toggleTimerButton.setVisible(false);
        }
        notificationLabel.setVisible(false);
        correctAnswerLabel.setVisible(false);
    }


    private void updateButtonVisibility() {
        if (submitAnswerButton != null) {
            submitAnswerButton.setVisible(immediateAnswerEnabled && !answerSubmitted);
        }
        if (nextQuestionButton != null) {
            nextQuestionButton.setVisible(!immediateAnswerEnabled || answerSubmitted);
        }
        if (toggleTimerButton != null) {
            toggleTimerButton.setVisible(prefs.getBoolean("timerEnabled", true));
        }
        if (exitQuizButton != null) {
            exitQuizButton.setVisible(true);
        }

        // Update the button box
        if (buttonBox != null) {
            buttonBox.getChildren().clear();
            if (submitAnswerButton != null && submitAnswerButton.isVisible()) buttonBox.getChildren().add(submitAnswerButton);
            if (nextQuestionButton != null && nextQuestionButton.isVisible()) buttonBox.getChildren().add(nextQuestionButton);
            if (toggleTimerButton != null && toggleTimerButton.isVisible()) buttonBox.getChildren().add(toggleTimerButton);
            if (exitQuizButton != null && exitQuizButton.isVisible()) buttonBox.getChildren().add(exitQuizButton);
        }
    }

    @FXML
    private void submitAnswer() {
        if (!answerSubmitted) {
            checkAnswer();
            showCorrectAnswer();
            answerSubmitted = true;
            updateButtonVisibility();
        }
    }

    private void showCorrectAnswer() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        correctAnswerLabel.setText("Correct Answer: " + currentQuestion.getCorrectAnswer());
        correctAnswerLabel.setVisible(true);
    }

    private void loadQuestions() {
        questions = currentQuiz.getQuestions();
        Collections.shuffle(questions);
        int numQuestions = Math.min(prefs.getInt("numQuestions", 5), questions.size());
        questions = questions.subList(0, numQuestions);
        updateQuizProgress();
    }

    private void updateQuizProgress() {
        double progress = (double) (currentQuestionIndex + 1) / questions.size();
        quizProgressBar.setProgress(progress);
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishQuiz();
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        questionNumberLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
        questionLabel.setText(currentQuestion.getQuestion());

        answerGrid.getChildren().clear();

        if (currentQuestion.getType().equals("MULTIPLE_CHOICE")) {
            displayMultipleChoiceQuestion(currentQuestion);
        } else if (currentQuestion.getType().equals("IDENTIFICATION")) {
            displayIdentificationQuestion();
        }

        resetTimer();
        notificationLabel.setVisible(false);
        correctAnswerLabel.setVisible(false);
        answerSubmitted = false;
        toggleTimerButton.setDisable(false);
        updateButtonVisibility();
        updateQuizProgress();

        // Add fade-in animation for the question
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), questionLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void displayMultipleChoiceQuestion(Question currentQuestion) {
        ToggleGroup group = new ToggleGroup();
        List<String> answers = currentQuestion.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            RadioButton rb = new RadioButton(answers.get(i));
            rb.setToggleGroup(group);
            rb.setUserData(answers.get(i));
            answerGrid.add(rb, 0, i);
        }
    }

    private void displayIdentificationQuestion() {
        TextField answerField = new TextField();
        answerField.setPromptText("Type your answer here");
        answerField.setStyle("-fx-prompt-text-fill: #808080;"); // Dark gray prompt text
        answerGrid.add(answerField, 0, 0);
    }

    private void initializeTimer() {
        remainingTime = java.time.Duration.ofSeconds(prefs.getInt("timePerQuestion", 15));
        timer = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    java.time.Duration timeLeft = remainingTime.minus(java.time.Duration.between(startTime, Instant.now()));
                    if (timeLeft.isZero() || timeLeft.isNegative()) {
                        handleTimeUp();
                    } else {
                        updateTimerDisplay(timeLeft);
                    }
                }),
                new KeyFrame(Duration.seconds(1))
        );
        timer.setCycleCount(Animation.INDEFINITE);
    }

    private void updateTimerDisplay(java.time.Duration timeLeft) {
        int seconds = (int) timeLeft.getSeconds();
        timerLabel.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));

        // Update the timer circle
        double progress = 1 - (double) seconds / prefs.getInt("timePerQuestion", 15);
        timerCircle.setStyle(String.format("-fx-fill: linear-gradient(from 0%% 0%% to 0%% 100%%, #003366 %f%%, #FFB300 %f%%);",
                progress * 100, progress * 100));
    }

    private void resetTimer() {
        if (timer != null) {
            timer.stop();
        }
        remainingTime = java.time.Duration.ofSeconds(prefs.getInt("timePerQuestion", 15));
        startTime = null;
        elapsedTime = java.time.Duration.ZERO;
        if (prefs.getBoolean("timerEnabled", true)) {
            startTimer();
        }
    }

    private void startTimer() {
        if (timer == null) {
            initializeTimer();
        }
        startTime = Instant.now();
        timer.play();
        timerRunning = true;
        toggleTimerButton.setText("Pause Timer");
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.pause();
            elapsedTime = elapsedTime.plus(java.time.Duration.between(startTime, Instant.now()));
            timerRunning = false;
            toggleTimerButton.setText("Resume Timer");
        }
    }


    private void handleTimeUp() {
        if (timer != null) {
            timer.stop();
        }
        timerRunning = false;
        Platform.runLater(() -> {
            notificationLabel.setText("Time's up! Click 'Next Question' to continue.");
            notificationLabel.setVisible(true);
            nextQuestionButton.setDisable(false);
            toggleTimerButton.setDisable(true);
            disableAnswerControls();
        });
    }

    private void disableAnswerControls() {
        for (javafx.scene.Node node : answerGrid.getChildren()) {
            node.setDisable(true);
        }
    }

    @FXML
    private void nextQuestion() {
        if (immediateAnswerEnabled && !answerSubmitted) {
            // If immediate answer is enabled but the answer hasn't been submitted, do nothing
            return;
        }

        if (!immediateAnswerEnabled) {
            checkAnswer();
        }

        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion();
            answerSubmitted = false;
            updateButtonVisibility();
        } else {
            finishQuiz();
        }
    }


    private boolean isAnswerSelected() {
        Question currentQuestion = questions.get(currentQuestionIndex);
        if (currentQuestion.getType().equals("MULTIPLE_CHOICE")) {
            ToggleGroup group = ((RadioButton) answerGrid.getChildren().get(0)).getToggleGroup();
            return group.getSelectedToggle() != null;
        } else if (currentQuestion.getType().equals("IDENTIFICATION")) {
            TextField answerField = (TextField) answerGrid.getChildren().get(0);
            return !answerField.getText().trim().isEmpty();
        }
        return false;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show();
    }

    private void checkAnswer() {
        if (answerSubmitted) {
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        if (currentQuestion.getType().equals("MULTIPLE_CHOICE")) {
            ToggleGroup group = ((RadioButton) answerGrid.getChildren().get(0)).getToggleGroup();
            if (group.getSelectedToggle() != null) {
                String selectedAnswer = (String) group.getSelectedToggle().getUserData();
                currentQuestion.setUserAnswer(selectedAnswer);
                if (currentQuestion.isCorrectAnswer(selectedAnswer)) {
                    score++;
                }
            }
        } else if (currentQuestion.getType().equals("IDENTIFICATION")) {
            TextField answerField = (TextField) answerGrid.getChildren().get(0);
            String userAnswer = answerField.getText().trim();
            currentQuestion.setUserAnswer(userAnswer);
            if (currentQuestion.isCorrectAnswer(userAnswer)) {
                score++;
            }
        }
        answerSubmitted = true;
    }

    private void finishQuiz() {
        if (timer != null) {
            timer.stop();
        }
        try {
            DreiMain.showScoreView(score, questions.size(), questions, currentQuiz.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exitQuiz() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Quiz");
        alert.setHeaderText("Are you sure you want to exit the quiz?");
        alert.setContentText("Your progress will be lost.");

        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                try {
                    DreiMain.showMainView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void toggleTimer() {
        if (timerRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }
}