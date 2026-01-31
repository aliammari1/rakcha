package com.esprit.controllers.products;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.esprit.models.common.Review;
import com.esprit.models.products.Product;
import com.esprit.models.users.Client;
import com.esprit.services.common.ReviewService;
import com.esprit.services.products.ProductService;
import com.esprit.utils.Chat;
import com.esprit.utils.PageRequest;
import com.esprit.utils.SessionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class CommentProductController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(CommentProductController.class.getName());
    private final Chat chat = new Chat();

    // FormsFX properties
    private final StringProperty commentProperty = new SimpleStringProperty("");
    private Form commentForm;

    @FXML
    private FlowPane CommentFlowPane;
    @FXML
    private TextArea monCommentaitreText;

    /**
     * Handle submission of a new product comment from the UI.
     *
     * <p>
     * Validates the entered text for prohibited words; if valid, creates and
     * persists a Comment,
     * otherwise shows a warning alert to the user.
     * </p>
     *
     * @param actionEvent the ActionEvent that triggered the submission
     */
    @FXML
    void addchat(final ActionEvent actionEvent) {
        // Validate form using FormsFX
        if (!validateCommentForm()) {
            return;
        }

        // Check for bad words and prevent further processing if found
        final String userMessage = commentProperty.get();
        CommentProductController.LOGGER.info("User Message: " + userMessage);
        try {
            // Convertir la chaîne de texte en objet Date
            final String dateString = this.monCommentaitreText.getText();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // ajustez le format selon vos
            // besoins
            final Date datecommantaire = dateFormat.parse(dateString);
            // Affichez la date pour vérification
            CommentProductController.LOGGER.info("datecommantaire: " + datecommantaire);
            final String badwordDetection = this.chat.badword(userMessage);
            CommentProductController.LOGGER.info("Badword Detection Result: " + badwordDetection);
            final ProductService produitService = new ProductService();
            final Product produit = new Product();
            if ("1".equals(badwordDetection)) {
                final Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Comment non valide");
                alert.setContentText("Votre comment contient des gros mots et ne peut pas être publié.");
                alert.showAndWait();
            } else {
                // Créez un objet Comment
                final Review comment = new Review((Client) SessionManager.getCurrentUser(), userMessage,
                    produitService.getProductById(produit.getId()));
                final ReviewService commentService = new ReviewService();
                // Ajoutez le comment à la base de données
                commentService.create(comment);
                // Mettez à jour l'interface utilisateur
                // updateCommentFlowPane(comment);
            }

        } catch (final ParseException e) {
            // Gérez l'exception si la conversion échoue
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    /**
     * Populate the CommentFlowPane with accepted product comments when the
     * controller is initialized.
     *
     * @param location  the location used to resolve relative paths for the root
     *                  object, may be null
     * @param resources the resources used to localize the root object, may be null
     */
    @Override
    /**
     * Initializes the JavaFX controller and sets up UI components. This method is
     * called automatically by JavaFX after loading the FXML file.
     */
    public void initialize(final URL location, final ResourceBundle resources) {
        this.loadAcceptedComment();

        // Initialize FormsFX
        setupFormsFX();
        setupBidirectionalBindings();
    }

    /**
     * Configures FormsFX for comment form validation.
     */
    private void setupFormsFX() {
        commentForm = Form.of(
            com.dlsc.formsfx.model.structure.Group.of(
                Field.ofStringType(commentProperty)
                    .label("Comment")
                    .validate(
                        StringLengthValidator.atLeast(3, "Comment must be at least 3 characters long")
                    )
            )
        );
    }

    /**
     * Sets up bidirectional bindings between UI fields and FormsFX properties.
     */
    private void setupBidirectionalBindings() {
        monCommentaitreText.textProperty().bindBidirectional(commentProperty);
    }

    /**
     * Validates the comment form using FormsFX.
     *
     * @return true if the form is valid, false otherwise
     */
    private boolean validateCommentForm() {
        commentForm.persist();
        if (!commentForm.isValid()) {
            StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n");
            commentForm.getGroups().forEach(group ->
                group.getElements().forEach(element -> {
                    if (element instanceof Field<?> field) {
                        if (!field.isValid()) {
                            errorMessage.append("- ").append(field.getLabel()).append(": ");
                            field.getErrorMessages().forEach(msg -> errorMessage.append(msg).append(" "));
                            errorMessage.append("\n");
                        }
                    }
                })
            );
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validation Error");
            alert.setContentText(errorMessage.toString());
            alert.showAndWait();
            return false;
        }
        return true;
    }

    /**
     * Cleans up bindings when the controller is destroyed.
     */
    public void cleanup() {
        monCommentaitreText.textProperty().unbindBidirectional(commentProperty);
    }

    /**
     * Load and display accepted comments in the CommentFlowPane.
     * <p>
     * Retrieves comments from CommentService, creates a UI card for each comment
     * via
     * createcommentcard(Comment), and adds those cards to the CommentFlowPane.
     */
    private void loadAcceptedComment() {
        // Récupérer toutes les produits depuis le service
        final ReviewService commentservices = new ReviewService();
        final List<Review> comments = commentservices.read(PageRequest.defaultPage()).getContent();
        // Créer une carte pour chaque produit et l'ajouter à la FlowPane
        for (final Review comm : comments) {
            final HBox cardContainer = this.createcommentcard(comm);
            this.CommentFlowPane.getChildren().add(cardContainer);
        }

    }

    /**
     * Create an HBox card that displays a comment's author and text.
     *
     * @param comm the Comment to render; the client's first and last name and the
     *             comment text are used
     * @return an HBox whose children form a styled card showing the comment
     * author's name and the comment text
     */
    public HBox createcommentcard(final Review comm) {
        // Créer une VBox pour chaque comment
        final HBox commentVBox = new HBox();
        commentVBox.setStyle("-fx-padding: 5px 0 0 10px");
        final AnchorPane card = new AnchorPane();
        card.setPrefWidth(200);
        // Nom de l'auteur
        final Label nomLabel = new Label(comm.getUser().getFirstName() + " " + comm.getUser().getLastName() + ":");
        nomLabel.setStyle("-fx-font-weight: bold");
        nomLabel.setFont(Font.font("Verdana", 15));
        nomLabel.setStyle("-fx-text-fill: black;");
        nomLabel.setPrefWidth(230);
        nomLabel.setLayoutX(20);
        nomLabel.setLayoutY(30);
        nomLabel.setMaxWidth(300);
        nomLabel.setWrapText(true);
        // Contenu du comment
        final Label commentLabel = new Label(comm.getComment());
        commentLabel.setFont(Font.font("Arial", 12));
        commentLabel.setTextFill(Color.web("black"));
        commentLabel.setPrefWidth(230);
        commentLabel.setLayoutX(20);
        commentLabel.setLayoutY(55);
        commentLabel.setMaxWidth(300);
        commentLabel.setWrapText(true);
        // Ajout des éléments à la VBox
        card.getChildren().addAll(nomLabel, commentLabel);
        commentVBox.getChildren().add(card);
        // Ajout de la VBox au FlowPane
        // CommentFlowPane.getChildren().add(commentVBox);
        return commentVBox;
    }

    /**
     * Open the cinema client view in a new window and close the current window.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    @FXML
    void cinemaclient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/produits/CommentProduct.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("cinema ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }

    }

    /**
     * Navigate to the event client interface.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    @FXML
    void eventClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource("/ui/AffichageEvenementClient.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Event ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }

    }

    /**
     * Opens the product client view in a new window and closes the current window.
     *
     * @param event the action event that triggered the navigation
     */
    @FXML
    void produitClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(
                this.getClass().getResource("/ui/produits/AfficherProductClient.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("products ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }

    }

    /**
     * No-op handler for client profile action.
     * <p>
     * Currently a placeholder and performs no action.
     */
    @FXML
    void profilclient(final ActionEvent event) {
    }

    /**
     * Open the movie client view in a new window and close the current window.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    @FXML
    void MovieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/films/filmuser.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("movie ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }

    }

    /**
     * Open the series client view and replace the current window with it.
     *
     * @param event the ActionEvent that triggered the navigation
     */
    @FXML
    void SerieClient(final ActionEvent event) {
        try {
            // Charger la nouvelle interface ShoppingCartProduct.fxml
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/Series-view.fxml"));
            final Parent root = loader.load();
            // Créer une nouvelle scène avec la nouvelle interface
            final Scene scene = new Scene(root);
            // Obtenir la Stage (fenêtre) actuelle à partir de l'événement
            final Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Créer une nouvelle fenêtre (stage) et y attacher la scène
            final Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("chat ");
            stage.show();
            // Fermer la fenêtre actuelle
            currentStage.close();
        } catch (final IOException e) {
            CommentProductController.LOGGER.log(Level.SEVERE, e.getMessage(), e); // Gérer l'exception
            // d'entrée/sortie
        }

    }

}
