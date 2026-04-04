package com.tijaramobile;

import com.tijaramobile.database.DatabaseConnection;
import com.tijaramobile.database.SchemaManager;
import com.tijaramobile.service.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        // Initialize database first
        SchemaManager.initializeDatabase();

        primaryStage = stage;
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        Parent root = FXMLLoader.load(getClass().getResource("/com/tijaramobile/views/login.fxml"), bundle);
        Scene scene = new Scene(root, 360, 640);
        stage.initStyle(javafx.stage.StageStyle.UNDECORATED);
        stage.setTitle("TijaraMobile");
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }

    public static void changeScene(String fxml, String title, int width, int height) throws Exception {
        ResourceBundle bundle = LanguageManager.getInstance().getBundle();
        Parent pane = FXMLLoader.load(MainApplication.class.getResource(fxml), bundle);
        primaryStage.setTitle(title);
        primaryStage.setScene(new Scene(pane, width, height));
    }

    @Override
    public void stop() {
        // Close database connection on app exit
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}