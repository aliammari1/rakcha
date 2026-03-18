package com.esprit.controllers.cinemas;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.DoubleRangeValidator;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Film;
import com.esprit.services.cinemas.CinemaHallService;
import com.esprit.services.cinemas.CinemaService;
import com.esprit.services.cinemas.MovieSessionService;
import com.esprit.services.films.FilmService;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Log4j2
public class MovieSessionManagementController {

    private static final Logger LOGGER = Logger.getLogger(MovieSessionManagementController.class.getName());
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final MovieSessionService sessionService;
    private final CinemaService cinemaService;
    private final CinemaHallService hallService;
    private final FilmService filmService;
    private final ListProperty<Film> filmListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<Film> selectedFilmProperty = new SimpleObjectProperty<>();
    private final ListProperty<CinemaHall> hallListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<CinemaHall> selectedHallProperty = new SimpleObjectProperty<>();
    private final ListProperty<String> timeSlotListProperty = new SimpleListProperty<>(FXCollections.observableArrayList());
    private final ObjectProperty<String> selectedTimeSlotProperty = new SimpleObjectProperty<>("14:00");
    private final ObjectProperty<LocalDate> sessionDateProperty = new SimpleObjectProperty<>(LocalDate.now().plusDays(1));
    private final DoubleProperty basePriceProperty = new SimpleDoubleProperty(12.0);
    private final DoubleProperty vipPriceProperty = new SimpleDoubleProperty(18.0);
    private final BooleanProperty is3DProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty isIMAXProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty isDolbyProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty isSubtitledProperty = new SimpleBooleanProperty(false);
    private final ObservableList<MovieSession> sessions;
    private final ObservableList<Cinema> cinemas;
    private final ObservableList<CinemaHall> halls;
    private final ObservableList<Film> films;
    @FXML
    private VBox managementContainer;
    @FXML
    private ComboBox<Cinema> cinemaFilter;
    @FXML
    private ComboBox<CinemaHall> hallFilter;
    @FXML
    private ComboBox<Film> filmFilter;
    @FXML
    private DatePicker dateFilter;
    @FXML
    private TableView<MovieSession> sessionsTable;
    @FXML
    private TableColumn<MovieSession, Integer> idColumn;
    @FXML
    private TableColumn<MovieSession, String> filmColumn;
    @FXML
    private TableColumn<MovieSession, String> hallColumn;
    @FXML
    private TableColumn<MovieSession, String> startTimeColumn;
    @FXML
    private TableColumn<MovieSession, String> endTimeColumn;
    @FXML
    private TableColumn<MovieSession, Double> priceColumn;
    @FXML
    private TableColumn<MovieSession, String> statusColumn;
    @FXML
    private Label totalSessionsLabel;
    @FXML
    private Label todaySessionsLabel;
    @FXML
    private Label upcomingSessionsLabel;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private VBox sessionFormContainer;
    private MovieSession selectedSession;
    private CinemaHall preFilterHall;
    // FormsFX Form and Properties
    private Form sessionForm;

    public MovieSessionManagementController() {
        this.sessionService = new MovieSessionService();
        this.cinemaService = new CinemaService();
        this.hallService = new CinemaHallService();
        this.filmService = new FilmService();
        this.sessions = FXCollections.observableArrayList();
        this.cinemas = FXCollections.observableArrayList();
        this.halls = FXCollections.observableArrayList();
        this.films = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        LOGGER.info("Initializing MovieSessionManagementController with FormsFX");

        setupTable();
        setupFilters();
        setupTimeSlots();
        setupFormsFX();
        loadData();
    }

    /**
     * Pre-filter by hall (from CinemaHallManagement).
     */
    public void setFilterHall(CinemaHall hall) {
        this.preFilterHall = hall;
    }

    private void setupTimeSlots() {
        ObservableList<String> timeSlots = FXCollections.observableArrayList();
        for (int hour = 9; hour <= 23; hour++) {
            timeSlots.add(String.format("%02d:00", hour));
            timeSlots.add(String.format("%02d:30", hour));
        }
        timeSlotListProperty.setAll(timeSlots);
    }

