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
import java.util.ResourceBundle;

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

    private ResourceBundle bundle;

    @FXML
    public void initialize() {
        bundle = DreiMain.getBundle();
        versionLabel.setText(bundle.getString("version.label"));
        descriptionLabel.setText(bundle.getString("description.label"));

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