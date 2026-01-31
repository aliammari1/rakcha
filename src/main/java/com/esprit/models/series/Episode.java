package com.esprit.models.series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;

@Log4j2
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Episode {

    /**
     * The season this episode belongs to.
     */
    private Season season;
    private int episodeNumber;
    private String videoUrl;
    private LocalDate releaseDate;
    private Long id;
    private String title;
    private String imageUrl;
    private Integer durationMin;

    /**
     * Create a new Episode populated with title, episode number, season, image,
     * video, and season identifier.
     *
     * @param season        the season this episode belongs to
     * @param episodeNumber the episode number within its season
     * @param title         the episode title
     * @param imageUrl      the episode image URL or path
     * @param videoUrl      the episode video URL or path
     * @param durationMin   the duration in minutes
     * @param releaseDate   the release date of the episode
     */
    public Episode(final Season season, final int episodeNumber, final String title, final String imageUrl,
                   final String videoUrl, final Integer durationMin, final LocalDate releaseDate) {
        this.season = season;
        this.episodeNumber = episodeNumber;
        this.title = title;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.durationMin = durationMin;
        this.releaseDate = releaseDate;
    }

    /**
     * Get the episode thumbnail URL (alias for compatibility).
     *
     * @return the image/thumbnail URL
     */
    public String getThumbnail() {
        return this.imageUrl;
    }

    /**
     * Set the episode thumbnail URL (alias for compatibility).
     *
     * @param thumbnail the thumbnail URL to set
     */
    public void setThumbnail(String thumbnail) {
        this.imageUrl = thumbnail;
    }

    /**
     * Get the episode duration in minutes (alias for compatibility).
     *
     * @return the duration in minutes
     */
    public Integer getDuration() {
        return this.durationMin;
    }

    /**
     * Set the episode duration in minutes (alias for compatibility).
     *
     * @param duration the duration in minutes
     */
    public void setDuration(Integer duration) {
        this.durationMin = duration;
    }

    /**
     * Get the episode description (alias for compatibility).
     * For episodes, the title serves as description.
     *
     * @return the episode title/description
     */
    public String getDescription() {
        return this.title;
    }

    /**
     * Set the episode description (alias for compatibility).
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.title = description;
    }

}




