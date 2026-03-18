package com.esprit.models.voice;

import com.esprit.models.users.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.Map;

@Log4j2
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class VoiceCommand {

    /**
     * The raw text of the voice command as recognized by the speech engine.
     */
    private String rawText;
    /**
     * The processed/normalized command text.
     */
    private String processedText;
    /**
     * The type/category of the voice command.
     */
    private CommandType commandType;
    /**
     * Whether this command requires confirmation before execution.
     */
    @Builder.Default
    private boolean requiresConfirmation = false;
    @EqualsAndHashCode.Include
    private Long id;
    /**
     * The action to be executed based on this command.
     */
    private String action;
    /**
     * Parameters extracted from the voice command.
     */
    private Map<String, Object> parameters;
    /**
     * Confidence score of the voice recognition (0.0 to 1.0).
     */
    @Builder.Default
    private double confidence = 0.0;
    /**
     * The user who issued the command.
     */
    private User user;
    /**
     * Timestamp when the command was recognized.
     */
    @Builder.Default
    private Timestamp recognizedAt = new Timestamp(System.currentTimeMillis());
    /**
     * Timestamp when the command was executed.
     */
    private Timestamp executedAt;
    /**
     * Status of the command execution.
     */
    @Builder.Default
    private CommandStatus status = CommandStatus.PENDING;
    /**
     * Error message if command execution failed.
     */
    private String errorMessage;
    /**
     * Duration of the audio input in milliseconds.
     */
    private Long audioDurationMs;
    /**
     * Language of the voice command.
     */
    @Builder.Default
    private String language = "en-US";
    /**
     * Priority level of the command.
     */
    @Builder.Default
    private CommandPriority priority = CommandPriority.NORMAL;

    /**
     * Creates a new VoiceCommand with basic information.
     *
     * @param rawText     the raw recognized text
     * @param commandType the type of command
     * @param confidence  the recognition confidence
     */
    public VoiceCommand(String rawText, CommandType commandType, double confidence) {
        this.rawText = rawText;
        this.processedText = rawText;
        this.commandType = commandType;
        this.confidence = confidence;
        this.recognizedAt = new Timestamp(System.currentTimeMillis());
        this.status = CommandStatus.PENDING;
        this.priority = CommandPriority.NORMAL;
        this.language = "en-US";
    }

    /**
     * Creates a new VoiceCommand with action and parameters.
     *
     * @param rawText     the raw recognized text
     * @param commandType the type of command
     * @param action      the action to execute
     * @param parameters  command parameters
     * @param confidence  the recognition confidence
     */
    public VoiceCommand(String rawText, CommandType commandType, String action,
                        Map<String, Object> parameters, double confidence) {
        this(rawText, commandType, confidence);
        this.action = action;
        this.parameters = parameters;
    }

    /**
     * Marks the command as executed.
     */
    public void markAsExecuted() {
        this.status = CommandStatus.EXECUTED;
        this.executedAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Marks the command as failed with an error message.
     *
     * @param errorMessage the error message
     */
    public void markAsFailed(String errorMessage) {
        this.status = CommandStatus.FAILED;
        this.errorMessage = errorMessage;
        this.executedAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Checks if the command has high confidence.
     *
     * @return true if confidence is above 0.7
     */
    public boolean hasHighConfidence() {
        return confidence > 0.7;
    }

    /**
     * Checks if the command is executable (has sufficient confidence and valid action).
     *
     * @return true if command can be executed
     */
    public boolean isExecutable() {
        return confidence > 0.5 && action != null && !action.trim().isEmpty()
            && status == CommandStatus.PENDING;
    }

    /**
     * Gets the execution duration in milliseconds.
     *
     * @return execution duration or -1 if not executed
     */
    public long getExecutionDuration() {
        if (recognizedAt != null && executedAt != null) {
            return executedAt.getTime() - recognizedAt.getTime();
        }
        return -1;
    }

    @Override
    public String toString() {
        return String.format("VoiceCommand{id=%d, text='%s', type=%s, confidence=%.2f, status=%s}",
            id, rawText, commandType, confidence, status);
    }

    /**
     * Enum defining command types.
     */
    public enum CommandType {
        NAVIGATION("Navigate to different screens"),
        SEARCH("Search for content"),
        MEDIA_CONTROL("Control media playback"),
        BOOKING("Movie ticket booking"),
        USER_ACCOUNT("User account management"),
        SYSTEM_CONTROL("System settings and controls"),
        ACCESSIBILITY("Accessibility features"),
        CUSTOM("Custom user-defined commands"),
        UNKNOWN("Unrecognized command type");

        private final String description;

        CommandType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Enum defining command execution status.
     */
    public enum CommandStatus {
        PENDING("Command recognized, waiting for execution"),
        PROCESSING("Command is being processed"),
        EXECUTED("Command executed successfully"),
        FAILED("Command execution failed"),
        CANCELLED("Command was cancelled"),
        TIMEOUT("Command execution timed out");

        private final String description;

        CommandStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Enum defining command priority levels.
     */
    public enum CommandPriority {
        LOW(1, "Low priority command"),
        NORMAL(2, "Normal priority command"),
        HIGH(3, "High priority command"),
        URGENT(4, "Urgent command requiring immediate attention");

        private final int level;
        private final String description;

        CommandPriority(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }
    }
}
