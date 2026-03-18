package com.esprit.controllers.cinemas;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.esprit.models.cinemas.Cinema;
import com.esprit.services.cinemas.CinemaService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Log4j2
public class ModifierCinemaController implements Initializable {

    // FormsFX properties for declarative form handling
    private final StringProperty cinemaNameProperty = new SimpleStringProperty("");
    private final StringProperty cinemaAddressProperty = new SimpleStringProperty("");
    @FXML
    private VBox cinemaFormContainer;
    @FXML
    private ImageView tfLogo;
    private Cinema cinema;
    private File selectedFile;
    private Form cinemaForm;

    /**
     * Called by the JavaFX runtime after the FXML is loaded; initializes FormsFX.
     *
     * @param location  the location used to resolve relative paths for the root object, or null if unknown
     * @param resources the resources used to localize the root object, or null if not localized
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFormsFX();
    }

    /**
     * Sets up the FormsFX form with declarative validation rules and renders it.
     */
    private void setupFormsFX() {
        this.cinemaForm = Form.of(
            Group.of(
                Field.ofStringType(this.cinemaNameProperty)
                    .label("Cinema Name")
                    .placeholder("Enter cinema name")
                    .validate(StringLengthValidator.atLeast(2, "Name must be at least 2 characters")),
                Field.ofStringType(this.cinemaAddressProperty)
                    .label("Cinema Address")
                    .placeholder("Enter cinema address")
                    .validate(StringLengthValidator.atLeast(5, "Address must be at least 5 characters"))
            )
        );

        // Render form using pure FormsFX
        if (this.cinemaFormContainer != null) {
            FormRenderer formRenderer = new FormRenderer(this.cinemaForm);
            this.cinemaFormContainer.getChildren().add(formRenderer);
        }
    }


    /**
     * Populate form fields with the given Cinema's data and update the logo preview.
     * <p>
     * If the cinema has a non-null, non-empty logo path, loads that image and sets it into the logo ImageView.
     *
     * @param cinema the Cinema whose values populate the form
     */
    public void initData(Cinema cinema) {
        this.cinema = cinema;
        cinemaNameProperty.set(cinema.getName());
        cinemaAddressProperty.set(cinema.getAddress());
        String logo = cinema.getLogoUrl();
        if (logo != null && !logo.isEmpty()) {
            Image image = new Image(logo);
            tfLogo.setImage(image);
        }

    }


    /**
     * Save edits to the currently selected cinema and open the cinema dashboard.
     * <p>
     * Validates that a cinema is selected and that required fields (name and address)
     * are filled; updates the cinema's name, address, and logo path, persists the change
     * via the cinema service, and opens the DashboardResponsableCinema UI. Shows an
     * informational alert on validation failure or success.
     *
     * @throws IOException if loading the dashboard FXML fails
     * @since 1.0
     */
    @FXML
    void modifier(ActionEvent event) throws IOException {
        if (cinema == null) {
            showAlert("Veuillez sélectionner un cinéma.");
            return;
        }

        // Récupérer les nouvelles valeurs des champs
        String nouveauNom = cinemaNameProperty.get();
        String nouvelleAdresse = cinemaAddressProperty.get();
        // Vérifier si les champs obligatoires sont remplis
        if (nouveauNom.isEmpty() || nouvelleAdresse.isEmpty()) {
            showAlert("Veuillez remplir tous les champs obligatoires.");
            return;
        }

        // Mettre à jour les informations du cinéma
        cinema.setName(nouveauNom);
        cinema.setAddress(nouvelleAdresse);
        cinema.setLogoUrl("");
        // Mettre à jour le cinéma dans la base de données
        CinemaService cinemaService = new CinemaService();
        cinemaService.update(cinema);
        showAlert("Les modifications ont été enregistrées avec succès.");
        // Charger la nouvelle interface ListCinemaAdmin.fxml
        final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/cinemas/DashboardResponsableCinema.fxml"));
        final Parent root = loader.load();
        // Créer une nouvelle scène avec la nouvelle interface
        final Scene scene = new Scene(root);
        // Créer une nouvelle fenêtre (stage) et y attacher la scène
        final Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }


    /**
     * Open a file chooser to select an image for the cinema logo and display it.
     * <p>
     * Stores the chosen file in {@code selectedFile} and sets the {@code tfLogo}
     * ImageView to the selected image if a file was picked.
     *
     * @param event the action event that triggered the file selection
     * @since 1.0
     */
    @FXML
    void select(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        this.selectedFile = fileChooser.showOpenDialog(null);
        if (null != selectedFile) {
            final Image selectedImage = new Image(this.selectedFile.toURI().toString());
            this.tfLogo.setImage(selectedImage);
        }

    }


    /**
     * Displays an information alert containing the provided message.
     *
     * @param message the text to display in the alert dialog
     */
    @FXML
    private void showAlert(final String message) {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

}
