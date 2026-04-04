package com.tijaramobile.controller;

import com.tijaramobile.dao.ClientDAO;
import com.tijaramobile.model.Client;
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

public class ClientController {

    @FXML private TableView<Client> table;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colPhone;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, Void> colActions;
    @FXML private TextField searchField;

    private final ClientDAO dao = new ClientDAO();

    @FXML
    public void initialize() {
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getName()));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        setupActionColumn();
        loadData();
    }

    private void loadData() {
        String q = searchField.getText().trim();
        List<Client> list = q.isEmpty() ? dao.getAll() : dao.search(q);
        table.setItems(FXCollections.observableArrayList(list));
    }

    @FXML private void handleSearch() { loadData(); }
    @FXML private void handleAdd() { openForm(null); }

    private void openForm(Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tijaramobile/views/client_form.fxml"), 
                LanguageManager.getInstance().getBundle());
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 360, 420));
            stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
            stage.setTitle(client == null ? "Nouveau Client" : "Modifier Client");
            stage.initModality(Modality.APPLICATION_MODAL);
            ClientFormController ctrl = loader.getController();
            ctrl.setClient(client);
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
                    Client c = getTableView().getItems().get(getIndex());
                    Optional<ButtonType> res = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + c.getName() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.YES) { dao.delete(c.getId()); loadData(); }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) { super.updateItem(v, empty); setGraphic(empty ? null : box); }
        });
    }
}
