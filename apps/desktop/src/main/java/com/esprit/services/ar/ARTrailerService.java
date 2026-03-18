package com.esprit.services.ar;

import com.esprit.models.ar.ARContent;
import com.esprit.models.ar.ARContent.ARContentType;
import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.Film;
import com.esprit.services.IService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import lombok.extern.log4j.Log4j2;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AR Trailer Service implementing IService<ARContent> following existing service architecture.
 * Provides AR content management for movie trailers and interactive experiences with camera overlay.
 * Uses existing camera access patterns for AR overlay functionality.
 * <p>
 * Requirements: 3.1, 3.4
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Log4j2
public class ARTrailerService implements IService<ARContent> {

    // AR overlay parameters
    private static final int AR_OVERLAY_WIDTH = 640;
    private static final int AR_OVERLAY_HEIGHT = 480;
    private static final Scalar AR_OVERLAY_COLOR = new Scalar(0, 255, 0); // Green overlay
    private final Connection connection;
    private final ExecutorService arProcessingExecutor;
    private VideoCapture arCamera;
    private boolean isARActive = false;

    public ARTrailerService() {
        this.connection = DataSource.getInstance().getConnection();
        this.arProcessingExecutor = Executors.newFixedThreadPool(2);
        createTableIfNotExists();
        initializeARCamera();
    }

    /**
     * Initialize AR camera using existing camera access patterns
     */
    private void initializeARCamera() {
        try {
            arCamera = new VideoCapture(0); // Use default camera like ComputerVisionUtil
            if (arCamera.isOpened()) {
                arCamera.set(3, AR_OVERLAY_WIDTH);  // Width
                arCamera.set(4, AR_OVERLAY_HEIGHT); // Height
                log.info("AR camera initialized successfully");
            } else {
                log.warn("AR camera could not be opened - AR overlay features will be limited");
            }
        } catch (Exception e) {
            log.error("Failed to initialize AR camera", e);
        }
    }

