package com.tijaramobile.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;

public class DatabaseConnection {

    private static final String DB_PATH;
    static {
        String dbName = "tijara.db";
        String platform = System.getProperty("javafx.platform");
        String folderName = ".tijaramobile";
        
        File dbDir;
        // On Android, the user.home usually points to a writable internal directory
        dbDir = new File(System.getProperty("user.home"), folderName);
        
        if (!dbDir.exists()) {
            boolean created = dbDir.mkdirs();
            System.out.println("Created database directory: " + created);
        }
        
        File dbFile = new File(dbDir, dbName);
        DB_PATH = dbFile.getAbsolutePath();
        System.out.println("Database path: " + DB_PATH);

        // Copy database from resources if it doesn't exist in the writable location
        if (!dbFile.exists()) {
            System.out.println("Database file not found, copying from resources...");
            try (InputStream is = DatabaseConnection.class.getResourceAsStream("/" + dbName);
                 OutputStream os = new FileOutputStream(dbFile)) {
                
                if (is != null) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        os.write(buffer, 0, length);
                    }
                    System.out.println("Database copied successfully to: " + DB_PATH);
                } else {
                    System.err.println("Database resource not found: /" + dbName);
                }
            } catch (IOException e) {
                System.err.println("Error copying database: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;
    private static Connection connection = null;

    static {
        // Explicitly load the driver for native image compatibility
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Could not find SQLite JDBC driver: " + e.getMessage());
        }
    }

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