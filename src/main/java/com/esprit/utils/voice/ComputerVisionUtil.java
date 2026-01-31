package com.esprit.utils.voice;

import lombok.extern.log4j.Log4j2;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Computer Vision utility class for gesture recognition and camera access.
 * Provides hand gesture recognition using OpenCV following existing utility patterns.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */
@Log4j2
public class ComputerVisionUtil {

    private static final Object LOCK = new Object();
    // Gesture recognition parameters
    private static final int GESTURE_HISTORY_SIZE = 10;
    // Camera parameters
    private static final int CAMERA_WIDTH = 640;
    private static final int CAMERA_HEIGHT = 480;
    private static final int CAMERA_FPS = 30;
    private static ComputerVisionUtil instance;

    static {
        // Load OpenCV native library
        try {
            nu.pattern.OpenCV.loadLocally();
            log.info("OpenCV library loaded successfully");
        } catch (Exception e) {
            log.error("Failed to load OpenCV library", e);
        }
    }

    // Background processing
    private final ExecutorService backgroundExecutor;
    private final List<GestureData> gestureHistory;
    private final Map<String, Consumer<GestureData>> gestureActions;
    // OpenCV components
    private VideoCapture camera;
    private CascadeClassifier handCascade;
    private boolean isInitialized = false;
    private boolean isCameraActive = false;

    /**
     * Private constructor following singleton pattern used in existing utilities.
     */
    private ComputerVisionUtil() {
        this.backgroundExecutor = Executors.newFixedThreadPool(2);
        this.gestureHistory = new ArrayList<>();
        this.gestureActions = new HashMap<>();
        initializeGestureActions();
    }

