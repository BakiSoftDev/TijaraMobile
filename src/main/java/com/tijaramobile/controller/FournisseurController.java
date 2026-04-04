package com.tijaramobile.controller;

import com.tijaramobile.dao.FournisseurDAO;
import com.tijaramobile.model.Fournisseur;
import com.tijaramobile.service.LanguageManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class FournisseurController {

    @FXML private TableView<Fournisseur> table;
    @FXML private TableColumn<Fournisseur, String> colName;
    @FXML private TableColumn<Fournisseur, String> colPhone;
    @FXML private TableColumn<Fournisseur, String> colEmail;
    @FXML private TableColumn<Fournisseur, String> colTva;
    @FXML private TableColumn<Fournisseur, Void> colActions;
    @FXML private TextField searchField;

    private final FournisseurDAO dao = new FournisseurDAO();

    @FXML public void initialize() {
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        colTva.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().isTvaExempt() ? "Oui" : "Non"));
        setupActionColumn();
        loadData();
    }

    private void loadData() {
        String q = searchField.getText().trim();
        List<Fournisseur> list = q.isEmpty() ? dao.getAll() : dao.search(q);
        table.setItems(FXCollections.observableArrayList(list));
    }

    @FXML private void handleSearch() { loadData(); }
    @FXML private void handleAdd() { openForm(null); }

    private void openForm(Fournisseur f) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tijaramobile/views/fournisseur_form.fxml"), 
                LanguageManager.getInstance().getBundle());
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 360, 450));
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle(f == null ? "Nouveau Fournisseur" : "Modifier Fournisseur");
            stage.initModality(Modality.APPLICATION_MODAL);
            FournisseurFormController ctrl = loader.getController();
            ctrl.setFournisseur(f);
            ctrl.setOnSaved(v -> loadData());
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
                    Fournisseur f = getTableView().getItems().get(getIndex());
                    Optional<ButtonType> res = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + f.getName() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.YES) { dao.delete(f.getId()); loadData(); }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) { super.updateItem(v, empty); setGraphic(empty ? null : box); }
        });
    }
}
