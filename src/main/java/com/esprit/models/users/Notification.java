package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Notification {

    @Builder.Default
    private boolean isRead = false;
    @Builder.Default
    private final java.sql.Timestamp createdAt = new java.sql.Timestamp(System.currentTimeMillis());
    private Long id;
    /**
     * The user this notification is for.
     */
    private User user;
    private String title;
    private String message;
    private String type; // e.g., 'ORDER_UPDATE', 'FRIEND_REQ'

}

