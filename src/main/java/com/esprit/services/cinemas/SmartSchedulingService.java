package com.esprit.services.cinemas;

import com.esprit.models.cinemas.CinemaHall;
import com.esprit.models.cinemas.MovieSession;
import com.esprit.models.films.Film;
import com.esprit.services.IService;
import com.esprit.services.films.FilmService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * AI-powered smart scheduling service implementing IService pattern.
 * Enhances existing cinema services with intelligent scheduling algorithms.
 * Provides demand prediction and conflict resolution for optimal movie scheduling.
 */

@Log4j2
public class SmartSchedulingService implements IService<MovieSession> {

    // Scheduling parameters
    private static final int MIN_BREAK_MINUTES = 30; // Minimum break between sessions
    private static final int MAX_SESSIONS_PER_DAY = 8; // Maximum sessions per hall per day
    private static final LocalTime FIRST_SESSION_TIME = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime LAST_SESSION_TIME = LocalTime.of(23, 0); // 11:00 PM
    private final DataSource dataSource;
    private final MovieSessionService movieSessionService;
    private final CinemaHallService cinemaHallService;
    private final FilmService filmService;
    private final ExecutorService executorService;
    // Caching for performance
    private final Cache<String, List<MovieSession>> scheduleCache;
    private final Cache<String, Double> demandPredictionCache;
    private final Cache<Long, Map<String, Object>> filmPopularityCache;

