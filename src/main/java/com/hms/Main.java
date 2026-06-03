package com.hms;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/hms/fxml/Login.fxml")
        );
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(
            getClass().getResource("/com/hms/styles/style.css").toExternalForm()
        );

        // Title bar icon
        stage.getIcons().add(
            new Image(getClass().getResourceAsStream("/com/hms/images/hms_title.png"))
        );

        stage.setTitle("Hospital Management System");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}