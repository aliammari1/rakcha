package com.esprit.services.cinemas;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** One-line description of a screening for the film details screen. */
public final class ShowtimeSummary {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("EEE d MMM HH:mm", Locale.ENGLISH);

    private ShowtimeSummary() {
    }

    public static String describe(final LocalDateTime start, final String cinema, final String hall,
                                  final double price, final LocalDateTime now) {
        if (start == null) {
            return "No upcoming showtimes";
        }
        final String when;
        if (start.toLocalDate().equals(now.toLocalDate())) {
            when = "Today " + TIME.format(start);
        } else if (start.toLocalDate().equals(now.toLocalDate().plusDays(1))) {
            when = "Tomorrow " + TIME.format(start);
        } else {
            when = DAY.format(start);
        }
        final String place = cinema == null || cinema.isBlank() ? hall : cinema + (hall == null ? "" : ", " + hall);
        return String.format(Locale.ENGLISH, "%s · %s · %.2f TND", when, place, price);
    }
}
