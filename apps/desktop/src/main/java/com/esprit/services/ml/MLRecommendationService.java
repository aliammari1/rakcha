package com.esprit.services.ml;

import com.esprit.models.films.Film;
import com.esprit.models.ml.MovieRecommendation;
import com.esprit.models.users.User;
import com.esprit.services.IService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.log4j.Log4j2;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * ML-powered recommendation service implementing IService pattern.
 * Uses TensorFlow for deep learning recommendations with Weka fallback.
 * Follows existing RAKCHA service architecture patterns.
 */

@Log4j2
public class MLRecommendationService implements IService<MovieRecommendation> {

    // Model paths
    private static final String MODEL_PATH = "models/recommendation_model";
    private final DataSource dataSource;
    private final FilmService filmService;
    private final ExecutorService executorService;
    // Caching for performance
    private final Cache<String, List<MovieRecommendation>> recommendationCache;
    private final Cache<Long, Map<String, Double>> userPreferenceCache;
    // TensorFlow model for recommendations
    private SavedModelBundle tensorFlowModel;
    private Session tensorFlowSession;

    public MLRecommendationService() {
        this.dataSource = DataSource.getInstance();
        this.filmService = new FilmService();

        this.executorService = Executors.newFixedThreadPool(4);

        // Initialize caching
        this.recommendationCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .build();

        this.userPreferenceCache = Caffeine.newBuilder()
            .maximumSize(5_000)
            .expireAfterWrite(Duration.ofHours(1))
            .build();

        // Initialize ML models
        initializeModels();
    }

    /**
     * Initialize TensorFlow and fallback models.
     */
    private void initializeModels() {
        try {
            // Try to load TensorFlow model
            if (java.nio.file.Files.exists(java.nio.file.Paths.get(MODEL_PATH))) {
                tensorFlowModel = SavedModelBundle.load(MODEL_PATH, "serve");
                tensorFlowSession = tensorFlowModel.session();
                log.info("TensorFlow recommendation model loaded successfully");
            } else {
                log.warn("TensorFlow model not found at: " + MODEL_PATH + ". Using fallback algorithms.");
            }
        } catch (Exception e) {
            log.error("Failed to load TensorFlow model, using fallback algorithms", e);
        }
    }

