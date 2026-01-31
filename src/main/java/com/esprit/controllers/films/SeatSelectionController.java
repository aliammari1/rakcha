package com.esprit.controllers.films;

import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.cinemas.Seat;
import com.esprit.models.users.Client;
import com.esprit.services.cinemas.SeatService;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Log4j2
public class SeatSelectionController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(SeatSelectionController.class.getName());
    private final List<Seat> selectedSeats = new ArrayList<>();
    private final List<Seat> recommendedSeats = new ArrayList<>();
    private final DecimalFormat priceFormat = new DecimalFormat("0.00");
    @FXML
    private GridPane seatGrid;
    @FXML
    private Button confirmButton;
    @FXML
    private Button backButton;
    @FXML
    private Button clearSelectionButton;
    @FXML
    private Button bestSeatsButton;
    @FXML
    private Button centerSeatsButton;
    @FXML
    private Button startTourButton;
    @FXML
    private Button toggle3DViewButton;
    @FXML
    private StackPane theater3DContainer;
    @FXML
    private Label movieInfoLabel;
    @FXML
    private Label sessionDetailsLabel;
    @FXML
    private Label selectedSeatsLabel;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label savingsLabel;
    @FXML
    private Label seatDetailsLabel;
    @FXML
    private VBox seatDetailsPanel;
    @FXML
    private StackPane onboardingOverlay;
    private MovieSession moviesession;
    private Client client;
    private boolean isFirstTime = true;

    // 3D Theater View components
    private Group theater3DRoot;
    private SubScene theater3DSubScene;
    private PerspectiveCamera theater3DCamera;
    private boolean is3DViewActive = false;
    private AnimationTimer cameraAnimation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize UI components
        updateSelectionSummary();
        setupButtonHandlers();

        // Show onboarding for first-time users
        if (isFirstTime && onboardingOverlay != null) {
            Platform.runLater(() -> onboardingOverlay.setVisible(true));
        }
    }

    /**
     * Setup button event handlers
     */
    private void setupButtonHandlers() {
        if (bestSeatsButton != null) {
            bestSeatsButton.setOnAction(e -> selectBestSeats());
        }
        if (centerSeatsButton != null) {
            centerSeatsButton.setOnAction(e -> selectCenterSeats());
        }
        if (clearSelectionButton != null) {
            clearSelectionButton.setOnAction(e -> clearAllSelections());
        }
        if (toggle3DViewButton != null) {
            toggle3DViewButton.setOnAction(e -> toggle3DTheaterView());
        }
    }

    /**
     * Initialize the controller with the specified movie session and client and load the seat layout.
     *
     * @param moviesession the MovieSession whose cinema hall seats should be displayed
     * @param client       the Client performing the selection
     * @throws IllegalArgumentException if {@code moviesession} or {@code client} is {@code null}
     */
    public void initializeWithData(MovieSession moviesession, Client client) {
        if (moviesession == null || client == null) {
            throw new IllegalArgumentException("MovieSession and Client cannot be null");
        }

        this.moviesession = moviesession;
        this.client = client;

        // Update movie info labels
        Platform.runLater(() -> {
            if (movieInfoLabel != null) {
                String movieInfo = String.format("%s • %s • %s",
                    moviesession.getFilm() != null ? moviesession.getFilm().getTitle() : "Movie",
                    moviesession.getCinemaHall() != null ? moviesession.getCinemaHall().getName() : "Cinema Hall",
                    moviesession.getStartTime() != null ? moviesession.getStartTime().toString() : "Showtime"
                );
                movieInfoLabel.setText(movieInfo);
            }

            if (sessionDetailsLabel != null) {
                String details = String.format("Base price: %.2f TND • Duration: %s • Hall capacity: %d seats",
                    moviesession.getPrice() != null ? moviesession.getPrice() : 15.0,
                    moviesession.getFilm() != null ?
                        moviesession.getFilm().getDurationMin() + " min" : "N/A",
                    moviesession.getCinemaHall() != null ?
                        (moviesession.getCinemaHall().getCapacity() != null ? moviesession.getCinemaHall().getCapacity() : 100) : 100
                );
                sessionDetailsLabel.setText(details);
            }

            loadSeats();
        });
    }


    /**
     * Populate the seatGrid with buttons representing seats for the current movie session's cinema hall.
     * <p>
     * Occupied seats are disabled and styled red; available seats are styled green. Clicking a seat
     * toggles its selection state and updates the controller's selectedSeats list.
     */
    private void loadSeats() {
        if (moviesession == null || moviesession.getCinemaHall() == null) {
            return;
        }

        SeatService seatService = new SeatService();
        List<Seat> seats = seatService.getSeatsByCinemaHallId(moviesession.getCinemaHall().getId());

        if (seats.isEmpty()) {
            // Create a default seat layout if no seats are found
            createDefaultSeatLayout();
            return;
        }

        int maxRow = 0;
        int maxCol = 0;
        for (Seat seat : seats) {
            try {
                maxRow = Math.max(maxRow, Integer.parseInt(seat.getRowLabel() != null ? seat.getRowLabel() : "0"));
                maxCol = Math.max(maxCol, Integer.parseInt(seat.getSeatNumber() != null ? seat.getSeatNumber() : "0"));
            } catch (NumberFormatException e) {
                // Handle invalid seat labels gracefully
                continue;
            }
        }

        // Clear existing seats
        seatGrid.getChildren().clear();

        // Add row labels
        for (int row = 0; row < maxRow; row++) {
            Label rowLabel = new Label(String.valueOf((char) ('A' + row)));
            rowLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-weight: bold; -fx-font-size: 14px;");
            rowLabel.setPrefWidth(30);
            seatGrid.add(rowLabel, 0, row + 1);
        }

        // Create seat buttons
        for (int row = 0; row < maxRow; row++) {
            for (int col = 0; col < maxCol; col++) {
                Button seatButton = createSeatButton();

                final Seat currentSeat = findSeat(seats, row + 1, col + 1);
                if (currentSeat != null) {
                    configureSeatButton(seatButton, currentSeat);
                } else {
                    // Create empty space for missing seats
                    seatButton.setVisible(false);
                }

                seatGrid.add(seatButton, col + 1, row + 1);
            }
        }
    }

    /**
     * Creates a default seat layout when no seats are found in the database
     */
    private void createDefaultSeatLayout() {
        seatGrid.getChildren().clear();

        int rows = 8;
        int seatsPerRow = 12;

        for (int row = 0; row < rows; row++) {
            // Add row label
            Label rowLabel = new Label(String.valueOf((char) ('A' + row)));
            rowLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-weight: bold; -fx-font-size: 14px;");
            rowLabel.setPrefWidth(30);
            seatGrid.add(rowLabel, 0, row);

            for (int col = 0; col < seatsPerRow; col++) {
                Button seatButton = createSeatButton();

                // Create a mock seat for demonstration
                Seat mockSeat = new Seat();
                mockSeat.setRowLabel(String.valueOf(row + 1));
                mockSeat.setSeatNumber(String.valueOf(col + 1));
                mockSeat.setIsOccupied(Math.random() < 0.3); // 30% chance of being occupied

                // Set seat type and price multiplier based on row (VIP seats in front rows)
                if (row < 2) {
                    mockSeat.setType("VIP");
                    mockSeat.setPriceMultiplier(1.5); // VIP seats cost 50% more
                } else {
                    mockSeat.setType("STANDARD");
                    mockSeat.setPriceMultiplier(1.0);
                }

                configureSeatButton(seatButton, mockSeat);

                // Add aisle space after seat 6
                int gridCol = col < 6 ? col + 1 : col + 2;
                seatGrid.add(seatButton, gridCol, row);
            }
        }
    }

    /**
     * Creates a styled seat button with enhanced interactions
     */
    private Button createSeatButton() {
        Button seatButton = new Button();
        seatButton.setPrefSize(40, 40);
        seatButton.setMinSize(40, 40);
        seatButton.setMaxSize(40, 40);
        seatButton.getStyleClass().add("animated-button");

        // Add hover effects and tooltips
        seatButton.setOnMouseEntered(e -> {
            if (!seatButton.isDisabled()) {
                seatButton.setScaleX(1.1);
                seatButton.setScaleY(1.1);
            }
        });

        seatButton.setOnMouseExited(e -> {
            seatButton.setScaleX(1.0);
            seatButton.setScaleY(1.0);
        });

        return seatButton;
    }

    /**
     * Configures a seat button with enhanced styling and behavior
     */
    private void configureSeatButton(Button seatButton, Seat seat) {
        // Check if seat is occupied for this specific movie session
        boolean isOccupiedForSession = isSeatOccupiedForSession(seat);
        boolean isRecommended = isRecommendedSeat(seat);

        seatButton.setDisable(isOccupiedForSession);
        seatButton.setUserData(seat);

        if (isOccupiedForSession) {
            // Occupied seat - red with animation
            seatButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #F44336, #D32F2F);" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #B71C1C;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;"
            );
            seatButton.setText("✗");

            // Add tooltip for occupied seats
            Tooltip occupiedTooltip = new Tooltip("This seat is already taken");
            occupiedTooltip.setStyle("-fx-background-color: #F44336; -fx-text-fill: white; -fx-font-size: 12px;");
            Tooltip.install(seatButton, occupiedTooltip);

        } else {
            // Available seat styling based on type and recommendation
            String backgroundColor, borderColor, icon, tooltipText;

            if ("VIP".equals(seat.getType())) {
                backgroundColor = isRecommended ?
                    "linear-gradient(to bottom, #9C27B0, #7B1FA2)" :
                    "linear-gradient(to bottom, #FF9800, #F57C00)";
                borderColor = isRecommended ? "#E1BEE7" : "#FFB74D";
                icon = "★";
                tooltipText = String.format("VIP Seat %s%s • Price: %.2f TND • Premium comfort & service",
                    getRowLetter(seat), seat.getSeatNumber(), calculateSeatPrice(seat));
            } else {
                backgroundColor = isRecommended ?
                    "linear-gradient(to bottom, #9C27B0, #7B1FA2)" :
                    "linear-gradient(to bottom, #4CAF50, #388E3C)";
                borderColor = isRecommended ? "#E1BEE7" : "#81C784";
                icon = isRecommended ? "◆" : "○";
                tooltipText = String.format("%s Seat %s%s • Price: %.2f TND%s",
                    isRecommended ? "Recommended" : "Standard",
                    getRowLetter(seat), seat.getSeatNumber(),
                    calculateSeatPrice(seat),
                    isRecommended ? " • Optimal viewing angle" : "");
            }

            seatButton.setStyle(
                "-fx-background-color: " + backgroundColor + ";" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: " + borderColor + ";" +
                    "-fx-border-width: 2;" +
                    "-fx-border-radius: 12;" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);"
            );
            seatButton.setText(icon);

            // Enhanced tooltip
            Tooltip seatTooltip = new Tooltip(tooltipText);
            seatTooltip.setStyle("-fx-background-color: rgba(0, 0, 0, 0.9); -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-background-radius: 8; -fx-padding: 8;");
            Tooltip.install(seatButton, seatTooltip);

            seatButton.setOnAction(e -> handleSeatSelection(seatButton, seat));

            // Enhanced hover effect with seat details
            seatButton.setOnMouseEntered(e -> {
                showSeatDetails(seat);
                if (!selectedSeats.contains(seat)) {
                    seatButton.setStyle(seatButton.getStyle() +
                        "-fx-effect: dropshadow(gaussian, rgba(255, 255, 255, 0.6), 10, 0, 0, 0);");
                }
            });

            seatButton.setOnMouseExited(e -> {
                hideSeatDetails();
                if (!selectedSeats.contains(seat)) {
                    seatButton.setStyle(seatButton.getStyle().replaceAll(
                        "-fx-effect: dropshadow\\(gaussian, rgba\\(255, 255, 255, 0\\.6\\), 10, 0, 0, 0\\);",
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.3), 5, 0, 0, 2);"));
                }
            });
        }
    }


    /**
     * Finds a specific seat in the list based on row and column coordinates.
     *
     * @param seats the list of seats to search through
     * @param row   the row number to find
     * @param col   the column number to find
     * @return the matching Seat object, or null if no match is found
     */
    private Seat findSeat(List<Seat> seats, int row, int col) {
        return seats.stream().filter(s -> {
            try {
                int seatRow = Integer.parseInt(s.getRowLabel() != null ? s.getRowLabel() : "0");
                int seatCol = Integer.parseInt(s.getSeatNumber() != null ? s.getSeatNumber() : "0");
                return seatRow == row && seatCol == col;
            } catch (NumberFormatException e) {
                return false;
            }
        }).findFirst().orElse(null);
    }


    /**
     * Enhanced seat selection handler with animations and feedback
     *
     * @param seatButton the button representing the seat in the UI
     * @param seat       the Seat model object associated with the button
     */
    private void handleSeatSelection(Button seatButton, Seat seat) {
        if (selectedSeats.contains(seat)) {
            // Deselect seat - restore original styling
            selectedSeats.remove(seat);
            configureSeatButton(seatButton, seat);
        } else {
            // Select seat - use golden highlighting for all selected seats
            selectedSeats.add(seat);
            seatButton.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #FFC107, #FF8F00);" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: #FFD54F;" +
                    "-fx-border-width: 3;" +
                    "-fx-border-radius: 12;" +
                    "-fx-text-fill: #1a0808;" +
                    "-fx-font-size: 14px;" +
                    "-fx-font-weight: bold;" +
                    "-fx-cursor: hand;" +
                    "-fx-effect: dropshadow(gaussian, rgba(255, 193, 7, 0.8), 12, 0, 0, 0);"
            );
            seatButton.setText("✓");

            // Add selection animation
            seatButton.setScaleX(1.2);
            seatButton.setScaleY(1.2);
            Platform.runLater(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(() -> {
                    seatButton.setScaleX(1.0);
                    seatButton.setScaleY(1.0);
                });
            });
        }

        updateSelectionSummary();
    }

    /**
     * Check if a seat is occupied for the current movie session
     */
    private boolean isSeatOccupiedForSession(Seat seat) {
        if (moviesession == null) {
            return seat.getIsOccupied(); // Fallback to general seat status
        }

        // Check if there are any tickets for this seat in this movie session
        return seat.getTickets().stream()
            .anyMatch(ticket -> ticket.getMovieSession() != null &&
                ticket.getMovieSession().getId().equals(moviesession.getId()));
    }

    /**
     * Calculate the price for a specific seat based on movie session price and seat multiplier
     */
    private double calculateSeatPrice(Seat seat) {
        if (moviesession == null || moviesession.getPrice() == null) {
            return 15.0; // Fallback price
        }

        double basePrice = moviesession.getPrice();
        double multiplier = seat.getPriceMultiplier() != null ? seat.getPriceMultiplier() : 1.0;

        return basePrice * multiplier;
    }

    /**
     * Get row letter for display
     */
    private String getRowLetter(Seat seat) {
        try {
            int rowNum = Integer.parseInt(seat.getRowLabel() != null ? seat.getRowLabel() : "1");
            return String.valueOf((char) ('A' + rowNum - 1));
        } catch (NumberFormatException e) {
            return seat.getRowLabel() != null ? seat.getRowLabel() : "?";
        }
    }

    /**
     * Check if a seat is in the recommended zone (center seats, optimal rows)
     */
    private boolean isRecommendedSeat(Seat seat) {
        try {
            int row = Integer.parseInt(seat.getRowLabel() != null ? seat.getRowLabel() : "0");
            int seatNum = Integer.parseInt(seat.getSeatNumber() != null ? seat.getSeatNumber() : "0");

            // Recommended: rows 3-6, center seats (4-9 for 12-seat rows)
            return row >= 3 && row <= 6 && seatNum >= 4 && seatNum <= 9;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Show seat details in the side panel
     */
    private void showSeatDetails(Seat seat) {
        if (seatDetailsPanel != null && seatDetailsLabel != null) {
            Platform.runLater(() -> {
                String details = String.format(
                    "Seat: %s%s\nType: %s\nPrice: %.2f TND\n%s",
                    getRowLetter(seat), seat.getSeatNumber(),
                    "VIP".equals(seat.getType()) ? "VIP Premium" : "Standard",
                    calculateSeatPrice(seat),
                    isRecommendedSeat(seat) ? "✨ Recommended for best view" :
                        "VIP".equals(seat.getType()) ? "👑 Premium comfort & service" : "🎬 Standard cinema experience"
                );
                seatDetailsLabel.setText(details);
                seatDetailsPanel.setVisible(true);
            });
        }
    }

    /**
     * Hide seat details panel
     */
    private void hideSeatDetails() {
        if (seatDetailsPanel != null) {
            Platform.runLater(() -> seatDetailsPanel.setVisible(false));
        }
    }

    /**
     * Select best available seats automatically
     */
    @FXML
    private void selectBestSeats() {
        clearAllSelections();

        // Find recommended seats that are available
        List<Seat> availableRecommended = recommendedSeats.stream()
            .filter(seat -> !isSeatOccupiedForSession(seat))
            .limit(2) // Select up to 2 seats
            .collect(Collectors.toList());

        for (Seat seat : availableRecommended) {
            // Find the button for this seat and simulate selection
            seatGrid.getChildren().stream()
                .filter(node -> node instanceof Button && seat.equals(node.getUserData()))
                .findFirst()
                .ifPresent(node -> handleSeatSelection((Button) node, seat));
        }
    }

    /**
     * Select center seats
     */
    @FXML
    private void selectCenterSeats() {
        clearAllSelections();

        // Find center seats in middle rows
        seatGrid.getChildren().stream()
            .filter(node -> node instanceof Button && node.getUserData() instanceof Seat)
            .map(node -> (Button) node)
            .filter(button -> {
                Seat seat = (Seat) button.getUserData();
                try {
                    int row = Integer.parseInt(seat.getRowLabel() != null ? seat.getRowLabel() : "0");
                    int seatNum = Integer.parseInt(seat.getSeatNumber() != null ? seat.getSeatNumber() : "0");
                    return row >= 4 && row <= 5 && seatNum >= 5 && seatNum <= 8 && !isSeatOccupiedForSession(seat);
                } catch (NumberFormatException e) {
                    return false;
                }
            })
            .limit(2)
            .forEach(button -> handleSeatSelection(button, (Seat) button.getUserData()));
    }

    /**
     * Clear all selected seats
     */
    @FXML
    private void clearAllSelections() {
        List<Seat> toRemove = new ArrayList<>(selectedSeats);
        for (Seat seat : toRemove) {
            // Find the button and restore its original styling
            seatGrid.getChildren().stream()
                .filter(node -> node instanceof Button && seat.equals(node.getUserData()))
                .findFirst()
                .ifPresent(node -> {
                    Button button = (Button) node;
                    selectedSeats.remove(seat);
                    configureSeatButton(button, seat);
                });
        }
        updateSelectionSummary();
    }

    /**
     * Start the onboarding tour
     */
    @FXML
    private void startTour() {
        if (onboardingOverlay != null) {
            onboardingOverlay.setVisible(false);
        }
        isFirstTime = false;

        // Identify recommended seats for highlighting
        identifyRecommendedSeats();
    }

    /**
     * Identify and store recommended seats
     */
    private void identifyRecommendedSeats() {
        recommendedSeats.clear();
        seatGrid.getChildren().stream()
            .filter(node -> node instanceof Button && node.getUserData() instanceof Seat)
            .map(node -> (Seat) node.getUserData())
            .filter(this::isRecommendedSeat)
            .forEach(recommendedSeats::add);
    }

    /**
     * Updates the selection summary labels and confirm button state with enhanced features
     */
    private void updateSelectionSummary() {
        Platform.runLater(() -> {
            if (selectedSeatsLabel != null) {
                if (selectedSeats.isEmpty()) {
                    selectedSeatsLabel.setText("Click seats to select them");
                } else {
                    String seatLabels = selectedSeats.stream()
                        .map(seat -> {
                            String row = getRowLetter(seat);
                            String seatNum = seat.getSeatNumber() != null ? seat.getSeatNumber() : "?";
                            String type = "VIP".equals(seat.getType()) ? " 👑" : "";
                            return row + seatNum + type;
                        })
                        .collect(Collectors.joining(" • "));
                    selectedSeatsLabel.setText(seatLabels);
                }
            }

            if (totalPriceLabel != null) {
                double totalPrice = selectedSeats.stream()
                    .mapToDouble(this::calculateSeatPrice)
                    .sum();
                totalPriceLabel.setText(priceFormat.format(totalPrice) + " TND");

                // Show savings if VIP seats are selected
                long vipCount = selectedSeats.stream()
                    .filter(seat -> "VIP".equals(seat.getType()))
                    .count();

                if (vipCount > 0 && savingsLabel != null) {
                    savingsLabel.setText(String.format("Includes %d VIP seat%s", vipCount, vipCount > 1 ? "s" : ""));
                    savingsLabel.setVisible(true);
                } else if (savingsLabel != null) {
                    savingsLabel.setVisible(false);
                }
            }

            if (confirmButton != null) {
                confirmButton.setDisable(selectedSeats.isEmpty());
            }

            if (clearSelectionButton != null) {
                clearSelectionButton.setVisible(!selectedSeats.isEmpty());
            }
        });
    }


    /**
     * Navigate to the payment screen for the currently selected seats.
     * Enhanced version with proper error handling and user feedback.
     */
    @FXML
    private void confirmSelection(ActionEvent event) {
        if (selectedSeats.isEmpty()) {
            showAlert("No Seats Selected", "Please select at least one seat before proceeding to payment.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/films/Paymentuser.fxml"));
            StackPane root = loader.load();

            PaymentUserController controller = loader.getController();
            controller.initWithSeats(moviesession, client, selectedSeats);

            Stage stage = (Stage) confirmButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Secure Payment - " + moviesession.getFilm().getTitle());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading payment screen", e);
            showAlert("Navigation Error", "Unable to load payment screen. Please try again.");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Unexpected error during navigation", e);
            showAlert("Error", "An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Show an alert dialog with the given title and message
     */
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Clear selection button handler
     */
    @FXML
    private void clearSelection(ActionEvent event) {
        clearAllSelections();
    }

    /**
     * Navigate back to the previous screen
     */
    @FXML
    private void goBack(ActionEvent event) {
        try {
            // Navigate back to movie session browser or previous screen
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close(); // Or navigate to previous screen
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Toggle 3D theater view for immersive seat selection
     */
    @FXML
    private void toggle3DTheaterView() {
        if (is3DViewActive) {
            disable3DTheaterView();
        } else {
            enable3DTheaterView();
        }
    }

    /**
     * Enable 3D theater view with realistic cinema hall visualization
     */
    private void enable3DTheaterView() {
        if (theater3DContainer == null) {
            LOGGER.warning("3D Theater container not found in FXML");
            return;
        }

        try {
            // Create 3D scene components
            theater3DRoot = new Group();
            theater3DCamera = new PerspectiveCamera(true);
            theater3DCamera.setTranslateZ(-600);
            theater3DCamera.setTranslateY(-100);
            theater3DCamera.setNearClip(0.1);
            theater3DCamera.setFarClip(2000.0);

            // Create 3D theater environment
            create3DTheaterEnvironment();
            create3DSeats();
            create3DScreen();

            // Create SubScene for 3D content
            theater3DSubScene = new SubScene(theater3DRoot, 800, 600, true, SceneAntialiasing.BALANCED);
            theater3DSubScene.setFill(Color.BLACK);
            theater3DSubScene.setCamera(theater3DCamera);

            // Add mouse controls for camera
            addCameraControls();

            // Add to container
            theater3DContainer.getChildren().clear();
            theater3DContainer.getChildren().add(theater3DSubScene);
            theater3DContainer.setVisible(true);

            is3DViewActive = true;
            if (toggle3DViewButton != null) {
                toggle3DViewButton.setText("Exit 3D View");
            }

            LOGGER.info("3D Theater view enabled");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error enabling 3D theater view", e);
            showAlert("3D Theater View Error", "Unable to load 3D theater view: " + e.getMessage());
        }
    }

    /**
     * Create 3D theater environment (walls, ceiling, floor)
     */
    private void create3DTheaterEnvironment() {
        // Floor
        Box floor = new Box(800, 5, 600);
        PhongMaterial floorMaterial = new PhongMaterial();
        floorMaterial.setDiffuseColor(Color.DARKRED);
        floor.setMaterial(floorMaterial);
        floor.setTranslateY(200);

        // Ceiling
        Box ceiling = new Box(800, 5, 600);
        PhongMaterial ceilingMaterial = new PhongMaterial();
        ceilingMaterial.setDiffuseColor(Color.BLACK);
        ceiling.setMaterial(ceilingMaterial);
        ceiling.setTranslateY(-200);

        // Side walls
        Box leftWall = new Box(5, 400, 600);
        Box rightWall = new Box(5, 400, 600);
        PhongMaterial wallMaterial = new PhongMaterial();
        wallMaterial.setDiffuseColor(Color.DARKSLATEGRAY);
        leftWall.setMaterial(wallMaterial);
        rightWall.setMaterial(wallMaterial);
        leftWall.setTranslateX(-400);
        rightWall.setTranslateX(400);

        theater3DRoot.getChildren().addAll(floor, ceiling, leftWall, rightWall);
    }

    /**
     * Create 3D representation of cinema seats
     */
    private void create3DSeats() {
        if (moviesession == null || moviesession.getCinemaHall() == null) {
            create3DDefaultSeats();
            return;
        }

        SeatService seatService = new SeatService();
        List<Seat> seats = seatService.getSeatsByCinemaHallId(moviesession.getCinemaHall().getId());

        if (seats.isEmpty()) {
            create3DDefaultSeats();
            return;
        }

        // Create 3D seats based on actual seat data
        for (Seat seat : seats) {
            create3DSeat(seat);
        }
    }

    /**
     * Create default 3D seats when no seat data is available
     */
    private void create3DDefaultSeats() {
        int rows = 8;
        int seatsPerRow = 12;
        double seatWidth = 40;
        double seatDepth = 40;
        double seatHeight = 35;
        double rowSpacing = 60;
        double seatSpacing = 45;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < seatsPerRow; col++) {
                // Skip aisle space
                if (col == 6) continue;

                // Create seat base
                Box seatBase = new Box(seatWidth, seatHeight * 0.3, seatDepth);
                Box seatBack = new Box(seatWidth, seatHeight * 0.7, seatDepth * 0.2);

                // Position seat back
                seatBack.setTranslateY(-seatHeight * 0.5);
                seatBack.setTranslateZ(-seatDepth * 0.4);

                Group seatGroup = new Group(seatBase, seatBack);

                // Position in theater
                double x = (col - seatsPerRow / 2.0) * seatSpacing;
                if (col > 6) x += seatSpacing; // Account for aisle
                double z = (row - rows / 2.0) * rowSpacing;

                seatGroup.setTranslateX(x);
                seatGroup.setTranslateZ(z);
                seatGroup.setTranslateY(100);

                // Color based on availability (mock data)
                PhongMaterial seatMaterial = new PhongMaterial();
                boolean isOccupied = Math.random() < 0.3;
                if (isOccupied) {
                    seatMaterial.setDiffuseColor(Color.DARKRED);
                } else {
                    seatMaterial.setDiffuseColor(Color.DARKGREEN);
                }
                seatMaterial.setSpecularColor(Color.WHITE);

                seatBase.setMaterial(seatMaterial);
                seatBack.setMaterial(seatMaterial);

                // Add hover effects
                if (!isOccupied) {
                    seatGroup.setOnMouseEntered(e -> {
                        seatMaterial.setDiffuseColor(Color.GOLD);
                        seatGroup.setScaleX(1.1);
                        seatGroup.setScaleY(1.1);
                        seatGroup.setScaleZ(1.1);
                    });

                    seatGroup.setOnMouseExited(e -> {
                        seatMaterial.setDiffuseColor(Color.DARKGREEN);
                        seatGroup.setScaleX(1.0);
                        seatGroup.setScaleY(1.0);
                        seatGroup.setScaleZ(1.0);
                    });

                    final int finalRow = row;
                    final int finalCol = col;
                    seatGroup.setOnMouseClicked(e -> {
                        seatMaterial.setDiffuseColor(Color.ORANGE);
                        showAlert("Seat Selected", String.format("Selected seat at Row %d, Seat %d", finalRow + 1, finalCol + 1));
                    });
                }

                theater3DRoot.getChildren().add(seatGroup);
            }
        }
    }

    /**
     * Create 3D representation of a specific seat
     */
    private void create3DSeat(Seat seat) {
        try {
            int row = Integer.parseInt(seat.getRowLabel() != null ? seat.getRowLabel() : "1");
            int seatNum = Integer.parseInt(seat.getSeatNumber() != null ? seat.getSeatNumber() : "1");

            // Create 3D seat geometry
            Box seatBase = new Box(35, 10, 35);
            Box seatBack = new Box(35, 25, 8);
            Cylinder armRest1 = new Cylinder(3, 20);
            Cylinder armRest2 = new Cylinder(3, 20);

            // Position components
            seatBack.setTranslateY(-17.5);
            seatBack.setTranslateZ(-13.5);
            armRest1.setTranslateX(-19);
            armRest1.setTranslateY(-10);
            armRest2.setTranslateX(19);
            armRest2.setTranslateY(-10);

            Group seatGroup = new Group(seatBase, seatBack, armRest1, armRest2);

            // Position in theater
            double x = (seatNum - 6.5) * 45;
            double z = (row - 4.5) * 60;
            seatGroup.setTranslateX(x);
            seatGroup.setTranslateZ(z);
            seatGroup.setTranslateY(100);

            // Apply materials based on seat status
            PhongMaterial seatMaterial = new PhongMaterial();
            boolean isOccupied = isSeatOccupiedForSession(seat);
            boolean isSelected = selectedSeats.contains(seat);

            if (isSelected) {
                seatMaterial.setDiffuseColor(Color.GOLD);
            } else if (isOccupied) {
                seatMaterial.setDiffuseColor(Color.DARKRED);
            } else if ("VIP".equals(seat.getType())) {
                seatMaterial.setDiffuseColor(Color.PURPLE);
            } else {
                seatMaterial.setDiffuseColor(Color.DARKGREEN);
            }

            seatMaterial.setSpecularColor(Color.WHITE);
            seatBase.setMaterial(seatMaterial);
            seatBack.setMaterial(seatMaterial);
            armRest1.setMaterial(seatMaterial);
            armRest2.setMaterial(seatMaterial);

            // Add interaction if seat is available
            if (!isOccupied) {
                seatGroup.setOnMouseClicked(e -> handle3DSeatSelection(seat, seatGroup));
            }

            theater3DRoot.getChildren().add(seatGroup);

        } catch (NumberFormatException e) {
            LOGGER.warning("Invalid seat position data for seat: " + seat.getId());
        }
    }

    /**
     * Handle seat selection in 3D view
     */
    private void handle3DSeatSelection(Seat seat, Group seatGroup) {
        if (selectedSeats.contains(seat)) {
            // Deselect
            selectedSeats.remove(seat);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor("VIP".equals(seat.getType()) ? Color.PURPLE : Color.DARKGREEN);
            material.setSpecularColor(Color.WHITE);
            seatGroup.getChildren().forEach(node -> {
                if (node instanceof Box || node instanceof Cylinder) {
                    ((javafx.scene.shape.Shape3D) node).setMaterial(material);
                }
            });
        } else {
            // Select
            selectedSeats.add(seat);
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(Color.GOLD);
            material.setSpecularColor(Color.WHITE);
            seatGroup.getChildren().forEach(node -> {
                if (node instanceof Box || node instanceof Cylinder) {
                    ((javafx.scene.shape.Shape3D) node).setMaterial(material);
                }
            });
        }

        updateSelectionSummary();

        // Also update 2D view if visible
        Platform.runLater(() -> {
            seatGrid.getChildren().stream()
                .filter(node -> node instanceof Button && seat.equals(node.getUserData()))
                .findFirst()
                .ifPresent(node -> configureSeatButton((Button) node, seat));
        });
    }

    /**
     * Create 3D movie screen
     */
    private void create3DScreen() {
        // Main screen
        Box screen = new Box(300, 180, 5);
        PhongMaterial screenMaterial = new PhongMaterial();
        screenMaterial.setDiffuseColor(Color.WHITE);
        screenMaterial.setSpecularColor(Color.LIGHTGRAY);
        screen.setMaterial(screenMaterial);
        screen.setTranslateZ(-250);
        screen.setTranslateY(-50);

        // Screen frame
        Box frame = new Box(320, 200, 8);
        PhongMaterial frameMaterial = new PhongMaterial();
        frameMaterial.setDiffuseColor(Color.BLACK);
        frame.setMaterial(frameMaterial);
        frame.setTranslateZ(-253);
        frame.setTranslateY(-50);

        theater3DRoot.getChildren().addAll(frame, screen);
    }

    /**
     * Add mouse controls for 3D camera navigation
     */
    private void addCameraControls() {
        if (theater3DSubScene == null) return;

        final double[] mousePos = new double[2];
        final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);

        theater3DCamera.getTransforms().addAll(rotateX, rotateY);

        theater3DSubScene.setOnMousePressed(event -> {
            mousePos[0] = event.getSceneX();
            mousePos[1] = event.getSceneY();
        });

        theater3DSubScene.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mousePos[0];
            double deltaY = event.getSceneY() - mousePos[1];

            rotateY.setAngle(rotateY.getAngle() + deltaX * 0.5);
            rotateX.setAngle(rotateX.getAngle() - deltaY * 0.5);

            mousePos[0] = event.getSceneX();
            mousePos[1] = event.getSceneY();
        });

        theater3DSubScene.setOnScroll(event -> {
            double delta = event.getDeltaY();
            theater3DCamera.setTranslateZ(theater3DCamera.getTranslateZ() + delta * 2);
        });
    }

    /**
     * Disable 3D theater view
     */
    private void disable3DTheaterView() {
        try {
            if (cameraAnimation != null) {
                cameraAnimation.stop();
                cameraAnimation = null;
            }

            if (theater3DContainer != null) {
                theater3DContainer.getChildren().clear();
                theater3DContainer.setVisible(false);
            }

            is3DViewActive = false;
            if (toggle3DViewButton != null) {
                toggle3DViewButton.setText("3D Theater View");
            }

            LOGGER.info("3D Theater view disabled");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error disabling 3D theater view", e);
        }
    }

}
