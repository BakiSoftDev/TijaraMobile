package com.tijaramobile.service;
import com.tijaramobile.service.SettingsManager;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static final LanguageManager instance = new LanguageManager();
    private final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(new Locale("fr"));
    private ResourceBundle bundle;

    private LanguageManager() {
        String savedLang = SettingsManager.get("language", "fr");
        this.locale.set(new Locale(savedLang));
        updateBundle();
    }

    public static LanguageManager getInstance() {
        return instance;
    }

    public ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public Locale getLocale() {
        return locale.get();
    }

    public void setLocale(Locale loc) {
        this.locale.set(loc);
        SettingsManager.set("language", loc.getLanguage());
        SettingsManager.save();
        updateBundle();
    }

    private void updateBundle() {
        this.bundle = ResourceBundle.getBundle("com.tijaramobile.i18n.messages", getLocale());
    }

    public String get(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public static String getStatic(String key) {
        return instance.get(key);
    }

    public ResourceBundle getBundle() {
        return bundle;
    }
}
