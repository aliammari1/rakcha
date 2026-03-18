package com.esprit.models.series;

import com.esprit.models.films.Film;
import com.esprit.models.users.Client;
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
/**
 * User Favorites entity class for the RAKCHA application. Links users to their favorite films and series.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */


public class Favorite {

    private Client user;
    private Long id;
    private Film movie;

    private Series series;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Creates a Favorite that links a user to a series.
     *
     * @param user   the user
     * @param series the series
     */
    public Favorite(final Client user, final Series series) {
        this.user = user;
        this.series = series;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Creates a Favorite that links a user to a movie.
     *
     * @param user  the user
     * @param movie the movie/film
     */
    public Favorite(final Client user, final Film movie) {
        this.user = user;
        this.movie = movie;
        this.createdAt = LocalDateTime.now();
    }

}


