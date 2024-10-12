package com.vox.drei;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

public class QuizGameController {

    @FXML private Label questionLabel;
    @FXML private GridPane answerGrid;
    @FXML private Label timerLabel;
    @FXML private Button nextQuestionButton;
    @FXML private Button submitAnswerButton; // New button for submitting answers
    @FXML private Button exitQuizButton;
    @FXML private Button toggleTimerButton;
    @FXML private Label notificationLabel;
    @FXML private Label correctAnswerLabel;

    private static Quiz currentQuiz;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private Preferences prefs = Preferences.userNodeForPackage(QuizSettingsController.class);
    private boolean answerSubmitted = false;
    private boolean immediateAnswerEnabled;

    private Instant startTime;
    private Duration elapsedTime = Duration.ZERO;
    private Duration remainingTime;
    private AnimationTimer timer;
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
        loadQuestions();
        initializeTimer();
        immediateAnswerEnabled = prefs.getBoolean("immediateAnswerEnabled", false);
        updateButtonVisibility();
        displayQuestion();
        if (!prefs.getBoolean("timerEnabled", true)) {
            timerLabel.setVisible(false);
            toggleTimerButton.setVisible(false);
        }
        notificationLabel.setVisible(false);
        correctAnswerLabel.setVisible(false);
    }

    private void updateButtonVisibility() {
        if (immediateAnswerEnabled) {
            submitAnswerButton.setVisible(true);
            nextQuestionButton.setVisible(false);
        } else {
            submitAnswerButton.setVisible(false);
            nextQuestionButton.setVisible(true);
        }
    }

    @FXML
    private void submitAnswer() {
        if (!answerSubmitted) {
            checkAnswer();
            showCorrectAnswer();
            submitAnswerButton.setVisible(false);
            nextQuestionButton.setVisible(true);
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
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= questions.size()) {
            finishQuiz();
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
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
        remainingTime = Duration.ofSeconds(prefs.getInt("timePerQuestion", 15));
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (startTime == null) {
                    startTime = Instant.now();
                }
                Duration currentElapsed = Duration.between(startTime, Instant.now());
                Duration totalElapsed = elapsedTime.plus(currentElapsed);
                Duration timeLeft = remainingTime.minus(totalElapsed);

                if (timeLeft.isZero() || timeLeft.isNegative()) {
                    handleTimeUp();
                } else {
                    updateTimerLabel(timeLeft);
                }
            }
        };
    }

    private void resetTimer() {
        if (timer != null) {
            timer.stop();
        }
        remainingTime = Duration.ofSeconds(prefs.getInt("timePerQuestion", 15));
        startTime = null;
        elapsedTime = Duration.ZERO;
        if (prefs.getBoolean("timerEnabled", true)) {
            startTimer();
        }
    }

    private void startTimer() {
        if (timer == null) {
            initializeTimer();
        }
        startTime = Instant.now();
        timer.start();
        timerRunning = true;
        toggleTimerButton.setText("Pause Timer");
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.stop();
            elapsedTime = elapsedTime.plus(Duration.between(startTime, Instant.now()));
            timerRunning = false;
            toggleTimerButton.setText("Resume Timer");
        }
    }

    private void updateTimerLabel(Duration timeLeft) {
        Platform.runLater(() -> timerLabel.setText("Time: " + timeLeft.getSeconds()));
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