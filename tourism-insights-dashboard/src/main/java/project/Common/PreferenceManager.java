package project.Common;

import java.util.prefs.Preferences;

import project.Controller.StatisticsController;

/**
 * The PreferenceManager class provides a utility for saving and retrieving user preferences
 * using the Java Preferences API. This class acts as a centralized manager to handle
 * key-value pairs for application settings or user-specific configurations.
 */
public class PreferenceManager {
    private static final Preferences prefs = Preferences.userNodeForPackage(StatisticsController.class);

    // Save preferences
    public static void savePreferences(String key, String value) {
        prefs.put(key, value);
    }

    // Load preferences
    public static String getPreference(String key, String defaultValue) {
        return prefs.get(key, defaultValue);
    }

    // Clear preferences
    public static void clearPreferences(String key) {
        prefs.remove(key);
    }
}
