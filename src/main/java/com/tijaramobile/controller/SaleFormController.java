package com.tijaramobile.controller;

import com.tijaramobile.dao.*;
import com.tijaramobile.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SaleFormController {

    @FXML private Label lblTitle;
    @FXML private TextField numField;
    @FXML private ComboBox<Client> clientCombo;
    @FXML private TextField payeField;
    @FXML private Label lblTotal;
    @FXML private Label errorLabel;
    @FXML private TextField productSearch;
    @FXML private ListView<Produit> suggestionList;
    @FXML private TableView<CmdLine> linesTable;
    @FXML private TableColumn<CmdLine, String> colProd;
    @FXML private TableColumn<CmdLine, String> colQnt;
    @FXML private TableColumn<CmdLine, String> colPrix;
    @FXML private TableColumn<CmdLine, String> colTot;
    @FXML private TableColumn<CmdLine, Void> colDel;

    private Cmd cmd;
    private final CmdDAO cmdDAO = new CmdDAO();
    private final CmdLineDAO lineDAO = new CmdLineDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final ProduitDAO produitDAO = new ProduitDAO();
    private ObservableList<CmdLine> lines = FXCollections.observableArrayList();
    private Consumer<Void> onSaved;

    @FXML public void initialize() {
        clientCombo.setItems(FXCollections.observableArrayList(clientDAO.getAll()));
        colProd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduitNom()));
        colQnt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getQnt().toPlainString()));
        colPrix.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getPVente())));
        colTot.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getTotal())));
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
                    setText(p.getNomP() + " (Stk: " + p.getQnt().toPlainString() + " | " + p.getPrixV() + " DA)");
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

    public void setCmd(Cmd c) {
        this.cmd = c;
        if (c != null) {
            lblTitle.setText("Modifier Vente");
            numField.setText(c.getNum());
            payeField.setText(c.getPaye().toPlainString());
            clientCombo.getItems().stream().filter(cl -> cl.getId() == c.getIdclient()).findFirst().ifPresent(clientCombo::setValue);
            lines.setAll(lineDAO.getByCmd(c.getIdcmd()));
            updateTotal();
        }
    }

    public void setOnSaved(Consumer<Void> cb) { this.onSaved = cb; }

    @FXML private void handleProductSearch() { /* Replaced by text listener */ }

    private void addProductLine(Produit p) {
        hideSuggestions();
        productSearch.clear();
        productSearch.requestFocus();

        Optional<CmdLine> existing = lines.stream().filter(l -> l.getIdprod() == p.getIdP()).findFirst();
        if (existing.isPresent()) {
            CmdLine l = existing.get();
            BigDecimal newQnt = l.getQnt().add(BigDecimal.ONE);
            if (p.getQnt().compareTo(newQnt) < 0) {
                errorLabel.setText("Stock insuffisant pour " + p.getNomP()); return;
            }
            l.setQnt(newQnt);
            linesTable.refresh();
        } else {
            if (p.getQnt().compareTo(BigDecimal.ONE) < 0) {
                errorLabel.setText("Stock insuffisant pour " + p.getNomP()); return;
            }
            CmdLine l = new CmdLine();
            l.setIdprod(p.getIdP());
            l.setProduitNom(p.getNomP());
            l.setProduitUnit(p.getUnit());
            l.setPAchat(p.getPrixA());
            l.setPVente(p.getPrixV());
            l.setQnt(BigDecimal.ONE);
            lines.add(l);
        }
        updateTotal();
        errorLabel.setText("");
    }

    private void updateTotal() {
        BigDecimal sum = lines.stream().map(CmdLine::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        lblTotal.setText(String.format("Total: %.2f", sum));
    }

    @FXML private void handleSave() {
        if (lines.isEmpty()) { errorLabel.setText("Ajoutez au moins un produit"); return; }

        BigDecimal tot = lines.stream().map(CmdLine::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paye = BigDecimal.ZERO;
        try { if (!payeField.getText().trim().isEmpty()) paye = new BigDecimal(payeField.getText().trim()); } catch (Exception ignored) {}

        if (cmd == null) {
            cmd = new Cmd();
            cmd.setNum(numField.getText().trim().isEmpty() ? "VT-" + System.currentTimeMillis() : numField.getText().trim());
            cmd.setCode("0");
        } else {
            // Restore stock
            List<CmdLine> oldLines = lineDAO.getByCmd(cmd.getIdcmd());
            for (CmdLine ol : oldLines) produitDAO.updateStock(ol.getIdprod(), ol.getQnt());
            lineDAO.deleteByCmd(cmd.getIdcmd());
        }

        cmd.setNum(numField.getText().trim().isEmpty() ? cmd.getNum() : numField.getText().trim());
        if (clientCombo.getValue() != null) cmd.setIdclient(clientCombo.getValue().getId());
        cmd.setTot(tot);
        cmd.setPaye(paye);
        cmd.setInitPaye(paye);

        int idcmd = cmd.getIdcmd() == 0 ? cmdDAO.insert(cmd) : (cmdDAO.update(cmd) ? cmd.getIdcmd() : -1);
        if (idcmd < 0) { errorLabel.setText("Erreur sauvegarde vente"); return; }

        for (CmdLine l : lines) {
            l.setIdcmd(idcmd);
            lineDAO.insert(l);
            produitDAO.updateStock(l.getIdprod(), l.getQnt().negate());
        }

        if (onSaved != null) onSaved.accept(null);
        close();
    }

    @FXML private void handleCancel() { close(); }
    private void close() { ((javafx.stage.Stage) numField.getScene().getWindow()).close(); }
}
