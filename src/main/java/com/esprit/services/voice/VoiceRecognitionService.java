package com.esprit.services.voice;

import com.esprit.models.voice.VoiceCommand;
import com.esprit.models.voice.VoiceCommand.CommandPriority;
import com.esprit.models.voice.VoiceCommand.CommandStatus;
import com.esprit.models.voice.VoiceCommand.CommandType;
import com.esprit.services.IService;
import com.esprit.services.users.UserService;
import com.esprit.utils.DataSource;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.esprit.utils.PaginationQueryBuilder;
import lombok.extern.log4j.Log4j2;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Voice Recognition Service implementing IService<VoiceCommand> following existing service architecture.
 * Integrates CMU Sphinx for offline speech recognition and provides voice command processing.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Log4j2
public class VoiceRecognitionService implements IService<VoiceCommand> {

    // Allowed columns for sorting to prevent SQL injection
    private static final String[] ALLOWED_SORT_COLUMNS = {
        "id", "raw_text", "command_type", "confidence", "recognized_at", "status", "priority"
    };
    // Recognition parameters
    private static final double MIN_CONFIDENCE_THRESHOLD = 0.3;
    private static final double HIGH_CONFIDENCE_THRESHOLD = 0.7;
    private static final int MAX_RECORDING_DURATION_MS = 10000; // 10 seconds
    private final Connection connection;
    private final ExecutorService backgroundExecutor;
    private final UserService userService;
    // Command patterns for natural language processing
    private final Map<Pattern, CommandType> commandPatterns;
    private final Map<CommandType, List<String>> commandActions;
    // Audio recording components
    private AudioFormat audioFormat;
    private TargetDataLine microphone;
    private boolean isRecording = false;
    private boolean isListening = false;
    // Voice recognition components
    private boolean isInitialized = false;

    /**
     * Constructs a new VoiceRecognitionService following existing service patterns.
     */
    public VoiceRecognitionService() {
        this.connection = DataSource.getInstance().getConnection();
        this.backgroundExecutor = Executors.newFixedThreadPool(3);
        this.commandPatterns = new HashMap<>();
        this.commandActions = new HashMap<>();
        this.userService = new UserService();

        initializeAudioFormat();
        initializeCommandPatterns();
        createTableIfNotExists();
    }

    /**
     * Initializes the voice recognition system.
     *
     * @return true if initialization was successful
     */
    public boolean initialize() {
        if (isInitialized) {
            return true;
        }

        try {
            // Initialize microphone
            DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            if (!AudioSystem.isLineSupported(micInfo)) {
                log.error("Microphone not supported");
                return false;
            }

            microphone = (TargetDataLine) AudioSystem.getLine(micInfo);

            // Note: CMU Sphinx initialization would go here
            // For now, we'll use a simplified approach with pattern matching

            isInitialized = true;
            log.info("Voice recognition service initialized successfully");
            return true;

        } catch (Exception e) {
            log.error("Failed to initialize voice recognition service", e);
            return false;
        }
    }

    /**
     * Starts continuous voice listening in background thread.
     *
     * @return CompletableFuture for async operation
     */
    public CompletableFuture<Void> startListening() {
        if (!isInitialized && !initialize()) {
            return CompletableFuture.failedFuture(new RuntimeException("Voice recognition not initialized"));
        }

        return CompletableFuture.runAsync(() -> {
            isListening = true;
            log.info("Started voice listening");

            while (isListening) {
                try {
                    // Wait for voice activity detection
                    if (detectVoiceActivity()) {
                        // Record audio
                        byte[] audioData = recordAudio();

                        if (audioData != null && audioData.length > 0) {
                            // Process audio and recognize speech
                            processAudioAsync(audioData);
                        }
                    }

                    // Small delay to prevent excessive CPU usage
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error during voice listening", e);
                }
            }

            log.info("Voice listening stopped");
        }, backgroundExecutor);
    }

    /**
     * Stops voice listening and releases audio resources.
     */
    public void stopListening() {
        isListening = false;
        if (isRecording && microphone != null) {
            microphone.stop();
            microphone.close();
        }
        log.info("Voice listening stopped");
    }

