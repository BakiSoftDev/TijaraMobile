package com.tijaramobile.controller;
import com.tijaramobile.MainApplication;
import com.tijaramobile.service.LanguageManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.NodeOrientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class DashboardController {

    @FXML private BorderPane rootPane;
    // contentArea is center of rootPane – loaded dynamically
    @FXML private Button btnHome;
    @FXML private Button btnStock;
    @FXML private Button btnClients;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnAchats;
    @FXML private Button btnVentes;
    @FXML private HBox bottomNav;

    private Button activeButton;
    private final Map<String, Node> viewCache = new HashMap<>();

    @FXML
    public void initialize() {
        updateOrientation();
        navigateTo("/com/tijaramobile/views/home.fxml", btnHome);
    }

    @FXML private void switchArabic() { setLanguage("ar"); }
    @FXML private void switchFrench() { setLanguage("fr"); }

    private void setLanguage(String lang) {
        LanguageManager.getInstance().setLocale(new Locale(lang));
        try {
            MainApplication.changeScene("/com/tijaramobile/views/dashboard.fxml", 
                LanguageManager.getInstance().get("app.title"), 360, 640);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateOrientation() {
        boolean isAr = LanguageManager.getInstance().getLocale().getLanguage().equals("ar");
        rootPane.setNodeOrientation(isAr ? NodeOrientation.RIGHT_TO_LEFT : NodeOrientation.LEFT_TO_RIGHT);
    }

    @FXML private void goHome() { navigateTo("/com/tijaramobile/views/home.fxml", btnHome); }
    @FXML private void goStock() { navigateTo("/com/tijaramobile/views/stock.fxml", btnStock); }
    @FXML private void goClients() { navigateTo("/com/tijaramobile/views/clients.fxml", btnClients); }
    @FXML private void goFournisseurs() { navigateTo("/com/tijaramobile/views/fournisseurs.fxml", btnFournisseurs); }
    @FXML private void goAchats() { navigateTo("/com/tijaramobile/views/purchases.fxml", btnAchats); }
    @FXML private void goVentes() { navigateTo("/com/tijaramobile/views/sales.fxml", btnVentes); }

    @FXML private void goUsers() {
        navigateTo("/com/tijaramobile/views/users.fxml", null);
    }

    @FXML private void handleLogout() {
        try {
            MainApplication.changeScene("/com/tijaramobile/views/login.fxml", "Login", 360, 640);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void navigateTo(String fxml, Button navBtn) {
        try {
            Node view = viewCache.get(fxml);
            if (view == null) {
                ResourceBundle bundle = LanguageManager.getInstance().getBundle();
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml), bundle);
                view = loader.load();
                viewCache.put(fxml, view);
            }
            rootPane.setCenter(view);
            setActive(navBtn);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Allow sub-controllers to reload their own view (after edit/add)
    public void reloadView(String fxml, Button navBtn) {
        viewCache.remove(fxml);
        navigateTo(fxml, navBtn);
    }

    public void pushView(String fxml) {
        try {
            ResourceBundle bundle = LanguageManager.getInstance().getBundle();
            Node view = FXMLLoader.load(getClass().getResource(fxml), bundle);
            rootPane.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load a view with a controller callback
    public <T> T pushViewWithController(String fxml) throws IOException {
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml), bundle);
        Node view = loader.load();
        rootPane.setCenter(view);
        return loader.getController();
    }

    private void setActive(Button btn) {
        if (activeButton != null) {
            activeButton.getStyleClass().remove("nav-btn-active");
        }
        activeButton = btn;
        if (btn != null) {
            btn.getStyleClass().add("nav-btn-active");
        }
    }
}