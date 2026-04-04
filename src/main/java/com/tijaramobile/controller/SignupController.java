package com.tijaramobile.controller;

import com.tijaramobile.MainApplication;
import com.tijaramobile.dao.UserDAO;
import com.tijaramobile.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class SignupController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;

    @FXML
    private Label messageLabel;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleSignUp(ActionEvent event) {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Please fill all fields");
            return;
        }

        if (userDAO.getByUsername(username) != null) {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Username already exists");
            return;
        }

        User newUser = new User(username, password, fullName, email);
        boolean success = userDAO.createUser(newUser);

        if (success) {
            messageLabel.setTextFill(Color.GREEN);
            messageLabel.setText("Registration successful!");
            fullNameField.clear();
            usernameField.clear();
            passwordField.clear();
            emailField.clear();
            // Automatically navigate back to login after short delay or instantly
            try {
                MainApplication.changeScene("/com/tijaramobile/views/login.fxml", "Login", 360, 640);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Registration failed. Try again.");
        }
    }

    @FXML
    private void handleGoToLogin(ActionEvent event) {
        try {
            MainApplication.changeScene("/com/tijaramobile/views/login.fxml", "Login", 360, 640);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setTextFill(Color.RED);
            messageLabel.setText("Error loading login page");
        }
    }
}
