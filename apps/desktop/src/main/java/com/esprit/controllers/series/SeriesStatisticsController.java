package com.esprit.controllers.series;

import com.esprit.models.series.Episode;
import com.esprit.models.series.Season;
import com.esprit.models.series.Series;
import com.esprit.services.series.EpisodeService;
import com.esprit.services.series.SeasonService;
import com.esprit.services.series.SeriesService;
import com.esprit.utils.NavigationManager;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Log4j2
public class SeriesStatisticsController {

    private static final Logger LOGGER = Logger.getLogger(SeriesStatisticsController.class.getName());

    private final SeriesService seriesService;
    private final SeasonService seasonService;
    private final EpisodeService episodeService;
    // EasyBind reactive properties
    private final BooleanProperty loadingProperty = new SimpleBooleanProperty(false);
    private final IntegerProperty totalSeriesProperty = new SimpleIntegerProperty(0);
    private final IntegerProperty totalSeasonsProperty = new SimpleIntegerProperty(0);
    private final IntegerProperty totalEpisodesProperty = new SimpleIntegerProperty(0);
    private final DoubleProperty avgEpisodesProperty = new SimpleDoubleProperty(0);
    // EasyBind subscriptions for cleanup
    private final List<Subscription> subscriptions = new ArrayList<>();
    @FXML
    private VBox statsContainer;
    @FXML
    private Label totalSeriesLabel;
    @FXML
    private Label totalSeasonsLabel;
    @FXML
    private Label totalEpisodesLabel;
    @FXML
    private Label avgEpisodesPerSeasonLabel;
    @FXML
    private PieChart seriesByCategoryChart;
    @FXML
    private BarChart<String, Number> episodesBySeriesChart;
    @FXML
    private ProgressIndicator loadingIndicator;

    /**
     * Default constructor initializing services.
     */
    public SeriesStatisticsController() {
        this.seriesService = new SeriesService();
        this.seasonService = new SeasonService();
        this.episodeService = new EpisodeService();
    }

    /**
     * Initializes the controller and loads statistics data.
     */
    @FXML
    public void initialize() {
        LOGGER.info("Initializing SeriesStatisticsController");
        setupEasyBindings();
        loadStatistics();
    }

    /**
     * Setup EasyBind reactive bindings for statistics properties.
     */
    private void setupEasyBindings() {
        // Bind loading indicator visibility
        Subscription loadingSub = EasyBind.subscribe(loadingProperty, loading -> {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(loading);
            }
        });
        subscriptions.add(loadingSub);

        // Bind total series label using EasyBind.map
        Subscription seriesSub = EasyBind.subscribe(totalSeriesProperty, count -> {
            if (totalSeriesLabel != null) {
                totalSeriesLabel.setText(String.valueOf(count));
            }
        });
        subscriptions.add(seriesSub);

        // Bind total seasons label
        Subscription seasonsSub = EasyBind.subscribe(totalSeasonsProperty, count -> {
            if (totalSeasonsLabel != null) {
                totalSeasonsLabel.setText(String.valueOf(count));
            }
        });
        subscriptions.add(seasonsSub);

        // Bind total episodes label
        Subscription episodesSub = EasyBind.subscribe(totalEpisodesProperty, count -> {
            if (totalEpisodesLabel != null) {
                totalEpisodesLabel.setText(String.valueOf(count));
            }
        });
        subscriptions.add(episodesSub);

