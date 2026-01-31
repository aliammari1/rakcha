package com.esprit.models.users;

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

public class Activity {

    // Whether the activity is public/visible to friends
    @Builder.Default
    private boolean isPublic = true;
    private Long id;
    /**
     * The client who performed this activity.
     */
    private Client user;
    // Activity type: "watch", "review", "ticket", "purchase", "achievement", etc.
    private String type;
    // Human-readable description of the activity
    private String description;
    // Reference to related entity (film ID, series ID, order ID, etc.)
    private Long referenceId;
    private String referenceType; // "film", "series", "order", "ticket", etc.
    // Timestamp of when the activity occurred
    private LocalDateTime timestamp;
    // Additional metadata (JSON or key-value pairs)
    private String metadata;
    // Icon for display purposes
    private String icon;

    /**
     * Factory method to create a watch activity.
     */
    public static Activity createWatchActivity(Client user, String title, Long contentId, String contentType) {
        return Activity.builder()
            .user(user)
            .type("watch")
            .description("Watched " + title)
            .referenceId(contentId)
            .referenceType(contentType)
            .timestamp(LocalDateTime.now())
            .icon("🎬")
            .build();
    }

    /**
     * Factory method to create a review activity.
     */
    public static Activity createReviewActivity(Client user, String title, int rating) {
        return Activity.builder()
            .user(user)
            .type("review")
            .description("Rated " + title + " ★" + rating)
            .timestamp(LocalDateTime.now())
            .icon("⭐")
            .build();
    }

    /**
     * Factory method to create a ticket purchase activity.
     */
    public static Activity createTicketActivity(Client user, String movieTitle, String cinemaName) {
        return Activity.builder()
            .user(user)
            .type("ticket")
            .description("Booked tickets for " + movieTitle + " at " + cinemaName)
            .timestamp(LocalDateTime.now())
            .icon("🎟️")
            .build();
    }

    /**
     * Factory method to create a purchase activity.
     */
    public static Activity createPurchaseActivity(Client user, String productName, Long orderId) {
        return Activity.builder()
            .user(user)
            .type("purchase")
            .description("Purchased " + productName)
            .referenceId(orderId)
            .referenceType("order")
            .timestamp(LocalDateTime.now())
            .icon("🛒")
            .build();
    }

    /**
     * Factory method to create an achievement activity.
     */
    public static Activity createAchievementActivity(Client user, String achievementName) {
        return Activity.builder()
            .user(user)
            .type("achievement")
            .description("Earned achievement: " + achievementName)
            .timestamp(LocalDateTime.now())
            .icon("🏆")
            .build();
    }

    /**
     * Get the related entity ID (alias for referenceId).
     *
     * @return the reference ID
     */
    public Long getRelatedEntityId() {
        return this.referenceId;
    }

    /**
     * Get the related entity type (alias for referenceType).
     *
     * @return the reference type
     */
    public String getRelatedEntityType() {
        return this.referenceType;
    }


}
