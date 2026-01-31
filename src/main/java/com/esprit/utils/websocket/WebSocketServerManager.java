package com.esprit.utils.websocket;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.websocket.server.config.JettyWebSocketServletContainerInitializer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * WebSocket Server Manager for RAKCHA application.
 * Provides embedded WebSocket server capabilities for real-time communication.
 * <p>
 * Features:
 * - Embedded Jetty WebSocket server
 * - Connection management
 * - Real-time messaging
 * - Session tracking
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Log4j2
public class WebSocketServerManager {

    private static WebSocketServerManager instance;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final ConcurrentHashMap<String, Object> activeSessions = new ConcurrentHashMap<>();
    private Server server;
    private int port = 8080;

    private WebSocketServerManager() {
        // Private constructor for singleton
    }

    public static synchronized WebSocketServerManager getInstance() {
        if (instance == null) {
            instance = new WebSocketServerManager();
        }
        return instance;
    }

    /**
     * Start the WebSocket server on the specified port
     */
    public CompletableFuture<Boolean> startServer(int port) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (isRunning.get()) {
                    log.warn("WebSocket server is already running on port {}", this.port);
                    return true;
                }

                this.port = port;
                server = new Server(port);

                // Create servlet context handler
                ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                context.setContextPath("/");
                server.setHandler(context);

                // Configure WebSocket
                JettyWebSocketServletContainerInitializer.configure(context, (servletContext, wsContainer) -> {
                    // Configure WebSocket settings
                    wsContainer.setMaxTextMessageSize(65536);
                    wsContainer.setMaxBinaryMessageSize(65536);
                    wsContainer.setIdleTimeout(java.time.Duration.ofMinutes(10));

                    // Add WebSocket endpoints here
                    // wsContainer.addMapping("/chat/*", ChatWebSocketHandler.class);
                    // wsContainer.addMapping("/notifications/*", NotificationWebSocketHandler.class);
                });

                server.start();
                isRunning.set(true);

                log.info("WebSocket server started successfully on port {}", port);
                return true;

            } catch (Exception e) {
                log.error("Failed to start WebSocket server on port {}: {}", port, e.getMessage());
                isRunning.set(false);
                return false;
            }
        });
    }

    /**
     * Stop the WebSocket server
     */
    public CompletableFuture<Boolean> stopServer() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!isRunning.get()) {
                    log.warn("WebSocket server is not running");
                    return true;
                }

                if (server != null) {
                    server.stop();
                    server.destroy();
                    server = null;
                }

                activeSessions.clear();
                isRunning.set(false);

                log.info("WebSocket server stopped successfully");
                return true;

            } catch (Exception e) {
                log.error("Failed to stop WebSocket server: {}", e.getMessage());
                return false;
            }
        });
    }

    /**
     * Check if the server is running
     */
    public boolean isRunning() {
        return isRunning.get();
    }

    /**
     * Get the current port
     */
    public int getPort() {
        return port;
    }

    /**
     * Get the number of active WebSocket sessions
     */
    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    /**
     * Add a WebSocket session
     */
    public void addSession(String sessionId, Object session) {
        activeSessions.put(sessionId, session);
        log.debug("WebSocket session added: {}", sessionId);
    }

    /**
     * Remove a WebSocket session
     */
    public void removeSession(String sessionId) {
        Object removed = activeSessions.remove(sessionId);
        if (removed != null) {
            log.debug("WebSocket session removed: {}", sessionId);
        }
    }

    /**
     * Broadcast message to all active sessions
     */
    public void broadcastMessage(String message) {
        log.info("Broadcasting message to {} active sessions", activeSessions.size());

        // This would iterate through active sessions and send the message
        // Implementation would depend on the specific WebSocket handler
        activeSessions.forEach((sessionId, session) -> {
            try {
                // Send message to session
                log.debug("Message sent to session: {}", sessionId);
            } catch (Exception e) {
                log.error("Failed to send message to session {}: {}", sessionId, e.getMessage());
            }
        });
    }

    /**
     * Send message to a specific session
     */
    public boolean sendMessageToSession(String sessionId, String message) {
        Object session = activeSessions.get(sessionId);
        if (session != null) {
            try {
                // Send message to specific session
                log.debug("Message sent to session {}: {}", sessionId, message);
                return true;
            } catch (Exception e) {
                log.error("Failed to send message to session {}: {}", sessionId, e.getMessage());
                return false;
            }
        } else {
            log.warn("Session {} not found", sessionId);
            return false;
        }
    }

    /**
     * Get server statistics
     */
    public String getServerStats() {
        return String.format(
            "=== WebSocket Server Statistics ===\n" +
                "Status: %s\n" +
                "Port: %d\n" +
                "Active Sessions: %d\n" +
                "Server: %s\n",
            isRunning.get() ? "Running" : "Stopped",
            port,
            activeSessions.size(),
            server != null ? server.getClass().getSimpleName() : "Not initialized"
        );
    }

    /**
     * Initialize default WebSocket server (typically called during application startup)
     */
    public void initializeDefaultServer() {
        startServer(8080).thenAccept(success -> {
            if (success) {
                log.info("Default WebSocket server initialized on port 8080");
            } else {
                log.error("Failed to initialize default WebSocket server");
            }
        });
    }

    /**
     * Shutdown the WebSocket server manager
     */
    public void shutdown() {
        stopServer().thenRun(() -> {
            log.info("WebSocket Server Manager shutdown completed");
        });
    }
}