    /**
     * Creates and configures the FormsFX form for session editing.
     */
    private void setupFormsFX() {
        sessionForm = Form.of(
            Group.of(
                Field.ofSingleSelectionType(filmListProperty, selectedFilmProperty)
                    .label("Movie")
                    .required("Please select a movie"),

                Field.ofSingleSelectionType(hallListProperty, selectedHallProperty)
                    .label("Hall")
                    .required("Please select a hall")
            ),

            Group.of(
                Field.ofSingleSelectionType(timeSlotListProperty, selectedTimeSlotProperty)
                    .label("Start Time")
            ),

            Group.of(
                Field.ofDoubleType(basePriceProperty)
                    .label("Regular Price ($)")
                    .validate(DoubleRangeValidator.between(0.0, 100.0, "Price must be between $0 and $100")),

                Field.ofDoubleType(vipPriceProperty)
                    .label("VIP Price ($)")
                    .validate(DoubleRangeValidator.between(0.0, 200.0, "Price must be between $0 and $200"))
            ),

            Group.of(
                Field.ofBooleanType(is3DProperty)
                    .label("3D"),

                Field.ofBooleanType(isIMAXProperty)
                    .label("IMAX"),

                Field.ofBooleanType(isDolbyProperty)
                    .label("Dolby Atmos"),

                Field.ofBooleanType(isSubtitledProperty)
                    .label("Subtitled")
            )
        ).title("Session Details");

        // Render the form into the container
        if (sessionFormContainer != null) {
            FormRenderer renderer = new FormRenderer(sessionForm);
            renderer.getStyleClass().add("form-renderer");
            sessionFormContainer.getChildren().clear();
            sessionFormContainer.getChildren().add(renderer);
        }
    }

