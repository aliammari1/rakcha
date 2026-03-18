package com.esprit.controllers.series;

import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.validators.IntegerRangeValidator;
import com.dlsc.formsfx.model.validators.StringLengthValidator;
import com.esprit.models.series.Season;
import com.esprit.models.series.Series;
import com.esprit.services.series.EpisodeService;
import com.esprit.services.series.SeasonService;
import com.esprit.services.series.SeriesService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class SeasonManagementController implements Initializable {

    // Services
    private final SeriesService seriesService = new SeriesService();
    private final SeasonService seasonService = new SeasonService();
    private final EpisodeService episodeService = new EpisodeService();
    // FormsFX model properties
    private final IntegerProperty seasonNumberProperty = new SimpleIntegerProperty(1);
    private final StringProperty seasonTitleProperty = new SimpleStringProperty("");
    private final StringProperty seasonDescriptionProperty = new SimpleStringProperty("");
    // EasyBind subscriptions (for cleanup)
    private final List<Subscription> subscriptions = new ArrayList<>();
    private final ObservableList<Season> seasonsList = FXCollections.observableArrayList();
    // FXML Injected Components
    @FXML
    private Label seriesNameLabel;
    @FXML
    private Label seriesTitleLabel;
    @FXML
    private Label seriesYearLabel;
    @FXML
    private Label totalSeasonsLabel;
    @FXML
    private Label formTitleLabel;
    @FXML
    private Label seasonCountLabel;
    @FXML
    private Label seasonNumberError;
    @FXML
    private Label seasonTitleError;
    @FXML
    private ImageView seriesPosterImage;
    @FXML
    private Spinner<Integer> seasonNumberSpinner;
    @FXML
    private TextField seasonTitleField;
    @FXML
    private TextArea seasonDescriptionArea;
    @FXML
    private TextField searchField;
    @FXML
    private Button saveButton;
    @FXML
    private TableView<Season> seasonsTable;
    @FXML
    private TableColumn<Season, Integer> seasonNumberColumn;
    @FXML
    private TableColumn<Season, String> seasonTitleColumn;
    @FXML
    private TableColumn<Season, Integer> episodeCountColumn;
    @FXML
    private TableColumn<Season, Void> actionsColumn;
    @FXML
    private VBox loadingOverlay;
    @FXML
    private VBox formContainer;
    // Data
    private Series currentSeries;
    private Season editingSeason;
    private FilteredList<Season> filteredSeasons;
    private Form seasonForm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupSpinner();
        setupTable();
        setupSearch();
        setupFormsFX();
        setupEasyBindings();
        clearErrors();
    }

    /**
     * Setup FormsFX for declarative form handling with validation.
     */
    private void setupFormsFX() {
        // Create FormsFX form with validation
        seasonForm = Form.of(
            Group.of(
                Field.ofIntegerType(seasonNumberProperty)
                    .label("Season Number")
                    .placeholder("Enter season number")
                    .validate(IntegerRangeValidator.between(1, 99,
                        "Season number must be between 1 and 99")),
                Field.ofStringType(seasonTitleProperty)
                    .label("Season Title")
                    .placeholder("e.g., Season 1 or The Beginning")
                    .required("Title is required")
                    .validate(StringLengthValidator.between(2, 100,
                        "Title must be between 2 and 100 characters")),
                Field.ofStringType(seasonDescriptionProperty)
                    .label("Description")
                    .placeholder("Optional season description")
                    .multiline(true)
            )
        ).title("Season Details");

        // Bind form properties to existing FXML fields for backward compatibility
        seasonNumberSpinner.getValueFactory().valueProperty().bindBidirectional(seasonNumberProperty.asObject());
        seasonTitleField.textProperty().bindBidirectional(seasonTitleProperty);
        if (seasonDescriptionArea != null) {
            seasonDescriptionArea.textProperty().bindBidirectional(seasonDescriptionProperty);
        }
    }

    /**
     * Setup EasyBind for reactive property bindings and subscriptions.
     */
    private void setupEasyBindings() {
        // Use EasyBind.subscribe for clean reactive updates
        Subscription searchSub = EasyBind.subscribe(searchField.textProperty(), filter -> {
            String lowerFilter = filter != null ? filter.toLowerCase().trim() : "";
            filteredSeasons.setPredicate(season -> {
                if (lowerFilter.isEmpty()) return true;
                return season.getTitle().toLowerCase().contains(lowerFilter)
                    || String.valueOf(season.getSeasonNumber()).contains(lowerFilter);
            });
        });
        subscriptions.add(searchSub);

        // Use Bindings.size() for season count display
        Subscription countSub = EasyBind.subscribe(Bindings.size(seasonsList), count -> {
            int size = count.intValue();
            String text = size + " Season" + (size != 1 ? "s" : "");
            // Update count label if needed
        });
        subscriptions.add(countSub);

        // Subscribe to form validity changes for save button state
        Subscription validitySub = EasyBind.subscribe(seasonForm.validProperty(), isValid -> {
            // Additional validation for duplicate season numbers
            boolean duplicateExists = checkDuplicateSeasonNumber();
            saveButton.setDisable(!isValid || duplicateExists);
        });
        subscriptions.add(validitySub);

        // Use EasyBind for error label updates from form validation
        Subscription titleErrorSub = EasyBind.subscribe(seasonTitleProperty, title -> {
            if (title == null || title.trim().isEmpty()) {
                seasonTitleError.setText("Title is required");
            } else if (title.trim().length() < 2) {
                seasonTitleError.setText("Title must be at least 2 characters");
            } else {
                seasonTitleError.setText("");
            }
        });
        subscriptions.add(titleErrorSub);
    }

    /**
     * Check if the current season number is a duplicate.
     */
    private boolean checkDuplicateSeasonNumber() {
        int seasonNum = seasonNumberProperty.get();
        return seasonsList.stream()
            .filter(s -> editingSeason == null || !s.getId().equals(editingSeason.getId()))
            .anyMatch(s -> s.getSeasonNumber() == seasonNum);
    }

    /**
     * Cleanup subscriptions when controller is destroyed.
     */
    public void cleanup() {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();
    }

    /**
     * Sets the series whose seasons should be managed.
     * Call this method when navigating from series list.
     */
    public void setSeries(Series series) {
        this.currentSeries = series;
        if (series != null) {
            loadSeriesInfo();
            loadSeasons();
        }
    }

    /**
     * Sets the series by ID.
     */
    public void setSeriesId(Long seriesId) {
        showLoading(true);
        CompletableFuture.runAsync(() -> {
            Series series = seriesService.getById(seriesId);
            Platform.runLater(() -> {
                showLoading(false);
                if (series != null) {
                    setSeries(series);
                } else {
                    showError("Series not found", "The series with ID " + seriesId + " could not be found.");
                }
            });
        });
    }

    private void setupSpinner() {
        SpinnerValueFactory<Integer> valueFactory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1);
        seasonNumberSpinner.setValueFactory(valueFactory);
    }

    private void setupTable() {
        // Season Number column
        seasonNumberColumn.setCellValueFactory(cellData ->
            new SimpleIntegerProperty(cellData.getValue().getSeasonNumber()).asObject());
        seasonNumberColumn.setStyle("-fx-alignment: CENTER;");

        // Title column
        seasonTitleColumn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getTitle()));

        // Episode Count column
        episodeCountColumn.setCellValueFactory(cellData -> {
            Season season = cellData.getValue();
            int count = episodeService.getEpisodesBySeason(season.getId()).size();
            return new SimpleIntegerProperty(count).asObject();
        });
        episodeCountColumn.setStyle("-fx-alignment: CENTER;");

        // Actions column
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = createIconButton("mdi2p-pencil:16", "edit-btn-small");
            private final Button deleteBtn = createIconButton("mdi2t-trash-can:16", "delete-btn-small");
            private final Button episodesBtn = createIconButton("mdi2f-format-list-bulleted:16", "info-btn-small");
            private final HBox actionsBox = new HBox(8, editBtn, episodesBtn, deleteBtn);

            {
                actionsBox.setAlignment(Pos.CENTER);

                editBtn.setTooltip(new Tooltip("Edit Season"));
                deleteBtn.setTooltip(new Tooltip("Delete Season"));
                episodesBtn.setTooltip(new Tooltip("Manage Episodes"));

                editBtn.setOnAction(e -> editSeason(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> deleteSeason(getTableView().getItems().get(getIndex())));
                episodesBtn.setOnAction(e -> openEpisodesForSeason(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actionsBox);
            }
        });

        // Initialize filtered list
        filteredSeasons = new FilteredList<>(seasonsList, s -> true);
        seasonsTable.setItems(filteredSeasons);

        // Double-click to edit
        seasonsTable.setRowFactory(tv -> {
            TableRow<Season> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editSeason(row.getItem());
                }
            });
            return row;
        });
    }

    private Button createIconButton(String iconLiteral, String styleClass) {
        Button btn = new Button();
        FontIcon icon = new FontIcon(iconLiteral);
        btn.setGraphic(icon);
        btn.getStyleClass().addAll("icon-btn", styleClass);
        return btn;
    }

    private void setupSearch() {
        // Search is now handled via EasyBind in setupEasyBindings()
        // Keep this method for any additional search setup if needed
    }

    private void loadSeriesInfo() {
        if (currentSeries == null) return;

        seriesNameLabel.setText("Series: " + currentSeries.getName());
        seriesTitleLabel.setText(currentSeries.getName());
        seriesYearLabel.setText(String.valueOf(currentSeries.getReleaseYear()));

        // Load poster image
        if (currentSeries.getImageUrl() != null && !currentSeries.getImageUrl().isEmpty()) {
            try {
                Image image = new Image(currentSeries.getImageUrl(), true);
                seriesPosterImage.setImage(image);
            } catch (Exception e) {
                // Keep default image
            }
        }

        updateSeasonNumbers();
    }

    private void loadSeasons() {
        if (currentSeries == null) return;

        showLoading(true);
        CompletableFuture.runAsync(() -> {
            List<Season> seasons = seasonService.getSeasonsBySeriesId(currentSeries.getId());
            Platform.runLater(() -> {
                showLoading(false);
                seasonsList.setAll(seasons);
                updateSeasonNumbers();
                suggestNextSeasonNumber();
            });
        });
    }

    private void updateSeasonNumbers() {
        int count = seasonsList.size();
        totalSeasonsLabel.setText(count + " Season" + (count != 1 ? "s" : ""));
        seasonCountLabel.setText("(" + count + ")");
    }

    private void suggestNextSeasonNumber() {
        int maxSeason = seasonsList.stream()
            .mapToInt(Season::getSeasonNumber)
            .max()
            .orElse(0);
        // Update FormsFX property (will sync to spinner via binding)
        seasonNumberProperty.set(maxSeason + 1);
    }

    @FXML
    private void saveSeason() {
        // Persist FormsFX form values
        seasonForm.persist();

        // Validate using FormsFX
        if (!seasonForm.isValid()) {
            showError("Validation Error", "Please fix the form errors before saving.");
            return;
        }

        // Check for duplicate season number
        if (checkDuplicateSeasonNumber()) {
            seasonNumberError.setText("Season " + seasonNumberProperty.get() + " already exists");
            return;
        }

        showLoading(true);
        Season season = editingSeason != null ? editingSeason : new Season();
        season.setSeasonNumber(seasonNumberProperty.get());
        season.setTitle(seasonTitleProperty.get().trim());
        season.setSeries(currentSeries);

        final boolean isEdit = editingSeason != null;

        CompletableFuture.runAsync(() -> {
            boolean operationResult = false;
            try {
                if (isEdit) {
                    seasonService.update(season);
                    operationResult = true;
                } else {
                    seasonService.create(season);
                    operationResult = season.getId() != null;
                }
            } catch (Exception e) {
                operationResult = false;
            }

            final boolean success = operationResult;
            Platform.runLater(() -> {
                showLoading(false);
                if (success) {
                    showSuccess(isEdit ? "Season Updated" : "Season Created",
                        "Season " + season.getSeasonNumber() + " has been saved successfully.");
                    clearForm();
                    loadSeasons();
                } else {
                    showError("Save Failed", "Could not save the season. Please try again.");
                }
            });
        });
    }

    private void handleAsyncError(Exception e, int seasonNumber) {
        Platform.runLater(() -> {
            showLoading(false);
            showError("Save Failed", "Error saving season " + seasonNumber + ": " + e.getMessage());
        });
    }

    private boolean validateForm() {
        clearErrors();
        // Use FormsFX validation
        seasonForm.persist();
        return seasonForm.isValid() && !checkDuplicateSeasonNumber();
    }

    private void clearErrors() {
        seasonNumberError.setText("");
        seasonTitleError.setText("");
    }

    private void editSeason(Season season) {
        this.editingSeason = season;
        formTitleLabel.setText("Edit Season " + season.getSeasonNumber());

        // Update FormsFX properties
        seasonNumberProperty.set(season.getSeasonNumber());
        seasonTitleProperty.set(season.getTitle());

        saveButton.setText("Update Season");
        clearErrors();
    }

    @FXML
    private void clearForm() {
        editingSeason = null;
        formTitleLabel.setText("Add New Season");

        // Reset FormsFX form
        seasonForm.reset();
        seasonTitleProperty.set("");
        seasonDescriptionProperty.set("");

        saveButton.setText("Save Season");
        clearErrors();
        suggestNextSeasonNumber();
    }

    private void deleteSeason(Season season) {
        int episodeCount = episodeService.getEpisodesBySeason(season.getId()).size();
        String message = "Are you sure you want to delete Season " + season.getSeasonNumber() + "?";
        if (episodeCount > 0) {
            message += "\n\nThis will also delete " + episodeCount + " episode(s).";
        }

        Optional<ButtonType> result = showConfirmation("Delete Season", message);
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showLoading(true);
            CompletableFuture.runAsync(() -> {
                try {
                    seasonService.delete(season.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    showLoading(false);
                    loadSeasons();
                    showSuccess("Season Deleted", "Season " + season.getSeasonNumber() + " has been deleted.");
                });
            });
        }
    }

    @FXML
    private void deleteSelected() {
        Season selected = seasonsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteSeason(selected);
        } else {
            showError("No Selection", "Please select a season to delete.");
        }
    }

    private void openEpisodesForSeason(Season season) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/Episode-view.fxml"));
            Parent root = loader.load();

            // The EpisodeController will handle episode management
            // Season context can be passed if EpisodeController supports it in future

            Stage stage = (Stage) seasonsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not open episode management.");
        }
    }

    @FXML
    private void manageEpisodes() {
        Season selected = seasonsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openEpisodesForSeason(selected);
        } else if (!seasonsList.isEmpty()) {
            openEpisodesForSeason(seasonsList.get(0));
        } else {
            showError("No Seasons", "Please create a season first.");
        }
    }

    @FXML
    private void viewSeriesDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/Serie-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) seasonsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not open series view.");
        }
    }

    @FXML
    private void refreshData() {
        loadSeasons();
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/Serie-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) seasonsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void exportSeasons() {
        // PDF export functionality
        showSuccess("Export", "PDF export functionality coming soon!");
    }

    // Helper methods
    private void showLoading(boolean show) {
        loadingOverlay.setVisible(show);
        loadingOverlay.setManaged(show);
    }

    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}
