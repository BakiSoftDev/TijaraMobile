package com.tijaramobile.controller;

import com.tijaramobile.dao.ClientDAO;
import com.tijaramobile.model.Client;
import com.tijaramobile.service.LanguageManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class ClientFormController {

    @FXML private Label lblTitle;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private Label errorLabel;

    private Client client;
    private final ClientDAO dao = new ClientDAO();
    private Consumer<Void> onSaved;

    public void setClient(Client c) {
        this.client = c;
        if (c != null) {
            lblTitle.setText(LanguageManager.getStatic("client.edit"));
            nameField.setText(c.getName());
            phoneField.setText(c.getPhone());
            emailField.setText(c.getEmail());
            addressField.setText(c.getAddress());
        } else {
            lblTitle.setText(LanguageManager.getStatic("client.add"));
        }
    }

    public void setOnSaved(Consumer<Void> cb) { this.onSaved = cb; }

    @FXML private void handleSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { errorLabel.setText(LanguageManager.getStatic("client.name_required")); return; }

        if (client == null) client = new Client();
        client.setName(name);
        client.setPhone(phoneField.getText().trim());
        client.setEmail(emailField.getText().trim());
        client.setAddress(addressField.getText().trim());

        boolean ok = client.getId() == 0 ? dao.insert(client) : dao.update(client);
        if (ok) { if (onSaved != null) onSaved.accept(null); close(); }
        else errorLabel.setText(LanguageManager.getStatic("msg.save_error"));
    }

    @FXML private void handleCancel() { close(); }

    private void close() { ((Stage) nameField.getScene().getWindow()).close(); }
}
