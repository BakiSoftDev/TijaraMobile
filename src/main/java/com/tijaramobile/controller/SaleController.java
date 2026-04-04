package com.tijaramobile.controller;

import com.tijaramobile.dao.CmdDAO;
import com.tijaramobile.dao.CmdLineDAO;
import com.tijaramobile.model.Cmd;
import com.tijaramobile.model.CmdLine;
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

public class SaleController {

    @FXML private TableView<Cmd> table;
    @FXML private TableColumn<Cmd, String> colNum;
    @FXML private TableColumn<Cmd, String> colClient;
    @FXML private TableColumn<Cmd, String> colDate;
    @FXML private TableColumn<Cmd, String> colTotal;
    @FXML private TableColumn<Cmd, String> colPaye;
    @FXML private TableColumn<Cmd, String> colReste;
    @FXML private TableColumn<Cmd, Void> colActions;

    private final CmdDAO cmdDAO = new CmdDAO();
    private final CmdLineDAO lineDAO = new CmdLineDAO();

    @FXML public void initialize() {
        colNum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNum()));
        colClient.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClientName()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt() != null ? c.getValue().getCreatedAt().toLocalDate().toString() : ""));
        colTotal.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getTot())));
        colPaye.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getPaye())));
        colReste.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.2f", c.getValue().getReste())));
        setupActionColumn();
        loadData();
    }

    private void loadData() {
        table.setItems(FXCollections.observableArrayList(cmdDAO.getAll()));
    }

    @FXML private void handleAdd() { openForm(null); }

    private void openForm(Cmd cmd) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tijaramobile/views/sale_form.fxml"), 
                LanguageManager.getInstance().getBundle());
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 380, 620));
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle(cmd == null ? "Nouvelle Vente" : "Modifier Vente");
            stage.initModality(Modality.APPLICATION_MODAL);
            SaleFormController ctrl = loader.getController();
            ctrl.setCmd(cmd);
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
                    Cmd c = getTableView().getItems().get(getIndex());
                    Optional<ButtonType> res = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer vente " + c.getNum() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.YES) {
                        // Restore stock
                        List<CmdLine> lines = lineDAO.getByCmd(c.getIdcmd());
                        com.tijaramobile.dao.ProduitDAO pd = new com.tijaramobile.dao.ProduitDAO();
                        for (CmdLine l : lines) pd.updateStock(l.getIdprod(), l.getQnt());
                        lineDAO.deleteByCmd(c.getIdcmd());
                        cmdDAO.delete(c.getIdcmd());
                        loadData();
                    }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) { super.updateItem(v, empty); setGraphic(empty ? null : box); }
        });
    }
}
