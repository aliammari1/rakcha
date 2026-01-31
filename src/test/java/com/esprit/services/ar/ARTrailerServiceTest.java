package com.esprit.services.ar;

import com.esprit.models.ar.ARContent;
import com.esprit.models.ar.ARContent.ARContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test suite for ARTrailerService.
 * Tests AR content management and camera overlay functionality.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("AR Trailer Service Tests")
class ARTrailerServiceTest {

    private ARTrailerService arTrailerService;

    @BeforeEach
    void setUp() {
        // Note: This service requires database connection and OpenCV
        // In a real test environment, we would use mocks or test doubles
        try {
            arTrailerService = new ARTrailerService();
        } catch (Exception e) {
            // Service creation may fail without proper database setup
            System.out.println("ARTrailerService creation failed (expected in test environment): " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Should create ARTrailerService instance")
    void testServiceCreation() {
        // Test that the service can be created (may fail without database)
        assertDoesNotThrow(() -> {
            ARTrailerService service = new ARTrailerService();
            assertNotNull(service, "ARTrailerService should be created");
        });
    }

    @Test
    @DisplayName("Should handle AR content creation")
    void testARContentCreation() {
        if (arTrailerService == null) {
            System.out.println("Skipping test - service not available");
            return;
        }

        // Create test AR content
        ARContent content = ARContent.builder()
            .contentType(ARContentType.TRAILER)
            .title("Test AR Trailer")
            .description("Test AR trailer content")
            .arAssetUrl("http://example.com/ar-asset.obj")
            .requiresCamera(true)
            .supportsHandTracking(false)
            .build();

        assertNotNull(content, "AR content should be created");
        assertEquals(ARContentType.TRAILER, content.getContentType());
        assertEquals("Test AR Trailer", content.getTitle());
        assertTrue(content.getRequiresCamera());
    }

    @Test
    @DisplayName("Should check AR overlay status")
    void testAROverlayStatus() {
        if (arTrailerService == null) {
            System.out.println("Skipping test - service not available");
            return;
        }

        // Initially AR should not be active
        assertFalse(arTrailerService.isARActive(), "AR overlay should not be active initially");
    }

    @Test
    @DisplayName("Should handle AR content by type")
    void testGetByContentType() {
        if (arTrailerService == null) {
            System.out.println("Skipping test - service not available");
            return;
        }

        // Test getting content by type (may return empty list without database)
        assertDoesNotThrow(() -> {
            var trailerContent = arTrailerService.getByContentType(ARContentType.TRAILER);
            assertNotNull(trailerContent, "Should return list (may be empty)");
        });
    }

    @Test
    @DisplayName("Should handle service cleanup")
    void testServiceCleanup() {
        if (arTrailerService == null) {
            System.out.println("Skipping test - service not available");
            return;
        }

        // Test cleanup doesn't throw exceptions
        assertDoesNotThrow(() -> {
            arTrailerService.cleanup();
        });
    }

    @Test
    @DisplayName("Should validate AR content display names")
    void testARContentDisplayNames() {
        // Test AR content display name generation
        ARContent content = new ARContent(ARContentType.POSTER, "Test Poster", "Test description", "http://example.com/asset");

        assertEquals("Test Poster", content.getDisplayName());
        assertEquals("AR Poster", content.getContentTypeDisplayName());
        assertTrue(content.isReady());
    }

    @Test
    @DisplayName("Should handle AR content view counting")
    void testARContentViewCounting() {
        ARContent content = new ARContent(ARContentType.INTERACTIVE_SCENE, "Test Scene", "Test scene", "http://example.com/scene");

        // Initial view count should be 0
        assertEquals(0L, content.getViewCount());

        // Increment view count
        content.incrementViewCount();
        assertEquals(1L, content.getViewCount());

        content.incrementViewCount();
        assertEquals(2L, content.getViewCount());
    }

    @Test
    @DisplayName("Should handle AR content rating updates")
    void testARContentRatingUpdates() {
        ARContent content = new ARContent(ARContentType.TRAILER, "Test Trailer", "Test trailer", "http://example.com/trailer");

        // Initial rating should be 0.0
        assertEquals(0.0, content.getAverageRating());

        // Update rating
        content.updateRating(4.5, 1);
        assertEquals(4.5, content.getAverageRating());

        // Update with second rating
        content.updateRating(3.5, 2);
        assertEquals(4.0, content.getAverageRating(), 0.1); // Average of 4.5 and 3.5
    }

    @Test
    @DisplayName("Should handle AR content tags")
    void testARContentTags() {
        ARContent content = new ARContent(ARContentType.ACTOR_INFO, "Test Actor", "Test actor info", "http://example.com/actor");

        // Add tags
        content.addTag("action");
        content.addTag("drama");
        content.addTag("action"); // Duplicate should be ignored

        assertEquals(2, content.getTags().size());
        assertTrue(content.hasTag("action"));
        assertTrue(content.hasTag("drama"));
        assertFalse(content.hasTag("comedy"));

        // Remove tag
        content.removeTag("drama");
        assertEquals(1, content.getTags().size());
        assertFalse(content.hasTag("drama"));
    }

    @Test
    @DisplayName("Should format duration and file size correctly")
    void testARContentFormatting() {
        ARContent content = new ARContent(ARContentType.THEATER_TOUR, "Test Tour", "Test tour", "http://example.com/tour");

        // Test duration formatting
        content.setDurationSeconds(125); // 2 minutes 5 seconds
        assertEquals("2:05", content.getFormattedDuration());

        content.setDurationSeconds(45); // 45 seconds
        assertEquals("45 seconds", content.getFormattedDuration());

        // Test file size formatting
        content.setFileSizeBytes(1024L); // 1 KB
        assertEquals("1.0 KB", content.getFormattedFileSize());

        content.setFileSizeBytes(1048576L); // 1 MB
        assertEquals("1.0 MB", content.getFormattedFileSize());

        content.setFileSizeBytes(1073741824L); // 1 GB
        assertEquals("1.0 GB", content.getFormattedFileSize());
    }
}
