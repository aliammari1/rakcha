package com.esprit.models.ml;

import com.esprit.models.films.Film;
import com.esprit.models.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Log4j2
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class MovieRecommendation {

    private User user;
    private Film film;
    private Double confidenceScore;
    private String reasoning;
    private String recommendationType; // "collaborative", "content-based", "hybrid"
    private LocalDateTime generatedAt;
    private Long id;
    @Builder.Default
    private boolean isViewed = false;

    @Builder.Default
    private boolean isAccepted = false;

    private LocalDateTime viewedAt;

    private LocalDateTime acceptedAt;

    /**
     * Constructor for creating a new recommendation.
     *
     * @param user               the user for whom the recommendation is made
     * @param film               the recommended film
     * @param confidenceScore    the confidence score (0.0 to 1.0)
     * @param reasoning          the explanation for the recommendation
     * @param recommendationType the type of recommendation algorithm used
     */
    public MovieRecommendation(User user, Film film, Double confidenceScore,
                               String reasoning, String recommendationType) {
        this.user = user;
        this.film = film;
        this.confidenceScore = confidenceScore;
        this.reasoning = reasoning;
        this.recommendationType = recommendationType;
        this.generatedAt = LocalDateTime.now();
        this.isViewed = false;
        this.isAccepted = false;
    }

    /**
     * Mark this recommendation as viewed by the user.
     */
    public void markAsViewed() {
        this.isViewed = true;
        this.viewedAt = LocalDateTime.now();
    }

    /**
     * Mark this recommendation as accepted by the user.
     */
    public void markAsAccepted() {
        this.isAccepted = true;
        this.acceptedAt = LocalDateTime.now();
        if (!this.isViewed) {
            markAsViewed();
        }
    }

    /**
     * Get the recommendation quality based on confidence score.
     *
     * @return quality rating as string
     */
    public String getQualityRating() {
        if (confidenceScore >= 0.8) {
            return "Excellent";
        } else if (confidenceScore >= 0.6) {
            return "Good";
        } else if (confidenceScore >= 0.4) {
            return "Fair";
        } else {
            return "Poor";
        }
    }
}