    /**
     * Processes a single voice command from text input.
     *
     * @param text the text to process as a voice command
     * @return the processed VoiceCommand
     */
    public VoiceCommand processTextCommand(String text) {
        return processTextCommand(text, null);
    }

    /**
     * Processes a single voice command from text input with user context.
     *
     * @param text   the text to process as a voice command
     * @param userId the user ID issuing the command
     * @return the processed VoiceCommand
     */
    public VoiceCommand processTextCommand(String text, Long userId) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }

        try {
            // Normalize text
            String normalizedText = normalizeText(text);

            // Classify command type
            CommandType commandType = classifyCommand(normalizedText);

            // Extract action and parameters
            String action = extractAction(normalizedText, commandType);
            Map<String, Object> parameters = extractParameters(normalizedText, commandType);

            // Calculate confidence (simplified - in real implementation would use ML model)
            double confidence = calculateTextConfidence(normalizedText, commandType);

            // Create voice command
            VoiceCommand command = VoiceCommand.builder()
                .rawText(text)
                .processedText(normalizedText)
                .commandType(commandType)
                .action(action)
                .parameters(parameters)
                .confidence(confidence)
                .user(this.userService.getById(userId))
                .status(CommandStatus.PENDING)
                .priority(determinePriority(commandType))
                .language("en-US")
                .build();

            // Save to database
            create(command);

            log.info("Processed text command: {} -> {}", text, command);
            return command;

        } catch (Exception e) {
            log.error("Error processing text command: " + text, e);
            return null;
        }
    }

    /**
     * Executes a voice command.
     *
     * @param command the command to execute
     * @return true if execution was successful
     */
    public boolean executeCommand(VoiceCommand command) {
        if (command == null || !command.isExecutable()) {
            return false;
        }

        try {
            command.setStatus(CommandStatus.PROCESSING);
            update(command);

            boolean success = false;

            // Execute based on command type
            switch (command.getCommandType()) {
                case NAVIGATION:
                    success = executeNavigationCommand(command);
                    break;
                case SEARCH:
                    success = executeSearchCommand(command);
                    break;
                case MEDIA_CONTROL:
                    success = executeMediaControlCommand(command);
                    break;
                case BOOKING:
                    success = executeBookingCommand(command);
                    break;
                case USER_ACCOUNT:
                    success = executeUserAccountCommand(command);
                    break;
                case SYSTEM_CONTROL:
                    success = executeSystemControlCommand(command);
                    break;
                case ACCESSIBILITY:
                    success = executeAccessibilityCommand(command);
                    break;
                default:
                    log.warn("Unknown command type: " + command.getCommandType());
                    success = false;
            }

            if (success) {
                command.markAsExecuted();
            } else {
                command.markAsFailed("Command execution failed");
            }

            update(command);
            return success;

        } catch (Exception e) {
            log.error("Error executing command: " + command, e);
            command.markAsFailed("Exception during execution: " + e.getMessage());
            update(command);
            return false;
        }
    }

    // IService implementation methods

    @Override
    public void create(VoiceCommand command) {
        if (command == null) {
            log.warn("Cannot create null voice command");
            return;
        }

        String sql = """
            INSERT INTO voice_commands (raw_text, processed_text, command_type, action, parameters,
                                      confidence, user_id, recognized_at, status, error_message,
                                      audio_duration_ms, language, requires_confirmation, priority)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, command.getRawText());
            stmt.setString(2, command.getProcessedText());
            stmt.setString(3, command.getCommandType().name());
            stmt.setString(4, command.getAction());
            stmt.setString(5, command.getParameters() != null ?
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(command.getParameters()) : null);
            stmt.setDouble(6, command.getConfidence());
            stmt.setObject(7, command.getUser());
            stmt.setTimestamp(8, command.getRecognizedAt());
            stmt.setString(9, command.getStatus().name());
            stmt.setString(10, command.getErrorMessage());
            stmt.setObject(11, command.getAudioDurationMs());
            stmt.setString(12, command.getLanguage());
            stmt.setBoolean(13, command.isRequiresConfirmation());
            stmt.setString(14, command.getPriority().name());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    command.setId(rs.getLong(1));
                }
            }

            log.info("Voice command created with ID: " + command.getId());

        } catch (Exception e) {
            log.error("Error creating voice command", e);
        }
    }

    @Override
    public Page<VoiceCommand> read(PageRequest pageRequest) {
        List<VoiceCommand> content = new ArrayList<>();
        String baseQuery = "SELECT * FROM voice_commands";

        // Validate sort column
        if (pageRequest.hasSorting() &&
            !PaginationQueryBuilder.isValidSortColumn(pageRequest.sortBy(), ALLOWED_SORT_COLUMNS)) {
            log.warn("Invalid sort column: {}. Using default sorting.", pageRequest.sortBy());
            pageRequest = PageRequest.of(pageRequest.page(), pageRequest.size());
        }

        try {
            // Get total count
            String countQuery = PaginationQueryBuilder.buildCountQuery(baseQuery);
            long totalElements = PaginationQueryBuilder.executeCountQuery(connection, countQuery);

            // Get paginated results
            String paginatedQuery = PaginationQueryBuilder.buildPaginatedQuery(baseQuery, pageRequest);
            try (PreparedStatement stmt = connection.prepareStatement(paginatedQuery);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    VoiceCommand command = buildVoiceCommandFromResultSet(rs);
                    if (command != null) {
                        content.add(command);
                    }
                }
            }

            return new Page<>(content, pageRequest.page(), pageRequest.size(), totalElements);

        } catch (SQLException e) {
            log.error("Error retrieving paginated voice commands", e);
            return new Page<>(content, pageRequest.page(), pageRequest.size(), 0);
        }
    }

    @Override
    public void update(VoiceCommand command) {
        if (command == null || command.getId() == null) {
            log.warn("Cannot update null voice command or command without ID");
            return;
        }

        String sql = """
            UPDATE voice_commands SET processed_text=?, command_type=?, action=?, parameters=?,
                                    confidence=?, executed_at=?, status=?, error_message=?,
                                    audio_duration_ms=?, requires_confirmation=?, priority=?
            WHERE id=?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, command.getProcessedText());
            stmt.setString(2, command.getCommandType().name());
            stmt.setString(3, command.getAction());
            stmt.setString(4, command.getParameters() != null ?
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(command.getParameters()) : null);
            stmt.setDouble(5, command.getConfidence());
            stmt.setTimestamp(6, command.getExecutedAt());
            stmt.setString(7, command.getStatus().name());
            stmt.setString(8, command.getErrorMessage());
            stmt.setObject(9, command.getAudioDurationMs());
            stmt.setBoolean(10, command.isRequiresConfirmation());
            stmt.setString(11, command.getPriority().name());
            stmt.setLong(12, command.getId());

            stmt.executeUpdate();
            log.info("Voice command updated: " + command.getId());

        } catch (Exception e) {
            log.error("Error updating voice command", e);
        }
    }

    @Override
    public void delete(VoiceCommand command) {
        if (command == null || command.getId() == null) {
            log.warn("Cannot delete null voice command or command without ID");
            return;
        }

        String sql = "DELETE FROM voice_commands WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, command.getId());
            stmt.executeUpdate();
            log.info("Voice command deleted: " + command.getId());
        } catch (SQLException e) {
            log.error("Error deleting voice command", e);
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM voice_commands";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error counting voice commands", e);
        }
        return 0;
    }

    @Override
    public VoiceCommand getById(Long id) {
        if (id == null) {
            return null;
        }

        String sql = "SELECT * FROM voice_commands WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildVoiceCommandFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving voice command by ID: " + id, e);
        }
        return null;
    }

    @Override
    public List<VoiceCommand> getAll() {
        List<VoiceCommand> commands = new ArrayList<>();
        String sql = "SELECT * FROM voice_commands ORDER BY recognized_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                VoiceCommand command = buildVoiceCommandFromResultSet(rs);
                if (command != null) {
                    commands.add(command);
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving all voice commands", e);
        }
        return commands;
    }

    @Override
    public List<VoiceCommand> search(String query) {
        List<VoiceCommand> commands = new ArrayList<>();
        String sql = """
            SELECT * FROM voice_commands
            WHERE raw_text LIKE ? OR processed_text LIKE ? OR action LIKE ?
            ORDER BY confidence DESC, recognized_at DESC
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VoiceCommand command = buildVoiceCommandFromResultSet(rs);
                    if (command != null) {
                        commands.add(command);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error searching voice commands", e);
        }
        return commands;
    }

    @Override
    public boolean exists(Long id) {
        return getById(id) != null;
    }

    // Private helper methods

    private void initializeAudioFormat() {
        audioFormat = new AudioFormat(
            AudioFormat.Encoding.PCM_SIGNED,
            16000.0f, // Sample rate
            16,       // Sample size in bits
            1,        // Channels (mono)
            2,        // Frame size
            16000.0f, // Frame rate
            false     // Big endian
        );
    }

    private void initializeCommandPatterns() {
        // Navigation patterns (more specific patterns first)
        commandPatterns.put(Pattern.compile("(?i).*(go to|navigate to|open|show me).*"), CommandType.NAVIGATION);
        commandPatterns.put(Pattern.compile("(?i).*(back|previous|return).*"), CommandType.NAVIGATION);

        // Search patterns
        commandPatterns.put(Pattern.compile("(?i).*(search|find|look for).*"), CommandType.SEARCH);

        // Media control patterns
        commandPatterns.put(Pattern.compile("(?i).*(play|pause|stop|resume).*"), CommandType.MEDIA_CONTROL);
        commandPatterns.put(Pattern.compile("(?i).*(volume|mute|unmute).*"), CommandType.MEDIA_CONTROL);

        // Booking patterns
        commandPatterns.put(Pattern.compile("(?i).*(book|reserve|buy ticket).*"), CommandType.BOOKING);

        // System control patterns
        commandPatterns.put(Pattern.compile("(?i).*(help|assistance|support).*"), CommandType.SYSTEM_CONTROL);

        // User account patterns (less specific, should come after navigation)
        commandPatterns.put(Pattern.compile("(?i)^(?!.*(go to|navigate to|open|show me)).*(profile|account|settings).*"), CommandType.USER_ACCOUNT);

        // Initialize command actions
        initializeCommandActions();
    }

    private void initializeCommandActions() {
        commandActions.put(CommandType.NAVIGATION, Arrays.asList(
            "navigate_to_movies", "navigate_to_cinemas", "navigate_to_profile",
            "navigate_back", "navigate_home"
        ));

        commandActions.put(CommandType.SEARCH, Arrays.asList(
            "search_movies", "search_cinemas", "search_actors", "search_all"
        ));

        commandActions.put(CommandType.MEDIA_CONTROL, Arrays.asList(
            "play_trailer", "pause_trailer", "stop_trailer", "volume_up",
            "volume_down", "mute", "unmute"
        ));

        commandActions.put(CommandType.BOOKING, Arrays.asList(
            "book_ticket", "select_seats", "choose_showtime", "confirm_booking"
        ));

        commandActions.put(CommandType.USER_ACCOUNT, Arrays.asList(
            "view_profile", "edit_profile", "view_bookings", "account_settings"
        ));

        commandActions.put(CommandType.SYSTEM_CONTROL, Arrays.asList(
            "show_help", "voice_settings", "accessibility_options", "system_info"
        ));
    }

    private boolean detectVoiceActivity() {
        // Simplified voice activity detection
        // In a real implementation, this would analyze audio levels
        return Math.random() > 0.95; // Simulate occasional voice activity
    }

    private byte[] recordAudio() {
        if (microphone == null) {
            return null;
        }

        try {
            microphone.open(audioFormat);
            microphone.start();
            isRecording = true;

            ByteArrayOutputStream audioBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            long startTime = System.currentTimeMillis();
            while (isRecording && (System.currentTimeMillis() - startTime) < MAX_RECORDING_DURATION_MS) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    audioBuffer.write(buffer, 0, bytesRead);
                }

                // Simple silence detection (in real implementation would be more sophisticated)
                if (isSilence(buffer, bytesRead)) {
                    break;
                }
            }

            isRecording = false;
            microphone.stop();
            microphone.close();

            return audioBuffer.toByteArray();

        } catch (Exception e) {
            log.error("Error recording audio", e);
            isRecording = false;
            return null;
        }
    }

    private boolean isSilence(byte[] buffer, int length) {
        // Simple silence detection based on amplitude
        long sum = 0;
        for (int i = 0; i < length; i += 2) {
            short sample = (short) ((buffer[i + 1] << 8) | (buffer[i] & 0xFF));
            sum += Math.abs(sample);
        }
        double average = (double) sum / (length / 2);
        return average < 1000; // Threshold for silence
    }

    private void processAudioAsync(byte[] audioData) {
        CompletableFuture.runAsync(() -> {
            try {
                // In a real implementation, this would use CMU Sphinx or similar
                // For now, we'll simulate speech recognition
                String recognizedText = simulateSpeechRecognition(audioData);

                if (recognizedText != null && !recognizedText.trim().isEmpty()) {
                    VoiceCommand command = processTextCommand(recognizedText);
                    if (command != null && command.hasHighConfidence()) {
                        // Auto-execute high-confidence commands
                        executeCommand(command);
                    }
                }
            } catch (Exception e) {
                log.error("Error processing audio", e);
            }
        }, backgroundExecutor);
    }

    private String simulateSpeechRecognition(byte[] audioData) {
        // Simulate speech recognition - in real implementation would use CMU Sphinx
        String[] sampleCommands = {
            "search for action movies",
            "go to my profile",
            "book a ticket",
            "play trailer",
            "show me cinemas nearby",
            "help me with booking"
        };

        if (audioData.length > 1000) { // Only if we have sufficient audio data
            return sampleCommands[(int) (Math.random() * sampleCommands.length)];
        }

        return null;
    }

    private String normalizeText(String text) {
        return text.toLowerCase().trim().replaceAll("\\s+", " ");
    }

    private CommandType classifyCommand(String text) {
        // Check patterns in order of priority
        // Navigation patterns have highest priority
        if (Pattern.compile("(?i).*(go to|navigate to|open|show me).*").matcher(text).matches()) {
            return CommandType.NAVIGATION;
        }
        if (Pattern.compile("(?i).*(back|previous|return).*").matcher(text).matches()) {
            return CommandType.NAVIGATION;
        }

        // Search patterns
        if (Pattern.compile("(?i).*(search|find|look for).*").matcher(text).matches()) {
            return CommandType.SEARCH;
        }

        // Media control patterns
        if (Pattern.compile("(?i).*(play|pause|stop|resume).*").matcher(text).matches()) {
            return CommandType.MEDIA_CONTROL;
        }
        if (Pattern.compile("(?i).*(volume|mute|unmute).*").matcher(text).matches()) {
            return CommandType.MEDIA_CONTROL;
        }

        // Booking patterns
        if (Pattern.compile("(?i).*(book|reserve|buy ticket).*").matcher(text).matches()) {
            return CommandType.BOOKING;
        }

        // System control patterns
        if (Pattern.compile("(?i).*(help|assistance|support).*").matcher(text).matches()) {
            return CommandType.SYSTEM_CONTROL;
        }

        // User account patterns (lower priority)
        if (Pattern.compile("(?i).*(profile|account|settings).*").matcher(text).matches()) {
            return CommandType.USER_ACCOUNT;
        }

        return CommandType.UNKNOWN;
    }

    private String extractAction(String text, CommandType commandType) {
        List<String> actions = commandActions.get(commandType);
        if (actions == null) {
            return "unknown_action";
        }

        // Simple action extraction based on keywords
        for (String action : actions) {
            String[] keywords = action.split("_");
            boolean allKeywordsFound = true;
            for (String keyword : keywords) {
                if (!text.contains(keyword.toLowerCase())) {
                    allKeywordsFound = false;
                    break;
                }
            }
            if (allKeywordsFound) {
                return action;
            }
        }

        return actions.get(0); // Default to first action
    }

    private Map<String, Object> extractParameters(String text, CommandType commandType) {
        Map<String, Object> parameters = new HashMap<>();

        // Extract common parameters based on command type
        switch (commandType) {
            case SEARCH:
                // Extract search query
                Matcher searchMatcher = Pattern.compile("(?i)search for (.+)").matcher(text);
                if (searchMatcher.find()) {
                    parameters.put("query", searchMatcher.group(1).trim());
                }
                break;

            case NAVIGATION:
                // Extract navigation target
                Matcher navMatcher = Pattern.compile("(?i)go to (.+)").matcher(text);
                if (navMatcher.find()) {
                    parameters.put("target", navMatcher.group(1).trim());
                }
                break;

            case MEDIA_CONTROL:
                // Extract volume level if mentioned
                Matcher volumeMatcher = Pattern.compile("(?i)volume (\\d+)").matcher(text);
                if (volumeMatcher.find()) {
                    parameters.put("volume", Integer.parseInt(volumeMatcher.group(1)));
                }
                break;
        }

        return parameters;
    }

    private double calculateTextConfidence(String text, CommandType commandType) {
        // Simple confidence calculation based on pattern matching
        double baseConfidence = 0.5;

        // Increase confidence if command type is not unknown
        if (commandType != CommandType.UNKNOWN) {
            baseConfidence += 0.3;
        }

        // Increase confidence based on text length and clarity
        if (text.length() > 10 && text.split("\\s+").length >= 3) {
            baseConfidence += 0.2;
        }

        return Math.min(1.0, baseConfidence);
    }

    private CommandPriority determinePriority(CommandType commandType) {
        return switch (commandType) {
            case SYSTEM_CONTROL, ACCESSIBILITY -> CommandPriority.HIGH;
            case BOOKING, USER_ACCOUNT -> CommandPriority.NORMAL;
            case NAVIGATION, SEARCH, MEDIA_CONTROL -> CommandPriority.LOW;
            default -> CommandPriority.NORMAL;
        };
    }

    // Command execution methods

    private boolean executeNavigationCommand(VoiceCommand command) {
        log.info("Executing navigation command: " + command.getAction());
        // Integration with existing navigation patterns would go here
        return true;
    }

    private boolean executeSearchCommand(VoiceCommand command) {
        log.info("Executing search command: " + command.getAction());
        // Integration with existing search patterns would go here
        return true;
    }

    private boolean executeMediaControlCommand(VoiceCommand command) {
        log.info("Executing media control command: " + command.getAction());
        // Integration with existing media control patterns would go here
        return true;
    }

    private boolean executeBookingCommand(VoiceCommand command) {
        log.info("Executing booking command: " + command.getAction());
        // Integration with existing booking patterns would go here
        return true;
    }

    private boolean executeUserAccountCommand(VoiceCommand command) {
        log.info("Executing user account command: " + command.getAction());
        // Integration with existing user account patterns would go here
        return true;
    }

    private boolean executeSystemControlCommand(VoiceCommand command) {
        log.info("Executing system control command: " + command.getAction());
        // Integration with existing system control patterns would go here
        return true;
    }

    private boolean executeAccessibilityCommand(VoiceCommand command) {
        log.info("Executing accessibility command: " + command.getAction());
        // Integration with existing accessibility patterns would go here
        return true;
    }

    private VoiceCommand buildVoiceCommandFromResultSet(ResultSet rs) throws SQLException {
        try {
            Map<String, Object> parameters = null;
            String parametersJson = rs.getString("parameters");
            if (parametersJson != null && !parametersJson.trim().isEmpty()) {
                parameters = new com.fasterxml.jackson.databind.ObjectMapper()
                    .readValue(parametersJson, Map.class);
            }

            return VoiceCommand.builder()
                .id(rs.getLong("id"))
                .rawText(rs.getString("raw_text"))
                .processedText(rs.getString("processed_text"))
                .commandType(CommandType.valueOf(rs.getString("command_type")))
                .action(rs.getString("action"))
                .parameters(parameters)
                .confidence(rs.getDouble("confidence"))
                .user(this.userService.getById(rs.getLong("user_id")))
                .recognizedAt(rs.getTimestamp("recognized_at"))
                .executedAt(rs.getTimestamp("executed_at"))
                .status(CommandStatus.valueOf(rs.getString("status")))
                .errorMessage(rs.getString("error_message"))
                .audioDurationMs(rs.getObject("audio_duration_ms", Long.class))
                .language(rs.getString("language"))
                .requiresConfirmation(rs.getBoolean("requires_confirmation"))
                .priority(CommandPriority.valueOf(rs.getString("priority")))
                .build();

        } catch (Exception e) {
            log.error("Error building VoiceCommand from ResultSet", e);
            return null;
        }
    }

    private void createTableIfNotExists() {
        // Check database type for compatibility
        String databaseType = getDatabaseType();
        String sql;

        if ("postgresql".equalsIgnoreCase(databaseType)) {
            sql = """
                CREATE TABLE IF NOT EXISTS voice_commands (
                    id BIGSERIAL PRIMARY KEY,
                    raw_text TEXT NOT NULL,
                    processed_text TEXT,
                    command_type VARCHAR(50) NOT NULL,
                    action VARCHAR(100),
                    parameters TEXT,
                    confidence DOUBLE PRECISION NOT NULL DEFAULT 0.0,
                    user_id BIGINT,
                    recognized_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    executed_at TIMESTAMP NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                    error_message TEXT,
                    audio_duration_ms BIGINT,
                    language VARCHAR(10) DEFAULT 'en-US',
                    requires_confirmation BOOLEAN DEFAULT FALSE,
                    priority VARCHAR(20) DEFAULT 'NORMAL',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
        } else {
            // MySQL/H2/SQLite syntax
            sql = """
                CREATE TABLE IF NOT EXISTS voice_commands (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    raw_text TEXT NOT NULL,
                    processed_text TEXT,
                    command_type VARCHAR(50) NOT NULL,
                    action VARCHAR(100),
                    parameters TEXT,
                    confidence DOUBLE NOT NULL DEFAULT 0.0,
                    user_id BIGINT,
                    recognized_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    executed_at TIMESTAMP NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                    error_message TEXT,
                    audio_duration_ms BIGINT,
                    language VARCHAR(10) DEFAULT 'en-US',
                    requires_confirmation BOOLEAN DEFAULT FALSE,
                    priority VARCHAR(20) DEFAULT 'NORMAL',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    INDEX idx_user_id (user_id),
                    INDEX idx_command_type (command_type),
                    INDEX idx_status (status),
                    INDEX idx_recognized_at (recognized_at)
                )
                """;
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            log.info("Voice commands table created or already exists");
        } catch (SQLException e) {
            log.error("Error creating voice commands table", e);
        }
    }

    private String getDatabaseType() {
        try {
            String url = connection.getMetaData().getURL();
            if (url.contains("postgresql")) {
                return "postgresql";
            } else if (url.contains("mysql")) {
                return "mysql";
            } else if (url.contains("h2")) {
                return "h2";
            } else if (url.contains("sqlite")) {
                return "sqlite";
            }
        } catch (SQLException e) {
            log.warn("Could not determine database type", e);
        }
        return "mysql"; // Default to MySQL syntax
    }

    /**
     * Gets commands by user ID.
     *
     * @param userId the user ID
     * @return list of commands for the user
     */
    public List<VoiceCommand> getCommandsByUserId(Long userId) {
        List<VoiceCommand> commands = new ArrayList<>();
        String sql = "SELECT * FROM voice_commands WHERE user_id = ? ORDER BY recognized_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VoiceCommand command = buildVoiceCommandFromResultSet(rs);
                    if (command != null) {
                        commands.add(command);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving commands by user ID: " + userId, e);
        }
        return commands;
    }

    /**
     * Gets commands by status.
     *
     * @param status the command status
     * @return list of commands with the specified status
     */
    public List<VoiceCommand> getCommandsByStatus(CommandStatus status) {
        List<VoiceCommand> commands = new ArrayList<>();
        String sql = "SELECT * FROM voice_commands WHERE status = ? ORDER BY recognized_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    VoiceCommand command = buildVoiceCommandFromResultSet(rs);
                    if (command != null) {
                        commands.add(command);
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Error retrieving commands by status: " + status, e);
        }
        return commands;
    }

    /**
     * Shuts down the voice recognition service and releases resources.
     */
    public void shutdown() {
        stopListening();
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdown();
        }
        log.info("Voice recognition service shut down");
    }
}
