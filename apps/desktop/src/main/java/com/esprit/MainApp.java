package com.esprit;

import com.esprit.controllers.SplashScreenController;
import com.esprit.utils.ThemeManager;
import fr.brouillard.oss.cssfx.CSSFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

@Log4j2
public class MainApp extends Application {

    private Stage mainStage;
    private final boolean showSplashScreen;

    /**
     * Creates the production application, including the cinematic splash screen.
     */
    public MainApp() {
        this(true);
    }

    /**
     * Creates the application with an explicit startup flow.
     *
     * <p>The direct-login mode keeps UI tests deterministic: they exercise the
     * login scene rather than depending on a timed, animated splash transition.
     * Production code uses the no-argument constructor and therefore always
     * displays the splash screen.</p>
     *
     * @param showSplashScreen whether to display the cinematic splash screen
     */
    public MainApp(final boolean showSplashScreen) {
        this.showSplashScreen = showSplashScreen;
    }

    /**
     * Launches the JavaFX application using the provided command-line arguments.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(final String[] args) {
        Application.launch(args);
    }


    /**
     * Initialize the primary stage with the login UI and display it.
     *
     * @param primaryStage the primary JavaFX Stage to set the login scene on and display
     * @throws Exception if the login FXML resource cannot be loaded or the scene cannot be created
     */
    @Override
    /**
     * Starts the application with a cinematic splash screen.
     *
     * @return the result of the operation
     */
    public void start(final Stage primaryStage) throws Exception {
        this.mainStage = primaryStage;

        // Initialize AtlantaFX theming system
        ThemeManager.getInstance().initialize();

        // Enable CssFX for live CSS hot-reload during development
        // This allows CSS changes to reflect immediately without restart
        CSSFX.start();

        // Configure main stage (hidden initially)
        mainStage.setTitle("RAKCHA - Cinema Experience");
        try {
            mainStage.getIcons().add(new Image(getClass().getResourceAsStream("/Logo.png")));
        } catch (Exception e) {
            System.out.println("Could not load application icon: " + e.getMessage());
        }

        if (showSplashScreen) {
            showSplashScreen();
        } else {
            showLoginScreen();
        }
    }

    /**
     * Displays the cinematic splash screen.
     */
    private void showSplashScreen() throws Exception {
        // Create splash stage
        Stage splashStage = new Stage();
        splashStage.initStyle(StageStyle.UNDECORATED);
        splashStage.setTitle("RAKCHA");

        try {
            splashStage.getIcons().add(new Image(getClass().getResourceAsStream("/Logo.png")));
        } catch (Exception e) {
            System.out.println("Could not load splash icon: " + e.getMessage());
        }

        // Load splash screen FXML
        FXMLLoader splashLoader = new FXMLLoader(getClass().getResource("/ui/splash/SplashScreen.fxml"));
        Parent splashRoot = splashLoader.load();

        // Get controller and set stage references
        SplashScreenController splashController = splashLoader.getController();
        splashController.setSplashStage(splashStage);
        splashController.setMainStage(mainStage);

        // Create scene with transparent fill for cinematic effect
        Scene splashScene = new Scene(splashRoot, 1400, 800);
        splashScene.setFill(Color.BLACK);

        // Apply AtlantaFX theme to the splash scene
        ThemeManager.getInstance().applyToScene(splashScene);

        splashStage.setScene(splashScene);
        splashStage.centerOnScreen();
        splashStage.setResizable(false);
        splashStage.show();
    }

    private void showLoginScreen() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/Login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        ThemeManager.getInstance().applyToScene(scene);
        mainStage.setScene(scene);
        mainStage.show();
    }

}