        // Bind average episodes label with formatting
        Subscription avgSub = EasyBind.subscribe(avgEpisodesProperty, avg -> {
            if (avgEpisodesPerSeasonLabel != null) {
                avgEpisodesPerSeasonLabel.setText(String.format("%.1f", avg));
            }
        });
        subscriptions.add(avgSub);
    }

    /**
     * Cleanup subscriptions when controller is destroyed.
     */
    public void cleanup() {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();
    }

    /**
     * Loads all statistics data asynchronously.
     */
    private void loadStatistics() {
        loadingProperty.set(true);

        new Thread(() -> {
            try {
                // Fetch data
                List<Series> allSeries = seriesService.getAll();
                List<Season> allSeasons = seasonService.getAll();
                List<Episode> allEpisodes = episodeService.getAll();

                // Calculate statistics
                int totalSeries = allSeries.size();
                int totalSeasons = allSeasons.size();
                int totalEpisodes = allEpisodes.size();
                double avgEpisodesPerSeason = totalSeasons > 0
                    ? (double) totalEpisodes / totalSeasons
                    : 0;

                // Group series by category (series can have multiple genres)
                Map<String, Long> seriesByCategory = new HashMap<>();
                for (Series s : allSeries) {
                    if (s.getGenres() != null) {
                        for (var category : s.getGenres()) {
                            if (category != null && category.getName() != null) {
                                seriesByCategory.merge(category.getName(), 1L, Long::sum);
                            }
                        }
                    }
                }

                // Group episodes by series (via season)
                Map<String, Long> episodesBySeries = allEpisodes.stream()
                    .filter(e -> e.getSeason() != null && e.getSeason().getSeries() != null)
                    .collect(Collectors.groupingBy(
                        e -> e.getSeason().getSeries().getNom(),
                        Collectors.counting()
                    ));

                // Update UI on JavaFX thread using EasyBind properties
                Platform.runLater(() -> {
                    // Update reactive properties - labels will auto-update via EasyBind subscriptions
                    totalSeriesProperty.set(totalSeries);
                    totalSeasonsProperty.set(totalSeasons);
                    totalEpisodesProperty.set(totalEpisodes);
                    avgEpisodesProperty.set(avgEpisodesPerSeason);

                    updateCharts(seriesByCategory, episodesBySeries);
                    loadingProperty.set(false);
                });

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error loading statistics", e);
                Platform.runLater(() -> {
                    loadingProperty.set(false);
                    showError("Failed to load statistics: " + e.getMessage());
                });
            }
        }).start();
    }

    /**
     * Updates the statistics labels with calculated values.
     * Note: With EasyBind, labels are now updated reactively via property subscriptions.
     * This method is retained for compatibility but labels are updated via properties.
     */
    private void updateStatLabels(int totalSeries, int totalSeasons, int totalEpisodes, double avgEpisodes) {
        // Using EasyBind properties - these will trigger label updates automatically
        totalSeriesProperty.set(totalSeries);
        totalSeasonsProperty.set(totalSeasons);
        totalEpisodesProperty.set(totalEpisodes);
        avgEpisodesProperty.set(avgEpisodes);
    }

    /**
     * Updates charts with category and episode data.
     */
    private void updateCharts(Map<String, Long> seriesByCategory, Map<String, Long> episodesBySeries) {
        // Update pie chart for series by category
        if (seriesByCategoryChart != null) {
            seriesByCategoryChart.getData().clear();
            seriesByCategory.forEach((category, count) -> {
                PieChart.Data data = new PieChart.Data(category + " (" + count + ")", count);
                seriesByCategoryChart.getData().add(data);
            });
        }

        // Update bar chart for episodes by series
        if (episodesBySeriesChart != null) {
            episodesBySeriesChart.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Episodes");

            // Take top 10 series by episode count
            episodesBySeries.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(entry -> {
                    String seriesName = entry.getKey();
                    if (seriesName.length() > 15) {
                        seriesName = seriesName.substring(0, 12) + "...";
                    }
                    series.getData().add(new XYChart.Data<>(seriesName, entry.getValue()));
                });

            episodesBySeriesChart.getData().add(series);
        }
    }

    /**
     * Refreshes statistics data.
     */
    @FXML
    private void handleRefresh() {
        LOGGER.info("Refreshing statistics");
        loadStatistics();
    }

    /**
     * Exports statistics to a file.
     */
    @FXML
    private void handleExport() {
        showInfo("Export functionality coming soon!");
    }

    /**
     * Navigates back to series management.
     */
    @FXML
    private void handleBack() {
        try {
            Stage stage = getStage();
            if (stage != null) {
                NavigationManager.navigate(stage, "/ui/series/Serie-view.fxml");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error navigating back", e);
        }
    }

    /**
     * Shows or hides the loading indicator using reactive property.
     */
    private void showLoading(boolean show) {
        loadingProperty.set(show);
    }

    /**
     * Gets the current stage from any available control.
     */
    private Stage getStage() {
        if (statsContainer != null && statsContainer.getScene() != null) {
            return (Stage) statsContainer.getScene().getWindow();
        }
        return null;
    }

    /**
     * Shows an info alert.
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Shows an error alert.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
