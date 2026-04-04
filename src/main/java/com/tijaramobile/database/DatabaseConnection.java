package com.tijaramobile.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Paths;

public class DatabaseConnection {

    private static final String DB_PATH;
    static {
        String userHome = System.getProperty("user.home");
        DB_PATH = Paths.get(userHome, ".tijaramobile", "tijara.db").toString();
        // Ensure the directory exists
        java.io.File dir = new java.io.File(Paths.get(userHome, ".tijaramobile").toString());
        if (!dir.exists()) dir.mkdirs();
    }

    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private static Connection connection = null;

    // Singleton pattern - one connection per app
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getDbPath() { return DB_PATH; }
}