    private void setupTable() {
        idColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getId().intValue()).asObject());
        filmColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getFilm() != null ?
                cellData.getValue().getFilm().getTitle() : "N/A"));
        hallColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCinemaHall() != null ?
                cellData.getValue().getCinemaHall().getHallName() : "N/A"));
        startTimeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getStartTime() != null ?
                cellData.getValue().getStartTime().format(DATE_TIME_FORMAT) : "N/A"));
        endTimeColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getEndTime() != null ?
                cellData.getValue().getEndTime().format(DATE_TIME_FORMAT) : "N/A"));
        priceColumn.setCellValueFactory(cellData ->
            new SimpleDoubleProperty(cellData.getValue().getBasePrice()).asObject());
        statusColumn.setCellValueFactory(cellData -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = cellData.getValue().getStartTime();
            LocalDateTime end = cellData.getValue().getEndTime();

            String status;
            if (end != null && end.isBefore(now)) {
                status = "Completed";
            } else if (start != null && start.isBefore(now)) {
                status = "In Progress";
            } else {
                status = "Scheduled";
            }
            return new SimpleStringProperty(status);
        });

        sessionsTable.setItems(sessions);

        sessionsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedSession = newSel;
                populateForm(newSel);
            }
        });
    }

    private void setupFilters() {
        cinemaFilter.setOnAction(e -> {
            loadHallsForCinema(cinemaFilter.getValue());
            filterSessions();
        });
        hallFilter.setOnAction(e -> filterSessions());
        filmFilter.setOnAction(e -> filterSessions());
        dateFilter.setOnAction(e -> filterSessions());
    }

    private void loadData() {
        showLoading(true);

        new Thread(() -> {
            try {
                List<Cinema> cinemaList = cinemaService.getAllCinemas();
                List<Film> filmList = filmService.getAllFilms();
                List<MovieSession> sessionList = sessionService.getAllSessions();

                Platform.runLater(() -> {
                    cinemas.setAll(cinemaList);
                    films.setAll(filmList);
                    sessions.setAll(sessionList);

                    // Update FormsFX lists
                    filmListProperty.setAll(filmList);

                    // Setup filter combos
                    Cinema allCinemas = new Cinema();
                    allCinemas.setId(-1L);
                    allCinemas.setName("All Cinemas");

                    cinemaFilter.getItems().clear();
                    cinemaFilter.getItems().add(allCinemas);
                    cinemaFilter.getItems().addAll(cinemas);
                    cinemaFilter.setValue(allCinemas);

                    Film allFilms = new Film();
                    allFilms.setId(-1L);
                    allFilms.setTitle("All Films");
                    filmFilter.getItems().clear();
                    filmFilter.getItems().add(allFilms);
                    filmFilter.getItems().addAll(films);
                    filmFilter.setValue(allFilms);

                    // Apply pre-filter if set
                    if (preFilterHall != null) {
                        hallFilter.setValue(preFilterHall);
                        filterSessions();
                    }

                    updateStatistics();
                    showLoading(false);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading data", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to load data.");
                });
            }
        }).start();
    }

    private void loadHallsForCinema(Cinema cinema) {
        if (cinema == null || cinema.getId() == -1) {
            hallFilter.getItems().clear();
            hallListProperty.clear();
            return;
        }

        new Thread(() -> {
            try {
                List<CinemaHall> hallList = hallService.getHallsByCinema(cinema.getId());

                Platform.runLater(() -> {
                    halls.setAll(hallList);
                    hallListProperty.setAll(hallList);

                    CinemaHall allHalls = new CinemaHall();
                    allHalls.setId(-1L);
                    allHalls.setHallName("All Halls");

                    hallFilter.getItems().clear();
                    hallFilter.getItems().add(allHalls);
                    hallFilter.getItems().addAll(halls);
                    hallFilter.setValue(allHalls);
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading halls", e);
            }
        }).start();
    }

    private void filterSessions() {
        Cinema selectedCinema = cinemaFilter.getValue();
        CinemaHall selectedHall = hallFilter.getValue();
        Film selectedFilm = filmFilter.getValue();
        LocalDate selectedDate = dateFilter.getValue();

        new Thread(() -> {
            try {
                List<MovieSession> allSessions = sessionService.getAllSessions();

                List<MovieSession> filtered = allSessions.stream()
                    .filter(session -> {
                        if (selectedCinema != null && selectedCinema.getId() != -1) {
                            if (session.getCinemaHall() == null ||
                                session.getCinemaHall().getCinema() == null ||
                                !session.getCinemaHall().getCinema().getId().equals(selectedCinema.getId())) {
                                return false;
                            }
                        }

                        if (selectedHall != null && selectedHall.getId() != -1) {
                            if (session.getCinemaHall() == null ||
                                !session.getCinemaHall().getId().equals(selectedHall.getId())) {
                                return false;
                            }
                        }

                        if (selectedFilm != null && selectedFilm.getId() != -1) {
                            if (session.getFilm() == null ||
                                !session.getFilm().getId().equals(selectedFilm.getId())) {
                                return false;
                            }
                        }

                        if (selectedDate != null && session.getStartTime() != null) {
                            return session.getStartTime().toLocalDate().equals(selectedDate);
                        }

                        return true;
                    })
                    .toList();

                Platform.runLater(() -> {
                    sessions.setAll(filtered);
                    updateStatistics();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error filtering sessions", e);
            }
        }).start();
    }

    /**
     * Populates the FormsFX form with data from the selected session.
     */
    private void populateForm(MovieSession session) {
        selectedFilmProperty.set(session.getFilm());
        selectedHallProperty.set(session.getCinemaHall());

        if (session.getStartTime() != null) {
            sessionDateProperty.set(session.getStartTime().toLocalDate());
            selectedTimeSlotProperty.set(session.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        basePriceProperty.set(session.getBasePrice());
        vipPriceProperty.set(session.getBasePrice() * 1.5); // Estimate VIP price
    }

    /**
     * Clears the FormsFX form to default values.
     */
    private void clearForm() {
        selectedSession = null;
        selectedFilmProperty.set(null);
        selectedHallProperty.set(null);
        sessionDateProperty.set(LocalDate.now().plusDays(1));
        selectedTimeSlotProperty.set("14:00");
        basePriceProperty.set(12.0);
        vipPriceProperty.set(18.0);
        is3DProperty.set(false);
        isIMAXProperty.set(false);
        isDolbyProperty.set(false);
        isSubtitledProperty.set(false);

        sessionsTable.getSelectionModel().clearSelection();

        if (sessionForm != null) {
            sessionForm.reset();
        }
    }

    private void updateStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        int total = sessions.size();
        long todayCount = sessions.stream()
            .filter(s -> s.getStartTime() != null &&
                s.getStartTime().toLocalDate().equals(today))
            .count();
        long upcoming = sessions.stream()
            .filter(s -> s.getStartTime() != null &&
                s.getStartTime().isAfter(now))
            .count();

        if (totalSessionsLabel != null) totalSessionsLabel.setText(String.valueOf(total));
        if (todaySessionsLabel != null) todaySessionsLabel.setText(String.valueOf(todayCount));
        if (upcomingSessionsLabel != null) upcomingSessionsLabel.setText(String.valueOf(upcoming));
    }

    @FXML
    private void handleAddSession() {
        clearForm();
    }

    @FXML
    private void handleSaveSession() {
        if (!sessionForm.isValid()) {
            sessionForm.persist();
            showError("Please fix the validation errors before saving.");
            return;
        }

        showLoading(true);

        MovieSession session = selectedSession != null ? selectedSession : new MovieSession();
        session.setFilm(selectedFilmProperty.get());
        session.setCinemaHall(selectedHallProperty.get());

        LocalDate date = sessionDateProperty.get();
        LocalTime time = LocalTime.parse(selectedTimeSlotProperty.get());
        session.setStartTime(LocalDateTime.of(date, time));

        int duration = session.getFilm() != null ? session.getFilm().getDurationMin() : 120;
        session.setEndTime(session.getStartTime().plusMinutes(duration + 15));

        session.setBasePrice(basePriceProperty.get());

        new Thread(() -> {
            try {
                if (selectedSession != null) {
                    sessionService.updateSession(session);
                } else {
                    sessionService.createSession(session);
                }

                Platform.runLater(() -> {
                    showLoading(false);
                    showSuccess(selectedSession != null ? "Session updated!" : "Session created!");
                    clearForm();
                    filterSessions();
                });
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error saving session", e);
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Failed to save session.");
                });
            }
        }).start();
    }

    @FXML
    private void handleDeleteSession() {
        if (selectedSession == null) {
            showError("Please select a session to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Session");
        confirm.setHeaderText("Delete this session?");
        confirm.setContentText("This will cancel all associated bookings.");

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            showLoading(true);

            new Thread(() -> {
                try {
                    sessionService.deleteSession(selectedSession.getId());

                    Platform.runLater(() -> {
                        showLoading(false);
                        showSuccess("Session deleted!");
                        clearForm();
                        filterSessions();
                    });
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error deleting session", e);
                    Platform.runLater(() -> {
                        showLoading(false);
                        showError("Failed to delete session.");
                    });
                }
            }).start();
        }
    }

    @FXML
    private void handleBulkCreate() {
        showInfo("Bulk session creation coming soon!");
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleClearFilters() {
        cinemaFilter.setValue(cinemaFilter.getItems().get(0));
        hallFilter.getItems().clear();
        filmFilter.setValue(filmFilter.getItems().get(0));
        dateFilter.setValue(null);
        filterSessions();
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
    private void createSession() {
        clearForm();
        showSessionDialog();
    }

    @FXML
    private void closeSessionDialog() {
        VBox dialog = (VBox) managementContainer.getScene().lookup("#sessionDialog");
        if (dialog != null) {
            dialog.setVisible(false);
            dialog.setManaged(false);
        }
    }

    private void showSessionDialog() {
        VBox dialog = (VBox) managementContainer.getScene().lookup("#sessionDialog");
        if (dialog != null) {
            dialog.setVisible(true);
            dialog.setManaged(true);
        }
    }

    @FXML
    private void saveSession() {
        handleSaveSession();
        closeSessionDialog();
    }

    @FXML
    private void bulkSchedule() {
        handleBulkCreate();
    }

    @FXML
    private void closeBulkDialog() {
        VBox dialog = (VBox) managementContainer.getScene().lookup("#bulkScheduleDialog");
        if (dialog != null) {
            dialog.setVisible(false);
            dialog.setManaged(false);
        }
    }

    @FXML
    private void createBulkSessions() {
        handleBulkCreate();
    }

    // Date navigation methods
    @FXML
    private void previousDay() {
        if (dateFilter.getValue() != null) {
            dateFilter.setValue(dateFilter.getValue().minusDays(1));
        }
    }

    @FXML
    private void nextDay() {
        if (dateFilter.getValue() != null) {
            dateFilter.setValue(dateFilter.getValue().plusDays(1));
        }
    }

    @FXML
    private void goToToday() {
        dateFilter.setValue(LocalDate.now());
    }

    @FXML
    private void addShowtime() {
        showInfo("Add custom showtime functionality");
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
