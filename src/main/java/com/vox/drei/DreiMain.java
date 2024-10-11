package com.vox.drei;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.prefs.Preferences;

public class DreiMain extends Application {

    private static Stage primaryStage;
    private static final double DEFAULT_WIDTH = 900;
    private static final double DEFAULT_HEIGHT = 600;
    private static StackPane root;
    private static Canvas backgroundCanvas;
    private static List<Particle> particles;
    private static Random random = new Random();
    private static Preferences prefs = Preferences.userNodeForPackage(DreiMain.class);

    @Override
    public void start(Stage primaryStage) throws Exception {
        DreiMain.primaryStage = primaryStage;
        root = new StackPane();
        backgroundCanvas = new Canvas(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        root.getChildren().add(backgroundCanvas);

        Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Quiz App");

        // Set application icon
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));

        initializeParticles();
        startBackgroundAnimation();

        showMainView();
        primaryStage.show();
    }

    private static void initializeParticles() {
        particles = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            particles.add(new Particle());
        }
    }

    private static void startBackgroundAnimation() {
        GraphicsContext gc = backgroundCanvas.getGraphicsContext2D();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (prefs.getBoolean("animationEnabled", true)) {
                    gc.setFill(Color.rgb(26, 42, 108, 0.1));
                    gc.fillRect(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());

                    for (Particle p : particles) {
                        p.update();
                        p.draw(gc);
                    }
                } else {
                    gc.setFill(Color.rgb(26, 42, 108, 1));
                    gc.fillRect(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
                }
            }
        }.start();
    }

    public static void showMainView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("drei-main.fxml"));
        Parent mainView = loader.load();
        setView(mainView);
    }

    public static void showQuizSelectionView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("QuizSelectionView.fxml"));
        Parent quizSelectionView = loader.load();
        setView(quizSelectionView);
    }

    public static void showManageQuizzesView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("ManageQuizzesView.fxml"));
        Parent manageQuizzesView = loader.load();
        setView(manageQuizzesView);
    }

    public static void showManageQuestionsView(Quiz quiz) throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("ManageQuestionsView.fxml"));
        Parent manageQuestionsView = loader.load();
        ManageQuestionsController controller = loader.getController();
        controller.setQuiz(quiz);
        setView(manageQuestionsView);
    }

    public static void showQuizGameView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("QuizGameView.fxml"));
        Parent quizGameView = loader.load();
        setView(quizGameView);
    }

    public static void showQuizSettingsView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("QuizSettingsView.fxml"));
        Parent quizSettingsView = loader.load();
        setView(quizSettingsView);
    }

    private static void setView(Parent view) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root.getChildren().get(root.getChildren().size() - 1));
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> {
            root.getChildren().remove(root.getChildren().size() - 1);
            root.getChildren().add(view);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), view);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static class Particle {
        private double x, y;
        private double speed;
        private double size;
        private Color color;

        public Particle() {
            reset();
        }

        private void reset() {
            x = random.nextDouble() * DEFAULT_WIDTH;
            y = random.nextDouble() * DEFAULT_HEIGHT;
            speed = 0.5 + random.nextDouble() * 1.5;
            size = 1 + random.nextDouble() * 3;
            color = Color.rgb(255, 255, 255, 0.5 + random.nextDouble() * 0.5);
        }

        public void update() {
            y += speed;
            if (y > DEFAULT_HEIGHT) {
                reset();
                y = 0;
            }
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(color);
            gc.fillOval(x, y, size, size);
        }
    }
}