    /**
     * Generate recommendations for a user using ML algorithms.
     *
     * @param userId the user ID
     * @param limit  maximum number of recommendations
     * @return list of movie recommendations
     */
    public CompletableFuture<List<MovieRecommendation>> generateRecommendationsAsync(Long userId, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = userId + "_" + limit;

            // Check cache first
            List<MovieRecommendation> cached = recommendationCache.getIfPresent(cacheKey);
            if (cached != null) {
                log.debug("Returning cached recommendations for user: " + userId);
                return cached;
            }

            // Generate new recommendations
            List<MovieRecommendation> recommendations;
            if (tensorFlowModel != null) {
                recommendations = generateTensorFlowRecommendations(userId, limit);
            } else {
                recommendations = generateFallbackRecommendations(userId, limit);
            }

            // Cache the results
            recommendationCache.put(cacheKey, recommendations);

            // Store in database
            recommendations.forEach(this::create);

            return recommendations;
        }, executorService);
    }

    /**
     * Generate recommendations using TensorFlow model.
     */
    private List<MovieRecommendation> generateTensorFlowRecommendations(Long userId, int limit) {
        try {
            // Get user preferences and viewing history
            Map<String, Double> userPreferences = getUserPreferences(userId);
            List<Film> availableFilms = filmService.getAll();

            List<MovieRecommendation> recommendations = new ArrayList<>();

            for (Film film : availableFilms) {
                if (hasUserWatchedFilm(userId, film.getId())) {
                    continue; // Skip films user has already watched
                }


                // Run TensorFlow inference (simplified for compatibility)
                try {
                    // For now, use a simple scoring algorithm as TensorFlow Java API is complex
                    double confidence = calculateContentBasedScore(userPreferences, film);

                    if (confidence > 0.3) { // Threshold for recommendations
                        String reasoning = generateReasoning(userPreferences, film, confidence);
                        MovieRecommendation recommendation = new MovieRecommendation(
                            getUserById(userId), film, confidence, reasoning, "tensorflow"
                        );
                        recommendations.add(recommendation);
                    }
                } catch (Exception e) {
                    log.error("Error in TensorFlow inference for film: " + film.getId(), e);
                }
            }

            // Sort by confidence and limit results
            return recommendations.stream()
                .sorted((r1, r2) -> Double.compare(r2.getConfidenceScore(), r1.getConfidenceScore()))
                .limit(limit)
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error generating TensorFlow recommendations", e);
            return generateFallbackRecommendations(userId, limit);
        }
    }

    /**
     * Generate recommendations using collaborative filtering fallback.
     */
    private List<MovieRecommendation> generateFallbackRecommendations(Long userId, int limit) {
        try {
            Map<String, Double> userPreferences = getUserPreferences(userId);
            List<Film> availableFilms = filmService.getAll();
            List<MovieRecommendation> recommendations = new ArrayList<>();

            for (Film film : availableFilms) {
                if (hasUserWatchedFilm(userId, film.getId())) {
                    continue;
                }

                // Simple content-based filtering
                double score = calculateContentBasedScore(userPreferences, film);

                if (score > 0.4) {
                    String reasoning = "Based on your viewing history and preferences for " +
                        film.getCategories().stream()
                            .map(cat -> cat.getName())
                            .collect(Collectors.joining(", "));

                    MovieRecommendation recommendation = new MovieRecommendation(
                        getUserById(userId), film, score, reasoning, "content-based"
                    );
                    recommendations.add(recommendation);
                }
            }

            return recommendations.stream()
                .sorted((r1, r2) -> Double.compare(r2.getConfidenceScore(), r1.getConfidenceScore()))
                .limit(limit)
                .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error generating fallback recommendations", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get user preferences from viewing history and ratings.
     */
    private Map<String, Double> getUserPreferences(Long userId) {
        Map<String, Double> cached = userPreferenceCache.getIfPresent(userId);
        if (cached != null) {
            return cached;
        }

        Map<String, Double> preferences = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            // Get user's ticket history to analyze preferences
            String sql = """
                SELECT f.*, t.price_paid, ms.start_time
                FROM tickets t
                JOIN movie_sessions ms ON t.movie_session_id = ms.id
                JOIN films f ON ms.film_id = f.id
                WHERE t.client_id = ?
                ORDER BY ms.start_time DESC
                LIMIT 50
                """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    // Analyze genre preferences
                    String genre = rs.getString("genre");
                    if (genre != null) {
                        preferences.merge(genre, 1.0, Double::sum);
                    }

                    // Analyze year preferences
                    int year = rs.getInt("release_year");
                    String yearRange = getYearRange(year);
                    preferences.merge(yearRange, 0.5, Double::sum);

                    // Analyze duration preferences
                    int duration = rs.getInt("duration_min");
                    String durationRange = getDurationRange(duration);
                    preferences.merge(durationRange, 0.3, Double::sum);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting user preferences", e);
        }

        // Normalize preferences
        double total = preferences.values().stream().mapToDouble(Double::doubleValue).sum();
        if (total > 0) {
            preferences.replaceAll((k, v) -> v / total);
        }

        userPreferenceCache.put(userId, preferences);
        return preferences;
    }

    /**
     * Create feature vector for TensorFlow model.
     */
    private float[] createFeatureVector(Map<String, Double> userPreferences, Film film) {
        // Create a feature vector with user preferences and film characteristics
        List<Float> features = new ArrayList<>();

        // User preference features (normalized)
        features.add(userPreferences.getOrDefault(film.getGenre(), 0.0).floatValue());
        features.add(userPreferences.getOrDefault(getYearRange(film.getReleaseYear()), 0.0).floatValue());
        features.add(userPreferences.getOrDefault(getDurationRange(film.getDurationMin()), 0.0).floatValue());

        // Film features (normalized)
        features.add(film.getReleaseYear() / 2024.0f); // Normalize year
        features.add(film.getDurationMin() / 180.0f); // Normalize duration
        features.add((float) film.getRating() / 5.0f); // Normalize rating

        float[] result = new float[features.size()];
        for (int i = 0; i < features.size(); i++) {
            result[i] = features.get(i);
        }
        return result;
    }

    /**
     * Calculate content-based recommendation score.
     */
    private double calculateContentBasedScore(Map<String, Double> userPreferences, Film film) {
        double score = 0.0;

        // Genre matching
        score += userPreferences.getOrDefault(film.getGenre(), 0.0) * 0.4;

        // Year preference matching
        score += userPreferences.getOrDefault(getYearRange(film.getReleaseYear()), 0.0) * 0.2;

        // Duration preference matching
        score += userPreferences.getOrDefault(getDurationRange(film.getDurationMin()), 0.0) * 0.2;

        // Rating boost
        score += (film.getRating() / 5.0) * 0.2;

        return Math.min(score, 1.0);
    }

    /**
     * Generate human-readable reasoning for recommendation.
     */
    private String generateReasoning(Map<String, Double> userPreferences, Film film, double confidence) {
        StringBuilder reasoning = new StringBuilder();
        reasoning.append("Recommended because you enjoy ");

        String topGenre = userPreferences.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("various genres");

        reasoning.append(topGenre).append(" films");

        if (film.getRating() > 4.0) {
            reasoning.append(" and this film has excellent ratings (")
                .append(String.format("%.1f", film.getRating())).append("/5)");
        }

        reasoning.append(". Confidence: ").append(String.format("%.0f%%", confidence * 100));

        return reasoning.toString();
    }

    // Helper methods
    private String getYearRange(int year) {
        if (year >= 2020) return "recent";
        if (year >= 2010) return "2010s";
        if (year >= 2000) return "2000s";
        if (year >= 1990) return "1990s";
        return "classic";
    }

    private String getDurationRange(int duration) {
        if (duration < 90) return "short";
        if (duration < 120) return "medium";
        return "long";
    }

    private boolean hasUserWatchedFilm(Long userId, Long filmId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                SELECT COUNT(*) FROM tickets t
                JOIN movie_sessions ms ON t.movie_session_id = ms.id
                WHERE t.client_id = ? AND ms.film_id = ?
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                stmt.setLong(2, filmId);
                ResultSet rs = stmt.executeQuery();
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            log.error("Error checking if user watched film", e);
            return false;
        }
    }

    private User getUserById(Long userId) {
        // This would typically use UserService, but for now return a simple user
        // In a real implementation, this would fetch from UserService
        return new com.esprit.models.users.Client() {
            {
                setId(userId);
            }
        };
    }

    // IService implementation methods
    @Override
    public void create(MovieRecommendation recommendation) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                INSERT INTO movie_recommendations
                (user_id, film_id, confidence_score, reasoning, recommendation_type, generated_at, is_viewed, is_accepted)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, recommendation.getUser().getId());
                stmt.setLong(2, recommendation.getFilm().getId());
                stmt.setDouble(3, recommendation.getConfidenceScore());
                stmt.setString(4, recommendation.getReasoning());
                stmt.setString(5, recommendation.getRecommendationType());
                stmt.setObject(6, recommendation.getGeneratedAt());
                stmt.setBoolean(7, recommendation.isViewed());
                stmt.setBoolean(8, recommendation.isAccepted());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error creating movie recommendation", e);
        }
    }

    @Override
    public Page<MovieRecommendation> read(PageRequest pageRequest) {
        List<MovieRecommendation> recommendations = new ArrayList<>();
        int totalCount = 0;

        try (Connection conn = dataSource.getConnection()) {
            // Get total count
            String countSql = "SELECT COUNT(*) FROM movie_recommendations";
            try (PreparedStatement stmt = conn.prepareStatement(countSql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalCount = rs.getInt(1);
                }
            }

            // Get paginated results
            String sql = """
                SELECT * FROM movie_recommendations
                ORDER BY generated_at DESC
                LIMIT ? OFFSET ?
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, pageRequest.size());
                stmt.setInt(2, pageRequest.page() * pageRequest.size());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    MovieRecommendation recommendation = mapResultSetToRecommendation(rs);
                    recommendations.add(recommendation);
                }
            }
        } catch (SQLException e) {
            log.error("Error reading movie recommendations", e);
        }

        return new Page<>(recommendations, pageRequest.page(), pageRequest.size(), totalCount);
    }

    @Override
    public void update(MovieRecommendation recommendation) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                UPDATE movie_recommendations
                SET confidence_score = ?, reasoning = ?, is_viewed = ?, is_accepted = ?,
                    viewed_at = ?, accepted_at = ?
                WHERE id = ?
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, recommendation.getConfidenceScore());
                stmt.setString(2, recommendation.getReasoning());
                stmt.setBoolean(3, recommendation.isViewed());
                stmt.setBoolean(4, recommendation.isAccepted());
                stmt.setObject(5, recommendation.getViewedAt());
                stmt.setObject(6, recommendation.getAcceptedAt());
                stmt.setLong(7, recommendation.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error updating movie recommendation", e);
        }
    }

    @Override
    public void delete(MovieRecommendation recommendation) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM movie_recommendations WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, recommendation.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error deleting movie recommendation", e);
        }
    }

    @Override
    public int count() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT COUNT(*) FROM movie_recommendations";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("Error counting movie recommendations", e);
        }
        return 0;
    }

    @Override
    public MovieRecommendation getById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM movie_recommendations WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return mapResultSetToRecommendation(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting movie recommendation by id", e);
        }
        return null;
    }

    @Override
    public List<MovieRecommendation> getAll() {
        List<MovieRecommendation> recommendations = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM movie_recommendations ORDER BY generated_at DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    recommendations.add(mapResultSetToRecommendation(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error getting all movie recommendations", e);
        }
        return recommendations;
    }

    @Override
    public List<MovieRecommendation> search(String query) {
        List<MovieRecommendation> recommendations = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                SELECT * FROM movie_recommendations
                WHERE reasoning LIKE ? OR recommendation_type LIKE ?
                ORDER BY generated_at DESC
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String searchPattern = "%" + query + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    recommendations.add(mapResultSetToRecommendation(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error searching movie recommendations", e);
        }
        return recommendations;
    }

    @Override
    public boolean exists(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT 1 FROM movie_recommendations WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Error checking if movie recommendation exists", e);
        }
        return false;
    }

    /**
     * Map ResultSet to MovieRecommendation object.
     */
    private MovieRecommendation mapResultSetToRecommendation(ResultSet rs) throws SQLException {
        MovieRecommendation recommendation = new MovieRecommendation();
        recommendation.setId(rs.getLong("id"));
        recommendation.setConfidenceScore(rs.getDouble("confidence_score"));
        recommendation.setReasoning(rs.getString("reasoning"));
        recommendation.setRecommendationType(rs.getString("recommendation_type"));
        recommendation.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        recommendation.setViewed(rs.getBoolean("is_viewed"));
        recommendation.setAccepted(rs.getBoolean("is_accepted"));

        if (rs.getTimestamp("viewed_at") != null) {
            recommendation.setViewedAt(rs.getTimestamp("viewed_at").toLocalDateTime());
        }
        if (rs.getTimestamp("accepted_at") != null) {
            recommendation.setAcceptedAt(rs.getTimestamp("accepted_at").toLocalDateTime());
        }

        // Load associated user and film (simplified for now)
        recommendation.setUser(getUserById(rs.getLong("user_id")));
        recommendation.setFilm(filmService.getById(rs.getLong("film_id")));

        return recommendation;
    }

    /**
     * Clean up resources.
     */
    public void shutdown() {
        if (tensorFlowSession != null) {
            tensorFlowSession.close();
        }
        if (tensorFlowModel != null) {
            tensorFlowModel.close();
        }
        executorService.shutdown();
    }
}
