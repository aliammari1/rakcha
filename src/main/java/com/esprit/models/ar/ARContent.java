package com.esprit.models.ar;

import com.esprit.models.cinemas.Cinema;
import com.esprit.models.films.Actor;
import com.esprit.models.films.Film;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ARContent {

    private Long id;

    /**
     * Type of AR content (TRAILER, POSTER, THEATER_TOUR, ACTOR_INFO, INTERACTIVE_SCENE)
     */
    private ARContentType contentType;

    /**
     * Title/name of the AR content
     */
    private String title;

    /**
     * Description of the AR content
     */
    private String description;

    /**
     * Associated film for movie-related AR content
     */
    private Film film;

    /**
     * Associated cinema for theater tour AR content
     */
    private Cinema cinema;

    /**
     * Associated actor for actor-specific AR content
     */
    private Actor actor;

    /**
     * URL to the 3D model or AR asset
     */
    private String arAssetUrl;

    /**
     * URL to the preview image/thumbnail
     */
    private String previewImageUrl;

    /**
     * Duration of the AR experience in seconds
     */
    private Integer durationSeconds;

    /**
     * 3D scene configuration data (JSON format)
     */
    private String sceneConfig;

    /**
     * Interactive hotspots data (JSON format)
     */
    private String hotspotsData;

    /**
     * Camera settings for AR view (JSON format)
     */
    private String cameraSettings;

    /**
     * Lighting configuration for 3D scene (JSON format)
     */
    private String lightingConfig;

    /**
     * Animation sequences data (JSON format)
     */
    private String animationData;

    /**
     * Audio track URL for the AR experience
     */
    private String audioUrl;

    /**
     * Whether this AR content requires camera access
     */
    private Boolean requiresCamera;

    /**
     * Whether this AR content supports hand tracking
     */
    private Boolean supportsHandTracking;

    /**
     * Minimum device capabilities required (JSON format)
     */
    private String deviceRequirements;

    /**
     * Content rating (G, PG, PG-13, R, etc.)
     */
    private String contentRating;

    /**
     * Tags for categorization and search
     */
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    /**
     * Custom metadata for extensibility (JSON format)
     */
    private String metadata;

    /**
     * Creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;

    /**
     * Whether the content is active/published
     */
    private Boolean isActive;

    /**
     * View count for analytics
     */
    private Long viewCount;

    /**
     * Average user rating (1-5 stars)
     */
    private Double averageRating;

    /**
     * File size in bytes for the AR assets
     */
    private Long fileSizeBytes;

    /**
     * Constructor for creating new AR content without ID
     */
    public ARContent(ARContentType contentType, String title, String description, String arAssetUrl) {
        this.contentType = contentType;
        this.title = title;
        this.description = description;
        this.arAssetUrl = arAssetUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isActive = true;
        this.viewCount = 0L;
        this.averageRating = 0.0;
        this.tags = new ArrayList<>();
    }

    /**
     * Constructor for film-related AR content
     */
    public ARContent(ARContentType contentType, String title, Film film, String arAssetUrl) {
        this(contentType, title, film.getDescription(), arAssetUrl);
        this.film = film;
    }

    /**
     * Constructor for cinema-related AR content
     */
    public ARContent(ARContentType contentType, String title, Cinema cinema, String arAssetUrl) {
        this(contentType, title, "AR experience for " + cinema.getName(), arAssetUrl);
        this.cinema = cinema;
    }

    /**
     * Constructor for actor-related AR content
     */
    public ARContent(ARContentType contentType, String title, Actor actor, String arAssetUrl) {
        this(contentType, title, actor.getBiography(), arAssetUrl);
        this.actor = actor;
    }

    /**
     * Get the display name for this AR content
     */
    public String getDisplayName() {
        if (title != null && !title.isEmpty()) {
            return title;
        }

        if (film != null) {
            return "AR: " + film.getTitle();
        }

        if (cinema != null) {
            return "AR Tour: " + cinema.getName();
        }

        if (actor != null) {
            return "AR: " + actor.getName();
        }

        return "AR Content #" + id;
    }

    /**
     * Get the content source (Film, Cinema, or Actor name)
     */
    public String getContentSource() {
        if (film != null) {
            return film.getTitle();
        }

        if (cinema != null) {
            return cinema.getName();
        }

        if (actor != null) {
            return actor.getName();
        }

        return "Unknown";
    }

    /**
     * Check if this AR content is ready for display
     */
    public boolean isReady() {
        return isActive != null && isActive &&
            arAssetUrl != null && !arAssetUrl.isEmpty() &&
            title != null && !title.isEmpty();
    }

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        if (viewCount == null) {
            viewCount = 0L;
        }
        viewCount++;
        updatedAt = LocalDateTime.now();
    }

    /**
     * Update rating with new rating value
     */
    public void updateRating(double newRating, long totalRatings) {
        if (averageRating == null) {
            averageRating = 0.0;
        }

        // Simple average calculation - in production, you might want more sophisticated rating logic
        averageRating = ((averageRating * (totalRatings - 1)) + newRating) / totalRatings;
        updatedAt = LocalDateTime.now();
    }

    /**
     * Add a tag to the content
     */
    public void addTag(String tag) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        if (!tags.contains(tag)) {
            tags.add(tag);
            updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Remove a tag from the content
     */
    public void removeTag(String tag) {
        if (tags != null && tags.contains(tag)) {
            tags.remove(tag);
            updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Get formatted duration string
     */
    public String getFormattedDuration() {
        if (durationSeconds == null || durationSeconds <= 0) {
            return "Unknown duration";
        }

        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;

        if (minutes > 0) {
            return String.format("%d:%02d", minutes, seconds);
        } else {
            return String.format("%d seconds", seconds);
        }
    }

    /**
     * Get formatted file size string
     */
    public String getFormattedFileSize() {
        if (fileSizeBytes == null || fileSizeBytes <= 0) {
            return "Unknown size";
        }

        if (fileSizeBytes < 1024) {
            return fileSizeBytes + " B";
        } else if (fileSizeBytes < 1024 * 1024) {
            return String.format("%.1f KB", fileSizeBytes / 1024.0);
        } else if (fileSizeBytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", fileSizeBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", fileSizeBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    /**
     * Check if content has specific tag
     */
    public boolean hasTag(String tag) {
        return tags != null && tags.contains(tag);
    }

    /**
     * Get content type display name
     */
    public String getContentTypeDisplayName() {
        if (contentType == null) {
            return "Unknown";
        }

        return switch (contentType) {
            case TRAILER -> "AR Trailer";
            case POSTER -> "AR Poster";
            case THEATER_TOUR -> "Theater Tour";
            case ACTOR_INFO -> "Actor Information";
            case INTERACTIVE_SCENE -> "Interactive Scene";
        };
    }

    /**
     * Enum for AR content types
     */
    public enum ARContentType {
        TRAILER,
        POSTER,
        THEATER_TOUR,
        ACTOR_INFO,
        INTERACTIVE_SCENE
    }
}
