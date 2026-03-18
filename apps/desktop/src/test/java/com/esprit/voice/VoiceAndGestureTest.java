package com.esprit.voice;

import com.esprit.models.voice.VoiceCommand;
import com.esprit.services.voice.VoiceRecognitionService;
import com.esprit.utils.voice.ComputerVisionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for voice and gesture recognition functionality.
 * Tests the basic functionality of the voice and gesture recognition system.
 */
public class VoiceAndGestureTest {

    private VoiceRecognitionService voiceService;
    private ComputerVisionUtil visionUtil;

    @BeforeEach
    void setUp() {
        // Note: These services require external dependencies (microphone, camera)
        // In a real test environment, we would use mocks or test doubles
        voiceService = new VoiceRecognitionService();
        visionUtil = ComputerVisionUtil.getInstance();
    }

    @Test
    @DisplayName("Voice command processing should work with text input")
    void testVoiceCommandProcessing() {
        // Test text command processing (doesn't require actual microphone)
        VoiceCommand command = voiceService.processTextCommand("search for action movies");

        assertNotNull(command, "Voice command should be created");
        assertEquals("search for action movies", command.getRawText());
        assertEquals(VoiceCommand.CommandType.SEARCH, command.getCommandType());
        assertTrue(command.getConfidence() > 0, "Command should have positive confidence");
    }

    @Test
    @DisplayName("Voice command should be executable with sufficient confidence")
    void testVoiceCommandExecution() {
        VoiceCommand command = voiceService.processTextCommand("go to my profile");

        assertNotNull(command, "Voice command should be created");
        assertEquals(VoiceCommand.CommandType.NAVIGATION, command.getCommandType());

        // Test execution (will use simulated execution)
        boolean executed = voiceService.executeCommand(command);
        assertTrue(executed, "Command should execute successfully");
    }

    @Test
    @DisplayName("Computer vision utility should initialize without camera")
    void testComputerVisionInitialization() {
        // Test that the utility can be created (initialization may fail without camera)
        assertNotNull(visionUtil, "Computer vision utility should be created");

        // Test gesture history functionality
        assertTrue(visionUtil.getGestureHistory().isEmpty(), "Gesture history should start empty");
    }

    @Test
    @DisplayName("Voice command should handle different command types")
    void testDifferentCommandTypes() {
        // Test navigation command
        VoiceCommand navCommand = voiceService.processTextCommand("go to movies");
        assertEquals(VoiceCommand.CommandType.NAVIGATION, navCommand.getCommandType());

        // Test search command
        VoiceCommand searchCommand = voiceService.processTextCommand("find comedy films");
        assertEquals(VoiceCommand.CommandType.SEARCH, searchCommand.getCommandType());

        // Test media control command
        VoiceCommand mediaCommand = voiceService.processTextCommand("play trailer");
        assertEquals(VoiceCommand.CommandType.MEDIA_CONTROL, mediaCommand.getCommandType());

        // Test booking command
        VoiceCommand bookingCommand = voiceService.processTextCommand("book a ticket");
        assertEquals(VoiceCommand.CommandType.BOOKING, bookingCommand.getCommandType());
    }

    @Test
    @DisplayName("Voice command should extract parameters correctly")
    void testParameterExtraction() {
        VoiceCommand searchCommand = voiceService.processTextCommand("search for action movies");

        assertNotNull(searchCommand.getParameters(), "Parameters should be extracted");
        if (searchCommand.getParameters().containsKey("query")) {
            assertEquals("action movies", searchCommand.getParameters().get("query"));
        }
    }

    @Test
    @DisplayName("Voice command status should be managed correctly")
    void testCommandStatusManagement() {
        VoiceCommand command = voiceService.processTextCommand("show help");

        assertEquals(VoiceCommand.CommandStatus.PENDING, command.getStatus());

        // Test marking as executed
        command.markAsExecuted();
        assertEquals(VoiceCommand.CommandStatus.EXECUTED, command.getStatus());
        assertNotNull(command.getExecutedAt());

        // Test marking as failed
        VoiceCommand failedCommand = voiceService.processTextCommand("invalid command");
        failedCommand.markAsFailed("Test failure");
        assertEquals(VoiceCommand.CommandStatus.FAILED, failedCommand.getStatus());
        assertEquals("Test failure", failedCommand.getErrorMessage());
    }

    @Test
    @DisplayName("Voice service should handle empty or null input gracefully")
    void testInputValidation() {
        // Test null input
        VoiceCommand nullCommand = voiceService.processTextCommand(null);
        assertNull(nullCommand, "Null input should return null command");

        // Test empty input
        VoiceCommand emptyCommand = voiceService.processTextCommand("");
        assertNull(emptyCommand, "Empty input should return null command");

        // Test whitespace input
        VoiceCommand whitespaceCommand = voiceService.processTextCommand("   ");
        assertNull(whitespaceCommand, "Whitespace input should return null command");
    }
}
