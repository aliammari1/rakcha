package com.esprit.models.films;

import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.common.Category;
import com.esprit.models.common.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
/**
 * Entity class for the RAKCHA application. Provides data persistence using
 * Hibernate/JPA annotations.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */


public class Film {

    private Long id;

    private String title;

    private String imageUrl;

    private int durationMin;

    private String description;

    private int releaseYear;

    private String trailerUrl;

    private String ageRating;

    private String language;

    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @Builder.Default
    private List<Actor> actors = new ArrayList<>();

    @Builder.Default
    private List<MovieSession> movieSessions = new ArrayList<>();

    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    /**
     * Constructor without id for creating new film instances.
     *
     * @param title       the title of the film
     * @param imageUrl    the image path or URL for the film
     * @param durationMin the duration of the film in minutes
     * @param description the description of the film
     * @param releaseYear the year the film was released
     * @param trailerUrl  the trailer URL of the film
     */
    public Film(final String title, final String imageUrl, final int durationMin, final String description,
                final int releaseYear, final String trailerUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.durationMin = durationMin;
        this.description = description;
        this.releaseYear = releaseYear;
        this.trailerUrl = trailerUrl;
        this.ageRating = null;
        this.language = null;
        this.categories = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    /**
     * Constructor without id for creating new film instances.
     *
     * @param title       the title of the film
     * @param imageUrl    the image path or URL for the film
     * @param durationMin the duration of the film in minutes
     * @param description the description of the film
     * @param releaseYear the year the film was released
     * @param trailerUrl  the trailer URL of the film
     * @param ageRating   the age rating of the film
     * @param language    the language of the film
     */
    public Film(final String title, final String imageUrl, final int durationMin, final String description,
                final int releaseYear, final String trailerUrl, final String ageRating, final String language) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.durationMin = durationMin;
        this.description = description;
        this.releaseYear = releaseYear;
        this.trailerUrl = trailerUrl;
        this.ageRating = ageRating;
        this.language = language;
        this.categories = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    /**
     * Constructor with id for existing film instances.
     *
     * @param id          the unique identifier for the film
     * @param title       the title of the film
     * @param imageUrl    the image path or URL for the film
     * @param durationMin the duration of the film in minutes
     * @param description the description of the film
     * @param releaseYear the year the film was released
     * @param trailerUrl  the trailer URL of the film
     */
    public Film(final Long id, final String title, final String imageUrl, final int durationMin,
                final String description,
                final int releaseYear, final String trailerUrl) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.durationMin = durationMin;
        this.description = description;
        this.releaseYear = releaseYear;
        this.trailerUrl = trailerUrl;
        this.ageRating = null;
        this.language = null;
        this.categories = new ArrayList<>();
        this.actors = new ArrayList<>();
        this.movieSessions = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    /**
     * Create a new Film initialized with the same field values as the given Film.
     * <p>
     * Primitive and object fields are copied directly. Each collection field is set
     * to a new ArrayList
     * containing the same elements as the source (shallow copy). If a source list
     * is null, the corresponding
     * field is initialized to an empty list.
     *
     * @param f the source Film to copy
     */
    public Film(Film f) {
        this.id = f.id;
        this.title = f.title;
        this.imageUrl = f.imageUrl;
        this.durationMin = f.durationMin;
        this.description = f.description;
        this.releaseYear = f.releaseYear;
        this.trailerUrl = f.trailerUrl;
        this.ageRating = f.ageRating;
        this.language = f.language;
        this.categories = new ArrayList<>(f.categories != null ? f.categories : new ArrayList<>());
        this.actors = new ArrayList<>(f.actors != null ? f.actors : new ArrayList<>());
        this.movieSessions = new ArrayList<>(f.movieSessions != null ? f.movieSessions : new ArrayList<>());
        this.reviews = new ArrayList<>(f.reviews != null ? f.reviews : new ArrayList<>());
    }

    /**
     * Constructor for creating a Film with just an ID.
     *
     * @param id the unique identifier for the film
     */
    public Film(final Long id) {
        this.id = id;
    }

    /**
     * Get the film rating/note (calculated from reviews or stored rating).
     *
     * @return the rating as a double
     */
    public double getRating() {
        // If we have reviews, calculate average rating
        if (this.reviews != null && !this.reviews.isEmpty()) {
            return this.reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        }
        return 0.0;
    }

    /**
     * Get the film genre (from categories or first category).
     *
     * @return the genre as a string
     */
    public String getGenre() {
        if (this.categories != null && !this.categories.isEmpty()) {
            return this.categories.get(0).getName();
        }
        return "Unknown";
    }
}


