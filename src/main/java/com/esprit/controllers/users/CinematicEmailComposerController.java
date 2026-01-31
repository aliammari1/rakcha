package com.esprit.controllers.users;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.RegexValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.esprit.utils.UserMail;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j2;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Log4j2
public class CinematicEmailComposerController {

    private static final Logger LOGGER = Logger.getLogger(CinematicEmailComposerController.class.getName());
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final int MAX_CHARACTERS = 5000;
    // FormsFX properties for declarative form handling
    private final StringProperty recipientEmailProperty = new SimpleStringProperty("");
    private final StringProperty subjectProperty = new SimpleStringProperty("");
    private final StringProperty messageContentProperty = new SimpleStringProperty("");
    @FXML
    private TextField recipientEmailField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea messageContentArea;
    @FXML
    private ComboBox<String> emailTypeCombo;
    @FXML
    private TextArea previewArea;
    @FXML
    private Button sendButton;
    @FXML
    private Button clearButton;
    @FXML
    private Label recipientErrorLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private Label charCountLabel;
    private Form emailForm;

    /**
     * Initializes the controller and sets up event listeners.
     */
    @FXML
    public void initialize() {
        setupFormsFX();
        setupEasyBindings();
        setupEmailTypeCombo();
        setupEventListeners();
        applyAnimations();
    }

    /**
     * Sets up the email type combo box with available formats.
     */
    private void setupEmailTypeCombo() {
        emailTypeCombo.getItems().addAll("Plain Text", "HTML");
        emailTypeCombo.setValue("Plain Text");
        emailTypeCombo.setOnAction(e -> updatePreview());
    }

    /**
     * Sets up the FormsFX form with declarative validation rules.
     */
    private void setupFormsFX() {
        this.emailForm = Form.of(
            Group.of(
                Field.ofStringType(this.recipientEmailProperty)
                    .label("Recipient Email")
                    .validate(RegexValidator.forPattern(
                        "^[A-Za-z0-9+_.-]+@(.+)$",
                        "Please enter a valid email address")),
                Field.ofStringType(this.subjectProperty)
                    .label("Subject")
                    .validate(StringLengthValidator.atLeast(1, "Subject is required")),
                Field.ofStringType(this.messageContentProperty)
                    .label("Message")
                    .validate(StringLengthValidator.atLeast(1, "Message content is required"))
            )
        );
    }

    /**
     * Sets up EasyBind subscriptions to sync text fields with FormsFX properties.
     */
    private void setupEasyBindings() {
        if (this.recipientEmailField != null) {
            this.recipientEmailField.textProperty().bindBidirectional(this.recipientEmailProperty);
        }
        if (this.subjectField != null) {
            this.subjectField.textProperty().bindBidirectional(this.subjectProperty);
        }
        if (this.messageContentArea != null) {
            this.messageContentArea.textProperty().bindBidirectional(this.messageContentProperty);
        }
    }

    /**
     * Cleanup method to unbind properties when the view is closed.
     */
    public void cleanup() {
        // Unbind bidirectional bindings
        if (this.recipientEmailField != null) {
            this.recipientEmailField.textProperty().unbindBidirectional(this.recipientEmailProperty);
        }
        if (this.subjectField != null) {
            this.subjectField.textProperty().unbindBidirectional(this.subjectProperty);
        }
        if (this.messageContentArea != null) {
            this.messageContentArea.textProperty().unbindBidirectional(this.messageContentProperty);
        }
    }