    public SmartSchedulingService() {
        this.dataSource = DataSource.getInstance();
        this.movieSessionService = new MovieSessionService();
        this.cinemaHallService = new CinemaHallService();
        this.filmService = new FilmService();

        this.executorService = Executors.newFixedThreadPool(4);

        // Initialize caching
        this.scheduleCache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build();

        this.demandPredictionCache = Caffeine.newBuilder()
            .maximumSize(5_000)
            .expireAfterWrite(Duration.ofHours(1))
            .build();

        this.filmPopularityCache = Caffeine.newBuilder()
            .maximumSize(1_000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .build();
    }

    /**
     * Generate optimal schedule for a cinema using AI algorithms.
     *
     * @param cinemaId  the cinema ID
     * @param startDate the start date for scheduling
     * @param endDate   the end date for scheduling
     * @return CompletableFuture with optimized schedule
     */
    public CompletableFuture<List<MovieSession>> generateOptimalScheduleAsync(Long cinemaId,
                                                                              LocalDateTime startDate,
                                                                              LocalDateTime endDate) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = cinemaId + "_" + startDate + "_" + endDate;

            // Check cache first
            List<MovieSession> cached = scheduleCache.getIfPresent(cacheKey);
            if (cached != null) {
                log.debug("Returning cached schedule for cinema: " + cinemaId);
                return cached;
            }

            // Generate optimal schedule
            List<MovieSession> optimizedSchedule = generateOptimalSchedule(cinemaId, startDate, endDate);

            // Cache the results
            scheduleCache.put(cacheKey, optimizedSchedule);

            return optimizedSchedule;
        }, executorService);
    }

    /**
     * Generate optimal schedule using AI-powered algorithms.
     */
    private List<MovieSession> generateOptimalSchedule(Long cinemaId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<MovieSession> optimizedSchedule = new ArrayList<>();

            // Get cinema halls
            List<CinemaHall> halls = getCinemaHalls(cinemaId);
            if (halls.isEmpty()) {
                log.warn("No halls found for cinema: " + cinemaId);
                return optimizedSchedule;
            }

            // Get available films with popularity scores
            List<Film> availableFilms = getAvailableFilmsWithPopularity();
            if (availableFilms.isEmpty()) {
                log.warn("No films available for scheduling");
                return optimizedSchedule;
            }

            // Generate schedule for each day
            LocalDateTime currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                List<MovieSession> dailySchedule = generateDailySchedule(halls, availableFilms, currentDate);
                optimizedSchedule.addAll(dailySchedule);
                currentDate = currentDate.plusDays(1);
            }

            // Resolve conflicts and optimize
            optimizedSchedule = resolveSchedulingConflicts(optimizedSchedule);
            optimizedSchedule = optimizeForRevenue(optimizedSchedule);

            log.info("Generated optimized schedule with " + optimizedSchedule.size() + " sessions");
            return optimizedSchedule;

        } catch (Exception e) {
            log.error("Error generating optimal schedule", e);
            return new ArrayList<>();
        }
    }

    /**
     * Generate daily schedule for all halls.
     */
    private List<MovieSession> generateDailySchedule(List<CinemaHall> halls, List<Film> films, LocalDateTime date) {
        List<MovieSession> dailySchedule = new ArrayList<>();

        for (CinemaHall hall : halls) {
            List<MovieSession> hallSchedule = generateHallSchedule(hall, films, date);
            dailySchedule.addAll(hallSchedule);
        }

        return dailySchedule;
    }

    /**
     * Generate schedule for a specific hall on a specific date.
     */
    private List<MovieSession> generateHallSchedule(CinemaHall hall, List<Film> films, LocalDateTime date) {
        List<MovieSession> hallSchedule = new ArrayList<>();

        // Get demand predictions for this date and hall
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;

        // Sort films by predicted demand
        List<Film> sortedFilms = films.stream()
            .sorted((f1, f2) -> Double.compare(
                predictDemand(f2, date, isWeekend),
                predictDemand(f1, date, isWeekend)
            ))
            .collect(Collectors.toList());

        // Schedule sessions throughout the day
        LocalDateTime currentTime = date.with(FIRST_SESSION_TIME);
        LocalDateTime endOfDay = date.with(LAST_SESSION_TIME);

        int sessionCount = 0;
        int filmIndex = 0;

        while (currentTime.isBefore(endOfDay) && sessionCount < MAX_SESSIONS_PER_DAY) {
            Film selectedFilm = sortedFilms.get(filmIndex % sortedFilms.size());

            // Calculate session end time
            LocalDateTime sessionEnd = currentTime.plusMinutes(selectedFilm.getDurationMin());

            // Check if session fits within operating hours
            if (sessionEnd.isAfter(endOfDay)) {
                break;
            }

            // Create movie session
            MovieSession session = MovieSession.builder()
                .cinemaHall(hall)
                .film(selectedFilm)
                .startTime(currentTime)
                .endTime(sessionEnd)
                .price(calculateOptimalPrice(selectedFilm, currentTime, isWeekend))
                .build();

            hallSchedule.add(session);

            // Move to next time slot
            currentTime = sessionEnd.plusMinutes(MIN_BREAK_MINUTES);
            sessionCount++;
            filmIndex++;
        }

        return hallSchedule;
    }

    /**
     * Predict demand for a film at a specific time.
     */
    private double predictDemand(Film film, LocalDateTime dateTime, boolean isWeekend) {
        String cacheKey = film.getId() + "_" + dateTime.toLocalDate() + "_" + isWeekend;

        Double cached = demandPredictionCache.getIfPresent(cacheKey);
        if (cached != null) {
            return cached;
        }

        double demand = calculateDemandScore(film, dateTime, isWeekend);
        demandPredictionCache.put(cacheKey, demand);

        return demand;
    }

    /**
     * Calculate demand score based on various factors.
     */
    private double calculateDemandScore(Film film, LocalDateTime dateTime, boolean isWeekend) {
        double score = 0.0;

        // Base popularity score (0.0 to 1.0)
        Map<String, Object> popularity = getFilmPopularity(film.getId());
        score += (Double) popularity.getOrDefault("averageRating", 3.0) / 5.0 * 0.3;
        score += Math.min((Integer) popularity.getOrDefault("totalTickets", 0) / 100.0, 1.0) * 0.2;

        // Time-based factors
        int hour = dateTime.getHour();
        if (hour >= 18 && hour <= 22) { // Prime time
            score += 0.3;
        } else if (hour >= 14 && hour <= 17) { // Afternoon
            score += 0.2;
        } else if (hour >= 10 && hour <= 13) { // Morning
            score += 0.1;
        }

        // Weekend boost
        if (isWeekend) {
            score += 0.2;
        }

        // Genre-based adjustments
        String genre = film.getGenre();
        if ("Action".equalsIgnoreCase(genre) || "Adventure".equalsIgnoreCase(genre)) {
            score += isWeekend ? 0.15 : 0.1;
        } else if ("Comedy".equalsIgnoreCase(genre) || "Romance".equalsIgnoreCase(genre)) {
            score += isWeekend ? 0.1 : 0.05;
        }

        // Recent release boost
        int currentYear = LocalDateTime.now().getYear();
        if (film.getReleaseYear() >= currentYear - 1) {
            score += 0.1;
        }

        return Math.min(score, 1.0);
    }

    /**
     * Calculate optimal price based on demand and time factors.
     */
    private double calculateOptimalPrice(Film film, LocalDateTime dateTime, boolean isWeekend) {
        double basePrice = 12.0; // Base ticket price

        // Demand-based pricing
        double demand = predictDemand(film, dateTime, isWeekend);
        double demandMultiplier = 0.8 + (demand * 0.4); // 0.8 to 1.2 multiplier

        // Time-based pricing
        int hour = dateTime.getHour();
        double timeMultiplier = 1.0;
        if (hour >= 18 && hour <= 22) { // Prime time
            timeMultiplier = 1.2;
        } else if (hour >= 10 && hour <= 14) { // Matinee
            timeMultiplier = 0.8;
        }

        // Weekend pricing
        double weekendMultiplier = isWeekend ? 1.1 : 1.0;

        // Film rating bonus
        double ratingMultiplier = 1.0;
        if (film.getRating() >= 4.5) {
            ratingMultiplier = 1.1;
        } else if (film.getRating() >= 4.0) {
            ratingMultiplier = 1.05;
        }

        double finalPrice = basePrice * demandMultiplier * timeMultiplier * weekendMultiplier * ratingMultiplier;

        // Round to nearest 0.50
        return Math.round(finalPrice * 2.0) / 2.0;
    }

    /**
     * Resolve scheduling conflicts using constraint satisfaction.
     */
    private List<MovieSession> resolveSchedulingConflicts(List<MovieSession> schedule) {
        List<MovieSession> resolvedSchedule = new ArrayList<>();

        // Group sessions by hall and date
        Map<String, List<MovieSession>> sessionsByHallAndDate = schedule.stream()
            .collect(Collectors.groupingBy(session ->
                session.getCinemaHall().getId() + "_" + session.getStartTime().toLocalDate()));

        // Resolve conflicts for each hall/date combination
        for (List<MovieSession> hallSessions : sessionsByHallAndDate.values()) {
            List<MovieSession> resolvedHallSessions = resolveHallConflicts(hallSessions);
            resolvedSchedule.addAll(resolvedHallSessions);
        }

        return resolvedSchedule;
    }

    /**
     * Resolve conflicts for a specific hall on a specific date.
     */
    private List<MovieSession> resolveHallConflicts(List<MovieSession> sessions) {
        // Sort sessions by start time
        sessions.sort(Comparator.comparing(MovieSession::getStartTime));

        List<MovieSession> resolved = new ArrayList<>();
        MovieSession lastSession = null;

        for (MovieSession session : sessions) {
            if (lastSession == null) {
                resolved.add(session);
                lastSession = session;
            } else {
                // Check for overlap
                LocalDateTime lastEnd = lastSession.getEndTime().plusMinutes(MIN_BREAK_MINUTES);
                if (session.getStartTime().isBefore(lastEnd)) {
                    // Conflict detected - adjust start time
                    MovieSession adjustedSession = MovieSession.builder()
                        .cinemaHall(session.getCinemaHall())
                        .film(session.getFilm())
                        .startTime(lastEnd)
                        .endTime(lastEnd.plusMinutes(session.getFilm().getDurationMin()))
                        .price(session.getPrice())
                        .build();

                    // Check if adjusted session fits within operating hours
                    if (adjustedSession.getEndTime().toLocalTime().isBefore(LAST_SESSION_TIME.plusMinutes(30))) {
                        resolved.add(adjustedSession);
                        lastSession = adjustedSession;
                    }
                    // Otherwise, skip this session
                } else {
                    resolved.add(session);
                    lastSession = session;
                }
            }
        }

        return resolved;
    }

    /**
     * Optimize schedule for maximum revenue.
     */
    private List<MovieSession> optimizeForRevenue(List<MovieSession> schedule) {
        // This is a simplified revenue optimization
        // In a real system, this would use more sophisticated algorithms

        return schedule.stream()
            .sorted((s1, s2) -> {
                double revenue1 = predictRevenue(s1);
                double revenue2 = predictRevenue(s2);
                return Double.compare(revenue2, revenue1);
            })
            .collect(Collectors.toList());
    }

    /**
     * Predict revenue for a movie session.
     */
    private double predictRevenue(MovieSession session) {
        double demand = predictDemand(session.getFilm(), session.getStartTime(),
            session.getStartTime().getDayOfWeek() == DayOfWeek.SATURDAY ||
                session.getStartTime().getDayOfWeek() == DayOfWeek.SUNDAY);

        int hallCapacity = session.getCinemaHall().getSeats().size();
        double expectedOccupancy = demand * 0.8; // Assume 80% of demand converts to tickets
        double expectedTickets = hallCapacity * expectedOccupancy;

        return expectedTickets * session.getPrice();
    }

    /**
     * Get cinema halls for a cinema.
     */
    private List<CinemaHall> getCinemaHalls(Long cinemaId) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM cinema_halls WHERE cinema_id = ?";
            List<CinemaHall> halls = new ArrayList<>();

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, cinemaId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    CinemaHall hall = cinemaHallService.getCinemaHallById(rs.getLong("id"));
                    if (hall != null) {
                        halls.add(hall);
                    }
                }
            }

            return halls;
        } catch (SQLException e) {
            log.error("Error getting cinema halls", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get available films with popularity metrics.
     */
    private List<Film> getAvailableFilmsWithPopularity() {
        List<Film> films = filmService.getAll();

        // Filter out films that are too old or not suitable for scheduling
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(6);

        return films.stream()
            .filter(film -> film.getReleaseYear() >= cutoffDate.getYear() - 1)
            .filter(film -> film.getDurationMin() > 60 && film.getDurationMin() < 240)
            .collect(Collectors.toList());
    }

    /**
     * Get film popularity metrics.
     */
    private Map<String, Object> getFilmPopularity(Long filmId) {
        Map<String, Object> cached = filmPopularityCache.getIfPresent(filmId);
        if (cached != null) {
            return cached;
        }

        Map<String, Object> popularity = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                SELECT
                    COUNT(t.id) as total_tickets,
                    AVG(f.rating) as average_rating,
                    COUNT(DISTINCT t.client_id) as unique_customers,
                    SUM(t.price_paid) as total_revenue
                FROM films f
                LEFT JOIN movie_sessions ms ON f.id = ms.film_id
                LEFT JOIN tickets t ON ms.id = t.movie_session_id
                WHERE f.id = ?
                GROUP BY f.id
                """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, filmId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    popularity.put("totalTickets", rs.getInt("total_tickets"));
                    popularity.put("averageRating", rs.getDouble("average_rating"));
                    popularity.put("uniqueCustomers", rs.getInt("unique_customers"));
                    popularity.put("totalRevenue", rs.getDouble("total_revenue"));
                } else {
                    // Default values for new films
                    popularity.put("totalTickets", 0);
                    popularity.put("averageRating", 3.0);
                    popularity.put("uniqueCustomers", 0);
                    popularity.put("totalRevenue", 0.0);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting film popularity", e);
            // Return default values
            popularity.put("totalTickets", 0);
            popularity.put("averageRating", 3.0);
            popularity.put("uniqueCustomers", 0);
            popularity.put("totalRevenue", 0.0);
        }

        filmPopularityCache.put(filmId, popularity);
        return popularity;
    }

    // IService implementation methods - delegate to MovieSessionService
    @Override
    public void create(MovieSession movieSession) {
        movieSessionService.create(movieSession);
    }

    @Override
    public Page<MovieSession> read(PageRequest pageRequest) {
        return movieSessionService.read(pageRequest);
    }

    @Override
    public void update(MovieSession movieSession) {
        movieSessionService.update(movieSession);
    }

    @Override
    public void delete(MovieSession movieSession) {
        movieSessionService.delete(movieSession);
    }

    @Override
    public int count() {
        return movieSessionService.count();
    }

    @Override
    public MovieSession getById(Long id) {
        return movieSessionService.getById(id);
    }

    @Override
    public List<MovieSession> getAll() {
        return movieSessionService.getAll();
    }

    @Override
    public List<MovieSession> search(String query) {
        return movieSessionService.search(query);
    }

    @Override
    public boolean exists(Long id) {
        return movieSessionService.exists(id);
    }

    /**
     * Clean up resources.
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
