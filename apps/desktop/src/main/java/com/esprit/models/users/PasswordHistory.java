package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PasswordHistory {

    /**
     * When this password was set
     */
    @Builder.Default
    private final java.sql.Timestamp createdAt = new java.sql.Timestamp(System.currentTimeMillis());
    /**
     * Unique identifier for password history entry
     */
    private Long id;
    /**
     * User this password belongs to
     */
    private User user;
    /**
     * Hashed password (BCrypt or similar)
     */
    private String passwordHash;

}

