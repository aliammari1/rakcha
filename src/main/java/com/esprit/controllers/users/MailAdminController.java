package com.esprit.controllers.users;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.RegexValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.esprit.services.users.UserService;
import com.esprit.utils.SessionManager;
import com.esprit.utils.UserMail;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class MailAdminController {

    private static final Logger LOGGER = Logger.getLogger(MailAdminController.class.getName());
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // FormsFX properties
    private final StringProperty emailProperty = new SimpleStringProperty("");
    private Form emailForm;

    @FXML
    private VBox emailFormContainer;
    @FXML
    private Button sendButton;

    /**
     * Initializes the controller.
     */
    @FXML
    void initialize() {
        setupFormsFX();
    }

    /**
     * Configures FormsFX for email form validation and renders it.
     */
    private void setupFormsFX() {
        emailForm = Form.of(
            Group.of(
                Field.ofStringType(emailProperty)
                    .label("Email Address")
                    .placeholder("Enter your email")
                    .required("Email address is required")
                    .validate(RegexValidator.forPattern(EMAIL_REGEX, "Please enter a valid email address"))
            )
        ).title("Password Recovery");

        // Render the form into the container
        if (emailFormContainer != null) {
            FormRenderer formRenderer = new FormRenderer(emailForm);
            emailFormContainer.getChildren().clear();
            emailFormContainer.getChildren().add(formRenderer);
        }
    }

    /**
     * Sends verification code to email.
     *
     * @param event the action event triggered by the send button
     */
    @FXML
    void sendMail(final ActionEvent event) {
        // Validate using FormsFX
        if (!emailForm.isValid()) {
            return;
        }

        final String email = emailProperty.get().trim();

        try {
            // Check if email exists in database
            final UserService userService = new UserService();
            if (!userService.checkEmailFound(email)) {
                return;
            }

            final SecureRandom random = new SecureRandom();
            final String verificationCode = String.format("%06d",
                random.nextInt(1000000));

            // Store code and email in session
            SessionManager.setVerificationData(verificationCode, email);

            // Send verification code email using the professional template
            UserMail.sendVerificationCode(email, "User", verificationCode, 10);

            // Navigate to verification code screen
            navigateToVerificationScreen();

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error sending email: " + e.getMessage(), e);
        }
    }

    /**
     * Navigates to the verification code screen.
     *
     * @throws IOException if FXML file cannot be loaded
     */
    private void navigateToVerificationScreen() throws IOException {
        final FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/ui/users/VerificationCode.fxml"));
        final Parent root = loader.load();
        final Stage stage = (Stage) this.sendButton.getScene().getWindow();
        final VerificationCodeController controller = loader.getController();
        controller.setEmail(emailProperty.get().trim());
        stage.setScene(new Scene(root));
    }
}
