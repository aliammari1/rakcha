package com.esprit.controllers.cinemas;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.DoubleRangeValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.Seat;
import com.esprit.services.cinemas.CinemaHallService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.SeatService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class SeatManagementController {

    private static final Logger LOGGER = Logger.getLogger(SeatManagementController.class.getName());
    private final SeatService seatService;
    private final CinemaHallService hallService;
    private final CinemaService cinemaService;
    private final ListProperty<String> seatTypeListProperty = new SimpleListProperty<>(
        FXCollections.observableArrayList("Standard", "Premium", "VIP", "Wheelchair Accessible", "Companion"));
    private final ObjectProperty<String> selectedSeatTypeProperty = new SimpleObjectProperty<>("Standard");
    private final DoubleProperty priceMultiplierProperty = new SimpleDoubleProperty(1.0);
    private final BooleanProperty isActiveProperty = new SimpleBooleanProperty(true);
    private final BooleanProperty isAccessibleProperty = new SimpleBooleanProperty(false);
    private final ObservableList<Seat> seats;
    private final List<Button> selectedSeatButtons = new ArrayList<>();
    @FXML
    private VBox managementContainer;
    @FXML
    private ComboBox<Cinema> cinemaCombo;
    @FXML
    private ComboBox<CinemaHall> hallCombo;
    @FXML
    private Label totalSeatsLabel;
    @FXML
    private Label regularSeatsLabel;
    @FXML
    private Label vipSeatsLabel;
    @FXML
    private Label accessibleSeatsLabel;
    @FXML
    private Label disabledSeatsLabel;
    @FXML
    private GridPane seatGrid;
    @FXML
    private VBox seatConfigFormContainer;
    @FXML
    private Rectangle screenIndicator;
    @FXML
    private RadioButton singleSelectMode;
    @FXML
    private RadioButton rectangleSelectMode;
    @FXML
    private RadioButton rowSelectMode;
    @FXML
    private ToggleGroup selectionModeGroup;
    @FXML
    private Spinner<Integer> seatsPerRowSpinner;
    @FXML
    private Spinner<Integer> aisleAfterSpinner;
    @FXML
    private VBox templateDialog;
    @FXML
    private VBox confirmDialog;
    @FXML
    private ProgressIndicator loadingIndicator;
    private CinemaHall currentHall;
    private Seat selectedSeat;
    private boolean isMultiSelectMode = false;
    // FormsFX Form and Properties
    private Form seatConfigForm;

    public SeatManagementController() {
        this.seatService = new SeatService();
        this.hallService = new CinemaHallService();
        this.cinemaService = new CinemaService();
        this.seats = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing SeatManagementController with FormsFX");

        setupSpinners();
        setupFormsFX();
        setupCombos();

        // Default selection mode
        if (singleSelectMode != null) {
            singleSelectMode.setSelected(true);
        }
    }

    /**
     * Sets the hall to manage seats for.
     */
    public void setHall(CinemaHall hall) {
        this.currentHall = hall;
        if (hallCombo != null && hall != null) {
            hallCombo.setValue(hall);
        }
        loadSeats();
    }

    private void setupSpinners() {
        if (seatsPerRowSpinner != null) {
            seatsPerRowSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 15));
        }
        if (aisleAfterSpinner != null) {
            aisleAfterSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 5));
        }
    }

    private void setupCombos() {
        // Load cinemas
        new Thread(() -> {
            try {
                List<Cinema> cinemaList = cinemaService.getAllCinemas();
                Platform.runLater(() -> {
                    if (cinemaCombo != null) {
                        cinemaCombo.getItems().setAll(cinemaList);
                        cinemaCombo.setOnAction(e -> loadHallsForCinema(cinemaCombo.getValue()));
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading cinemas", e);
            }
        }).start();
    }

    private void loadHallsForCinema(Cinema cinema) {
        if (cinema == null) return;

        new Thread(() -> {
            try {
                List<CinemaHall> hallList = hallService.getHallsByCinema(cinema.getId());
                Platform.runLater(() -> {
                    if (hallCombo != null) {
                        hallCombo.getItems().setAll(hallList);
                        hallCombo.setOnAction(e -> {
                            currentHall = hallCombo.getValue();
                            if (currentHall != null) {
                                loadSeats();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading halls", e);
            }
        }).start();
    }

    /**
     * Creates and configures the FormsFX form for seat configuration.
     */
    private void setupFormsFX() {
        seatConfigForm = Form.of(
            Group.of(
                Field.ofSingleSelectionType(seatTypeListProperty, selectedSeatTypeProperty)
                    .label("Seat Type"),

                Field.ofDoubleType(priceMultiplierProperty)
                    .label("Price Multiplier")
                    .validate(DoubleRangeValidator.between(0.5, 3.0, "Multiplier must be between 0.5 and 3.0")),

                Field.ofBooleanType(isActiveProperty)
                    .label("Active"),

                Field.ofBooleanType(isAccessibleProperty)
                    .label("Accessible")
            )
        ).title("Edit Seat");

        // Render the form into the container
        if (seatConfigFormContainer != null) {
            FormRenderer renderer = new FormRenderer(seatConfigForm);
            renderer.getStyleClass().add("form-renderer");
            seatConfigFormContainer.getChildren().clear();
            seatConfigFormContainer.getChildren().add(renderer);
        }
    }

    private void loadSeats() {
        if (currentHall == null) return;

        showLoading(true);

        new Thread(() -> {
            try {
                List<Seat> seatList = seatService.getSeatsByHall(currentHall.getId());

                Platform.runLater(() -> {
                    seats.setAll(seatList);
                    displaySeatMap();
                    updateStatistics();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading seats", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load seats.");
                });
            }
        }).start();
    }

    private void displaySeatMap() {
        seatGrid.getChildren().clear();
        seatGrid.getColumnConstraints().clear();
        seatGrid.getRowConstraints().clear();

        if (seats.isEmpty()) {
            Label emptyLabel = new Label("No seats configured. Select a hall and apply a template.");
            emptyLabel.getStyleClass().add("empty-state-label");
            seatGrid.add(emptyLabel, 0, 0);
            return;
        }

        // Find max row and column
        int maxRow = seats.stream().mapToInt(s -> getRowNumber(s.getRowLabel())).max().orElse(0);
        int maxCol = seats.stream().mapToInt(s -> {
            try {
                return Integer.parseInt(s.getSeatNumber());
            } catch (NumberFormatException e) {
                return 0;
            }
        }).max().orElse(0);

        // Update screen indicator width
        if (screenIndicator != null) {
            screenIndicator.setWidth(Math.min(maxCol * 35 + 100, 600));
        }

        // Add column headers
        for (int col = 1; col <= maxCol; col++) {
            Label colLabel = new Label(String.valueOf(col));
            colLabel.getStyleClass().add("seat-header");
            colLabel.setMinWidth(35);
            colLabel.setAlignment(Pos.CENTER);
            seatGrid.add(colLabel, col, 0);
        }

        // Add row headers and seats
        for (Seat seat : seats) {
            int row = getRowNumber(seat.getRowLabel());
            int col;
            try {
                col = Integer.parseInt(seat.getSeatNumber());
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid seat number format: " + seat.getSeatNumber());
                col = 0;
            }

            // Row header (only once per row)
            if (seatGrid.getChildren().stream().noneMatch(node ->
                GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == 0)) {
                Label rowLabel = new Label(seat.getRowLabel());
                rowLabel.getStyleClass().add("seat-header");
                rowLabel.setMinWidth(35);
                rowLabel.setAlignment(Pos.CENTER);
                seatGrid.add(rowLabel, 0, row);
            }

            // Seat button
            Button seatBtn = createSeatButton(seat);
            seatGrid.add(seatBtn, col, row);
        }
    }

    private Button createSeatButton(Seat seat) {
        Button btn = new Button(seat.getRowLabel() + seat.getSeatNumber());
        btn.setMinSize(35, 35);
        btn.setMaxSize(35, 35);
        btn.getStyleClass().add("seat-button");

        // Style based on seat type
        String seatType = seat.getSeatType() != null ? seat.getSeatType().toLowerCase() : "standard";
        btn.getStyleClass().add("seat-" + seatType.replace(" ", "-"));

        if (!seat.isActive()) {
            btn.getStyleClass().add("seat-disabled");
        }

        if (seat.isAccessible()) {
            btn.getStyleClass().add("seat-accessible");
        }

        btn.setOnAction(e -> {
            if (isMultiSelectMode) {
                toggleSeatSelection(btn, seat);
            } else {
                selectSeat(seat, btn);
            }
        });

        // Tooltip
        Tooltip tooltip = new Tooltip(String.format(
            "Seat %s%s\nType: %s\nPrice Multiplier: %.1fx\nStatus: %s",
            seat.getRowLabel(), seat.getSeatNumber(),
            seat.getSeatType(),
            seat.getPriceMultiplier(),
            seat.isActive() ? "Active" : "Disabled"
        ));
        Tooltip.install(btn, tooltip);

        return btn;
    }

    private int getRowNumber(String rowLabel) {
        if (rowLabel == null || rowLabel.isEmpty()) return 1;
        return rowLabel.charAt(0) - 'A' + 1;
    }

    /**
     * Selects a seat and populates the FormsFX form.
     */
    private void selectSeat(Seat seat, Button btn) {
        // Clear previous selection visual
        selectedSeatButtons.forEach(b -> b.getStyleClass().remove("seat-selected"));
        selectedSeatButtons.clear();

        selectedSeat = seat;
        selectedSeatButtons.add(btn);
        btn.getStyleClass().add("seat-selected");

        // Populate FormsFX form
        selectedSeatTypeProperty.set(seat.getSeatType() != null ? seat.getSeatType() : "Standard");
        priceMultiplierProperty.set(seat.getPriceMultiplier());
        isActiveProperty.set(seat.isActive());
        isAccessibleProperty.set(seat.isAccessible());
    }

    private void toggleSeatSelection(Button btn, Seat seat) {
        if (selectedSeatButtons.contains(btn)) {
            selectedSeatButtons.remove(btn);
            btn.getStyleClass().remove("seat-selected");
        } else {
            selectedSeatButtons.add(btn);
            btn.getStyleClass().add("seat-selected");
        }
    }

    private void updateStatistics() {
        int total = seats.size();
        long regular = seats.stream().filter(s -> "Standard".equalsIgnoreCase(s.getSeatType())).count();
        long vip = seats.stream().filter(s -> "VIP".equalsIgnoreCase(s.getSeatType()) || "Premium".equalsIgnoreCase(s.getSeatType())).count();
        long accessible = seats.stream().filter(Seat::isAccessible).count();
        long disabled = seats.stream().filter(s -> !s.isActive()).count();

        if (totalSeatsLabel != null) totalSeatsLabel.setText(String.valueOf(total));
        if (regularSeatsLabel != null) regularSeatsLabel.setText(String.valueOf(regular));
        if (vipSeatsLabel != null) vipSeatsLabel.setText(String.valueOf(vip));
        if (accessibleSeatsLabel != null) accessibleSeatsLabel.setText(String.valueOf(accessible));
        if (disabledSeatsLabel != null) disabledSeatsLabel.setText(String.valueOf(disabled));
    }

    // ==================== Type Assignment Buttons ====================

    @FXML
    private void setRegularType() {
        applyTypeToSelected("Standard", 1.0);
    }

    @FXML
    private void setVIPType() {
        applyTypeToSelected("VIP", 1.5);
    }

    @FXML
    private void setAccessibleType() {
        if (selectedSeatButtons.isEmpty()) {
            showError("No seats selected.");
            return;
        }

        for (Button btn : selectedSeatButtons) {
            Seat seat = findSeatByButton(btn);
            if (seat != null) {
                seat.setAccessible(true);
                saveSeat(seat);
            }
        }
        loadSeats();
    }

    @FXML
    private void setDisabledType() {
        if (selectedSeatButtons.isEmpty()) {
            showError("No seats selected.");
            return;
        }

        for (Button btn : selectedSeatButtons) {
            Seat seat = findSeatByButton(btn);
            if (seat != null) {
                seat.setActive(false);
                saveSeat(seat);
            }
        }
        loadSeats();
    }

    private void applyTypeToSelected(String type, double multiplier) {
        if (selectedSeatButtons.isEmpty()) {
            showError("No seats selected.");
            return;
        }

        for (Button btn : selectedSeatButtons) {
            Seat seat = findSeatByButton(btn);
            if (seat != null) {
                seat.setSeatType(type);
                seat.setPriceMultiplier(multiplier);
                seat.setActive(true);
                saveSeat(seat);
            }
        }
        loadSeats();
    }

    private Seat findSeatByButton(Button btn) {
        String text = btn.getText();
        return seats.stream()
            .filter(s -> (s.getRowLabel() + s.getSeatNumber()).equals(text))
            .findFirst()
            .orElse(null);
    }

    private void saveSeat(Seat seat) {
        try {
            seatService.updateSeat(seat);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error saving seat", e);
        }
    }

    // ==================== Row Operations ====================

    @FXML
    private void addRow() {
        if (currentHall == null) {
            showError("Please select a hall first.");
            return;
        }

        int seatsPerRow = seatsPerRowSpinner != null ? seatsPerRowSpinner.getValue() : 15;
        int maxRow = seats.stream().mapToInt(s -> getRowNumber(s.getRowLabel())).max().orElse(0);
        String newRowLabel = String.valueOf((char) ('A' + maxRow));

        new Thread(() -> {
            try {
                for (int i = 1; i <= seatsPerRow; i++) {
                    Seat seat = new Seat();
                    seat.setCinemaHall(currentHall);
                    seat.setRowLabel(newRowLabel);
                    seat.setSeatNumber(String.valueOf(i));
                    seat.setSeatType("Standard");
                    seat.setPriceMultiplier(1.0);
                    seat.setActive(true);
                    seatService.createSeat(seat);
                }

                Platform.runLater(() -> {
                    showSuccess("Row " + newRowLabel + " added!");
                    loadSeats();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error adding row", e);
                Platform.runLater(() -> showError("Failed to add row."));
            }
        }).start();
    }

    @FXML
    private void removeRow() {
        if (seats.isEmpty()) return;

        int maxRow = seats.stream().mapToInt(s -> getRowNumber(s.getRowLabel())).max().orElse(0);
        String lastRowLabel = String.valueOf((char) ('A' + maxRow - 1));

        List<Seat> rowSeats = seats.stream()
            .filter(s -> s.getRowLabel().equals(lastRowLabel))
            .toList();

        if (rowSeats.isEmpty()) return;

        new Thread(() -> {
            try {
                for (Seat seat : rowSeats) {
                    seatService.deleteSeat(seat.getId());
                }

                Platform.runLater(() -> {
                    showSuccess("Row " + lastRowLabel + " removed!");
                    loadSeats();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error removing row", e);
                Platform.runLater(() -> showError("Failed to remove row."));
            }
        }).start();
    }

    @FXML
    private void applySeatsPerRow() {
        showInfo("Seats per row will be applied when adding new rows.");
    }

    @FXML
    private void addAisle() {
        showInfo("Aisle management coming soon!");
    }

    // ==================== Quick Actions ====================

    @FXML
    private void selectAll() {
        isMultiSelectMode = true;
        selectedSeatButtons.clear();

        seatGrid.getChildren().stream()
            .filter(node -> node instanceof Button)
            .map(node -> (Button) node)
            .forEach(btn -> {
                if (!btn.getText().matches("\\d+") && !btn.getText().matches("[A-Z]")) {
                    selectedSeatButtons.add(btn);
                    btn.getStyleClass().add("seat-selected");
                }
            });

        showInfo("Selected " + selectedSeatButtons.size() + " seats.");
    }

    @FXML
    private void clearSelection() {
        selectedSeatButtons.forEach(btn -> btn.getStyleClass().remove("seat-selected"));
        selectedSeatButtons.clear();
        selectedSeat = null;
        isMultiSelectMode = false;
    }

    @FXML
    private void resetLayout() {
        if (confirmDialog != null) {
            confirmDialog.setVisible(true);
            confirmDialog.setManaged(true);
        }
    }

    @FXML
    private void cancelReset() {
        if (confirmDialog != null) {
            confirmDialog.setVisible(false);
            confirmDialog.setManaged(false);
        }
    }

    @FXML
    private void confirmReset() {
        if (currentHall == null) {
            showError("No hall selected.");
            cancelReset();
            return;
        }

        showLoading(true);

        new Thread(() -> {
            try {
                for (Seat seat : seats) {
                    seatService.deleteSeat(seat.getId());
                }

                Platform.runLater(() -> {
                    showLoading(false);
                    cancelReset();
                    showSuccess("Layout reset!");
                    loadSeats();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error resetting layout", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    cancelReset();
                    showError("Failed to reset layout.");
                });
            }
        }).start();
    }

    // ==================== Template Operations ====================

    @FXML
    private void saveLayout() {
        showSuccess("Layout saved!");
    }

    @FXML
    private void applyTemplate() {
        if (templateDialog != null) {
            templateDialog.setVisible(true);
            templateDialog.setManaged(true);
        }
    }

    @FXML
    private void closeTemplateDialog() {
        if (templateDialog != null) {
            templateDialog.setVisible(false);
            templateDialog.setManaged(false);
        }
    }

    @FXML
    private void selectStandardTemplate() {
        generateSeats(10, 15, "standard");
        closeTemplateDialog();
    }

    @FXML
    private void selectPremiumTemplate() {
        generateSeats(8, 12, "premium");
        closeTemplateDialog();
    }

    @FXML
    private void selectIMAXTemplate() {
        generateSeats(15, 20, "imax");
        closeTemplateDialog();
    }

    @FXML
    private void selectBoutiqueTemplate() {
        generateSeats(5, 10, "boutique");
        closeTemplateDialog();
    }

    private void generateSeats(int rows, int seatsPerRow, String template) {
        if (currentHall == null) {
            showError("Please select a hall first.");
            return;
        }

        showLoading(true);

        new Thread(() -> {
            try {
                // Delete existing seats
                for (Seat seat : seats) {
                    seatService.deleteSeat(seat.getId());
                }

                // Generate new seats
                List<Seat> newSeats = new ArrayList<>();
                for (int row = 0; row < rows; row++) {
                    String rowLabel = String.valueOf((char) ('A' + row));

                    for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                        Seat seat = new Seat();
                        seat.setCinemaHall(currentHall);
                        seat.setRowLabel(rowLabel);
                        seat.setSeatNumber(String.valueOf(seatNum));

                        // Determine seat type based on template and position
                        switch (template) {
                            case "premium":
                                if (row >= rows - 2) {
                                    seat.setSeatType("VIP");
                                    seat.setPriceMultiplier(2.0);
                                } else {
                                    seat.setSeatType("Premium");
                                    seat.setPriceMultiplier(1.5);
                                }
                                break;
                            case "imax":
                                if (row >= rows - 3) {
                                    seat.setSeatType("Premium");
                                    seat.setPriceMultiplier(1.5);
                                } else {
                                    seat.setSeatType("Standard");
                                    seat.setPriceMultiplier(1.0);
                                }
                                break;
                            case "boutique":
                                seat.setSeatType("VIP");
                                seat.setPriceMultiplier(2.5);
                                break;
                            default: // standard
                                if (row >= rows - 2) {
                                    seat.setSeatType("Premium");
                                    seat.setPriceMultiplier(1.5);
                                } else {
                                    seat.setSeatType("Standard");
                                    seat.setPriceMultiplier(1.0);
                                }
                        }

                        seat.setActive(true);
                        seat.setAccessible(row == 0 && (seatNum == 1 || seatNum == seatsPerRow));

                        seatService.createSeat(seat);
                        newSeats.add(seat);
                    }
                }

                // Update hall capacity
                currentHall.setCapacity(rows * seatsPerRow);
                hallService.updateHall(currentHall);

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess("Generated " + newSeats.size() + " seats using " + template + " template!");
                    loadSeats();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error generating seats", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to generate seats.");
                });
            }
        }).start();
    }

    // ==================== Utility Methods ====================

    private void showLoading(boolean show) {
        if (loadingIndicator != null) loadingIndicator.setVisible(show);
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
