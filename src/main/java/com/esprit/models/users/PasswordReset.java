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

public class PasswordReset {

    @Builder.Default
    private final java.sql.Timestamp requestedAt = new java.sql.Timestamp(System.currentTimeMillis());
    private Long id;
    private User user;
    private String selector;
    private String hashedToken;
    private java.sql.Timestamp expiresAt;

}