    /**
     * Sets up event listeners for real-time validation and preview updates.
     */
    private void setupEventListeners() {
        // Real-time character count
        messageContentArea.textProperty().addListener((obs, oldVal, newVal) -> {
            updateCharCount();
            updatePreview();
        });

        // Subject preview update
        subjectField.textProperty().addListener((obs, oldVal, newVal) -> updatePreview());

        // Email type change
        emailTypeCombo.setOnAction(e -> updatePreview());

        // Clear button action
        clearButton.setOnAction(this::clearForm);

        // Send button action
        sendButton.setOnAction(this::sendEmail);

        // Email validation on focus lost
        recipientEmailField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                validateEmail();
            }
        });
    }

    /**
     * Updates the character count display.
     */
    private void updateCharCount() {
        int count = messageContentArea.getText().length();
        charCountLabel.setText(count + " / " + MAX_CHARACTERS + " characters");

        if (count > MAX_CHARACTERS) {
            messageContentArea.deleteText(MAX_CHARACTERS, count);
            charCountLabel.setStyle("-fx-text-fill: #ff4444;");
        } else if (count > MAX_CHARACTERS * 0.8) {
            charCountLabel.setStyle("-fx-text-fill: #ffaa00;");
        } else {
            charCountLabel.setStyle("-fx-text-fill: #666666;");
        }
    }

    /**
     * Updates the preview area with formatted content.
     */
    private void updatePreview() {
        String preview = "TO: " + (recipientEmailField.getText().isEmpty() ? "[recipient email]" : recipientEmailField.getText()) + "\n\n" +
            "SUBJECT: " + (subjectField.getText().isEmpty() ? "[subject line]" : subjectField.getText()) + "\n\n" +
            "FORMAT: " + emailTypeCombo.getValue() + "\n\n" +
            "---\n\n" +
            (messageContentArea.getText().isEmpty() ? "[message content]" : messageContentArea.getText());

        previewArea.setText(preview);
    }

    /**
     * Validates the recipient email address format.
     *
     * @return true if email is valid, false otherwise
     */
    private boolean validateEmail() {
        String email = recipientEmailField.getText().trim();

        if (email.isEmpty()) {
            recipientErrorLabel.setText("Email address is required");
            return false;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            recipientErrorLabel.setText("Invalid email format");
            return false;
        }

        recipientErrorLabel.setText("");
        return true;
    }

    /**
     * Validates all form fields before sending.
     *
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateForm() {
        // Validate recipient email
        if (!validateEmail()) {
            return false;
        }

        // Validate subject
        if (subjectField.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter a subject line");
            return false;
        }

        // Validate message content
        if (messageContentArea.getText().trim().isEmpty()) {
            showAlert("Validation Error", "Please enter message content");
            return false;
        }

        return true;
    }

    /**
     * Clears all form fields and resets the form.
     *
     * @param event the action event triggered by the clear button
     */
    @FXML
    private void clearForm(final ActionEvent event) {
        // Animate the clear action
        FadeTransition fade = new FadeTransition(Duration.millis(200), messageContentArea);
        fade.setFromValue(1.0);
        fade.setToValue(0.5);
        fade.setOnFinished(e -> {
            recipientEmailField.clear();
            subjectField.clear();
            messageContentArea.clear();
            previewArea.clear();
            emailTypeCombo.setValue("Plain Text");
            recipientErrorLabel.setText("");
            statusLabel.setText("");
            charCountLabel.setText("0 / " + MAX_CHARACTERS + " characters");

            // Fade back in
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), messageContentArea);
            fadeIn.setFromValue(0.5);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fade.play();
    }

    /**
     * Sends the email with the current form data.
     *
     * @param event the action event triggered by the send button
     */
    @FXML
    private void sendEmail(final ActionEvent event) {
        if (!validateForm()) {
            return;
        }

        // Disable send button and show loading state
        sendButton.setDisable(true);
        statusLabel.setText("Sending email...");
        statusLabel.setStyle("-fx-text-fill: #ffaa00;");

        // Send email in a background thread
        new Thread(() -> {
            try {
                String recipient = recipientEmailField.getText().trim();
                String subject = subjectField.getText().trim();
                String message = messageContentArea.getText();
                boolean isHtml = "HTML".equals(emailTypeCombo.getValue());

                // Send the email
                if (isHtml) {
                    UserMail.sendHtml(recipient, subject, message);
                } else {
                    UserMail.send(recipient, subject, message);
                }

                // Update UI on success
                Platform.runLater(() -> {
                    showSuccessAnimation();
                    statusLabel.setText("✓ Email sent successfully!");
                    statusLabel.setStyle("-fx-text-fill: #00cc00;");

                    // Show success alert
                    showAlert("Success", "Email sent successfully to " + recipient);

                    // Clear the form after successful send
                    clearForm(null);
                    sendButton.setDisable(false);
                });

            } catch (Exception e) {
                // Update UI on error
                Platform.runLater(() -> {
                    LOGGER.log(Level.SEVERE, "Error sending email: " + e.getMessage(), e);
                    statusLabel.setText("✗ Failed to send email: " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: #ff4444;");
                    showAlert("Error", "Failed to send email:\n" + e.getMessage());
                    sendButton.setDisable(false);
                });
            }
        }).start();
    }

    /**
     * Displays a success animation on the send button.
     */
    private void showSuccessAnimation() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(300), sendButton);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.play();
    }

    /**
     * Shows an alert dialog to the user.
     *
     * @param title   the alert title
     * @param message the alert message
     */
    private void showAlert(final String title, final String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Applies cinematic animations to UI elements.
     */
    private void applyAnimations() {
        // Add hover effect to send button (optional enhancement)
        sendButton.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), sendButton);
            scale.setToX(1.02);
            scale.setToY(1.02);
            scale.play();
        });

        sendButton.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), sendButton);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }
}
