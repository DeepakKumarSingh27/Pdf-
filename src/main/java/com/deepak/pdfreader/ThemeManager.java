package com.deepak.pdfreader;

import javafx.scene.Scene;
import java.net.URL;

public class ThemeManager {

    private static boolean isDark = true;

    public static void applyTheme(Scene scene, boolean darkTheme) {
        isDark = darkTheme;
        scene.getStylesheets().clear();
        String themeFile = darkTheme ? "/css/dark.css" : "/css/light.css";
        URL themeUrl = ThemeManager.class.getResource(themeFile);
        
        if (themeUrl != null) {
            scene.getStylesheets().add(themeUrl.toExternalForm());
        } else {
            System.err.println("Failed to load theme: " + themeFile);
        }
    }
    
    public static boolean isDarkTheme() {
        return isDark;
    }
}
