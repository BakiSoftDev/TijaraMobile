package com.tijaramobile.controller;

import com.tijaramobile.dao.AchatLineDAO;
import com.tijaramobile.dao.FacAchatDAO;
import com.tijaramobile.model.AchatLine;
import com.tijaramobile.model.FacAchat;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PurchaseController {

    @FXML private TableView<FacAchat> table;
    @FXML private TableColumn<FacAchat, String> colNum;
    @FXML private TableColumn<FacAchat, String> colFournisseur;
    @FXML private TableColumn<FacAchat, String> colDate;
    @FXML private TableColumn<FacAchat, String> colTotal;
    @FXML private TableColumn<FacAchat, String> colPaye;
    @FXML private TableColumn<FacAchat, String> colReste;
    @FXML private TableColumn<FacAchat, Void> colActions;

    private final FacAchatDAO dao = new FacAchatDAO();
    private final AchatLineDAO lineDAO = new AchatLineDAO();

    @FXML public void initialize() {
        colNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNum()));
        colFournisseur.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFournisseurName()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDt() != null ? c.getValue().getDt().toString() : ""));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getTotTtc())));
        colPaye.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getPaye())));
        colReste.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getReste())));
        setupActionColumn();
        loadData();
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(dao.getAll()));
    }

    @FXML private void handleAdd() { openForm(null); }

    private void openForm(FacAchat fa) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tijaramobile/views/purchase_form.fxml"), 
                LanguageManager.getInstance().getBundle());
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 380, 620));
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle(fa == null ? "Nouvel Achat" : "Modifier Achat");
            stage.initModality(Modality.APPLICATION_MODAL);
            PurchaseFormController ctrl = loader.getController();
            ctrl.setFacAchat(fa);
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
                    FacAchat fa = getTableView().getItems().get(getIndex());
                    Optional<ButtonType> res = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer achat " + fa.getNum() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.YES) {
                        // Reverse stock
                        List<AchatLine> lines = lineDAO.getByFacAchat(fa.getIdfa());
                        com.tijaramobile.dao.ProduitDAO produitDAO = new com.tijaramobile.dao.ProduitDAO();
                        for (AchatLine l : lines) produitDAO.updateStock(l.getIdprod(), l.getQnt().negate());
                        lineDAO.deleteByFacAchat(fa.getIdfa());
                        dao.delete(fa.getIdfa());
                        loadData();
                    }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) { super.updateItem(v, empty); setGraphic(empty ? null : box); }
        });
    }
}
