package com.esprit.controllers.users;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.RegexValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.esprit.utils.UserSMSAPI;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ResourceBundle;

@Log4j2
public class SMSAdminController implements Initializable {

    // FormsFX properties for declarative form handling
    private final StringProperty codeProperty = new SimpleStringProperty("");
    private final StringProperty phoneProperty = new SimpleStringProperty("");
    private final StringProperty messageProperty = new SimpleStringProperty("");
    int verificationCode;
    @FXML
    private VBox smsFormContainer;
    @FXML
    private Button sendSMS;
    private Form smsForm;

    /**
     * Sets up the FormsFX form with declarative validation rules and renders it.
     */
    private void setupFormsFX() {
        this.smsForm = Form.of(
            Group.of(
                Field.ofStringType(this.phoneProperty)
                    .label("Phone Number")
                    .placeholder("Enter phone number (8 digits)")
                    .validate(StringLengthValidator.exactly(8, "Phone number must be 8 digits"))
                    .validate(RegexValidator.forPattern("\\d{8}", "Phone number must contain only digits")),
                Field.ofStringType(this.codeProperty)
                    .label("Verification Code")
                    .placeholder("Enter verification code")
                    .validate(StringLengthValidator.exactly(6, "Code must be 6 digits"))
                    .validate(RegexValidator.forPattern("\\d{6}", "Code must contain only digits")),
                Field.ofStringType(this.messageProperty)
                    .label("Message")
                    .placeholder("Type your message here...")
                    .multiline(true)
            )
        ).title("SMS Verification");

        // Render the form into the container
        if (this.smsFormContainer != null) {
            FormRenderer formRenderer = new FormRenderer(this.smsForm);
            this.smsFormContainer.getChildren().clear();
            this.smsFormContainer.getChildren().add(formRenderer);
        }

        // Add listener for phone number to auto-send verification code
        this.phoneProperty.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.trim().length() == 8) {
                final SecureRandom random = new SecureRandom();
                this.verificationCode = random.nextInt(999999 - 100000) + 100000;
                UserSMSAPI.sendSMS(Integer.parseInt(newValue.trim()), "Rakcha Admin",
                    "your code is " + this.verificationCode);
            }
        });
    }

    /**
     * Handle the verification-code submission; if the entered code matches the generated code,
     * replace the current scene with the ResetPasswordlogin view.
     *
     * @param event the UI action event that triggered this handler
     * @throws RuntimeException if loading the ResetPasswordlogin view fails
     */
    public void sendSMS(final ActionEvent event) {
        if (this.verificationCode == Integer.parseInt(this.codeProperty.get())) {
            try {
                final FXMLLoader loader = new FXMLLoader(
                    this.getClass().getResource("/ui/users/ResetPasswordlogin.fxml"));
                final Parent root = loader.load();
                final Stage stage = (Stage) this.sendSMS.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        setupFormsFX();
    }
}
