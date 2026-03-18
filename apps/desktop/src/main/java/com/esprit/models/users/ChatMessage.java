package com.esprit.models.users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;

@Log4j2
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class ChatMessage {

    @Builder.Default
    private final boolean isRead = false;
    @Builder.Default
    private final Timestamp sentAt = new Timestamp(System.currentTimeMillis());
    private Long id;
    /**
     * The user who sent the message.
     */
    private User sender;
    /**
     * The user who received the message.
     */
    private User receiver;
    private String content;

}