    /**
     * Gets the singleton instance following existing utility patterns.
     *
     * @return the ComputerVisionUtil instance
     */
    public static ComputerVisionUtil getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ComputerVisionUtil();
                }
            }
        }
        return instance;
    }

    /**
     * Initializes the computer vision system with camera and cascade classifiers.
     *
     * @return true if initialization was successful
     */
    public boolean initialize() {
        if (isInitialized) {
            return true;
        }

        try {
            // Initialize camera
            camera = new VideoCapture(0); // Default camera
            if (!camera.isOpened()) {
                log.error("Failed to open camera");
                return false;
            }

            // Set camera properties
            camera.set(3, CAMERA_WIDTH);  // Width
            camera.set(4, CAMERA_HEIGHT); // Height
            camera.set(5, CAMERA_FPS);    // FPS

            // Load hand cascade classifier
            String cascadePath = getClass().getClassLoader().getResource("haar/haarcascade_hand.xml").getPath();
            handCascade = new CascadeClassifier();
            if (!handCascade.load(cascadePath)) {
                log.warn("Hand cascade not found, using face cascade as fallback");
                cascadePath = getClass().getClassLoader().getResource("haar/haarcascade_frontalface_default.xml").getPath();
                if (!handCascade.load(cascadePath)) {
                    log.error("Failed to load cascade classifier");
                    return false;
                }
            }

            isInitialized = true;
            log.info("Computer vision system initialized successfully");
            return true;

        } catch (Exception e) {
            log.error("Failed to initialize computer vision system", e);
            return false;
        }
    }

    /**
     * Starts gesture recognition in background thread.
     *
     * @param gestureCallback callback to handle detected gestures
     * @return CompletableFuture for async operation
     */
    public CompletableFuture<Void> startGestureRecognition(Consumer<GestureData> gestureCallback) {
        if (!isInitialized && !initialize()) {
            return CompletableFuture.failedFuture(new RuntimeException("Computer vision system not initialized"));
        }

        return CompletableFuture.runAsync(() -> {
            isCameraActive = true;
            Mat frame = new Mat();
            Mat grayFrame = new Mat();

            log.info("Starting gesture recognition");

            while (isCameraActive && camera.isOpened()) {
                try {
                    if (camera.read(frame) && !frame.empty()) {
                        // Convert to grayscale for processing
                        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                        // Detect gestures
                        List<GestureData> gestures = detectGestures(grayFrame, frame);

                        // Process detected gestures
                        for (GestureData gesture : gestures) {
                            addToGestureHistory(gesture);

                            // Trigger callback
                            if (gestureCallback != null) {
                                gestureCallback.accept(gesture);
                            }

                            // Execute mapped actions
                            executeGestureAction(gesture);
                        }

                        // Small delay to prevent excessive CPU usage
                        Thread.sleep(33); // ~30 FPS
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error during gesture recognition", e);
                }
            }

            log.info("Gesture recognition stopped");
        }, backgroundExecutor);
    }

    /**
     * Stops gesture recognition and releases camera resources.
     */
    public void stopGestureRecognition() {
        isCameraActive = false;
        if (camera != null && camera.isOpened()) {
            camera.release();
        }
        log.info("Gesture recognition stopped and camera released");
    }

    /**
     * Detects gestures in the given frame.
     *
     * @param grayFrame  grayscale frame for detection
     * @param colorFrame original color frame for reference
     * @return list of detected gestures
     */
    private List<GestureData> detectGestures(Mat grayFrame, Mat colorFrame) {
        List<GestureData> gestures = new ArrayList<>();

        // Detect objects (hands/faces) using cascade classifier
        MatOfRect detections = new MatOfRect();
        handCascade.detectMultiScale(grayFrame, detections, 1.1, 3, 0, new Size(30, 30), new Size());

        Rect[] detectionArray = detections.toArray();

        for (Rect detection : detectionArray) {
            // Analyze the detected region for gesture classification
            GestureType gestureType = classifyGesture(grayFrame, detection);

            if (gestureType != GestureType.UNKNOWN) {
                GestureData gesture = new GestureData(
                    gestureType,
                    detection,
                    System.currentTimeMillis(),
                    calculateConfidence(grayFrame, detection)
                );
                gestures.add(gesture);
            }
        }

        return gestures;
    }

    /**
     * Classifies the gesture type based on the detected region.
     *
     * @param frame  the frame containing the gesture
     * @param region the region of interest
     * @return the classified gesture type
     */
    private GestureType classifyGesture(Mat frame, Rect region) {
        try {
            // Extract region of interest
            Mat roi = new Mat(frame, region);

            // Simple gesture classification based on region properties
            double aspectRatio = (double) region.width / region.height;
            int area = region.width * region.height;

            // Basic gesture classification logic
            if (aspectRatio > 1.5 && area > 5000) {
                return GestureType.SWIPE_RIGHT;
            } else if (aspectRatio < 0.7 && area > 3000) {
                return GestureType.SWIPE_UP;
            } else if (area > 8000) {
                return GestureType.OPEN_PALM;
            } else if (area > 2000 && area < 4000) {
                return GestureType.POINT;
            }

            return GestureType.UNKNOWN;

        } catch (Exception e) {
            log.error("Error classifying gesture", e);
            return GestureType.UNKNOWN;
        }
    }

    /**
     * Calculates confidence score for the detected gesture.
     *
     * @param frame  the frame containing the gesture
     * @param region the region of interest
     * @return confidence score between 0.0 and 1.0
     */
    private double calculateConfidence(Mat frame, Rect region) {
        try {
            // Simple confidence calculation based on region stability and size
            double sizeScore = Math.min(1.0, region.area() / 10000.0);
            double positionScore = 0.8; // Default position score

            // Check against gesture history for stability
            if (!gestureHistory.isEmpty()) {
                GestureData lastGesture = gestureHistory.get(gestureHistory.size() - 1);
                double distance = Math.sqrt(
                    Math.pow(region.x - lastGesture.boundingBox().x, 2) +
                        Math.pow(region.y - lastGesture.boundingBox().y, 2)
                );
                positionScore = Math.max(0.3, 1.0 - (distance / 100.0));
            }

            return (sizeScore + positionScore) / 2.0;

        } catch (Exception e) {
            log.error("Error calculating confidence", e);
            return 0.5; // Default confidence
        }
    }

    /**
     * Adds gesture to history and maintains history size.
     *
     * @param gesture the gesture to add
     */
    private void addToGestureHistory(GestureData gesture) {
        synchronized (gestureHistory) {
            gestureHistory.add(gesture);
            if (gestureHistory.size() > GESTURE_HISTORY_SIZE) {
                gestureHistory.remove(0);
            }
        }
    }

    /**
     * Initializes default gesture-to-action mappings following existing command patterns.
     */
    private void initializeGestureActions() {
        // Map gestures to actions following existing command patterns
        gestureActions.put(GestureType.SWIPE_RIGHT.name(), gesture -> {
            log.info("Executing SWIPE_RIGHT action: Navigate forward");
            // This would integrate with existing navigation patterns
        });

        gestureActions.put(GestureType.SWIPE_LEFT.name(), gesture -> {
            log.info("Executing SWIPE_LEFT action: Navigate backward");
            // This would integrate with existing navigation patterns
        });

        gestureActions.put(GestureType.SWIPE_UP.name(), gesture -> {
            log.info("Executing SWIPE_UP action: Scroll up");
            // This would integrate with existing scroll patterns
        });

        gestureActions.put(GestureType.SWIPE_DOWN.name(), gesture -> {
            log.info("Executing SWIPE_DOWN action: Scroll down");
            // This would integrate with existing scroll patterns
        });

        gestureActions.put(GestureType.OPEN_PALM.name(), gesture -> {
            log.info("Executing OPEN_PALM action: Stop/Pause");
            // This would integrate with existing media control patterns
        });

        gestureActions.put(GestureType.POINT.name(), gesture -> {
            log.info("Executing POINT action: Select/Click");
            // This would integrate with existing selection patterns
        });
    }

    /**
     * Executes the action mapped to the detected gesture.
     *
     * @param gesture the detected gesture
     */
    private void executeGestureAction(GestureData gesture) {
        Consumer<GestureData> action = gestureActions.get(gesture.type().name());
        if (action != null && gesture.confidence() > 0.6) {
            try {
                action.accept(gesture);
            } catch (Exception e) {
                log.error("Error executing gesture action for " + gesture.type(), e);
            }
        }
    }

    /**
     * Registers a custom gesture action following existing command patterns.
     *
     * @param gestureType the gesture type to map
     * @param action      the action to execute
     */
    public void registerGestureAction(GestureType gestureType, Consumer<GestureData> action) {
        gestureActions.put(gestureType.name(), action);
        log.info("Registered custom action for gesture: " + gestureType);
    }

    /**
     * Gets the current gesture history.
     *
     * @return list of recent gestures
     */
    public List<GestureData> getGestureHistory() {
        synchronized (gestureHistory) {
            return new ArrayList<>(gestureHistory);
        }
    }

    /**
     * Checks if the camera is currently active.
     *
     * @return true if camera is active
     */
    public boolean isCameraActive() {
        return isCameraActive;
    }

    /**
     * Checks if the system is initialized.
     *
     * @return true if initialized
     */
    public boolean isInitialized() {
        return isInitialized;
    }

    /**
     * Releases all resources and shuts down the utility.
     */
    public void shutdown() {
        stopGestureRecognition();
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdown();
        }
        log.info("Computer vision utility shut down");
    }

    /**
     * Enum defining supported gesture types.
     */
    public enum GestureType {
        SWIPE_LEFT,
        SWIPE_RIGHT,
        SWIPE_UP,
        SWIPE_DOWN,
        OPEN_PALM,
        CLOSED_FIST,
        POINT,
        PINCH,
        ZOOM_IN,
        ZOOM_OUT,
        UNKNOWN
    }

    /**
     * Data class representing a detected gesture.
     */
    public record GestureData(GestureType type, Rect boundingBox, long timestamp, double confidence) {

        @Override
        public String toString() {
            return String.format("Gesture{type=%s, confidence=%.2f, timestamp=%d}",
                type, confidence, timestamp);
        }
    }
}
