package com.vox.drei;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import java.awt.Desktop;
import java.net.URI;

public class AboutController {

    @FXML
    private Label versionLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private VBox rootVBox;

    @FXML
    private Button instagramButton;

    @FXML
    private Button facebookButton;

    @FXML
    private Button twitterButton;

    @FXML
    private Button githubButton;

    @FXML
    public void initialize() {
        versionLabel.setText("Version 1.0");
        descriptionLabel.setText("Welcome to JavaFX Quiz App, a desktop application built with JavaFX in IntelliJ IDEA.");

        // Add fade-in animation
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1.5), rootVBox);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        // Add scale animation for the logo
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), rootVBox.getChildren().get(0));
        scaleTransition.setFromX(0.5);
        scaleTransition.setFromY(0.5);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();
    }

    @FXML
    private void openSocialMedia(javafx.event.ActionEvent event) {
        String url = "";
        if (event.getSource() == instagramButton) {
            url = "https://www.instagram.com/drei_izel/";
        } else if (event.getSource() == facebookButton) {
            url = "https://www.facebook.com/MharAndrei";
        } else if (event.getSource() == twitterButton) {
            url = "https://x.com/drei_zx";
        } else if (event.getSource() == githubButton) {
            url = "https://github.com/VoxDroid";
        }

        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void backToMain() throws Exception {
        DreiMain.showMainView();
    }
}