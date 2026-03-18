package com.esprit.utils;

import atlantafx.base.theme.CupertinoDark;
import atlantafx.base.theme.CupertinoLight;
import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import atlantafx.base.theme.PrimerDark;
import atlantafx.base.theme.PrimerLight;
import atlantafx.base.theme.Theme;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * ThemeManager provides centralized theme management for the RAKCHA application.
 * Uses AtlantaFX themes for modern, consistent styling across all UI components.
 * Supports theme persistence, live CSS reload, and custom RAKCHA branding.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ThemeManager {

    private static final Logger LOGGER = Logger.getLogger(ThemeManager.class.getName());
    private static final String PREF_KEY_THEME = "rakcha.theme";
    private static final String DEFAULT_THEME = "PRIMER_DARK";

    private static ThemeManager instance;
    private final Preferences prefs;
    private final ObjectProperty<Theme> currentTheme;
    private final Map<String, Theme> availableThemes;
    private boolean cssFxEnabled = false;

    private ThemeManager() {
        this.prefs = Preferences.userNodeForPackage(ThemeManager.class);
        this.currentTheme = new SimpleObjectProperty<>();
        this.availableThemes = new HashMap<>();

        // Register all available themes
        for (RakchaTheme theme : RakchaTheme.values()) {
            availableThemes.put(theme.name(), theme.getTheme());
        }

        // Load saved theme preference
        String savedTheme = prefs.get(PREF_KEY_THEME, DEFAULT_THEME);
        Theme theme = availableThemes.getOrDefault(savedTheme, RakchaTheme.PRIMER_DARK.getTheme());
        currentTheme.set(theme);
    }

    /**
     * Gets the singleton instance of ThemeManager.
     *
     * @return the ThemeManager instance
     */
    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    /**
     * Initializes the theme system and applies the saved theme globally.
     * Call this in MainApp before creating any scenes.
     */
    public void initialize() {
        applyTheme(currentTheme.get());
        LOGGER.info("ThemeManager initialized with theme: " + getThemeName(currentTheme.get()));
    }

    /**
     * Applies the specified theme globally to all application windows.
     *
     * @param theme the AtlantaFX theme to apply
     */
    public void applyTheme(Theme theme) {
        if (theme != null) {
            Application.setUserAgentStylesheet(theme.getUserAgentStylesheet());
            currentTheme.set(theme);
            saveThemePreference(theme);
            LOGGER.info("Applied theme: " + getThemeName(theme));
        }
    }

    /**
     * Applies a theme by its enum value.
     *
     * @param rakchaTheme the RAKCHA theme enum value
     */
    public void applyTheme(RakchaTheme rakchaTheme) {
        applyTheme(rakchaTheme.getTheme());
    }

    /**
     * Applies a theme by its name.
     *
     * @param themeName the theme name (e.g., "PRIMER_DARK", "DRACULA")
     */
    public void applyTheme(String themeName) {
        Theme theme = availableThemes.get(themeName);
        if (theme != null) {
            applyTheme(theme);
        } else {
            LOGGER.warning("Unknown theme: " + themeName + ", using default");
            applyTheme(RakchaTheme.PRIMER_DARK);
        }
    }

    /**
     * Applies the current theme to a specific scene and enables CSS hot-reload if development mode is active.
     * Use this for each new scene created.
     *
     * @param scene the scene to style
     */
    public void applyToScene(Scene scene) {
        if (scene == null) return;

        // Load RAKCHA variables CSS first to ensure custom properties are available
        var variablesCss = getClass().getResource("/ui/styles/rakcha-variables.css");
        if (variablesCss != null) {
            scene.getStylesheets().add(variablesCss.toExternalForm());
        }

        // Add RAKCHA unified styles on top of AtlantaFX
        var unifiedCss = getClass().getResource("/ui/styles/rakcha-unified.css");
        if (unifiedCss != null) {
            scene.getStylesheets().add(unifiedCss.toExternalForm());
        }

        // Enable CSS hot-reload for development
        if (cssFxEnabled) {
            CSSFX.start(scene);
            LOGGER.fine("CssFX hot-reload enabled for scene");
        }
    }

    /**
     * Enables CssFX hot-reload for CSS development.
     * Call this once at application startup if you want live CSS editing.
     */
    public void enableCssFxHotReload() {
        if (!cssFxEnabled) {
            CSSFX.start();
            cssFxEnabled = true;
            LOGGER.info("CssFX hot-reload enabled globally");
        }
    }

    /**
     * Gets the current theme.
     *
     * @return the current AtlantaFX theme
     */
    public Theme getCurrentTheme() {
        return currentTheme.get();
    }

    /**
     * Gets the current theme property for binding.
     *
     * @return the current theme property
     */
    public ObjectProperty<Theme> currentThemeProperty() {
        return currentTheme;
    }

    /**
     * Checks if the current theme is a dark theme.
     *
     * @return true if dark theme is active
     */
    public boolean isDarkTheme() {
        Theme theme = currentTheme.get();
        return theme instanceof PrimerDark
            || theme instanceof NordDark
            || theme instanceof CupertinoDark
            || theme instanceof Dracula;
    }

    /**
     * Toggles between dark and light variants of the current theme family.
     */
    public void toggleDarkMode() {
        Theme current = currentTheme.get();
        Theme newTheme;

        if (current instanceof PrimerDark) {
            newTheme = RakchaTheme.PRIMER_LIGHT.getTheme();
        } else if (current instanceof PrimerLight) {
            newTheme = RakchaTheme.PRIMER_DARK.getTheme();
        } else if (current instanceof NordDark) {
            newTheme = RakchaTheme.NORD_LIGHT.getTheme();
        } else if (current instanceof NordLight) {
            newTheme = RakchaTheme.NORD_DARK.getTheme();
        } else if (current instanceof CupertinoDark) {
            newTheme = RakchaTheme.CUPERTINO_LIGHT.getTheme();
        } else if (current instanceof CupertinoLight) {
            newTheme = RakchaTheme.CUPERTINO_DARK.getTheme();
        } else if (current instanceof Dracula) {
            newTheme = RakchaTheme.PRIMER_LIGHT.getTheme();
        } else {
            newTheme = RakchaTheme.PRIMER_DARK.getTheme();
        }

        applyTheme(newTheme);
    }

    /**
     * Gets all available themes.
     *
     * @return array of available RAKCHA themes
     */
    public RakchaTheme[] getAvailableThemes() {
        return RakchaTheme.values();
    }

    private void saveThemePreference(Theme theme) {
        String themeName = getThemeKey(theme);
        if (themeName != null) {
            prefs.put(PREF_KEY_THEME, themeName);
        }
    }

    private String getThemeKey(Theme theme) {
        for (Map.Entry<String, Theme> entry : availableThemes.entrySet()) {
            if (entry.getValue().getClass().equals(theme.getClass())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private String getThemeName(Theme theme) {
        if (theme == null) return "null";
        for (RakchaTheme rt : RakchaTheme.values()) {
            if (rt.getTheme().getClass().equals(theme.getClass())) {
                return rt.getDisplayName();
            }
        }
        return theme.getClass().getSimpleName();
    }

    /**
     * Available themes for RAKCHA application.
     */
    public enum RakchaTheme {
        /**
         * Dark theme with primer colors - default cinema experience
         */
        PRIMER_DARK("Primer Dark", new PrimerDark()),
        /**
         * Light theme with primer colors
         */
        PRIMER_LIGHT("Primer Light", new PrimerLight()),
        /**
         * Nord inspired dark theme - cool cinema vibes
         */
        NORD_DARK("Nord Dark", new NordDark()),
        /**
         * Nord inspired light theme
         */
        NORD_LIGHT("Nord Light", new NordLight()),
        /**
         * Cupertino dark - Apple-inspired elegance
         */
        CUPERTINO_DARK("Cupertino Dark", new CupertinoDark()),
        /**
         * Cupertino light - Clean and modern
         */
        CUPERTINO_LIGHT("Cupertino Light", new CupertinoLight()),
        /**
         * Dracula theme - Popular dark theme for cinema feel
         */
        DRACULA("Dracula", new Dracula());

        private final String displayName;
        private final Theme theme;

        RakchaTheme(String displayName, Theme theme) {
            this.displayName = displayName;
            this.theme = theme;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Theme getTheme() {
            return theme;
        }
    }
}
