package com.vox.drei;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;

public class QuizGameController {

    @FXML private Label questionLabel;
    @FXML private GridPane answerGrid;
    @FXML private Label timerLabel;

    private static Quiz currentQuiz;
    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private int timeRemaining;
    private Timeline timer;
    private Preferences prefs = Preferences.userNodeForPackage(QuizSettingsController.class);

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
        displayQuestion();
        if (prefs.getBoolean("timerEnabled", true)) {
            startTimer();
        } else {
            timerLabel.setVisible(false);
        }
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

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), questionLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            questionLabel.setText(currentQuestion.getQuestion());
            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), questionLabel);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();

        answerGrid.getChildren().clear();

        if (currentQuestion.getType().equals("MULTIPLE_CHOICE")) {
            displayMultipleChoiceQuestion(currentQuestion);
        } else if (currentQuestion.getType().equals("IDENTIFICATION")) {
            displayIdentificationQuestion();
        }

        resetTimer();
    }

    private void displayMultipleChoiceQuestion(Question currentQuestion) {
        ToggleGroup group = new ToggleGroup();
        List<String> answers = currentQuestion.getAnswers();
        for (int i = 0; i < answers.size(); i++) {
            RadioButton rb = new RadioButton(answers.get(i));
            rb.setToggleGroup(group);
            rb.setUserData(answers.get(i));

            answerGrid.add(rb, 0, i);

            FadeTransition ft = new FadeTransition(Duration.millis(500), rb);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.setDelay(Duration.millis(i * 100));
            ft.play();
        }
    }

    private void displayIdentificationQuestion() {
        TextField answerField = new TextField();
        answerField.setPromptText("Enter your answer here");

        answerGrid.add(answerField, 0, 0);

        FadeTransition ft = new FadeTransition(Duration.millis(500), answerField);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

    private void startTimer() {
        timeRemaining = prefs.getInt("timePerQuestion", 15);
        updateTimerLabel();

        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            updateTimerLabel();
            if (timeRemaining <= 0) {
                nextQuestion();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void resetTimer() {
        if (timer != null) {
            timer.stop();
        }
        if (prefs.getBoolean("timerEnabled", true)) {
            startTimer();
        }
    }

    private void updateTimerLabel() {
        timerLabel.setText("Time: " + timeRemaining);
    }

    @FXML
    private void nextQuestion() {
        checkAnswer();
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.size()) {
            displayQuestion();
        } else {
            finishQuiz();
        }
    }

    private void checkAnswer() {
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
    }

    private void finishQuiz() {
        if (timer != null) {
            timer.stop();
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ScoreView.fxml"));
            Parent root = loader.load();
            ScoreController scoreController = loader.getController();
            scoreController.setScore(score, questions.size());
            scoreController.setQuestions(questions);
            scoreController.setQuizName(currentQuiz.getName());

            Scene scene = new Scene(root, 600, 400);
            DreiMain.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}