package com.tijaramobile.controller;

import com.tijaramobile.dao.ProduitDAO;
import com.tijaramobile.model.Produit;
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

import java.util.List;
import java.util.Optional;

public class StockController {

    @FXML private TableView<Produit> table;
    @FXML private TableColumn<Produit, String> colNom;
    @FXML private TableColumn<Produit, String> colRef;
    @FXML private TableColumn<Produit, String> colUnit;
    @FXML private TableColumn<Produit, String> colPrixA;
    @FXML private TableColumn<Produit, String> colPrixV;
    @FXML private TableColumn<Produit, String> colQnt;
    @FXML private TableColumn<Produit, Void> colActions;
    @FXML private TextField searchField;

    private final ProduitDAO dao = new ProduitDAO();
    private ObservableList<Produit> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNomP()));
        colRef.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRef()));
        colUnit.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUnit()));
        colPrixA.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getPrixA())));
        colPrixV.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getPrixV())));
        colQnt.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.3f", c.getValue().getQnt())));
        setupActionColumn();
        loadData();

        // Real-time search filter
        searchField.textProperty().addListener((obs, oldVal, newVal) -> loadData());
    }

    private void loadData() {
        String q = searchField.getText().trim();
        List<Produit> list = q.isEmpty() ? dao.getAll() : dao.search(q);
        data.setAll(list);
        table.setItems(data);
    }

    @FXML
    private void handleSearch() { loadData(); }

    @FXML
    private void handleAdd() { openForm(null); }

    private void openForm(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tijaramobile/views/product_form.fxml"), 
                LanguageManager.getInstance().getBundle());
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 360, 580));
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle(produit == null ? "Nouveau Produit" : "Modifier Produit");
            stage.initModality(Modality.APPLICATION_MODAL);
            ProductFormController ctrl = loader.getController();
            ctrl.setProduit(produit);
            ctrl.setOnSaved(loadData -> this.loadData());
            stage.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void setupActionColumn() {
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnEdit = new Button("✏");
            private final Button btnDel = new Button("🗑");
            private final HBox box = new HBox(6, btnEdit, btnDel);

            {
                btnEdit.getStyleClass().addAll("btn", "btn-blue");
                btnDel.getStyleClass().addAll("btn", "btn-red");
                btnEdit.setOnAction(e -> openForm(getTableView().getItems().get(getIndex())));
                btnDel.setOnAction(e -> {
                    Produit p = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + p.getNomP() + "?", ButtonType.YES, ButtonType.NO);
                    Optional<ButtonType> res = alert.showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.YES) {
                        dao.delete(p.getIdP());
                        loadData();
                    }
                });
            }

            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });
    }
}
