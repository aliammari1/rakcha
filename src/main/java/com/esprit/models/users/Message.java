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

public class Message {

    @Builder.Default
    private final boolean deleted = false;
    // Message type
    @Builder.Default
    private final MessageType type = MessageType.TEXT;
    private Long id;
    /**
     * The user who sent the message.
     */
    private User sender;
    /**
     * The user who received the message.
     */
    private User recipient;
    // Message content
    private String content;
    // Optional attachment (image, file URL)
    private String attachmentUrl;
    private String attachmentType; // "image", "file", "gif", etc.
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    // Status flags
    @Builder.Default
    private boolean read = false;
    // For reply threading
    private Long replyToId;

    /**
     * Factory method to create a text message.
     */
    public static Message createTextMessage(User sender, User recipient, String content) {
        return Message.builder()
            .sender(sender)
            .recipient(recipient)
            .content(content)
            .type(MessageType.TEXT)
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * Factory method to create an image message.
     */
    public static Message createImageMessage(User sender, User recipient, String imageUrl) {
        return Message.builder()
            .sender(sender)
            .recipient(recipient)
            .content("📷 Image")
            .attachmentUrl(imageUrl)
            .attachmentType("image")
            .type(MessageType.IMAGE)
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * Factory method to create a system message.
     */
    public static Message createSystemMessage(User recipient, String content) {
        return Message.builder()
            .recipient(recipient)
            .content(content)
            .type(MessageType.SYSTEM)
            .createdAt(LocalDateTime.now())
            .build();
    }

    /**
     * Marks the message as read.
     */
    public void markAsRead() {
        if (!this.read) {
            this.read = true;
            this.readAt = LocalDateTime.now();
        }
    }

    /**
     * Checks if the message was sent by the given user.
     */
    public boolean isSentBy(User user) {
        return sender != null && sender.equals(user);
    }

    /**
     * Checks if the message is a reply to another message.
     */
    public boolean isReply() {
        return replyToId != null;
    }

    /**
     * Enum representing different message types.
     */
    public enum MessageType {
        TEXT,
        IMAGE,
        FILE,
        SYSTEM,
        EMOJI
    }
}
