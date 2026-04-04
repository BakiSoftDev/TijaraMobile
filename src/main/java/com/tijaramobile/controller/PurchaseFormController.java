package com.tijaramobile.controller;

import com.tijaramobile.dao.*;
import com.tijaramobile.model.*;
import com.tijaramobile.service.LanguageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PurchaseFormController {

    @FXML private Label lblTitle;
    @FXML private TextField numField;
    @FXML private ComboBox<Fournisseur> fournisseurCombo;
    @FXML private DatePicker dtPicker;
    @FXML private TextField payeField;
    @FXML private CheckBox tvaCheck;
    @FXML private Label lblTotal;
    @FXML private Label errorLabel;
    @FXML private TextField productSearch;
    @FXML private ListView<Produit> suggestionList;
    @FXML private TableView<AchatLine> linesTable;
    @FXML private TableColumn<AchatLine, String> colProd;
    @FXML private TableColumn<AchatLine, String> colQnt;
    @FXML private TableColumn<AchatLine, String> colPrix;
    @FXML private TableColumn<AchatLine, String> colTot;
    @FXML private TableColumn<AchatLine, Void> colDel;

    private FacAchat facAchat;
    private final FacAchatDAO facAchatDAO = new FacAchatDAO();
    private final AchatLineDAO lineDAO = new AchatLineDAO();
    private final FournisseurDAO fournisseurDAO = new FournisseurDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private ObservableList<AchatLine> lines = FXCollections.observableArrayList();
    private Consumer<Void> onSaved;

    @FXML public void initialize() {
        fournisseurCombo.setItems(FXCollections.observableArrayList(fournisseurDAO.getAll()));
        dtPicker.setValue(LocalDate.now());

        colProd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduitNom()));
        colQnt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getQnt().toPlainString()));
        colPrix.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getPAchat())));
        colTot.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getTot())));
        setupDelColumn();
        linesTable.setItems(lines);

        // Autocomplete logic
        productSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            updateSuggestions(newVal);
        });

        // Show suggestions on focus if not empty
        productSearch.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) updateSuggestions(productSearch.getText());
        });

        suggestionList.setCellFactory(lv -> new ListCell<Produit>() {
            @Override protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(p.getNomP() + " (PA: " + p.getPrixA() + " DA)");
                    setStyle("-fx-padding: 10; -fx-font-size: 14;");
                }
            }
        });

        // Use selection listener for better mobile support
        suggestionList.getSelectionModel().selectedItemProperty().addListener((obs, old, p) -> {
            if (p != null) {
                addProductLine(p);
                // Clear selection so it can be re-selected if needed
                javafx.application.Platform.runLater(() -> suggestionList.getSelectionModel().clearSelection());
            }
        });

        // Support ENTER to add first suggestion
        productSearch.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {
                Produit p = suggestionList.getItems().isEmpty() ? null : suggestionList.getItems().get(0);
                if (p != null) addProductLine(p);
            }
        });
    }

    private void updateSuggestions(String query) {
        if (query == null || query.trim().isEmpty()) {
            hideSuggestions();
            return;
        }
        List<Produit> results = produitDAO.search(query.trim());
        if (results.isEmpty()) hideSuggestions();
        else {
            suggestionList.setItems(FXCollections.observableArrayList(results));
            suggestionList.setVisible(true);
            suggestionList.setManaged(true);
            suggestionList.setPrefHeight(Math.min(results.size() * 50 + 10, 200));
            suggestionList.toFront();
        }
    }

    private void hideSuggestions() {
        suggestionList.setVisible(false);
        suggestionList.setManaged(false);
    }

    private void setupDelColumn() {
        colDel.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("✕");
            { btn.getStyleClass().addAll("btn", "btn-red"); btn.setOnAction(e -> { lines.remove(getIndex()); updateTotal(); }); }
            @Override protected void updateItem(Void v, boolean empty) { super.updateItem(v, empty); setGraphic(empty ? null : btn); }
        });
    }

    public void setFacAchat(FacAchat fa) {
        this.facAchat = fa;
        if (fa != null) {
            lblTitle.setText("Modifier Achat");
            numField.setText(fa.getNum());
            dtPicker.setValue(fa.getDt());
            tvaCheck.setSelected(fa.isTva());
            payeField.setText(fa.getPaye().toPlainString());
            if (fa.getIdfour() != null) fournisseurCombo.getItems().stream().filter(f -> f.getId() == fa.getIdfour()).findFirst().ifPresent(fournisseurCombo::setValue);
            lines.setAll(lineDAO.getByFacAchat(fa.getIdfa()));
            updateTotal();
        }
    }

    public void setOnSaved(Consumer<Void> cb) { this.onSaved = cb; }

    @FXML private void handleProductSearch() { /* Replaced by text listener */ }

    @FXML private void handleNewProduct() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tijaramobile/views/product_form.fxml"), 
                LanguageManager.getInstance().getBundle());
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 360, 580));
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle("Nouveau Produit");
            stage.initModality(Modality.APPLICATION_MODAL);
            ProductFormController ctrl = loader.getController();
            ctrl.setProduit(null);
            ctrl.setOnSaved(v -> {});
            stage.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void addProductLine(Produit p) {
        hideSuggestions();
        productSearch.clear();
        productSearch.requestFocus();

        Optional<AchatLine> existing = lines.stream().filter(l -> l.getIdprod() == p.getIdP()).findFirst();
        if (existing.isPresent()) {
            AchatLine l = existing.get();
            BigDecimal newQnt = l.getQnt().add(BigDecimal.ONE);
            l.setQnt(newQnt);
            l.setTot(p.getPrixA().multiply(newQnt));
            linesTable.refresh();
        } else {
            AchatLine l = new AchatLine();
            l.setIdprod(p.getIdP());
            l.setProduitNom(p.getNomP());
            l.setProduitUnit(p.getUnit());
            l.setPAchat(p.getPrixA());
            l.setQnt(BigDecimal.ONE);
            l.setTot(p.getPrixA());
            lines.add(l);
        }
        updateTotal();
    }

    private void updateTotal() {
        BigDecimal sum = lines.stream().map(AchatLine::getTot).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tva = tvaCheck.isSelected() ? sum.multiply(new BigDecimal("0.20")) : BigDecimal.ZERO;
        BigDecimal ttc = sum.add(tva);
        lblTotal.setText(String.format("HT: %.2f  TVA: %.2f  TTC: %.2f", sum, tva, ttc));
    }

    @FXML private void handleSave() {
        if (lines.isEmpty()) { errorLabel.setText("Ajoutez au moins un produit"); return; }

        BigDecimal sumHt = lines.stream().map(AchatLine::getTot).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tvaAmt = tvaCheck.isSelected() ? sumHt.multiply(new BigDecimal("0.20")) : BigDecimal.ZERO;
        BigDecimal ttc = sumHt.add(tvaAmt);
        BigDecimal paye = BigDecimal.ZERO;
        try { if (!payeField.getText().trim().isEmpty()) paye = new BigDecimal(payeField.getText().trim()); } catch (Exception ignored) {}

        if (facAchat == null) {
            facAchat = new FacAchat();
            facAchat.setNum(numField.getText().trim().isEmpty() ? "FA-" + System.currentTimeMillis() : numField.getText().trim());
        } else {
            // Reverse old stock
            List<AchatLine> oldLines = lineDAO.getByFacAchat(facAchat.getIdfa());
            for (AchatLine ol : oldLines) produitDAO.updateStock(ol.getIdprod(), ol.getQnt().negate());
            lineDAO.deleteByFacAchat(facAchat.getIdfa());
        }

        facAchat.setNum(numField.getText().trim().isEmpty() ? facAchat.getNum() : numField.getText().trim());
        if (fournisseurCombo.getValue() != null) facAchat.setIdfour(fournisseurCombo.getValue().getId());
        facAchat.setDt(dtPicker.getValue());
        facAchat.setTva(tvaCheck.isSelected());
        facAchat.setTotHt(sumHt);
        facAchat.setTotTva(tvaAmt);
        facAchat.setTotTtc(ttc);
        facAchat.setPaye(paye);
        facAchat.setInitPaye(paye);

        int idfa = facAchat.getIdfa() == 0 ? facAchatDAO.insert(facAchat) : (facAchatDAO.update(facAchat) ? facAchat.getIdfa() : -1);
        if (idfa < 0) { errorLabel.setText("Erreur sauvegarde facture"); return; }

        for (AchatLine l : lines) {
            l.setIdfacAchat(idfa);
            lineDAO.insert(l);
            produitDAO.updateStock(l.getIdprod(), l.getQnt());
        }

        if (onSaved != null) onSaved.accept(null);
        close();
    }

    @FXML private void handleCancel() { close(); }
    private void close() { ((Stage) numField.getScene().getWindow()).close(); }
    private ProduitDAO produitDAO() { return produitDAO; }
}

