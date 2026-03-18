package com.esprit.controllers.cinemas;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.IntegerRangeValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.services.cinemas.CinemaHallService;
import com.esprit.services.cinemas.CinemaService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class CinemaHallManagementController {

    private static final Logger LOGGER = Logger.getLogger(CinemaHallManagementController.class.getName());
    private final CinemaService cinemaService;
    private final CinemaHallService cinemaHallService;
    private final StringProperty hallNameProperty = new SimpleStringProperty("");
    private final ListProperty<Cinema> cinemaListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Cinema> selectedCinemaProperty = new SimpleObjectProperty<>();
    private final IntegerProperty rowsProperty = new SimpleIntegerProperty(10);
    private final IntegerProperty seatsPerRowProperty = new SimpleIntegerProperty(15);
    private final ListProperty<String> screenTypeListProperty = new SimpleListProperty<>(
        FXCollections.observableArrayList("Standard", "IMAX", "3D", "4DX", "Dolby Atmos", "VIP"));
    private final ObjectProperty<String> selectedScreenTypeProperty = new SimpleObjectProperty<>("Standard");
    private final BooleanProperty isActiveProperty = new SimpleBooleanProperty(true);
    private final StringProperty descriptionProperty = new SimpleStringProperty("");
    // Observable collections
    private final ObservableList<CinemaHall> halls;
    private final ObservableList<Cinema> cinemas;
    // FXML injected fields
    @FXML
    private VBox managementContainer;
    @FXML
    private ComboBox<Cinema> cinemaFilter;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<CinemaHall> hallsTable;
    @FXML
    private TableColumn<CinemaHall, Integer> idColumn;
    @FXML
    private TableColumn<CinemaHall, String> nameColumn;
    @FXML
    private TableColumn<CinemaHall, String> cinemaColumn;
    @FXML
    private TableColumn<CinemaHall, Integer> capacityColumn;
    @FXML
    private TableColumn<CinemaHall, String> screenTypeColumn;
    @FXML
    private TableColumn<CinemaHall, String> statusColumn;
    @FXML
    private Label totalHallsLabel;
    @FXML
    private Label activeHallsLabel;
    @FXML
    private Label totalSeatsLabel;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox formContainer;
    private CinemaHall selectedHall;
    // FormsFX Form and Properties
    private Form hallForm;

    public CinemaHallManagementController() {
        this.cinemaService = new CinemaService();
        this.cinemaHallService = new CinemaHallService();
        this.halls = FXCollections.observableArrayList();
        this.cinemas = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing CinemaHallManagementController with FormsFX");

        setupTable();
        setupFilters();
        setupFormsFX();
        loadCinemas();
        loadHalls();
    }

    /**
     * Creates and configures the FormsFX form for cinema hall editing.
     */
    private void setupFormsFX() {
        // Create the FormsFX form with validation
        hallForm = Form.of(
            Group.of(
                Field.ofStringType(hallNameProperty)
                    .label("Hall Name")
                    .placeholder("Enter hall name")
                    .required("Hall name is required")
                    .validate(StringLengthValidator.atLeast(2, "Name must be at least 2 characters")),

                Field.ofSingleSelectionType(cinemaListProperty, selectedCinemaProperty)
                    .label("Cinema")
                    .required("Please select a cinema")
            ),

            Group.of(
                Field.ofIntegerType(rowsProperty)
                    .label("Number of Rows")
                    .validate(IntegerRangeValidator.between(1, 30, "Rows must be between 1 and 30")),

                Field.ofIntegerType(seatsPerRowProperty)
                    .label("Seats per Row")
                    .validate(IntegerRangeValidator.between(1, 50, "Seats per row must be between 1 and 50"))
            ),

            Group.of(
                Field.ofSingleSelectionType(screenTypeListProperty, selectedScreenTypeProperty)
                    .label("Screen Type"),

                Field.ofBooleanType(isActiveProperty)
                    .label("Active Status")
            ),

            Group.of(
                Field.ofStringType(descriptionProperty)
                    .label("Description")
                    .placeholder("Enter hall description (optional)")
                    .multiline(true)
            )
        ).title("Cinema Hall Details");

        // Render the form into the container
        if (formContainer != null) {
            FormRenderer renderer = new FormRenderer(hallForm);
            renderer.getStyleClass().add("form-renderer");
            formContainer.getChildren().clear();
            formContainer.getChildren().add(renderer);
        }
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getId().intValue()).asObject());
        nameColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getHallName()));
        cinemaColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCinema() != null ?
                cellData.getValue().getCinema().getName() : "N/A"));
        capacityColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getCapacity()).asObject());
        screenTypeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getScreenType()));
        statusColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().isActive() ? "Active" : "Inactive"));

        hallsTable.setItems(halls);

        hallsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedHall = newSel;
                populateForm(newSel);
            }
        });
    }

    private void setupFilters() {
        cinemaFilter.setOnAction(e -> filterHalls());

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> filterHalls());
        }
    }

    private void loadCinemas() {
        new Thread(() -> {
            try {
                List<Cinema> cinemaList = cinemaService.getAllCinemas();

                Platform.runLater(() -> {
                    cinemas.setAll(cinemaList);
                    cinemaListProperty.setAll(cinemaList);

                    Cinema allOption = new Cinema();
                    allOption.setName("All Cinemas");
                    allOption.setId(-1L);

                    cinemaFilter.getItems().clear();
                    cinemaFilter.getItems().add(allOption);
                    cinemaFilter.getItems().addAll(cinemas);
                    cinemaFilter.setValue(allOption);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading cinemas", e);
            }
        }).start();
    }

    private void loadHalls() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<CinemaHall> hallList = cinemaHallService.getAllHalls();

                Platform.runLater(() -> {
                    halls.setAll(hallList);
                    updateStatistics();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading halls", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load cinema halls.");
                });
            }
        }).start();
    }

    private void filterHalls() {
        Cinema selectedCinema = cinemaFilter.getValue();
        String searchText = searchField != null ? searchField.getText().toLowerCase() : "";

        new Thread(() -> {
            try {
                List<CinemaHall> allHalls = cinemaHallService.getAllHalls();

                List<CinemaHall> filtered = allHalls.stream()
                    .filter(hall -> {
                        // Cinema filter
                        if (selectedCinema != null && selectedCinema.getId() != -1) {
                            if (hall.getCinema() == null ||
                                !hall.getCinema().getId().equals(selectedCinema.getId())) {
                                return false;
                            }
                        }

                        // Search filter
                        if (!searchText.isEmpty()) {
                            String hallName = hall.getHallName().toLowerCase();
                            String cinemaName = hall.getCinema() != null ?
                                hall.getCinema().getName().toLowerCase() : "";

                            return hallName.contains(searchText) || cinemaName.contains(searchText);
                        }

                        return true;
                    })
                    .toList();

                Platform.runLater(() -> {
                    halls.setAll(filtered);
                    updateStatistics();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error filtering halls", e);
            }
        }).start();
    }

    /**
     * Populates the FormsFX form with data from the selected hall.
     */
    private void populateForm(CinemaHall hall) {
        hallNameProperty.set(hall.getHallName() != null ? hall.getHallName() : "");
        selectedCinemaProperty.set(hall.getCinema());
        selectedScreenTypeProperty.set(hall.getScreenType() != null ? hall.getScreenType() : "Standard");
        isActiveProperty.set(hall.isActive());
        descriptionProperty.set(hall.getDescription() != null ? hall.getDescription() : "");

        // Calculate rows and seats per row from capacity
        int capacity = hall.getCapacity() != null ? hall.getCapacity() : 150;
        int rows = (int) Math.ceil(capacity / 15.0);
        int seatsPerRow = capacity / Math.max(rows, 1);

        rowsProperty.set(rows);
        seatsPerRowProperty.set(seatsPerRow);
    }

    /**
     * Clears the FormsFX form to default values.
     */
    private void clearForm() {
        selectedHall = null;
        hallNameProperty.set("");
        selectedCinemaProperty.set(null);
        selectedScreenTypeProperty.set("Standard");
        rowsProperty.set(10);
        seatsPerRowProperty.set(15);
        isActiveProperty.set(true);
        descriptionProperty.set("");

        hallsTable.getSelectionModel().clearSelection();

        // Reset form validation state
        if (hallForm != null) {
            hallForm.reset();
        }
    }

    private void updateStatistics() {
        int total = halls.size();
        long active = halls.stream().filter(CinemaHall::isActive).count();
        int totalSeats = halls.stream()
            .filter(h -> h.getCapacity() != null)
            .mapToInt(CinemaHall::getCapacity)
            .sum();

        if (totalHallsLabel != null) totalHallsLabel.setText(String.valueOf(total));
        if (activeHallsLabel != null) activeHallsLabel.setText(String.valueOf(active));
        if (totalSeatsLabel != null) totalSeatsLabel.setText(String.valueOf(totalSeats));
    }

    @FXML
    private void handleAddHall() {
        clearForm();
    }

    @FXML
    private void handleSaveHall() {
        // Use FormsFX validation
        if (!hallForm.isValid()) {
            hallForm.persist();  // This triggers validation display
            showError("Please fix the validation errors before saving.");
            return;
        }

        showLoading(true);

        CinemaHall hall = selectedHall != null ? selectedHall : new CinemaHall();
        hall.setHallName(hallNameProperty.get().trim());
        hall.setCinema(selectedCinemaProperty.get());
        hall.setScreenType(selectedScreenTypeProperty.get());
        hall.setCapacity(rowsProperty.get() * seatsPerRowProperty.get());
        hall.setActive(isActiveProperty.get());
        hall.setDescription(descriptionProperty.get());

        new Thread(() -> {
            try {
                if (selectedHall != null) {
                    cinemaHallService.updateHall(hall);
                } else {
                    cinemaHallService.createHall(hall);
                }

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess(selectedHall != null ? "Hall updated successfully!" : "Hall created successfully!");
                    clearForm();
                    loadHalls();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error saving hall", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to save hall.");
                });
            }
        }).start();
    }

    @FXML
    private void handleDeleteHall() {
        if (selectedHall == null) {
            showError("Please select a hall to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Hall");
        confirm.setHeaderText("Delete \"" + selectedHall.getHallName() + "\"?");
        confirm.setContentText("This will also delete all associated seats and sessions. This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            showLoading(true);

            new Thread(() -> {
                try {
                    cinemaHallService.deleteHall(selectedHall.getId());

                    Platform.runLater(() -> {
                        showLoading(false);
                        showSuccess("Hall deleted successfully!");
                        clearForm();
                        loadHalls();
                    });
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error deleting hall", e);
                    Platform.runLater(() -> {
                        showLoading(false);
                        showError("Failed to delete hall. It may have active sessions.");
                    });
                }
            }).start();
        }
    }

    @FXML
    private void handleManageSeats() {
        if (selectedHall == null) {
            showError("Please select a hall first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/admin/SeatManagement.fxml"));
            Parent root = loader.load();

            SeatManagementController controller = loader.getController();
            controller.setHall(selectedHall);

            Stage stage = (Stage) managementContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/admin.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening seat management", e);
            showError("Could not open seat management.");
        }
    }

    @FXML
    private void handleViewSessions() {
        if (selectedHall == null) {
            showError("Please select a hall first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/admin/MovieSessionManagement.fxml"));
            Parent root = loader.load();

            MovieSessionManagementController controller = loader.getController();
            controller.setFilterHall(selectedHall);

            Stage stage = (Stage) managementContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/ui/styles/admin.css").toExternalForm());
            stage.setScene(scene);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error opening session management", e);
            showError("Could not open session management.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadHalls();
    }

    @FXML
    private void handleClearForm() {
        clearForm();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/users/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) managementContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    // ==================== Dialog methods for FXML compatibility ====================

    @FXML
    private void createNewHall() {
        clearForm();
        showHallDialog();
    }

    @FXML
    private void closeHallDialog() {
        // Find and hide the dialog
        VBox dialog = (VBox) managementContainer.getScene().lookup("#hallDialog");
        if (dialog != null) {
            dialog.setVisible(false);
            dialog.setManaged(false);
        }
    }

    private void showHallDialog() {
        VBox dialog = (VBox) managementContainer.getScene().lookup("#hallDialog");
        if (dialog != null) {
            dialog.setVisible(true);
            dialog.setManaged(true);
        }
    }

    @FXML
    private void saveHall() {
        handleSaveHall();
        closeHallDialog();
    }

    // View toggle methods for FXML compatibility
    @FXML
    private void showGridView() {
        // Grid view logic
    }

    @FXML
    private void showTableView() {
        // Table view logic
    }

    // Delete dialog methods
    @FXML
    private void cancelDelete() {
        VBox deleteDialog = (VBox) managementContainer.getScene().lookup("#deleteDialog");
        if (deleteDialog != null) {
            deleteDialog.setVisible(false);
            deleteDialog.setManaged(false);
        }
    }

    @FXML
    private void confirmDelete() {
        handleDeleteHall();
        cancelDelete();
    }

    // ==================== Utility methods ====================

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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
