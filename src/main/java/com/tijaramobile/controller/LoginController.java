package com.tijaramobile.controller;

import com.tijaramobile.MainApplication;
import com.tijaramobile.dao.UserDAO;
import com.tijaramobile.model.User;
import com.tijaramobile.service.LanguageManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import com.tijaramobile.service.SettingsManager;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private CheckBox rememberMe;

    private UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        if ("true".equals(SettingsManager.get("remember", "false"))) {
            usernameField.setText(SettingsManager.get("username", ""));
            passwordField.setText(SettingsManager.get("password", ""));
            rememberMe.setSelected(true);
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText(LanguageManager.getStatic("login.error"));
            return;
        }

        User user = userDAO.authenticate(username, password);

        if (user != null) {
            // Save remember me settings
            if (rememberMe.isSelected()) {
                SettingsManager.set("remember", "true");
                SettingsManager.set("username", username);
                SettingsManager.set("password", password);
            } else {
                SettingsManager.set("remember", "false");
                SettingsManager.set("username", "");
                SettingsManager.set("password", "");
            }
            SettingsManager.save();

            try {
                MainApplication.changeScene("/com/tijaramobile/views/dashboard.fxml",
                        LanguageManager.getStatic("nav.home") + " - " + user.getFullName(), 360, 640);
            } catch (Exception e) {
                e.printStackTrace();
                errorLabel.setText("Error loading dashboard");
            }
        } else {
            errorLabel.setText(LanguageManager.getStatic("login.error"));
            passwordField.clear();
        }
    }

    @FXML
    private void handleGoToSignUp(ActionEvent event) {
        try {
            MainApplication.changeScene("/com/tijaramobile/views/signup.fxml", LanguageManager.getStatic("login.signup"), 360, 640);
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Error loading sign up page");
        }
    }
}