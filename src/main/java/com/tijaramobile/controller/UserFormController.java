package com.tijaramobile.controller;

import com.tijaramobile.dao.UserDAO;
import com.tijaramobile.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class UserFormController {

    @FXML private Label lblTitle;
    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private Label errorLabel;

    private User user;
    private final UserDAO dao = new UserDAO();
    private Consumer<Void> onSaved;

    public void setUser(User u) {
        this.user = u;
        if (u != null) {
            lblTitle.setText("Modifier Utilisateur");
            fullNameField.setText(u.getFullName());
            usernameField.setText(u.getUsername());
            emailField.setText(u.getEmail());
            usernameField.setDisable(true);
        } else {
            lblTitle.setText("Nouvel Utilisateur");
        }
    }

    public void setOnSaved(Consumer<Void> cb) { this.onSaved = cb; }

    @FXML private void handleSave() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String email = emailField.getText().trim();

        if (user == null && (username.isEmpty() || password.isEmpty())) {
            errorLabel.setText("Username et mot de passe requis"); return;
        }
        if (fullName.isEmpty()) { errorLabel.setText("Nom complet requis"); return; }

        if (user == null) {
            if (dao.getByUsername(username) != null) { errorLabel.setText("Username déjà existant"); return; }
            User newUser = new User(username, password, fullName, email);
            if (!dao.createUser(newUser)) { errorLabel.setText("Erreur lors de la création"); return; }
        } else {
            user.setFullName(fullName);
            user.setEmail(email);
            if (!password.isEmpty()) user.setPassword(password);
            if (!dao.update(user)) { errorLabel.setText("Erreur lors de la mise à jour"); return; }
        }
        if (onSaved != null) onSaved.accept(null);
        close();
    }

    @FXML private void handleCancel() { close(); }
    private void close() { ((Stage) fullNameField.getScene().getWindow()).close(); }
}
