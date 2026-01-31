package com.esprit.models.cinemas;

import com.esprit.models.films.Film;
import com.esprit.models.films.Ticket;
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
/**
 * Cinema management entity class for the RAKCHA application. Handles
 * cinema-related data with database persistence capabilities.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
public class MovieSession {

    private CinemaHall cinemaHall;
    private Film film;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long id;
    private Double price;

    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();

    /**
     * Constructor without id for creating new movie session instances.
     */
    public MovieSession(final CinemaHall cinemaHall, final Film film, final LocalDateTime startTime,
                        final LocalDateTime endTime,
                        final Double price) {
        this.cinemaHall = cinemaHall;
        this.film = film;
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.tickets = new ArrayList<>();
    }

    /**
     * Get the base price of the movie session.
     *
     * @return the price
     */
    public Double getBasePrice() {
        return this.price;
    }

    /**
     * Set the base price of the movie session.
     *
     * @param basePrice the price to set
     */
    public void setBasePrice(Double basePrice) {
        this.price = basePrice;
    }

}