    /**
     * Start AR overlay for trailer content
     *
     * @param content the AR content to overlay
     * @return CompletableFuture for async AR processing
     */
    public CompletableFuture<Void> startAROverlay(ARContent content) {
        if (!arCamera.isOpened()) {
            log.warn("AR camera not available - cannot start AR overlay");
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.runAsync(() -> {
            isARActive = true;
            Mat frame = new Mat();

            log.info("Starting AR overlay for content: {}", content.getTitle());

            while (isARActive && arCamera.isOpened()) {
                try {
                    if (arCamera.read(frame) && !frame.empty()) {
                        // Apply AR overlay based on content type
                        applyAROverlay(frame, content);

                        // Increment view count
                        content.incrementViewCount();

                        Thread.sleep(33); // ~30 FPS
                    }
                } catch (Exception e) {
                    log.error("Error in AR overlay processing", e);
                    break;
                }
            }

            log.info("AR overlay stopped for content: {}", content.getTitle());
        }, arProcessingExecutor);
    }

    /**
     * Apply AR overlay effects based on content type
     */
    private void applyAROverlay(Mat frame, ARContent content) {
        switch (content.getContentType()) {
            case TRAILER -> applyTrailerOverlay(frame, content);
            case POSTER -> applyPosterOverlay(frame, content);
            case THEATER_TOUR -> applyTheaterTourOverlay(frame, content);
            case ACTOR_INFO -> applyActorInfoOverlay(frame, content);
            case INTERACTIVE_SCENE -> applyInteractiveSceneOverlay(frame, content);
        }
    }

    /**
     * Apply trailer-specific AR overlay
     */
    private void applyTrailerOverlay(Mat frame, ARContent content) {
        // Add trailer information overlay
        String overlayText = "AR Trailer: " + content.getTitle();
        Imgproc.putText(frame, overlayText,
            new org.opencv.core.Point(10, 30),
            Imgproc.FONT_HERSHEY_SIMPLEX,
            1.0, AR_OVERLAY_COLOR, 2);

        // Add duration overlay
        if (content.getDurationSeconds() != null) {
            String durationText = "Duration: " + content.getFormattedDuration();
            Imgproc.putText(frame, durationText,
                new org.opencv.core.Point(10, 60),
                Imgproc.FONT_HERSHEY_SIMPLEX,
                0.7, AR_OVERLAY_COLOR, 2);
        }

        // Add interactive hotspots if available
        if (content.getHotspotsData() != null) {
            addInteractiveHotspots(frame, content.getHotspotsData());
        }
    }

    /**
     * Apply poster-specific AR overlay
     */
    private void applyPosterOverlay(Mat frame, ARContent content) {
        String overlayText = "AR Poster: " + content.getTitle();
        Imgproc.putText(frame, overlayText,
            new org.opencv.core.Point(10, 30),
            Imgproc.FONT_HERSHEY_SIMPLEX,
            1.0, new Scalar(255, 0, 0), 2); // Blue for posters

        // Add rating overlay if available
        if (content.getAverageRating() != null && content.getAverageRating() > 0) {
            String ratingText = String.format("Rating: %.1f/5.0", content.getAverageRating());
            Imgproc.putText(frame, ratingText,
                new org.opencv.core.Point(10, 60),
                Imgproc.FONT_HERSHEY_SIMPLEX,
                0.7, new Scalar(255, 0, 0), 2);
        }
    }

    /**
     * Apply theater tour AR overlay
     */
    private void applyTheaterTourOverlay(Mat frame, ARContent content) {
        String overlayText = "Theater Tour: " + content.getContentSource();
        Imgproc.putText(frame, overlayText,
            new org.opencv.core.Point(10, 30),
            Imgproc.FONT_HERSHEY_SIMPLEX,
            1.0, new Scalar(0, 0, 255), 2); // Red for theater tours

        // Add navigation hints
        Imgproc.putText(frame, "Move device to explore",
            new org.opencv.core.Point(10, frame.height() - 30),
            Imgproc.FONT_HERSHEY_SIMPLEX,
            0.6, new Scalar(0, 0, 255), 2);
    }

    /**
     * Apply actor info AR overlay
     */
    private void applyActorInfoOverlay(Mat frame, ARContent content) {
        String overlayText = "Actor Info: " + content.getContentSource();
        Imgproc.putText(frame, overlayText,
            new org.opencv.core.Point(10, 30),
            Imgproc.FONT_HERSHEY_SIMPLEX,
            1.0, new Scalar(255, 255, 0), 2); // Cyan for actor info
    }

    /**
     * Apply interactive scene AR overlay
     */
    private void applyInteractiveSceneOverlay(Mat frame, ARContent content) {
        String overlayText = "Interactive Scene: " + content.getTitle();
        Imgproc.putText(frame, overlayText,
            new org.opencv.core.Point(10, 30),
            Imgproc.FONT_HERSHEY_SIMPLEX,
            1.0, new Scalar(255, 0, 255), 2); // Magenta for interactive scenes

        // Add interaction hints
        Imgproc.putText(frame, "Tap to interact",
            new org.opencv.core.Point(10, frame.height() - 30),
            Imgproc.FONT_HERSHEY_SIMPLEX,
            0.6, new Scalar(255, 0, 255), 2);
    }

    /**
     * Add interactive hotspots to the AR overlay
     */
    private void addInteractiveHotspots(Mat frame, String hotspotsData) {
        // Simple implementation - in production, parse JSON hotspots data
        // Add circular hotspots at predefined locations
        org.opencv.core.Point[] hotspotLocations = {
            new org.opencv.core.Point(100, 100),
            new org.opencv.core.Point(300, 200),
            new org.opencv.core.Point(500, 150)
        };

        for (org.opencv.core.Point location : hotspotLocations) {
            Imgproc.circle(frame, location, 20, new Scalar(0, 255, 255), 3); // Yellow circles
            Imgproc.putText(frame, "i",
                new org.opencv.core.Point(location.x - 5, location.y + 5),
                Imgproc.FONT_HERSHEY_SIMPLEX,
                0.8, new Scalar(0, 255, 255), 2);
        }
    }

    /**
     * Stop AR overlay processing
     */
    public void stopAROverlay() {
        isARActive = false;
        if (arCamera != null && arCamera.isOpened()) {
            arCamera.release();
        }
        log.info("AR overlay stopped and camera released");
    }

    /**
     * Check if AR overlay is currently active
     */
    public boolean isARActive() {
        return isARActive;
    }

    /**
     * Get AR content by film
     */
    public List<ARContent> getByFilm(Film film) {
        List<ARContent> results = new ArrayList<>();
        String sql = "SELECT * FROM ar_content WHERE film_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, film.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToARContent(rs));
            }
        } catch (SQLException e) {
            log.error("Error retrieving AR content by film", e);
        }

