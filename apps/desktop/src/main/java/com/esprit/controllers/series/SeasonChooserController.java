package com.esprit.controllers.series;

import com.esprit.models.series.Favorite;
import com.esprit.models.series.Season;
import com.esprit.models.series.Series;
import com.esprit.models.users.Client;
import com.esprit.models.users.User;
import com.esprit.services.series.EpisodeService;
import com.esprit.services.series.FavoriteService;
import com.esprit.services.series.SeasonService;
import com.esprit.services.series.SeriesService;
import com.esprit.utils.SessionManager;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.fxmisc.easybind.EasyBind;
import org.fxmisc.easybind.Subscription;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class SeasonChooserController implements Initializable {

    // Services
    private final SeriesService seriesService = new SeriesService();
    private final SeasonService seasonService = new SeasonService();
    private final EpisodeService episodeService = new EpisodeService();
    private final FavoriteService favoriteService = new FavoriteService();
    // State - Using ObservableProperties with EasyBind
    private final ObjectProperty<Series> currentSeriesProperty = new SimpleObjectProperty<>();
    private final ObservableList<Season> seasonsProperty = FXCollections.observableArrayList();
    private final BooleanProperty isFavoriteProperty = new SimpleBooleanProperty(false);
    private final BooleanProperty loadingProperty = new SimpleBooleanProperty(false);
    // EasyBind subscriptions for cleanup
    private final List<Subscription> subscriptions = new ArrayList<>();
    // FXML Injected Components - Hero Section
    @FXML
    private ImageView seriesPosterImage;
    @FXML
    private Label seriesTitleLabel;
    @FXML
    private Label yearLabel;
    @FXML
    private Label seasonCountLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Label genreLabel;
    @FXML
    private Label descriptionLabel;
    @FXML
    private Label directorLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Button continueWatchingBtn;
    @FXML
    private Button addToListBtn;
    // Continue Watching Banner
    @FXML
    private HBox continueWatchingBanner;
    @FXML
    private Label lastWatchedLabel;
    @FXML
    private ProgressBar episodeProgressBar;
    // Season Grid
    @FXML
    private FlowPane seasonsFlowPane;
    @FXML
    private ComboBox<Season> seasonDropdown;
    @FXML
    private VBox emptyState;
    // Recommendations
    @FXML
    private VBox recommendationsSection;
    @FXML
    private HBox recommendationsBox;
    // Loading
    @FXML
    private VBox loadingOverlay;
    // Legacy references for compatibility
    private Series currentSeries;
    private User currentUser;
    private List<Season> seasons;
    private boolean isFavorite = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SessionManager.getInstance();
        currentUser = SessionManager.getCurrentUser();
        setupSeasonDropdown();
        setupEasyBindings();
    }

    /**
     * Setup EasyBind reactive bindings for improved state management.
     */
    private void setupEasyBindings() {
        // Bind loading overlay visibility to loading property
        Subscription loadingSub = EasyBind.subscribe(loadingProperty, loading -> {
            loadingOverlay.setVisible(loading);
            loadingOverlay.setManaged(loading);
        });
        subscriptions.add(loadingSub);

        // Bind favorite button state to isFavoriteProperty
        Subscription favoriteSub = EasyBind.subscribe(isFavoriteProperty, favorite -> {
            isFavorite = favorite;
            updateFavoriteButton();
        });
        subscriptions.add(favoriteSub);

        // Bind season count label using Bindings.size() with EasyBind.subscribe
        var seasonCountSub = EasyBind.subscribe(Bindings.size(seasonsProperty), count -> {
            int size = count.intValue();
            String text = size + " Season" + (size != 1 ? "s" : "");
            // Update UI if seasonCountLabel exists
            Platform.runLater(() -> {
                // seasonCountLabel would be updated here if it exists
            });
        });
        subscriptions.add(seasonCountSub);

        // Use EasyBind.subscribe for series changes
        Subscription seriesSub = EasyBind.subscribe(currentSeriesProperty, series -> {
            currentSeries = series;
            if (series != null) {
                Platform.runLater(() -> {
                    loadSeriesDetails();
                    loadSeasons();
                    loadFavoriteStatus();
                    loadRecommendations();
                    // Hide continue watching banner - simplified version without watch progress
                    continueWatchingBanner.setVisible(false);
                    continueWatchingBanner.setManaged(false);
                });
            }
        });
        subscriptions.add(seriesSub);

        // Bind empty state visibility using Bindings.isEmpty()
        Subscription emptyStateSub = EasyBind.subscribe(
            Bindings.isEmpty(seasonsProperty),
            empty -> {
                emptyState.setVisible(empty);
                emptyState.setManaged(empty);
                seasonsFlowPane.setVisible(!empty);
            }
        );
        subscriptions.add(emptyStateSub);
    }

    /**
     * Cleanup subscriptions when controller is destroyed.
     */
    public void cleanup() {
        subscriptions.forEach(Subscription::unsubscribe);
        subscriptions.clear();
    }

    /**
     * Sets the series to display.
     * Call this when navigating from series list or search.
     */
    public void setSeries(Series series) {
        // Using EasyBind reactive property - will trigger setupEasyBindings subscription
        currentSeriesProperty.set(series);
    }

    /**
     * Sets series by ID (async loading).
     */
    public void setSeriesId(Long seriesId) {
        loadingProperty.set(true);
        CompletableFuture.runAsync(() -> {
            Series series = seriesService.getById(seriesId);
            Platform.runLater(() -> {
                loadingProperty.set(false);
                if (series != null) {
                    setSeries(series);
                } else {
                    showError("Series not found.");
                }
            });
        });
    }

    private void setupSeasonDropdown() {
        seasonDropdown.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Season item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Season " + item.getSeasonNumber());
            }
        });
        seasonDropdown.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Season item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Season " + item.getSeasonNumber());
            }
        });
        seasonDropdown.setOnAction(e -> {
            Season selected = seasonDropdown.getValue();
            if (selected != null) {
                openSeasonView(selected);
            }
        });
    }

    private void loadSeriesDetails() {
        if (currentSeries == null) return;

        seriesTitleLabel.setText(currentSeries.getName());
        descriptionLabel.setText(currentSeries.getSummary() != null ? currentSeries.getSummary() : "No description available.");
        directorLabel.setText(currentSeries.getDirector() != null ? currentSeries.getDirector() : "Unknown");
        countryLabel.setText(currentSeries.getCountry() != null ? currentSeries.getCountry() : "Unknown");
        yearLabel.setText(String.valueOf(currentSeries.getReleaseYear()));
        ratingLabel.setText(String.format("%.1f/5", currentSeries.getNote()));

        // Genre (category)
        if (currentSeries.getCategories() != null && !currentSeries.getCategories().isEmpty()) {
            genreLabel.setText(currentSeries.getCategories().stream()
                .map(c -> c.getName())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Uncategorized"));
        } else {
            genreLabel.setText("Uncategorized");
        }

        // Load poster
        if (currentSeries.getImageUrl() != null && !currentSeries.getImageUrl().isEmpty()) {
            try {
                Image image = new Image(currentSeries.getImageUrl(), true);
                seriesPosterImage.setImage(image);
            } catch (Exception e) {
                // Keep placeholder
            }
        }
    }

    private void loadSeasons() {
        loadingProperty.set(true);
        CompletableFuture.runAsync(() -> {
            seasons = seasonService.getSeasonsBySeriesId(currentSeries.getId());
            Platform.runLater(() -> {
                loadingProperty.set(false);
                // Update observable list - EasyBind will handle UI updates
                seasonsProperty.setAll(seasons);
                seasonCountLabel.setText(seasons.size() + " Season" + (seasons.size() != 1 ? "s" : ""));

                if (!seasons.isEmpty()) {
                    populateSeasonCards();
                    populateSeasonDropdown();
                }
            });
        });
    }

    private void populateSeasonCards() {
        seasonsFlowPane.getChildren().clear();

        for (Season season : seasons) {
            VBox card = createSeasonCard(season);
            seasonsFlowPane.getChildren().add(card);
        }
    }

    private VBox createSeasonCard(Season season) {
        VBox card = new VBox(8);
        card.getStyleClass().add("season-card");
        card.setPrefWidth(180);
        card.setPrefHeight(200);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));

        // Season number badge
        Label numberLabel = new Label(String.valueOf(season.getSeasonNumber()));
        numberLabel.getStyleClass().add("season-number-badge");
        numberLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #e50914;");

        // Season text
        Label seasonLabel = new Label("SEASON");
        seasonLabel.getStyleClass().add("season-text");
        seasonLabel.setStyle("-fx-text-fill: #aaa; -fx-font-size: 12px;");

        // Title
        Label titleLabel = new Label(season.getTitle());
        titleLabel.getStyleClass().add("season-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(150);
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        // Episode count
        int episodeCount = episodeService.getEpisodesBySeason(season.getId()).size();
        Label episodesLabel = new Label(episodeCount + " Episode" + (episodeCount != 1 ? "s" : ""));
        episodesLabel.getStyleClass().add("episode-count");
        episodesLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

        // Play button
        Button playBtn = new Button("Watch");
        FontIcon playIcon = new FontIcon("mdi2p-play:16");
        playIcon.setIconColor(Color.WHITE);
        playBtn.setGraphic(playIcon);
        playBtn.getStyleClass().add("play-btn-small");
        playBtn.setOnAction(e -> openSeasonView(season));

        card.getChildren().addAll(numberLabel, seasonLabel, titleLabel, episodesLabel, playBtn);

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: rgba(255,255,255,0.12); -fx-background-radius: 12;"));
        card.setOnMouseExited(e -> card.setStyle(""));
        card.setOnMouseClicked(e -> openSeasonView(season));

        return card;
    }

    private void populateSeasonDropdown() {
        seasonDropdown.getItems().clear();
        seasonDropdown.getItems().addAll(seasons);
    }

    private void loadFavoriteStatus() {
        if (currentUser == null) return;

        CompletableFuture.runAsync(() -> {
            try {
                Favorite fav = favoriteService.getByIdUserAndIdSerie(currentUser.getId(), currentSeries.getId());
                Platform.runLater(() -> {
                    // Using EasyBind reactive property
                    isFavoriteProperty.set(fav != null);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    isFavoriteProperty.set(false);
                });
            }
        });
    }

    private void updateFavoriteButton() {
        if (isFavorite) {
            addToListBtn.setText("In My List");
            FontIcon icon = new FontIcon("mdi2c-check:18");
            icon.setIconColor(Color.WHITE);
            addToListBtn.setGraphic(icon);
        } else {
            addToListBtn.setText("My List");
            FontIcon icon = new FontIcon("mdi2p-plus:18");
            icon.setIconColor(Color.WHITE);
            addToListBtn.setGraphic(icon);
        }
    }

    private void loadRecommendations() {
        CompletableFuture.runAsync(() -> {
            // Get similar series (same category if available)
            List<Series> allSeries = seriesService.getAll();
            List<Series> similar = allSeries.stream()
                .filter(s -> !s.getId().equals(currentSeries.getId()))
                .limit(6)
                .toList();

            Platform.runLater(() -> {
                recommendationsBox.getChildren().clear();
                if (similar.isEmpty()) {
                    recommendationsSection.setVisible(false);
                    recommendationsSection.setManaged(false);
                } else {
                    recommendationsSection.setVisible(true);
                    recommendationsSection.setManaged(true);
                    for (Series series : similar) {
                        VBox card = createRecommendationCard(series);
                        recommendationsBox.getChildren().add(card);
                    }
                }
            });
        });
    }

    private VBox createRecommendationCard(Series series) {
        VBox card = new VBox(8);
        card.getStyleClass().add("recommendation-card");
        card.setPrefWidth(140);
        card.setAlignment(Pos.TOP_CENTER);

        ImageView poster = new ImageView();
        poster.setFitWidth(130);
        poster.setFitHeight(180);
        poster.setPreserveRatio(true);
        poster.setEffect(new DropShadow(10, Color.web("#00000066")));

        if (series.getImageUrl() != null && !series.getImageUrl().isEmpty()) {
            try {
                poster.setImage(new Image(series.getImageUrl(), true));
            } catch (Exception ignored) {
            }
        }

        Label title = new Label(series.getName());
        title.setWrapText(true);
        title.setMaxWidth(130);
        title.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12px;");

        card.getChildren().addAll(poster, title);
        card.setOnMouseClicked(e -> setSeries(series));
        card.setOnMouseEntered(e -> card.setOpacity(0.8));
        card.setOnMouseExited(e -> card.setOpacity(1.0));

        return card;
    }

    // Actions
    @FXML
    private void continueWatching() {
        if (!seasons.isEmpty()) {
            openSeasonView(seasons.get(0));
        }
    }

    @FXML
    private void continueFromLastEpisode() {
        // Simplified: just play first season
        if (!seasons.isEmpty()) {
            openSeasonView(seasons.get(0));
        }
    }

    @FXML
    private void toggleFavorite() {
        if (currentUser == null) {
            showError("Please log in to add to your list.");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                if (isFavorite) {
                    Favorite existing = favoriteService.getByIdUserAndIdSerie(currentUser.getId(), currentSeries.getId());
                    if (existing != null) {
                        favoriteService.delete(existing);
                    }
                } else {
                    Favorite newFav = Favorite.builder()
                        .user((Client) currentUser)
                        .series(currentSeries)
                        .build();
                    favoriteService.create(newFav);
                }
                Platform.runLater(() -> {
                    isFavorite = !isFavorite;
                    updateFavoriteButton();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Could not update favorites."));
            }
        });
    }

    @FXML
    private void shareSeriesUrl() {
        String shareUrl = "https://rakcha.com/series/" + currentSeries.getId();
        // Copy to clipboard
        javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(shareUrl);
        clipboard.setContent(content);

        // Show toast/notification
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Share");
        alert.setHeaderText(null);
        alert.setContentText("Link copied to clipboard!");
        alert.showAndWait();
    }

    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/SeriesClient.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) seriesTitleLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openSeasonView(Season season) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/series/SeasonView.fxml"));
            Parent root = loader.load();

            SeasonViewController controller = loader.getController();
            controller.setSeries(currentSeries);

            Stage stage = (Stage) seriesTitleLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Could not open season view.");
        }
    }

    // Helpers
    private void showLoading(boolean show) {
        // Use EasyBind property - UI updates will be handled by subscription
        loadingProperty.set(show);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
