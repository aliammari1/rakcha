package com.esprit.models.cinemas;

import com.esprit.enums.CinemaStatus;
import com.esprit.models.common.Review;
import com.esprit.models.users.CinemaManager;
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
 * Cinema management entity class for the RAKCHA application. Handles
 * cinema-related data with database persistence capabilities.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

public class Cinema {

    private CinemaManager manager;
    private String logoUrl;
    private String name;
    private String address;
    private Long id;
    /**
     * Cinema approval status: PENDING, ACCEPTED, REFUSED
     */
    @Builder.Default
    private CinemaStatus status = CinemaStatus.PENDING;

    @Builder.Default
    private List<CinemaHall> cinemaHalls = new ArrayList<>();

    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    /**
     * Constructor without id for creating new cinema instances.
     *
     * @param name    the name of the cinema
     * @param address the address of the cinema
     * @param manager the cinema manager
     * @param logoUrl the path to the cinema's logo
     * @param status  the current status of the cinema
     */
    public Cinema(final String name, final String address, final CinemaManager manager, final String logoUrl, final CinemaStatus status) {
        this.name = name;
        this.address = address;
        this.manager = manager;
        this.logoUrl = logoUrl;
        this.status = status;
        this.cinemaHalls = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    /**
     * Retrieve all movie sessions for this cinema across all halls.
     *
     * @return a list of all MovieSession objects for this cinema; the list will be
     * empty if there are no sessions
     */
    public List<MovieSession> getMovieSessions() {
        List<MovieSession> allSessions = new ArrayList<>();
        if (cinemaHalls != null) {
            for (CinemaHall hall : cinemaHalls) {
                if (hall.getMovieSessions() != null) {
                    allSessions.addAll(hall.getMovieSessions());
                }

            }

        }

        return allSessions;
    }
}
