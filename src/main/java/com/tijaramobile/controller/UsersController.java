package com.tijaramobile.controller;

import com.tijaramobile.dao.UserDAO;
import com.tijaramobile.model.User;
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

public class UsersController {

    @FXML private TableView<User> table;
    @FXML private TableColumn<User, String> colUsername;
    @FXML private TableColumn<User, String> colFullName;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, Void> colActions;
    @FXML private TextField searchField;

    private final UserDAO dao = new UserDAO();

    @FXML public void initialize() {
        colUsername.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUsername()));
        colFullName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        colEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        setupActionColumn();
        loadData();
    }

    private void loadData() {
        List<User> list = dao.getAll();
        String q = searchField.getText().trim().toLowerCase();
        if (!q.isEmpty()) list = list.stream().filter(u -> (u.getUsername() != null && u.getUsername().toLowerCase().contains(q)) || (u.getFullName() != null && u.getFullName().toLowerCase().contains(q))).toList();
        table.setItems(FXCollections.observableArrayList(list));
    }

    @FXML private void handleSearch() { loadData(); }
    @FXML private void handleAdd() { openForm(null); }

    private void openForm(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tijaramobile/views/user_form.fxml"), 
                LanguageManager.getInstance().getBundle());
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load(), 360, 420));
            stage.setTitle(user == null ? "Nouvel Utilisateur" : "Modifier Utilisateur");
            stage.initModality(Modality.APPLICATION_MODAL);
            UserFormController ctrl = loader.getController();
            ctrl.setUser(user);
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
                    User u = getTableView().getItems().get(getIndex());
                    if ("admin".equals(u.getUsername())) { new Alert(Alert.AlertType.WARNING, "Cannot delete admin!").showAndWait(); return; }
                    Optional<ButtonType> res = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer " + u.getUsername() + "?", ButtonType.YES, ButtonType.NO).showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.YES) { dao.delete(u.getId()); loadData(); }
                });
            }
            @Override protected void updateItem(Void v, boolean empty) { super.updateItem(v, empty); setGraphic(empty ? null : box); }
        });
    }
}
