package com.esprit.controllers.users;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.esprit.services.users.UserService;
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
import org.mindrot.jbcrypt.BCrypt;

import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class ResetPasswordController {

    private static final Logger LOGGER = Logger.getLogger(ResetPasswordController.class.getName());
    // FormsFX properties for declarative form handling
    private final StringProperty newPasswordProperty = new SimpleStringProperty("");
    private final StringProperty confirmPasswordProperty = new SimpleStringProperty("");
    @FXML
    private VBox passwordFormContainer;
    @FXML
    private Button resetButton;
    private String userEmail;
    private Form resetPasswordForm;

    /**
     * Initializes the controller after FXML loading.
     */
    @FXML
    void initialize() {
        this.setupFormsFX();
    }

    /**
     * Sets up the FormsFX form with declarative validation rules and renders it.
     */
    private void setupFormsFX() {
        this.resetPasswordForm = Form.of(
            Group.of(
                Field.ofStringType(this.newPasswordProperty)
                    .label("New Password")
                    .placeholder("Enter new password")
                    .required("New password is required")
                    .validate(StringLengthValidator.atLeast(6, "Password must be at least 6 characters")),
                Field.ofStringType(this.confirmPasswordProperty)
                    .label("Confirm Password")
                    .placeholder("Confirm your password")
                    .required("Please confirm your password")
                    .validate(StringLengthValidator.atLeast(6, "Password must be at least 6 characters"))
            )
        ).title("Reset Password");

        // Render the form into the container
        if (this.passwordFormContainer != null) {
            FormRenderer formRenderer = new FormRenderer(this.resetPasswordForm);
            this.passwordFormContainer.getChildren().clear();
            this.passwordFormContainer.getChildren().add(formRenderer);
        }
    }

    /**
     * Sets the user email for password reset.
     *
     * @param email the user's email address
     */
    public void setUserEmail(final String email) {
        this.userEmail = email;
    }

    /**
     * Resets the user's password.
     *
     * @param event the action event triggered by the reset button
     */
    @FXML
    void resetPassword(final ActionEvent event) {
        // Validate form first
        if (!resetPasswordForm.isValid()) {
            return;
        }

        final String newPassword = this.newPasswordProperty.get().trim();
        final String confirmPassword = this.confirmPasswordProperty.get().trim();

        if (!newPassword.equals(confirmPassword)) {
            // Passwords don't match - FormsFX will show validation
            return;
        }

        try {
            // Hash the new password using BCrypt
            String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

            // Update password in database for this.userEmail
            final UserService userService = new UserService();
            userService.updatePassword(this.userEmail, hashedPassword);

            LOGGER.log(Level.INFO, "Password reset successfully for email: " + this.userEmail);

            // Clear session data
            SessionManager.clearVerificationData();

            // Navigate to login screen
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Login.fxml"));
            final Parent root = loader.load();
            final Stage stage = (Stage) this.resetButton.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (final Exception e) {
            ResetPasswordController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
