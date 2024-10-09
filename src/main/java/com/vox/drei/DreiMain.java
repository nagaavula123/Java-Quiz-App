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
        showView("drei-main.fxml");
    }

    public static void showView(String fxmlFile) throws Exception {
        Parent root = FXMLLoader.load(DreiMain.class.getResource(fxmlFile));
        primaryStage.setTitle("Quiz Game");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}