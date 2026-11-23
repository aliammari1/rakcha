package com.esprit.controllers.users;

import com.esprit.enums.UserRole;
import com.esprit.models.users.CinemaManager;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.users.UserService;
import com.esprit.utils.CloudinaryStorage;
import com.esprit.utils.SessionManager;
import com.esprit.utils.validators.EmailValidator;
import com.esprit.utils.validators.PasswordValidator;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.StringConverter;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class SignUpController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(SignUpController.class.getName());
    private static final Random RANDOM = new Random();

    private final int maxDynamicElements = 15; // Number of dynamic elements to create
    @FXML
    private StackPane rootContainer;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private TextField adresseTextField;
    @FXML
    private DatePicker dateDeNaissanceDatePicker;
    @FXML
    private TextField emailTextField;
    @FXML
    private TextField nomTextField;
    @FXML
    private TextField num_telephoneTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private ImageView photoDeProfilImageView;
    @FXML
    private TextField prenomTextField;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Button loginButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Label signUpErrorLabel;
    // Animation elements
    @FXML
    private Circle particle1, particle2, particle3, particle4, particle5, particle6;
    @FXML
    private Circle particle7, particle8, particle9, particle10, particle11, particle12;
    @FXML
    private Polygon shape1, shape2, shape5;
    @FXML
    private Rectangle shape3, shape6;
    @FXML
    private Circle shape4;
    // Animation control
    private Timeline particleCreationTimeline;
    // Arrays to store dynamic particles and shapes
    private Circle[] dynamicParticles;
    private Polygon[] dynamicShapes;
    private Rectangle[] dynamicRectangles;
    // Cloudinary image URL
    private String cloudinaryImageUrl;

    /**
     * @param event
     * @throws IOException
     */
    @FXML
    void switchToLogin(ActionEvent event) throws IOException {
        try {
            stopAllAnimations();
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error switching to login view", e);
            throw e;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize arrays for dynamic elements
        dynamicParticles = new Circle[maxDynamicElements];
        dynamicShapes = new Polygon[maxDynamicElements / 3];
        dynamicRectangles = new Rectangle[maxDynamicElements / 3];

        dateDeNaissanceDatePicker.setConverter(new LocalDateStringConverter(
            DateTimeFormatter.ISO_LOCAL_DATE, DateTimeFormatter.ISO_LOCAL_DATE) {
            @Override
            public LocalDate fromString(final String value) {
                try {
                    return super.fromString(value);
                } catch (final java.time.format.DateTimeParseException incomplete) {
                    return null; // still typing; the form reports a missing date when submitted
                }
            }
        });
        roleComboBox.getItems().setAll(UserRole.CLIENT.name(), UserRole.CINEMA_MANAGER.name());
        roleComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(String role) {
                if (UserRole.CLIENT.name().equals(role)) {
                    return "Client";
                }
                if (UserRole.CINEMA_MANAGER.name().equals(role)) {
                    return "Cinema manager";
                }
                return "";
            }

            @Override
            public String fromString(String label) {
                return roleComboBox.getItems().stream()
                    .filter(role -> toString(role).equals(label))
                    .findFirst().orElse(null);
            }
        });
        for (TextField field : List.of(prenomTextField, nomTextField, emailTextField, passwordTextField,
            num_telephoneTextField, adresseTextField, dateDeNaissanceDatePicker.getEditor())) {
            field.textProperty().addListener((observable, oldValue, newValue) -> signUpErrorLabel.setVisible(false));
        }
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> signUpErrorLabel.setVisible(false));

        // Initialize animations with a slight delay to ensure FXML is fully loaded
        Timeline delayedInit = new Timeline(new KeyFrame(Duration.millis(100), e -> {
            LOGGER.info("Starting animation initialization...");
            initializeParticleAnimation();
            initializeShapeAnimation();
            createDynamicAnimations();
            LOGGER.info("Animation initialization completed.");
        }));
        delayedInit.play();

        // Add event handler for login button
        loginButton.setOnAction(event -> {
            try {
                switchToLogin(event);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error switching to login view", e);
            }
        });
    }

    /**
     * @param event
     */
    @FXML
    void importImage(final ActionEvent event) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG", "*.png"),
            new FileChooser.ExtensionFilter("JPG", "*.jpg"));
        fileChooser.setTitle("Sélectionner une image");
        final File selectedFile = fileChooser.showOpenDialog(signUpButton.getScene().getWindow());
        if (null != selectedFile) {
            try {
                // Use the CloudinaryStorage service to upload the image
                CloudinaryStorage cloudinaryStorage = CloudinaryStorage.getInstance();
                cloudinaryImageUrl = cloudinaryStorage.uploadImage(selectedFile);

                // Display the image in the ImageView
                final Image selectedImage = new Image(selectedFile.toURI().toString(), 64, 64, true, true, true);
                this.photoDeProfilImageView.setImage(selectedImage);

                LOGGER.info("Image uploaded to Cloudinary: " + cloudinaryImageUrl);

            } catch (final IOException | RuntimeException e) {
                SignUpController.LOGGER.log(Level.SEVERE, "Error uploading image to Cloudinary", e);
                new Alert(Alert.AlertType.ERROR,
                    "The photo couldn't be uploaded. You can still create your account.", ButtonType.CLOSE).show();
            }
        }
    }

    /**
     * @param event
     * @throws IOException
     */
    @FXML
    void signup(final ActionEvent event) {
        final String selectedRole = this.roleComboBox.getValue();
        final String nom = this.nomTextField.getText().strip();
        final String prenom = this.prenomTextField.getText().strip();
        final String num_telephone = this.num_telephoneTextField.getText().strip();
        final String password = this.passwordTextField.getText();
        final String email = EmailValidator.sanitize(this.emailTextField.getText());
        final String address = this.adresseTextField.getText().strip();
        final LocalDate dateDeNaissance = this.dateDeNaissanceDatePicker.getValue();
        if (prenom.isEmpty() || nom.isEmpty()) {
            showSignUpError("Enter your first and last name.", prenom.isEmpty() ? prenomTextField : nomTextField);
            return;
        }
        if (!EmailValidator.isValid(email)) {
            showSignUpError("Enter a valid email address.", emailTextField);
            return;
        }
        List<String> passwordErrors = PasswordValidator.getValidationErrors(password);
        if (!passwordErrors.isEmpty()) {
            showSignUpError(passwordErrors.getFirst(), passwordTextField);
            return;
        }
        if (!num_telephone.matches("[0-9]{8,15}")) {
            showSignUpError("Enter a phone number with 8 to 15 digits.", num_telephoneTextField);
            return;
        }
        if (dateDeNaissance == null) {
            showSignUpError("Enter your date of birth as YYYY-MM-DD.", dateDeNaissanceDatePicker.getEditor());
            return;
        }
        if (dateDeNaissance.isAfter(LocalDate.now())) {
            showSignUpError("Date of birth cannot be in the future.", dateDeNaissanceDatePicker.getEditor());
            return;
        }
        if (!UserRole.CLIENT.name().equals(selectedRole) && !UserRole.CINEMA_MANAGER.name().equals(selectedRole)) {
            showSignUpError("Choose an account type.", roleComboBox);
            return;
        }

        // Check if email already exists
        final UserService userService = new UserService();
        if (userService.checkEmailFound(email)) {
            showSignUpError("This email is already registered. Use Sign In instead.", emailTextField);
            return;
        }

        final Date birthDate = Date.valueOf(dateDeNaissance);
        final String photo = cloudinaryImageUrl != null ? cloudinaryImageUrl : "";
        final User user = UserRole.CLIENT.name().equals(selectedRole)
            ? new Client(prenom, nom, num_telephone, password, UserRole.CLIENT, address, birthDate, email, photo)
            : new CinemaManager(prenom, nom, num_telephone, password, UserRole.CINEMA_MANAGER, address, birthDate, email, photo);

        if (!userService.createAccount(user)) {
            showSignUpError("Account creation failed. Please try again.", signUpButton);
            return;
        }
        final User previousUser = SessionManager.getCurrentUser();
        try {
            final User createdUser = userService.getUserByEmail(email);
            if (createdUser == null) {
                throw new IllegalStateException("Created user could not be loaded");
            }
            SessionManager.setCurrentUser(createdUser);
            final Stage stage = (Stage) this.nomTextField.getScene().getWindow();
            final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/ui/users/Profile.fxml"));
            final Parent root = loader.load();
            stage.setScene(new Scene(root));
            stopAllAnimations();
        } catch (Exception e) {
            SessionManager.setCurrentUser(previousUser);
            LOGGER.log(Level.SEVERE, "Could not open the profile after signup", e);
            showSignUpError("Account created. Sign in to open your profile.", loginButton);
        }
    }

    private void showSignUpError(String message, Node field) {
        field.requestFocus();
        signUpErrorLabel.setText(message);
        signUpErrorLabel.setVisible(true);
    }

    /**
     * Stops all animations when leaving the signup screen
     */
    private void stopAllAnimations() {
        if (particleCreationTimeline != null) {
            particleCreationTimeline.stop();
        }

        // Clean up dynamic elements by removing them from the scene
        if (dynamicParticles != null) {
            AnchorPane foregroundPane = null;
            try {
                foregroundPane = (AnchorPane) rootContainer.getChildren().get(2);
            } catch (Exception e) {
                LOGGER.warning("Could not access foreground pane for cleanup: " + e.getMessage());
            }

            if (foregroundPane != null) {
                for (Circle particle : dynamicParticles) {
                    if (particle != null) {
                        try {
                            foregroundPane.getChildren().remove(particle);
                        } catch (Exception e) {
                            LOGGER.warning("Could not remove particle: " + e.getMessage());
                        }
                    }
                }
            }
        }

        // Clean up dynamic shapes
        if (dynamicShapes != null) {
            AnchorPane foregroundPane = null;
            try {
                foregroundPane = (AnchorPane) rootContainer.getChildren().get(2);
            } catch (Exception e) {
                LOGGER.warning("Could not access foreground pane for cleanup: " + e.getMessage());
            }

            if (foregroundPane != null) {
                for (Polygon shape : dynamicShapes) {
                    if (shape != null) {
                        try {
                            foregroundPane.getChildren().remove(shape);
                        } catch (Exception e) {
                            LOGGER.warning("Could not remove shape: " + e.getMessage());
                        }
                    }
                }
            }
        }

        // Clean up dynamic rectangles
        if (dynamicRectangles != null) {
            AnchorPane foregroundPane = null;
            try {
                foregroundPane = (AnchorPane) rootContainer.getChildren().get(2);
            } catch (Exception e) {
                LOGGER.warning("Could not access foreground pane for cleanup: " + e.getMessage());
            }

            if (foregroundPane != null) {
                for (Rectangle rect : dynamicRectangles) {
                    if (rect != null) {
                        try {
                            foregroundPane.getChildren().remove(rect);
                        } catch (Exception e) {
                            LOGGER.warning("Could not remove rectangle: " + e.getMessage());
                        }
                    }
                }
            }
        }

        LOGGER.info("All animations stopped and dynamic elements cleaned up");
    }

    /**
     * Initializes the particle animation for floating red particles
     */
    private void initializeParticleAnimation() {
        Circle[] particles = {particle1, particle2, particle3, particle4, particle5, particle6,
            particle7, particle8, particle9, particle10, particle11, particle12};

        for (int i = 0; i < particles.length; i++) {
            if (particles[i] != null) {
                // Floating X movement
                TranslateTransition particleFloatX = new TranslateTransition(Duration.seconds(3 + i * 0.3),
                    particles[i]);
                particleFloatX.setFromX(0);
                particleFloatX.setToX(30 - i * 5);
                particleFloatX.setCycleCount(Animation.INDEFINITE);
                particleFloatX.setAutoReverse(true);
                particleFloatX.setInterpolator(Interpolator.EASE_BOTH);

                // Floating Y movement
                TranslateTransition particleFloatY = new TranslateTransition(Duration.seconds(4 + i * 0.2),
                    particles[i]);
                particleFloatY.setFromY(0);
                particleFloatY.setToY(25 - i * 3);
                particleFloatY.setCycleCount(Animation.INDEFINITE);
                particleFloatY.setAutoReverse(true);
                particleFloatY.setInterpolator(Interpolator.EASE_BOTH);

                // Pulsing scale effect
                ScaleTransition particleScale = new ScaleTransition(Duration.seconds(2.5 + i * 0.15), particles[i]);
                particleScale.setFromX(1.0);
                particleScale.setFromY(1.0);
                particleScale.setToX(1.4);
                particleScale.setToY(1.4);
                particleScale.setCycleCount(Animation.INDEFINITE);
                particleScale.setAutoReverse(true);
                particleScale.setInterpolator(Interpolator.EASE_BOTH);

                // Opacity animation for glow effect
                FadeTransition particleFade = new FadeTransition(Duration.seconds(3 + i * 0.1), particles[i]);
                particleFade.setFromValue(particles[i].getOpacity() * 0.5);
                particleFade.setToValue(particles[i].getOpacity() * 1.2);
                particleFade.setCycleCount(Animation.INDEFINITE);
                particleFade.setAutoReverse(true);
                particleFade.setInterpolator(Interpolator.EASE_BOTH);

                // Start all particle animations
                particleFloatX.play();
                particleFloatY.play();
                particleScale.play();
                particleFade.play();

                LOGGER.info("Started animations for particle" + (i + 1));
            } else {
                LOGGER.warning("particle" + (i + 1) + " is null");
            }
        }
    }

    /**
     * Initializes the geometric shape animations
     */
    private void initializeShapeAnimation() {
        // Array of all existing shapes
        Object[] shapes = {shape1, shape2, shape3, shape4, shape5, shape6};

        for (int i = 0; i < shapes.length; i++) {
            if (shapes[i] != null) {
                if (shapes[i] instanceof Polygon) {
                    animatePolygon((Polygon) shapes[i], i);
                } else if (shapes[i] instanceof Rectangle) {
                    animateRectangleShape((Rectangle) shapes[i], i);
                } else if (shapes[i] instanceof Circle) {
                    animateCircleShape((Circle) shapes[i], i);
                }
            }
        }
    }

    /**
     * Animates a polygon shape
     */
    private void animatePolygon(Polygon shape, int index) {
        // Rotation animation
        RotateTransition rotate = new RotateTransition(Duration.seconds(12 - index), shape);
        rotate.setByAngle(index % 2 == 0 ? 360 : -360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

        // Scale animation
        ScaleTransition scale = new ScaleTransition(Duration.seconds(6 + index * 0.5), shape);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        scale.play();

        // Movement animation
        TranslateTransition move = new TranslateTransition(Duration.seconds(15 + index), shape);
        move.setFromX(-15);
        move.setFromY(-15);
        move.setToX(15);
        move.setToY(15);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);
        move.play();

        LOGGER.info("Started animations for polygon shape" + (index + 1));
    }

    /**
     * Animates a rectangle shape
     */
    private void animateRectangleShape(Rectangle shape, int index) {
        // Rotation animation
        RotateTransition rotate = new RotateTransition(Duration.seconds(10 - index * 0.5), shape);
        rotate.setByAngle(index % 2 == 0 ? 360 : -360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);
        rotate.play();

        // Fade animation
        FadeTransition fade = new FadeTransition(Duration.seconds(4 + index * 0.3), shape);
        fade.setFromValue(0.3);
        fade.setToValue(0.8);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.setInterpolator(Interpolator.EASE_BOTH);
        fade.play();

        // Movement animation
        TranslateTransition move = new TranslateTransition(Duration.seconds(12 + index), shape);
        move.setFromX(-10);
        move.setFromY(-10);
        move.setToX(10);
        move.setToY(10);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);
        move.play();

        LOGGER.info("Started animations for rectangle shape" + (index + 1));
    }

    /**
     * Animates a circle shape
     */
    private void animateCircleShape(Circle shape, int index) {
        // Scale animation
        ScaleTransition scale = new ScaleTransition(Duration.seconds(5 + index * 0.5), shape);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        scale.play();

        // Glow animation
        FadeTransition glow = new FadeTransition(Duration.seconds(3 + index * 0.2), shape);
        glow.setFromValue(0.4);
        glow.setToValue(0.9);
        glow.setCycleCount(Animation.INDEFINITE);
        glow.setAutoReverse(true);
        glow.setInterpolator(Interpolator.EASE_BOTH);
        glow.play();

        // Movement animation
        TranslateTransition move = new TranslateTransition(Duration.seconds(8 + index), shape);
        move.setFromX(-5);
        move.setFromY(-5);
        move.setToX(5);
        move.setToY(5);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);
        move.play();

        LOGGER.info("Started animations for circle shape" + (index + 1));
    }

    /**
     * Creates dynamic animated elements on the screen
     */
    private void createDynamicAnimations() {
        // Get a reference to the foreground AnchorPane (the last AnchorPane in the
        // StackPane)
        AnchorPane foregroundPane = (AnchorPane) rootContainer.getChildren().get(2);

        // Create dynamic particles
        for (int i = 0; i < maxDynamicElements; i++) {
            Circle particle = new Circle();
            particle.setRadius(2 + RANDOM.nextDouble() * 5); // Random size between 2-7
            particle.setLayoutX(RANDOM.nextDouble() * 1150 + 25); // Random X position
            particle.setLayoutY(RANDOM.nextDouble() * 580 + 25); // Random Y position

            // Apply styling
            particle.getStyleClass().addAll("floating-particle", "glow-red");

            // Random red shade
            int red = 180 + RANDOM.nextInt(75);
            int darkRed = 80 + RANDOM.nextInt(100);
            particle.setStyle("-fx-fill: radial-gradient(center 50% 50%, radius 50%, #" +
                String.format("%02X", red) + "2222, #" + String.format("%02X", darkRed) + "0000); " +
                "-fx-effect: dropshadow(gaussian, #ff" + String.format("%02X", red) +
                String.format("%02X", red) + ", " + (10 + RANDOM.nextInt(15)) + ", 0, 0, 0); " +
                "-fx-opacity: " + (0.6 + RANDOM.nextDouble() * 0.4) + ";" +
                "-fx-z-index: " + (250 + i) + ";");

            // Add to parent and store reference
            foregroundPane.getChildren().add(particle);
            dynamicParticles[i] = particle;

            // Create animation
            animateParticle(particle, i);
        }

        // Create polygons
        for (int i = 0; i < dynamicShapes.length; i++) {
            Polygon shape = new Polygon();

            // Create a random polygon with 3-6 points
            int sides = 3 + RANDOM.nextInt(4);
            Double[] points = new Double[sides * 2];
            double radius = 10 + RANDOM.nextDouble() * 20;
            double centerX = RANDOM.nextDouble() * 1120 + 40;
            double centerY = RANDOM.nextDouble() * 550 + 40;

            for (int j = 0; j < sides; j++) {
                double angle = 2 * Math.PI * j / sides;
                points[j * 2] = centerX + radius * Math.cos(angle);
                points[j * 2 + 1] = centerY + radius * Math.sin(angle);
            }

            shape.getPoints().addAll(points);

            // Apply styling
            shape.getStyleClass().addAll("rotating-shape", "pulsing-shape");
            int red = 100 + RANDOM.nextInt(100);
            int darkRed = 40 + RANDOM.nextInt(60);
            shape.setStyle("-fx-fill: linear-gradient(to bottom right, rgba(" + red + ", 0, 0, 0.5), rgba(" + darkRed
                + ", 0, 0, 0.3)); " +
                "-fx-stroke: #" + String.format("%02X", red) + "0000; " +
                "-fx-stroke-width: " + (1 + RANDOM.nextInt(2)) + "; " +
                "-fx-effect: dropshadow(gaussian, rgba(" + red + ", 0, 0, 0.7), " + (8 + RANDOM.nextInt(10))
                + ", 0, 0, 0); " +
                "-fx-z-index: " + (300 + i) + ";");
            shape.setOpacity(0.4 + RANDOM.nextDouble() * 0.4);

            // Add to parent and store reference
            foregroundPane.getChildren().add(shape);
            dynamicShapes[i] = shape;

            // Create animation
            animateShape(shape, i);
        }

        // Create rectangles
        for (int i = 0; i < dynamicRectangles.length; i++) {
            Rectangle rect = new Rectangle();
            rect.setWidth(10 + RANDOM.nextDouble() * 30);
            rect.setHeight(10 + RANDOM.nextDouble() * 30);
            rect.setLayoutX(RANDOM.nextDouble() * 1150 + 25);
            rect.setLayoutY(RANDOM.nextDouble() * 580 + 25);
            rect.setRotate(RANDOM.nextDouble() * 45);

            // Apply styling
            rect.getStyleClass().addAll("rotating-shape", "pulsing-shape");
            int red = 150 + RANDOM.nextInt(100);
            int darkRed = 60 + RANDOM.nextInt(80);
            rect.setStyle("-fx-fill: linear-gradient(to bottom right, rgba(" + red + ", " + (red / 4) + ", " + (red / 4)
                + ", 0.6), rgba(" + darkRed + ", " + (darkRed / 6) + ", " + (darkRed / 6) + ", 0.4)); " +
                "-fx-stroke: #" + String.format("%02X", red) + "2222; " +
                "-fx-stroke-width: " + (RANDOM.nextDouble() * 2) + "; " +
                "-fx-effect: dropshadow(gaussian, rgba(" + red + ", " + (red / 4) + ", " + (red / 4) + ", 0.6), "
                + (8 + RANDOM.nextInt(8)) + ", 0, 0, 0); " +
                "-fx-z-index: " + (330 + i) + ";");
            rect.setOpacity(0.3 + RANDOM.nextDouble() * 0.5);

            // Add to parent and store reference
            foregroundPane.getChildren().add(rect);
            dynamicRectangles[i] = rect;

            // Create animation
            animateRectangle(rect, i);
        }

        LOGGER.info("Created " + maxDynamicElements + " dynamic particles and shapes");
    }

    /**
     * Creates animations for a particle
     */
    private void animateParticle(Circle particle, int index) {
        // Create X movement
        TranslateTransition moveX = new TranslateTransition(Duration.seconds(5 + RANDOM.nextDouble() * 15), particle);
        moveX.setFromX(-20 - RANDOM.nextDouble() * 50);
        moveX.setToX(20 + RANDOM.nextDouble() * 50);
        moveX.setCycleCount(Animation.INDEFINITE);
        moveX.setAutoReverse(true);
        moveX.setInterpolator(Interpolator.EASE_BOTH);

        // Create Y movement
        TranslateTransition moveY = new TranslateTransition(Duration.seconds(5 + RANDOM.nextDouble() * 10), particle);
        moveY.setFromY(-20 - RANDOM.nextDouble() * 40);
        moveY.setToY(20 + RANDOM.nextDouble() * 40);
        moveY.setCycleCount(Animation.INDEFINITE);
        moveY.setAutoReverse(true);
        moveY.setInterpolator(Interpolator.EASE_BOTH);

        // Create scale pulse
        ScaleTransition scale = new ScaleTransition(Duration.seconds(2 + RANDOM.nextDouble() * 6), particle);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);

        // Create fade pulse
        FadeTransition fade = new FadeTransition(Duration.seconds(3 + RANDOM.nextDouble() * 4), particle);
        double baseOpacity = particle.getOpacity();
        fade.setFromValue(baseOpacity * 0.5);
        fade.setToValue(baseOpacity);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.setInterpolator(Interpolator.EASE_BOTH);

        // Start all animations with small delay for each particle
        PauseTransition delay = new PauseTransition(Duration.millis(index * 50));
        delay.setOnFinished(e -> {
            moveX.play();
            moveY.play();
            scale.play();
            fade.play();
        });
        delay.play();
    }

    /**
     * Creates animations for a shape
     */
    private void animateShape(Polygon shape, int index) {
        // Create rotation
        RotateTransition rotate = new RotateTransition(Duration.seconds(10 + RANDOM.nextDouble() * 20), shape);
        rotate.setByAngle(RANDOM.nextBoolean() ? 360 : -360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);

        // Create scale pulse
        ScaleTransition scale = new ScaleTransition(Duration.seconds(4 + RANDOM.nextDouble() * 8), shape);
        scale.setFromX(0.8);
        scale.setFromY(0.8);
        scale.setToX(1.5);
        scale.setToY(1.5);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);

        // Create movement
        TranslateTransition move = new TranslateTransition(Duration.seconds(15 + RANDOM.nextDouble() * 15), shape);
        move.setFromX(-30 - RANDOM.nextDouble() * 50);
        move.setFromY(-30 - RANDOM.nextDouble() * 50);
        move.setToX(30 + RANDOM.nextDouble() * 50);
        move.setToY(30 + RANDOM.nextDouble() * 50);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);

        // Start all animations with small delay for each shape
        PauseTransition delay = new PauseTransition(Duration.millis(index * 200));
        delay.setOnFinished(e -> {
            rotate.play();
            scale.play();
            move.play();
        });
        delay.play();
    }

    /**
     * Creates animations for a rectangle
     */
    private void animateRectangle(Rectangle rect, int index) {
        // Create rotation
        RotateTransition rotate = new RotateTransition(Duration.seconds(8 + RANDOM.nextDouble() * 12), rect);
        rotate.setByAngle(RANDOM.nextBoolean() ? 360 : -360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.setInterpolator(Interpolator.LINEAR);

        // Create scale pulse
        ScaleTransition scale = new ScaleTransition(Duration.seconds(5 + RANDOM.nextDouble() * 5), rect);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.3);
        scale.setToY(1.3);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);

        // Create fade pulse
        FadeTransition fade = new FadeTransition(Duration.seconds(4 + RANDOM.nextDouble() * 6), rect);
        double baseOpacity = rect.getOpacity();
        fade.setFromValue(baseOpacity * 0.5);
        fade.setToValue(baseOpacity);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setAutoReverse(true);
        fade.setInterpolator(Interpolator.EASE_BOTH);

        // Create movement
        TranslateTransition move = new TranslateTransition(Duration.seconds(10 + RANDOM.nextDouble() * 20), rect);
        move.setFromX(-20 - RANDOM.nextDouble() * 40);
        move.setFromY(-20 - RANDOM.nextDouble() * 40);
        move.setToX(20 + RANDOM.nextDouble() * 40);
        move.setToY(20 + RANDOM.nextDouble() * 40);
        move.setCycleCount(Animation.INDEFINITE);
        move.setAutoReverse(true);
        move.setInterpolator(Interpolator.EASE_BOTH);

        // Start all animations with small delay for each rectangle
        PauseTransition delay = new PauseTransition(Duration.millis(index * 150));
        delay.setOnFinished(e -> {
            rotate.play();
            scale.play();
            fade.play();
            move.play();
        });
        delay.play();
    }
}