        return results;
    }

    /**
     * Get AR content by cinema
     */
    public List<ARContent> getByCinema(Cinema cinema) {
        List<ARContent> results = new ArrayList<>();
        String sql = "SELECT * FROM ar_content WHERE cinema_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, cinema.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToARContent(rs));
            }
        } catch (SQLException e) {
            log.error("Error retrieving AR content by cinema", e);
        }

        return results;
    }

    /**
     * Get AR content by type
     */
    public List<ARContent> getByContentType(ARContentType contentType) {
        List<ARContent> results = new ArrayList<>();
        String sql = "SELECT * FROM ar_content WHERE content_type = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, contentType.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToARContent(rs));
            }
        } catch (SQLException e) {
            log.error("Error retrieving AR content by type", e);
        }

        return results;
    }

    @Override
    public void create(ARContent content) {
        String sql = """
            INSERT INTO ar_content (content_type, title, description, film_id, cinema_id, actor_id,
                                  ar_asset_url, preview_image_url, duration_seconds, scene_config,
                                  hotspots_data, camera_settings, lighting_config, animation_data,
                                  audio_url, requires_camera, supports_hand_tracking, device_requirements,
                                  content_rating, tags, metadata, created_at, updated_at, is_active,
                                  view_count, average_rating, file_size_bytes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, content.getContentType().name());
            stmt.setString(2, content.getTitle());
            stmt.setString(3, content.getDescription());
            stmt.setObject(4, content.getFilm() != null ? content.getFilm().getId() : null);
            stmt.setObject(5, content.getCinema() != null ? content.getCinema().getId() : null);
            stmt.setObject(6, content.getActor() != null ? content.getActor().getId() : null);
            stmt.setString(7, content.getArAssetUrl());
            stmt.setString(8, content.getPreviewImageUrl());
            stmt.setObject(9, content.getDurationSeconds());
            stmt.setString(10, content.getSceneConfig());
            stmt.setString(11, content.getHotspotsData());
            stmt.setString(12, content.getCameraSettings());
            stmt.setString(13, content.getLightingConfig());
            stmt.setString(14, content.getAnimationData());
            stmt.setString(15, content.getAudioUrl());
            stmt.setBoolean(16, content.getRequiresCamera() != null ? content.getRequiresCamera() : false);
            stmt.setBoolean(17, content.getSupportsHandTracking() != null ? content.getSupportsHandTracking() : false);
            stmt.setString(18, content.getDeviceRequirements());
            stmt.setString(19, content.getContentRating());
            stmt.setString(20, content.getTags() != null ? String.join(",", content.getTags()) : null);
            stmt.setString(21, content.getMetadata());
            stmt.setTimestamp(22, Timestamp.valueOf(content.getCreatedAt() != null ? content.getCreatedAt() : LocalDateTime.now()));
            stmt.setTimestamp(23, Timestamp.valueOf(content.getUpdatedAt() != null ? content.getUpdatedAt() : LocalDateTime.now()));
            stmt.setBoolean(24, content.getIsActive() != null ? content.getIsActive() : true);
            stmt.setLong(25, content.getViewCount() != null ? content.getViewCount() : 0L);
            stmt.setDouble(26, content.getAverageRating() != null ? content.getAverageRating() : 0.0);
            stmt.setObject(27, content.getFileSizeBytes());

            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                content.setId(generatedKeys.getLong(1));
            }

            log.info("Created AR content: {}", content.getTitle());
        } catch (SQLException e) {
            log.error("Error creating AR content", e);
            throw new RuntimeException("Failed to create AR content", e);
        }
    }

    @Override
    public Page<ARContent> read(PageRequest pageRequest) {
        List<ARContent> results = new ArrayList<>();
        String sql = "SELECT * FROM ar_content ORDER BY created_at DESC LIMIT ? OFFSET ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pageRequest.size());
            stmt.setInt(2, pageRequest.page() * pageRequest.size());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToARContent(rs));
            }
        } catch (SQLException e) {
            log.error("Error reading AR content with pagination", e);
        }

        int totalElements = count();
        return new Page<>(results, pageRequest.page(), pageRequest.size(), totalElements);
    }

    @Override
    public void update(ARContent content) {
        String sql = """
            UPDATE ar_content SET content_type = ?, title = ?, description = ?, film_id = ?, cinema_id = ?, actor_id = ?,
                                ar_asset_url = ?, preview_image_url = ?, duration_seconds = ?, scene_config = ?,
                                hotspots_data = ?, camera_settings = ?, lighting_config = ?, animation_data = ?,
                                audio_url = ?, requires_camera = ?, supports_hand_tracking = ?, device_requirements = ?,
                                content_rating = ?, tags = ?, metadata = ?, updated_at = ?, is_active = ?,
                                view_count = ?, average_rating = ?, file_size_bytes = ?
            WHERE id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, content.getContentType().name());
            stmt.setString(2, content.getTitle());
            stmt.setString(3, content.getDescription());
            stmt.setObject(4, content.getFilm() != null ? content.getFilm().getId() : null);
            stmt.setObject(5, content.getCinema() != null ? content.getCinema().getId() : null);
            stmt.setObject(6, content.getActor() != null ? content.getActor().getId() : null);
            stmt.setString(7, content.getArAssetUrl());
            stmt.setString(8, content.getPreviewImageUrl());
            stmt.setObject(9, content.getDurationSeconds());
            stmt.setString(10, content.getSceneConfig());
            stmt.setString(11, content.getHotspotsData());
            stmt.setString(12, content.getCameraSettings());
            stmt.setString(13, content.getLightingConfig());
            stmt.setString(14, content.getAnimationData());
            stmt.setString(15, content.getAudioUrl());
            stmt.setBoolean(16, content.getRequiresCamera() != null ? content.getRequiresCamera() : false);
            stmt.setBoolean(17, content.getSupportsHandTracking() != null ? content.getSupportsHandTracking() : false);
            stmt.setString(18, content.getDeviceRequirements());
            stmt.setString(19, content.getContentRating());
            stmt.setString(20, content.getTags() != null ? String.join(",", content.getTags()) : null);
            stmt.setString(21, content.getMetadata());
            stmt.setTimestamp(22, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setBoolean(23, content.getIsActive() != null ? content.getIsActive() : true);
            stmt.setLong(24, content.getViewCount() != null ? content.getViewCount() : 0L);
            stmt.setDouble(25, content.getAverageRating() != null ? content.getAverageRating() : 0.0);
            stmt.setObject(26, content.getFileSizeBytes());
            stmt.setLong(27, content.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Updated AR content: {}", content.getTitle());
            } else {
                log.warn("No AR content found with ID: {}", content.getId());
            }
        } catch (SQLException e) {
            log.error("Error updating AR content", e);
            throw new RuntimeException("Failed to update AR content", e);
        }
    }

    @Override
    public void delete(ARContent content) {
        String sql = "DELETE FROM ar_content WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, content.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                log.info("Deleted AR content: {}", content.getTitle());
            } else {
                log.warn("No AR content found with ID: {}", content.getId());
            }
        } catch (SQLException e) {
            log.error("Error deleting AR content", e);
            throw new RuntimeException("Failed to delete AR content", e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM ar_content";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error counting AR content", e);
        }

        return 0;
    }

    @Override
    public ARContent getById(Long id) {
        String sql = "SELECT * FROM ar_content WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToARContent(rs);
            }
        } catch (SQLException e) {
            log.error("Error retrieving AR content by ID", e);
        }

        return null;
    }

    @Override
    public List<ARContent> getAll() {
        List<ARContent> results = new ArrayList<>();
        String sql = "SELECT * FROM ar_content ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                results.add(mapResultSetToARContent(rs));
            }
        } catch (SQLException e) {
            log.error("Error retrieving all AR content", e);
        }

        return results;
    }

    @Override
    public List<ARContent> search(String query) {
        List<ARContent> results = new ArrayList<>();
        String sql = "SELECT * FROM ar_content WHERE title LIKE ? OR description LIKE ? OR tags LIKE ? ORDER BY created_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToARContent(rs));
            }
        } catch (SQLException e) {
            log.error("Error searching AR content", e);
        }

        return results;
    }

    @Override
    public boolean exists(Long id) {
        String sql = "SELECT 1 FROM ar_content WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            log.error("Error checking AR content existence", e);
        }

        return false;
    }

    /**
     * Map ResultSet to ARContent object
     */
    private ARContent mapResultSetToARContent(ResultSet rs) throws SQLException {
        ARContent content = new ARContent();
        content.setId(rs.getLong("id"));
        content.setContentType(ARContentType.valueOf(rs.getString("content_type")));
        content.setTitle(rs.getString("title"));
        content.setDescription(rs.getString("description"));
        // Note: Film, Cinema, Actor would need to be loaded separately in a full implementation
        content.setArAssetUrl(rs.getString("ar_asset_url"));
        content.setPreviewImageUrl(rs.getString("preview_image_url"));
        content.setDurationSeconds(rs.getObject("duration_seconds", Integer.class));
        content.setSceneConfig(rs.getString("scene_config"));
        content.setHotspotsData(rs.getString("hotspots_data"));
        content.setCameraSettings(rs.getString("camera_settings"));
        content.setLightingConfig(rs.getString("lighting_config"));
        content.setAnimationData(rs.getString("animation_data"));
        content.setAudioUrl(rs.getString("audio_url"));
        content.setRequiresCamera(rs.getBoolean("requires_camera"));
        content.setSupportsHandTracking(rs.getBoolean("supports_hand_tracking"));
        content.setDeviceRequirements(rs.getString("device_requirements"));
        content.setContentRating(rs.getString("content_rating"));

        String tagsString = rs.getString("tags");
        if (tagsString != null && !tagsString.isEmpty()) {
            content.setTags(List.of(tagsString.split(",")));
        }

        content.setMetadata(rs.getString("metadata"));
        content.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        content.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        content.setIsActive(rs.getBoolean("is_active"));
        content.setViewCount(rs.getLong("view_count"));
        content.setAverageRating(rs.getDouble("average_rating"));
        content.setFileSizeBytes(rs.getObject("file_size_bytes", Long.class));

        return content;
    }

    /**
     * Create the AR content table if it doesn't exist
     */
    private void createTableIfNotExists() {
        String sql = """
            CREATE TABLE IF NOT EXISTS ar_content (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                content_type VARCHAR(50) NOT NULL,
                title VARCHAR(255) NOT NULL,
                description TEXT,
                film_id BIGINT,
                cinema_id BIGINT,
                actor_id BIGINT,
                ar_asset_url VARCHAR(500),
                preview_image_url VARCHAR(500),
                duration_seconds INT,
                scene_config TEXT,
                hotspots_data TEXT,
                camera_settings TEXT,
                lighting_config TEXT,
                animation_data TEXT,
                audio_url VARCHAR(500),
                requires_camera BOOLEAN DEFAULT FALSE,
                supports_hand_tracking BOOLEAN DEFAULT FALSE,
                device_requirements TEXT,
                content_rating VARCHAR(10),
                tags TEXT,
                metadata TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                is_active BOOLEAN DEFAULT TRUE,
                view_count BIGINT DEFAULT 0,
                average_rating DOUBLE DEFAULT 0.0,
                file_size_bytes BIGINT,
                INDEX idx_content_type (content_type),
                INDEX idx_film_id (film_id),
                INDEX idx_cinema_id (cinema_id),
                INDEX idx_actor_id (actor_id),
                INDEX idx_is_active (is_active),
                INDEX idx_created_at (created_at)
            )
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            log.info("AR content table initialized successfully");
        } catch (SQLException e) {
            log.error("Error creating AR content table", e);
            throw new RuntimeException("Failed to create AR content table", e);
        }
    }

    /**
     * Cleanup resources when service is destroyed
     */
    public void cleanup() {
        stopAROverlay();
        if (arProcessingExecutor != null && !arProcessingExecutor.isShutdown()) {
            arProcessingExecutor.shutdown();
        }
        log.info("ARTrailerService cleanup completed");
    }
}
