package com.esprit.models.users;

import com.esprit.models.films.Film;
import com.esprit.models.series.Episode;
import com.esprit.models.series.Series;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Log4j2
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class WatchProgress {

    // Status
    @Builder.Default
    private final WatchStatus status = WatchStatus.NOT_STARTED;
    private Long id;
    /**
     * The client tracking this progress.
     */
    private Client user;
    /**
     * The series being watched (if applicable).
     */
    private Series series;
    /**
     * The film being watched (if applicable).
     */
    private Film film;
    // Progress tracking
    private int watchedEpisodes;
    private int totalEpisodes;
    private double progress; // 0.0 to 1.0
    // Current position
    private Episode lastWatchedEpisode;
    private int lastWatchedSeason;
    private int lastWatchedEpisodeNum;
    private int lastPosition; // Position in seconds within the episode
    // Time tracking
    private int watchedMinutes;
    private LocalDateTime lastWatchedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    // User rating (1-5)
    private int userRating;

    /**
     * Checks if the series/film is completed.
     */
    public boolean isCompleted() {
        return progress >= 1.0 || status == WatchStatus.COMPLETED;
    }

    /**
     * Checks if the user has started watching.
     */
    public boolean hasStarted() {
        return progress > 0 || status == WatchStatus.IN_PROGRESS;
    }

    /**
     * Enum representing the watch status of content.
     */
    public enum WatchStatus {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED,
        DROPPED,
        PLAN_TO_WATCH
    }

}
