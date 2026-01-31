package com.esprit.controllers.users;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.RegexValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.esprit.utils.SessionManager;
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
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class VerificationCodeController {

    private static final Logger LOGGER = Logger.getLogger(VerificationCodeController.class.getName());
    // FormsFX properties for declarative form handling
    private final StringProperty codeProperty = new SimpleStringProperty("");
    private String email;
    @FXML
    private VBox codeFormContainer;
    @FXML
    private Button verifyButton;
    private Form codeForm;

    /**
     * Initialize method called by JavaFX.
     */
    @FXML
    public void initialize() {
        setupFormsFX();
    }

    /**
     * Sets the email associated with the verification code.
     *
     * @param email the user's email
     */
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * Sets up the FormsFX form with declarative validation rules and renders it.
     */
    private void setupFormsFX() {
        this.codeForm = Form.of(
                Group.of(
                    Field.ofStringType(this.codeProperty)
                        .label("Verification Code")
                        .placeholder("Enter your 6-digit code")
                        .required("Verification code is required")
                        .validate(StringLengthValidator.exactly(6, "Code must be exactly 6 digits"))
                        .validate(RegexValidator.forPattern("\\d{6}", "Code must contain only numbers"))))
            .title("Email Verification");

        // Render the form into the container
        if (this.codeFormContainer != null) {
            FormRenderer formRenderer = new FormRenderer(this.codeForm);
            this.codeFormContainer.getChildren().clear();
            this.codeFormContainer.getChildren().add(formRenderer);
        }
    }

    /**
     * Verifies the code entered by user.
     *
     * @param event the action event triggered by the verify button
     */
    @FXML
    void verifyCode(final ActionEvent event) {
        // Validate form first
        if (!codeForm.isValid()) {
            return;
        }

        final String code = this.codeProperty.get().trim();

        // Check if code has expired
        if (SessionManager.isCodeExpired(email)) {
            SessionManager.clearVerificationData();
            return;
        }

        // Verify the code
        if (!SessionManager.verifyCode(email, code)) {
            return;
        }

        try {
            // Code is valid, navigate to reset password screen
            navigateToResetPassword();

        } catch (final Exception e) {
            LOGGER.log(Level.SEVERE, "Error navigating to reset password: " + e.getMessage(), e);
        }
    }

    /**
     * Navigates to the reset password screen.
     *
     * @throws IOException if FXML file cannot be loaded
     */
    private void navigateToResetPassword() throws IOException {
        final FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/ui/users/ResetPasswordlogin.fxml"));
        final Parent root = loader.load();
        final ResetPasswordController controller = loader.getController();

        // Pass the email to the reset password controller
        controller.setUserEmail(SessionManager.getUserEmail(email));

        final Stage stage = (Stage) this.verifyButton.getScene().getWindow();
        stage.setScene(new Scene(root));
    }
}
