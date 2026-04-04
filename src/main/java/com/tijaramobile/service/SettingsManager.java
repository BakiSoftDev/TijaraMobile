package com.tijaramobile.service;

import java.io.*;
import java.util.Properties;

public class SettingsManager {
    private static final String APP_DIR = System.getProperty("user.home") + File.separator + ".tijaramobile";
    private static final String SETTINGS_FILE = APP_DIR + File.separator + "settings.properties";
    private static final Properties properties = new Properties();

    static {
        loadSettings();
    }

    private static void loadSettings() {
        File appDir = new File(APP_DIR);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }

        File settingsFile = new File(SETTINGS_FILE);
        if (!settingsFile.exists()) {
            try {
                settingsFile.createNewFile();
                // Default settings
                properties.setProperty("language", "fr");
                properties.setProperty("remember", "false");
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            properties.load(input);
        } catch (IOException ex) {
            // Error loading settings
        }
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static void set(String key, String value) {
        properties.setProperty(key, value);
    }

    public static void save() {
        try (OutputStream output = new FileOutputStream(SETTINGS_FILE)) {
            properties.store(output, "TijaraMobile Application Settings");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }
}
