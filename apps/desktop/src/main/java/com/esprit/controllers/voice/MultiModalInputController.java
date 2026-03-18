package com.esprit.controllers.voice;

import com.esprit.models.voice.VoiceCommand;
import com.esprit.services.voice.VoiceRecognitionService;
import com.esprit.utils.SessionManager;
import com.esprit.utils.voice.ComputerVisionUtil;
import com.esprit.utils.voice.ComputerVisionUtil.GestureData;
import com.esprit.utils.voice.ComputerVisionUtil.GestureType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/**
 * Multi-Modal Input Controller that combines voice and gesture recognition
 * following existing controller patterns. Enhances accessibility features
 * and provides natural interaction methods.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Log4j2
public class MultiModalInputController implements Initializable {

    // UI Styles following existing patterns
    private static final String ACTIVE_BUTTON_STYLE =
        "-fx-background-color: linear-gradient(to right, rgba(0, 139, 0, 0.3), rgba(0, 180, 0, 0.2));" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #008b00;" +
            "-fx-border-width: 2;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 12 20 12 20;";
    private static final String INACTIVE_BUTTON_STYLE =
        "-fx-background-color: linear-gradient(to right, rgba(139, 0, 0, 0.3), rgba(180, 0, 0, 0.2));" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #8b0000;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 12 20 12 20;";
    private static final String ACCESSIBILITY_BUTTON_STYLE =
        "-fx-background-color: linear-gradient(to right, rgba(0, 0, 139, 0.3), rgba(0, 0, 180, 0.2));" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 15;" +
            "-fx-border-color: #00008b;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 15;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 12 20 12 20;";
    // FXML Components
    @FXML
    private VBox mainContainer;
    @FXML
    private HBox controlsContainer;
    @FXML
    private Button voiceToggleButton;
    @FXML
    private Button gestureToggleButton;
    @FXML
    private Button accessibilityButton;
    @FXML
    private Label statusLabel;
    @FXML
    private TextArea commandHistoryArea;
    @FXML
    private TextField testCommandField;
    @FXML
    private Button testCommandButton;
    @FXML
    private ProgressIndicator processingIndicator;
    @FXML
    private VBox feedbackContainer;
    // Services and utilities
    private VoiceRecognitionService voiceService;
    private ComputerVisionUtil visionUtil;
    // State management
    private boolean isVoiceActive = false;
    private boolean isGestureActive = false;
    private boolean isAccessibilityMode = false;
    // Async operations
    private CompletableFuture<Void> voiceListeningTask;
    private CompletableFuture<Void> gestureRecognitionTask;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Initializing MultiModalInputController");

        // Initialize services
        initializeServices();

        // Setup UI components
        setupUIComponents();

        // Setup event handlers following existing patterns
        setupEventHandlers();

        // Initialize accessibility features
        initializeAccessibilityFeatures();

        log.info("MultiModalInputController initialized successfully");
    }

    /**
     * Initializes the voice and vision services.
     */
    private void initializeServices() {
        try {
            // Initialize voice recognition service
            voiceService = new VoiceRecognitionService();
            if (!voiceService.initialize()) {
                log.warn("Voice recognition service failed to initialize");
                updateStatus("Voice recognition unavailable", false);
            }

            // Initialize computer vision utility
            visionUtil = ComputerVisionUtil.getInstance();
            if (!visionUtil.initialize()) {
                log.warn("Computer vision utility failed to initialize");
                updateStatus("Gesture recognition unavailable", false);
            }

            updateStatus("Multi-modal input ready", true);

        } catch (Exception e) {
            log.error("Error initializing services", e);
            updateStatus("Service initialization failed", false);
        }
    }

    /**
     * Sets up UI components following existing controller patterns.
     */
    private void setupUIComponents() {
        // Configure voice toggle button
        if (voiceToggleButton != null) {
            voiceToggleButton.setStyle(INACTIVE_BUTTON_STYLE);
            FontIcon voiceIcon = new FontIcon("mdi2m-microphone:20:#ffffff");
            voiceToggleButton.setGraphic(voiceIcon);
            voiceToggleButton.setText("Voice OFF");
        }

        // Configure gesture toggle button
        if (gestureToggleButton != null) {
            gestureToggleButton.setStyle(INACTIVE_BUTTON_STYLE);
            FontIcon gestureIcon = new FontIcon("mdi2h-hand-wave:20:#ffffff");
            gestureToggleButton.setGraphic(gestureIcon);
            gestureToggleButton.setText("Gesture OFF");
        }

        // Configure accessibility button
        if (accessibilityButton != null) {
            accessibilityButton.setStyle(ACCESSIBILITY_BUTTON_STYLE);
            FontIcon accessIcon = new FontIcon("mdi2a-accessibility:20:#ffffff");
            accessibilityButton.setGraphic(accessIcon);
            accessibilityButton.setText("Accessibility");
        }

        // Configure test command components
        if (testCommandButton != null) {
            testCommandButton.setStyle(INACTIVE_BUTTON_STYLE);
            FontIcon testIcon = new FontIcon("mdi2p-play:16:#ffffff");
            testCommandButton.setGraphic(testIcon);
        }

        // Configure processing indicator
        if (processingIndicator != null) {
            processingIndicator.setVisible(false);
        }

        // Configure command history area
        if (commandHistoryArea != null) {
            commandHistoryArea.setEditable(false);
            commandHistoryArea.setWrapText(true);
            commandHistoryArea.appendText("Multi-modal input system initialized.\n");
            commandHistoryArea.appendText("Use voice commands or gestures to interact.\n\n");
        }
    }

    /**
     * Sets up event handlers following existing event handling patterns.
     */
    private void setupEventHandlers() {
        // Voice toggle button handler
        if (voiceToggleButton != null) {
            voiceToggleButton.setOnAction(e -> toggleVoiceRecognition());
        }

        // Gesture toggle button handler
        if (gestureToggleButton != null) {
            gestureToggleButton.setOnAction(e -> toggleGestureRecognition());
        }

        // Accessibility button handler
        if (accessibilityButton != null) {
            accessibilityButton.setOnAction(e -> toggleAccessibilityMode());
        }

        // Test command button handler
        if (testCommandButton != null) {
            testCommandButton.setOnAction(e -> processTestCommand());
        }

        // Test command field handler (Enter key)
        if (testCommandField != null) {
            testCommandField.setOnAction(e -> processTestCommand());
        }
    }

    /**
     * Initializes accessibility features enhancing existing accessibility.
     */
    private void initializeAccessibilityFeatures() {
        // Setup keyboard shortcuts for accessibility
        if (mainContainer != null && mainContainer.getScene() != null) {
            mainContainer.getScene().setOnKeyPressed(event -> {
                switch (event.getCode()) {
                    case F1:
                        if (event.isControlDown()) {
                            toggleVoiceRecognition();
                        }
                        break;
                    case F2:
                        if (event.isControlDown()) {
                            toggleGestureRecognition();
                        }
                        break;
                    case F3:
                        if (event.isControlDown()) {
                            toggleAccessibilityMode();
                        }
                        break;
                    case ESCAPE:
                        stopAllRecognition();
                        break;
                }
            });
        }

        // Setup accessibility tooltips
        setupAccessibilityTooltips();
    }

    /**
     * Sets up accessibility tooltips for all interactive elements.
     */
    private void setupAccessibilityTooltips() {
        if (voiceToggleButton != null) {
            Tooltip voiceTooltip = new Tooltip("Toggle voice recognition (Ctrl+F1)");
            Tooltip.install(voiceToggleButton, voiceTooltip);
        }

        if (gestureToggleButton != null) {
            Tooltip gestureTooltip = new Tooltip("Toggle gesture recognition (Ctrl+F2)");
            Tooltip.install(gestureToggleButton, gestureTooltip);
        }

        if (accessibilityButton != null) {
            Tooltip accessTooltip = new Tooltip("Toggle accessibility mode (Ctrl+F3)");
            Tooltip.install(accessibilityButton, accessTooltip);
        }

        if (testCommandField != null) {
            Tooltip testTooltip = new Tooltip("Enter a voice command to test (Press Enter to execute)");
            Tooltip.install(testCommandField, testTooltip);
        }
    }

    /**
     * Toggles voice recognition on/off.
     */
    @FXML
    private void toggleVoiceRecognition() {
        if (isVoiceActive) {
            stopVoiceRecognition();
        } else {
            startVoiceRecognition();
        }
    }

    /**
     * Starts voice recognition following existing async patterns.
     */
    private void startVoiceRecognition() {
        if (voiceService == null) {
            updateStatus("Voice service not available", false);
            return;
        }

        try {
            showProcessing(true);

            // Start voice listening
            voiceListeningTask = voiceService.startListening();

            voiceListeningTask.thenRun(() -> {
                Platform.runLater(() -> {
                    isVoiceActive = true;
                    updateVoiceButton(true);
                    updateStatus("Voice recognition active", true);
                    addToHistory("Voice recognition started");
                    showProcessing(false);
                });
            }).exceptionally(throwable -> {
                Platform.runLater(() -> {
                    log.error("Error starting voice recognition", throwable);
                    updateStatus("Voice recognition failed to start", false);
                    showProcessing(false);
                });
                return null;
            });

        } catch (Exception e) {
            log.error("Error starting voice recognition", e);
            updateStatus("Voice recognition error", false);
            showProcessing(false);
        }
    }

    /**
     * Stops voice recognition.
     */
    private void stopVoiceRecognition() {
        if (voiceService != null) {
            voiceService.stopListening();
        }

        if (voiceListeningTask != null && !voiceListeningTask.isDone()) {
            voiceListeningTask.cancel(true);
        }

        isVoiceActive = false;
        updateVoiceButton(false);
        updateStatus("Voice recognition stopped", true);
        addToHistory("Voice recognition stopped");
    }

    /**
     * Toggles gesture recognition on/off.
     */
    @FXML
    private void toggleGestureRecognition() {
        if (isGestureActive) {
            stopGestureRecognition();
        } else {
            startGestureRecognition();
        }
    }

    /**
     * Starts gesture recognition following existing async patterns.
     */
    private void startGestureRecognition() {
        if (visionUtil == null) {
            updateStatus("Vision service not available", false);
            return;
        }

        try {
            showProcessing(true);

            // Start gesture recognition with callback
            gestureRecognitionTask = visionUtil.startGestureRecognition(this::handleGestureDetected);

            gestureRecognitionTask.thenRun(() -> {
                Platform.runLater(() -> {
                    isGestureActive = true;
                    updateGestureButton(true);
                    updateStatus("Gesture recognition active", true);
                    addToHistory("Gesture recognition started");
                    showProcessing(false);
                });
            }).exceptionally(throwable -> {
                Platform.runLater(() -> {
                    log.error("Error starting gesture recognition", throwable);
                    updateStatus("Gesture recognition failed to start", false);
                    showProcessing(false);
                });
                return null;
            });

        } catch (Exception e) {
            log.error("Error starting gesture recognition", e);
            updateStatus("Gesture recognition error", false);
            showProcessing(false);
        }
    }

    /**
     * Stops gesture recognition.
     */
    private void stopGestureRecognition() {
        if (visionUtil != null) {
            visionUtil.stopGestureRecognition();
        }

        if (gestureRecognitionTask != null && !gestureRecognitionTask.isDone()) {
            gestureRecognitionTask.cancel(true);
        }

        isGestureActive = false;
        updateGestureButton(false);
        updateStatus("Gesture recognition stopped", true);
        addToHistory("Gesture recognition stopped");
    }

    /**
     * Toggles accessibility mode enhancing existing accessibility features.
     */
    @FXML
    private void toggleAccessibilityMode() {
        isAccessibilityMode = !isAccessibilityMode;

        if (isAccessibilityMode) {
            enableAccessibilityMode();
        } else {
            disableAccessibilityMode();
        }
    }

    /**
     * Enables enhanced accessibility mode.
     */
    private void enableAccessibilityMode() {
        // Increase font sizes for better readability
        if (statusLabel != null) {
            statusLabel.setStyle(statusLabel.getStyle() + "-fx-font-size: 16px;");
        }

        if (commandHistoryArea != null) {
            commandHistoryArea.setStyle(commandHistoryArea.getStyle() + "-fx-font-size: 14px;");
        }

        // Enable high contrast mode
        if (mainContainer != null) {
            mainContainer.getStyleClass().add("high-contrast");
        }

        // Update accessibility button
        if (accessibilityButton != null) {
            accessibilityButton.setStyle(ACTIVE_BUTTON_STYLE);
            accessibilityButton.setText("Accessibility ON");
        }

        updateStatus("Accessibility mode enabled", true);
        addToHistory("Accessibility mode enabled - Enhanced UI for better usability");

        // Announce accessibility mode activation
        announceToScreenReader("Accessibility mode activated. Enhanced interface enabled.");
    }

    /**
     * Disables enhanced accessibility mode.
     */
    private void disableAccessibilityMode() {
        // Reset font sizes
        if (statusLabel != null) {
            statusLabel.setStyle(statusLabel.getStyle().replace("-fx-font-size: 16px;", ""));
        }

        if (commandHistoryArea != null) {
            commandHistoryArea.setStyle(commandHistoryArea.getStyle().replace("-fx-font-size: 14px;", ""));
        }

        // Disable high contrast mode
        if (mainContainer != null) {
            mainContainer.getStyleClass().remove("high-contrast");
        }

        // Update accessibility button
        if (accessibilityButton != null) {
            accessibilityButton.setStyle(ACCESSIBILITY_BUTTON_STYLE);
            accessibilityButton.setText("Accessibility");
        }

        updateStatus("Accessibility mode disabled", true);
        addToHistory("Accessibility mode disabled");

        // Announce accessibility mode deactivation
        announceToScreenReader("Accessibility mode deactivated. Standard interface restored.");
    }

    /**
     * Processes test command from text field.
     */
    @FXML
    private void processTestCommand() {
        if (testCommandField == null || voiceService == null) {
            return;
        }

        String commandText = testCommandField.getText().trim();
        if (commandText.isEmpty()) {
            updateStatus("Please enter a command to test", false);
            return;
        }

        showProcessing(true);

        CompletableFuture.supplyAsync(() -> {
            // Process the text command
            return voiceService.processTextCommand(commandText,
                SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : null);
        }).thenAccept(command -> {
            Platform.runLater(() -> {
                if (command != null) {
                    addToHistory("Test Command: " + commandText);
                    addToHistory("Processed: " + command);

                    // Execute the command if it has high confidence
                    if (command.hasHighConfidence()) {
                        boolean executed = voiceService.executeCommand(command);
                        addToHistory("Execution: " + (executed ? "SUCCESS" : "FAILED"));
                        updateStatus("Command executed: " + (executed ? "Success" : "Failed"), executed);
                    } else {
                        addToHistory("Execution: SKIPPED (Low confidence: " +
                            String.format("%.2f", command.getConfidence()) + ")");
                        updateStatus("Command confidence too low", false);
                    }
                } else {
                    addToHistory("Test Command: " + commandText);
                    addToHistory("Processing: FAILED");
                    updateStatus("Command processing failed", false);
                }

                testCommandField.clear();
                showProcessing(false);
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                log.error("Error processing test command", throwable);
                updateStatus("Command processing error", false);
                showProcessing(false);
            });
            return null;
        });
    }

    /**
     * Handles detected gestures following existing event handling patterns.
     *
     * @param gestureData the detected gesture data
     */
    private void handleGestureDetected(GestureData gestureData) {
        Platform.runLater(() -> {
            if (gestureData.confidence() > 0.6) {
                addToHistory("Gesture Detected: " + gestureData.type() +
                    " (Confidence: " + String.format("%.2f", gestureData.confidence()) + ")");

                // Execute gesture-based actions following existing command patterns
                executeGestureAction(gestureData);

                updateStatus("Gesture recognized: " + gestureData.type(), true);

                // Announce gesture to screen reader if accessibility mode is on
                if (isAccessibilityMode) {
                    announceToScreenReader("Gesture detected: " + gestureData.type().name().replace("_", " "));
                }
            }
        });
    }

    /**
     * Executes actions based on detected gestures following existing command patterns.
     *
     * @param gestureData the gesture data
     */
    private void executeGestureAction(GestureData gestureData) {
        try {
            Stage currentStage = (Stage) mainContainer.getScene().getWindow();

            switch (gestureData.type()) {
                case SWIPE_RIGHT:
                    // Navigate forward/next
                    addToHistory("Action: Navigate forward");
                    break;

                case SWIPE_LEFT:
                    // Navigate backward/previous
                    addToHistory("Action: Navigate backward");
                    break;

                case SWIPE_UP:
                    // Scroll up or go to top
                    addToHistory("Action: Scroll up");
                    break;

                case SWIPE_DOWN:
                    // Scroll down or go to bottom
                    addToHistory("Action: Scroll down");
                    break;

                case OPEN_PALM:
                    // Stop/Pause action
                    addToHistory("Action: Stop/Pause");
                    stopAllRecognition();
                    break;

                case POINT:
                    // Select/Click action
                    addToHistory("Action: Select/Click");
                    break;

                case PINCH:
                    // Zoom out action
                    addToHistory("Action: Zoom out");
                    break;

                case ZOOM_IN:
                    // Zoom in action
                    addToHistory("Action: Zoom in");
                    break;

                default:
                    addToHistory("Action: Unknown gesture - no action mapped");
                    break;
            }

        } catch (Exception e) {
            log.error("Error executing gesture action", e);
            addToHistory("Action: Error executing gesture action");
        }
    }

    /**
     * Stops all recognition services.
     */
    @FXML
    private void stopAllRecognition() {
        stopVoiceRecognition();
        stopGestureRecognition();
        updateStatus("All recognition stopped", true);
        addToHistory("All recognition services stopped");
    }

    /**
     * Updates the voice button appearance based on state.
     *
     * @param active whether voice recognition is active
     */
    private void updateVoiceButton(boolean active) {
        if (voiceToggleButton != null) {
            if (active) {
                voiceToggleButton.setStyle(ACTIVE_BUTTON_STYLE);
                voiceToggleButton.setText("Voice ON");
                FontIcon activeIcon = new FontIcon("mdi2m-microphone:20:#ffffff");
                voiceToggleButton.setGraphic(activeIcon);
            } else {
                voiceToggleButton.setStyle(INACTIVE_BUTTON_STYLE);
                voiceToggleButton.setText("Voice OFF");
                FontIcon inactiveIcon = new FontIcon("mdi2m-microphone-off:20:#ffffff");
                voiceToggleButton.setGraphic(inactiveIcon);
            }
        }
    }

    /**
     * Updates the gesture button appearance based on state.
     *
     * @param active whether gesture recognition is active
     */
    private void updateGestureButton(boolean active) {
        if (gestureToggleButton != null) {
            if (active) {
                gestureToggleButton.setStyle(ACTIVE_BUTTON_STYLE);
                gestureToggleButton.setText("Gesture ON");
                FontIcon activeIcon = new FontIcon("mdi2h-hand-wave:20:#ffffff");
                gestureToggleButton.setGraphic(activeIcon);
            } else {
                gestureToggleButton.setStyle(INACTIVE_BUTTON_STYLE);
                gestureToggleButton.setText("Gesture OFF");
                FontIcon inactiveIcon = new FontIcon("mdi2h-hand-back-left:20:#ffffff");
                gestureToggleButton.setGraphic(inactiveIcon);
            }
        }
    }

    /**
     * Updates the status label with current system status.
     *
     * @param message the status message
     * @param success whether the status indicates success
     */
    private void updateStatus(String message, boolean success) {
        if (statusLabel != null) {
            Platform.runLater(() -> {
                statusLabel.setText(message);
                if (success) {
                    statusLabel.setStyle("-fx-text-fill: #00aa00; -fx-font-weight: bold;");
                } else {
                    statusLabel.setStyle("-fx-text-fill: #aa0000; -fx-font-weight: bold;");
                }
            });
        }

        log.info("Status: {} ({})", message, success ? "SUCCESS" : "ERROR");
    }

    /**
     * Adds a message to the command history area.
     *
     * @param message the message to add
     */
    private void addToHistory(String message) {
        if (commandHistoryArea != null) {
            Platform.runLater(() -> {
                String timestamp = java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                commandHistoryArea.appendText("[" + timestamp + "] " + message + "\n");

                // Auto-scroll to bottom
                commandHistoryArea.setScrollTop(Double.MAX_VALUE);
            });
        }
    }

    /**
     * Shows or hides the processing indicator.
     *
     * @param show whether to show the indicator
     */
    private void showProcessing(boolean show) {
        if (processingIndicator != null) {
            Platform.runLater(() -> {
                processingIndicator.setVisible(show);
            });
        }
    }

    /**
     * Announces text to screen readers for accessibility.
     *
     * @param text the text to announce
     */
    private void announceToScreenReader(String text) {
        // In a full implementation, this would use platform-specific screen reader APIs
        // For now, we'll log the announcement and add it to history
        log.info("Screen Reader Announcement: {}", text);
        if (isAccessibilityMode) {
            addToHistory("Announcement: " + text);
        }
    }

    /**
     * Cleanup method called when the controller is being destroyed.
     */
    public void cleanup() {
        log.info("Cleaning up MultiModalInputController");

        // Stop all recognition services
        stopAllRecognition();

        // Shutdown services
        if (voiceService != null) {
            voiceService.shutdown();
        }

        if (visionUtil != null) {
            visionUtil.shutdown();
        }

        log.info("MultiModalInputController cleanup completed");
    }

    /**
     * Gets the current voice recognition status.
     *
     * @return true if voice recognition is active
     */
    public boolean isVoiceActive() {
        return isVoiceActive;
    }

    /**
     * Gets the current gesture recognition status.
     *
     * @return true if gesture recognition is active
     */
    public boolean isGestureActive() {
        return isGestureActive;
    }

    /**
     * Gets the current accessibility mode status.
     *
     * @return true if accessibility mode is enabled
     */
    public boolean isAccessibilityMode() {
        return isAccessibilityMode;
    }

    /**
     * Programmatically executes a voice command (for integration with other controllers).
     *
     * @param commandText the command text to execute
     * @return the processed voice command
     */
    public VoiceCommand executeVoiceCommand(String commandText) {
        if (voiceService == null || commandText == null || commandText.trim().isEmpty()) {
            return null;
        }

        VoiceCommand command = voiceService.processTextCommand(commandText,
            SessionManager.getCurrentUser() != null ? SessionManager.getCurrentUser().getId() : null);

        if (command != null && command.isExecutable()) {
            boolean executed = voiceService.executeCommand(command);
            addToHistory("External Command: " + commandText + " -> " +
                (executed ? "SUCCESS" : "FAILED"));
        }

        return command;
    }

    /**
     * Registers a custom gesture action (for integration with other controllers).
     *
     * @param gestureType the gesture type
     * @param action      the action to execute
     */
    public void registerCustomGestureAction(GestureType gestureType, Runnable action) {
        if (visionUtil != null) {
            visionUtil.registerGestureAction(gestureType, gestureData -> {
                try {
                    action.run();
                    addToHistory("Custom gesture action executed: " + gestureType);
                } catch (Exception e) {
                    log.error("Error executing custom gesture action", e);
                    addToHistory("Custom gesture action failed: " + gestureType);
                }
            });
        }
    }
}
