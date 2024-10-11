package com.vox.drei;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DreiMain extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        DreiMain.primaryStage = primaryStage;
        showMainView();
    }

    public static void showMainView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("drei-main.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Drei Quiz Game");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }

    public static void showQuizSelectionView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("QuizSelectionView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Select Quiz");
        primaryStage.setScene(new Scene(root, 600, 400));
    }

    public static void showManageQuizzesView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("ManageQuizzesView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Manage Quizzes");
        primaryStage.setScene(new Scene(root, 800, 600));
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
        primaryStage.setScene(new Scene(root, 600, 400));
    }

    public static void showQuizSettingsView() throws Exception {
        FXMLLoader loader = new FXMLLoader(DreiMain.class.getResource("QuizSettingsView.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Quiz Settings");
        primaryStage.setScene(new Scene(root, 600, 400));
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}