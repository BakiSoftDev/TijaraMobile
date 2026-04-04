package com.tijaramobile.controller;

import com.tijaramobile.dao.ProduitDAO;
import com.tijaramobile.model.Produit;
import com.tijaramobile.service.LanguageManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.function.Consumer;

public class ProductFormController {

    @FXML private Label lblTitle;
    @FXML private TextField nomField;
    @FXML private TextField refField;
    @FXML private ComboBox<String> unitCombo;
    @FXML private TextField prixAField;
    @FXML private TextField prixVField;
    @FXML private TextField qntField;
    @FXML private TextField packPrixField;
    @FXML private TextField packNbField;
    @FXML private Label errorLabel;

    private Produit produit;
    private final ProduitDAO dao = new ProduitDAO();
    private Consumer<Void> onSaved;

    @FXML
    public void initialize() {
        unitCombo.getItems().addAll("Pièce", "Kg", "L", "M", "Carton", "Boîte", "Sachet");
    }

    public void setProduit(Produit p) {
        this.produit = p;
        if (p != null) {
            lblTitle.setText(LanguageManager.getStatic("stock.edit"));
            nomField.setText(p.getNomP());
            refField.setText(p.getRef());
            unitCombo.setValue(p.getUnit());
            prixAField.setText(p.getPrixA().toPlainString());
            prixVField.setText(p.getPrixV().toPlainString());
            qntField.setText(p.getQnt().toPlainString());
            if (p.getPackPrix() != null) packPrixField.setText(p.getPackPrix().toPlainString());
            if (p.getPackNb() != null) packNbField.setText(p.getPackNb().toString());
        } else {
            lblTitle.setText(LanguageManager.getStatic("stock.add"));
        }
    }

    public void setOnSaved(Consumer<Void> callback) { this.onSaved = callback; }

    @FXML
    private void handleSave() {
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) { errorLabel.setText(LanguageManager.getStatic("stock.name_required")); return; }

        try {
            BigDecimal prixA = parseDec(prixAField.getText(), BigDecimal.ZERO);
            BigDecimal prixV = parseDec(prixVField.getText(), BigDecimal.ZERO);
            BigDecimal qnt = parseDec(qntField.getText(), BigDecimal.ZERO);
            BigDecimal packPrix = parseDecNullable(packPrixField.getText());
            Integer packNb = packNbField.getText().trim().isEmpty() ? null : Integer.parseInt(packNbField.getText().trim());

            if (produit == null) {
                produit = new Produit();
            }
            produit.setNomP(nom);
            produit.setRef(refField.getText().trim());
            produit.setUnit(unitCombo.getValue());
            produit.setPrixA(prixA);
            produit.setPrixV(prixV);
            produit.setQnt(qnt);
            produit.setPackPrix(packPrix);
            produit.setPackNb(packNb);

            boolean ok = produit.getIdP() == 0 ? dao.insert(produit) : dao.update(produit);
            if (ok) {
                if (onSaved != null) onSaved.accept(null);
                closeStage();
            } else {
                errorLabel.setText(LanguageManager.getStatic("msg.save_error"));
            }
        } catch (NumberFormatException ex) {
            errorLabel.setText(LanguageManager.getStatic("stock.invalid_numbers"));
        }
    }

    @FXML
    private void handleCancel() { closeStage(); }

    private void closeStage() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    private BigDecimal parseDec(String s, BigDecimal def) {
        try { return new BigDecimal(s.trim().replace(",", ".")); } catch (Exception e) { return def; }
    }

    private BigDecimal parseDecNullable(String s) {
        if (s == null || s.trim().isEmpty()) return null;
        try { return new BigDecimal(s.trim().replace(",", ".")); } catch (Exception e) { return null; }
    }
}
