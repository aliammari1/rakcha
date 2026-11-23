package com.esprit.controllers.cinemas;

import com.esprit.enums.UserRole;
import com.esprit.models.users.User;
import com.esprit.utils.DataSource;
import com.esprit.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import lombok.extern.log4j.Log4j2;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Statistics for the signed-in cinema manager's cinemas (every cinema for an admin), computed from sessions and
 * tickets in the database.
 */
@Log4j2
public class CinemaStatisticsController implements Initializable {
    @FXML
    private Label scopeLabel;
    @FXML
    private Label cinemasValue;
    @FXML
    private Label sessionsValue;
    @FXML
    private Label ticketsValue;
    @FXML
    private Label revenueValue;
    @FXML
    private BarChart<String, Number> filmChart;
    @FXML
    private BarChart<String, Number> hallChart;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        final User user = SessionManager.getCurrentUser();
        final boolean admin = user != null && user.getRole() == UserRole.ADMIN;
        // -1 matches no manager id; 0 means "every cinema" in the queries below.
        final long managerId = admin ? 0 : (user != null && user.getId() != null ? user.getId() : -1);
        scopeLabel.setText(admin ? "All cinemas on the platform" : "Your cinemas");
        try {
            final Connection con = DataSource.getInstance().getConnection(); // shared: never close
            final Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            final String scope = "(? = 0 OR c.manager_id = ?)";

            cinemasValue.setText(String.valueOf((long) number(con,
                "SELECT COUNT(*) FROM cinemas c WHERE " + scope, managerId, managerId)));
            sessionsValue.setText(String.valueOf((long) number(con,
                "SELECT COUNT(*) FROM screenings s JOIN cinema_halls h ON h.id = s.hall_id "
                    + "JOIN cinemas c ON c.id = h.cinema_id WHERE " + scope + " AND s.start_time >= ?",
                managerId, managerId, now)));
            final String tickets = "FROM tickets t JOIN screenings s ON s.id = t.screening_id "
                + "JOIN cinema_halls h ON h.id = s.hall_id JOIN cinemas c ON c.id = h.cinema_id "
                + "WHERE " + scope + " AND t.status <> 'CANCELLED'";
            ticketsValue.setText(String.valueOf((long) number(con, "SELECT COUNT(*) " + tickets, managerId, managerId)));
            revenueValue.setText(String.format(Locale.ENGLISH, "%.2f TND",
                number(con, "SELECT COALESCE(SUM(t.price_paid), 0) " + tickets, managerId, managerId)));

            final XYChart.Series<String, Number> films = new XYChart.Series<>();
            try (PreparedStatement st = con.prepareStatement(
                "SELECT m.title, COUNT(*) FROM screenings s JOIN movies m ON m.id = s.movie_id "
                    + "JOIN cinema_halls h ON h.id = s.hall_id JOIN cinemas c ON c.id = h.cinema_id "
                    + "WHERE " + scope + " AND s.start_time >= ? GROUP BY m.id, m.title ORDER BY COUNT(*) DESC, m.title")) {
                st.setLong(1, managerId);
                st.setLong(2, managerId);
                st.setTimestamp(3, now);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        films.getData().add(new XYChart.Data<>(rs.getString(1), rs.getLong(2)));
                    }
                }
            }
            filmChart.getData().setAll(films);

            final XYChart.Series<String, Number> halls = new XYChart.Series<>();
            try (PreparedStatement st = con.prepareStatement(
                "SELECT c.name || ' · ' || h.name, "
                    + "(SELECT COUNT(*) FROM tickets t JOIN screenings s2 ON s2.id = t.screening_id "
                    + "  WHERE s2.hall_id = h.id AND t.status <> 'CANCELLED') * 100.0 "
                    + "/ MAX(1, h.capacity * MAX(1, (SELECT COUNT(*) FROM screenings s3 WHERE s3.hall_id = h.id))) "
                    + "FROM cinema_halls h JOIN cinemas c ON c.id = h.cinema_id WHERE " + scope + " ORDER BY c.name, h.name")) {
                st.setLong(1, managerId);
                st.setLong(2, managerId);
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        halls.getData().add(new XYChart.Data<>(rs.getString(1), Math.round(rs.getDouble(2) * 10) / 10.0));
                    }
                }
            }
            hallChart.getData().setAll(halls);
        } catch (final SQLException e) {
            log.error("Could not load cinema statistics", e);
        }
    }

    private static double number(final Connection con, final String sql, final Object... params) throws SQLException {
        try (PreparedStatement st = con.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                st.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = st.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }
}
