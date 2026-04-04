package com.tijaramobile.controller;

import com.tijaramobile.dao.FournisseurDAO;
import com.tijaramobile.model.Fournisseur;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class FournisseurFormController {

    @FXML private Label lblTitle;
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private CheckBox tvaExemptCheck;
    @FXML private Label errorLabel;

    private Fournisseur fournisseur;
    private final FournisseurDAO dao = new FournisseurDAO();
    private Consumer<Void> onSaved;

    public void setFournisseur(Fournisseur f) {
        this.fournisseur = f;
        if (f != null) {
            lblTitle.setText("Modifier Fournisseur");
            nameField.setText(f.getName());
            phoneField.setText(f.getPhone());
            emailField.setText(f.getEmail());
            addressField.setText(f.getAddress());
            tvaExemptCheck.setSelected(f.isTvaExempt());
        } else {
            lblTitle.setText("Nouveau Fournisseur");
        }
    }

    public void setOnSaved(Consumer<Void> cb) { this.onSaved = cb; }

    @FXML private void handleSave() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { errorLabel.setText("Le nom est obligatoire"); return; }

        if (fournisseur == null) fournisseur = new Fournisseur();
        fournisseur.setName(name);
        fournisseur.setPhone(phoneField.getText().trim());
        fournisseur.setEmail(emailField.getText().trim());
        fournisseur.setAddress(addressField.getText().trim());
        fournisseur.setTvaExempt(tvaExemptCheck.isSelected());

        boolean ok = fournisseur.getId() == 0 ? dao.insert(fournisseur) : dao.update(fournisseur);
        if (ok) { if (onSaved != null) onSaved.accept(null); close(); }
        else errorLabel.setText("Erreur lors de la sauvegarde");
    }

    @FXML private void handleCancel() { close(); }
    private void close() { ((Stage) nameField.getScene().getWindow()).close(); }
}
