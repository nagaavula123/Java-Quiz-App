package com.vox.drei;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DreiMain extends Application {

    private static Stage primaryStage;
    private static final double DEFAULT_WIDTH = 600;
    private static final double DEFAULT_HEIGHT = 400;

    @Override
    public void start(Stage primaryStage) throws Exception {
        DreiMain.primaryStage = primaryStage;
        showMainView();
    }

    public static void showMainView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("drei-main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Drei Quiz Game");
        setSceneWithDefaultSize(root);
        primaryStage.show();
    }

    public static void showQuizSelectionView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("QuizSelectionView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Select Quiz");
        setSceneWithDefaultSize(root);
    }

    public static void showManageQuizzesView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("ManageQuizzesView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Manage Quizzes");
        setSceneWithDefaultSize(root, 900, 600);
    }

    public static void showManageQuestionsView(Quiz quiz) throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("ManageQuestionsView.fxml"));
        Parent root = loader.load();
        ManageQuestionsController controller = loader.getController();
        controller.setQuiz(quiz);
        Stage stage = new Stage();
        stage.setTitle("Manage Questions for " + quiz.getName());
        stage.setScene(new Scene(root, 800, 600));
        stage.show();
    }

    public static void showQuizGameView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("QuizGameView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Quiz Game");
        setSceneWithDefaultSize(root);
    }

    public static void showQuizSettingsView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("QuizSettingsView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Quiz Settings");
        setSceneWithDefaultSize(root);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    private static void setSceneWithDefaultSize(Parent root) {
        setSceneWithDefaultSize(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    private static void setSceneWithDefaultSize(Parent root, double width, double height) {
        Scene scene = new Scene(root, width, height);
        primaryStage.setScene(scene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
    }

    public static void main(String[] args) {
        launch(args);
    }
}