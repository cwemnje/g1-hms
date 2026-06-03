package com.hms.controllers;

import com.hms.models.User;
import com.hms.services.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML private TextField userIdField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String userId   = userIdField.getText().trim().toUpperCase();
        String password = passwordField.getText();

        if (userId.isEmpty() || password.isEmpty()) {
            showError("Please enter your Staff ID and password.");
            return;
        }

        User user = authService.loginById(userId, password);

        if (user == null) {
            showError("Invalid Staff ID or password. Please try again.");
            return;
        }

        String fxmlFile = getDashboardForRole(user.getRoleId());
        if (fxmlFile == null) {
            showError("Unknown role. Contact your administrator.");
            return;
        }

        navigateTo(fxmlFile, user);
    }

    private String getDashboardForRole(int roleId) {
        return switch (roleId) {
            case 1 -> "/com/hms/fxml/AdminDashboard.fxml";
            case 2 -> "/com/hms/fxml/DoctorDashboard.fxml";
            case 3 -> "/com/hms/fxml/NurseDashboard.fxml";
            case 4 -> "/com/hms/fxml/ReceptionistDashboard.fxml";
            case 5 -> "/com/hms/fxml/PharmacistDashboard.fxml";
            case 6 -> "/com/hms/fxml/LabDashboard.fxml";
            default -> null;
        };
    }

    private void navigateTo(String fxmlPath, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).initUser(user);
            }

            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 750);
            scene.getStylesheets().add(
                getClass().getResource("/com/hms/styles/style.css").toExternalForm()
            );
            stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/com/hms/images/hms_title.png"))
            );
            stage.setScene(scene);
            stage.setTitle("HMS — " + getRoleName(user.getRoleId()));
            stage.setMaximized(true);
            stage.setResizable(true);
            stage.show();

        } catch (IOException e) {
            showError("Failed to load dashboard: " + e.getMessage());
        }
    }

    private String getRoleName(int roleId) {
        return switch (roleId) {
            case 1 -> "Admin";
            case 2 -> "Doctor";
            case 3 -> "Nurse";
            case 4 -> "Receptionist";
            case 5 -> "Pharmacist";
            case 6 -> "Lab Technician";
            default -> "Unknown";
        };
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}