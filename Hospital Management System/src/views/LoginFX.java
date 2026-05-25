package views;

import auth.AuthService;
import auth.User;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginFX extends Application {

    @Override
    public void start(Stage stage) {

        Label title =
                new Label("Hospital Management System");

        TextField usernameField =
                new TextField();

        usernameField.setPromptText(
                "Enter Username");

        PasswordField passwordField =
                new PasswordField();

        passwordField.setPromptText(
                "Enter Password");

        Button loginButton =
                new Button("Login");

        loginButton.setOnAction(e -> {

            String username =
                    usernameField.getText();

            String password =
                    passwordField.getText();

            User user =
                    AuthService.login(
                            username,
                            password
                    );

            if (user != null) {

                Alert alert =
                        new Alert(
                                Alert.AlertType.INFORMATION
                        );

                alert.setTitle(
                        "Login Success"
                );

                alert.setHeaderText(null);

                alert.setContentText(
                        "Welcome "
                        + user.getUsername()
                        + "\nRole: "
                        + user.getRole()
                );

                alert.showAndWait();

            } else {

                Alert alert =
                        new Alert(
                                Alert.AlertType.ERROR
                        );

                alert.setTitle(
                        "Login Failed"
                );

                alert.setHeaderText(null);

                alert.setContentText(
                        "Invalid Username or Password!"
                );

                alert.showAndWait();
            }
        });

        VBox root = new VBox(15);

        root.setAlignment(Pos.CENTER);

        root.getChildren().addAll(
                title,
                usernameField,
                passwordField,
                loginButton
        );

        Scene scene =
                new Scene(root, 400, 300);

        stage.setTitle("HMS Login");

        stage.setScene(scene);

        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}