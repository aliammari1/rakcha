package com.esprit.models.series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * Season entity class for the RAKCHA application.
 * Represents a season within a series, containing multiple episodes.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

public class Season {

    /**
     * The series this season belongs to.
     */
    private Series series;
    private int seasonNumber;
    private Long id;
    private String title;

    /**
     * Episodes in this season.
     */
    @Builder.Default
    private List<Episode> episodes = new ArrayList<>();

    /**
     * Constructor for creating a new season.
     *
     * @param series       the series this season belongs to
     * @param seasonNumber the season number
     * @param title        the season title
     */
    public Season(final Series series, final int seasonNumber, final String title) {
        this.series = series;
        this.seasonNumber = seasonNumber;
        this.title = title;
        this.episodes = new ArrayList<>();
    }

    /**
     * Get the season description (alias for compatibility).
     * For seasons, the title serves as description.
     *
     * @return the season title/description
     */
    public String getDescription() {
        return this.title;
    }

    /**
     * Set the season description (alias for compatibility).
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.title = description;
    }

}


