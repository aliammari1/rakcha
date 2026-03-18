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

public class Friendship {

    @Builder.Default
    private final java.sql.Timestamp createdAt = new java.sql.Timestamp(System.currentTimeMillis());
    private Long id;
    /**
     * The user who initiated the friend request.
     */
    private User requester;
    /**
     * The user who received the friend request.
     */
    private User addressee;
    /**
     * Status of the friendship: PENDING, ACCEPTED, REJECTED, BLOCKED
     */
    private String status;

}

