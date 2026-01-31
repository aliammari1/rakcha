package com.esprit.utils.performance;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import lombok.extern.log4j.Log4j2;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Performance monitoring utility for RAKCHA application.
 * Provides comprehensive system metrics, performance tracking, and monitoring capabilities.
 * <p>
 * Features:
 * - JVM metrics monitoring
 * - Application performance metrics
 * - Database connection pool monitoring
 * - Cache performance tracking
 * - Custom metrics registration
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Log4j2
public class PerformanceMonitor {

    private static PerformanceMonitor instance;
    private final MeterRegistry meterRegistry;
    private final ScheduledExecutorService scheduler;
    private final AtomicLong requestCount = new AtomicLong(0);
    private final AtomicLong errorCount = new AtomicLong(0);

    // JVM monitoring beans
    private final MemoryMXBean memoryBean;
    private final OperatingSystemMXBean osBean;
    private final RuntimeMXBean runtimeBean;

    // Metrics
    private final Counter requestCounter;
    private final Counter errorCounter;
    private final Timer responseTimer;

    private PerformanceMonitor() {
        this.meterRegistry = new SimpleMeterRegistry();
        this.scheduler = Executors.newScheduledThreadPool(2);

        // Initialize JVM monitoring beans
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.runtimeBean = ManagementFactory.getRuntimeMXBean();

        // Initialize metrics
        this.requestCounter = Counter.builder("rakcha.requests.total")
            .description("Total number of requests")
            .register(meterRegistry);

        this.errorCounter = Counter.builder("rakcha.errors.total")
            .description("Total number of errors")
            .register(meterRegistry);

        this.responseTimer = Timer.builder("rakcha.response.time")
            .description("Response time")
            .register(meterRegistry);

        initializeSystemMetrics();
        startPeriodicMonitoring();
    }

    public static synchronized PerformanceMonitor getInstance() {
        if (instance == null) {
            instance = new PerformanceMonitor();
        }
        return instance;
    }

    /**
     * Initialize system-level metrics
     */
    private void initializeSystemMetrics() {
        // Memory metrics
        Gauge.builder("rakcha.jvm.memory.used", this, monitor -> memoryBean.getHeapMemoryUsage().getUsed())
            .description("Used memory in bytes")
            .register(meterRegistry);

        Gauge.builder("rakcha.jvm.memory.max", this, monitor -> memoryBean.getHeapMemoryUsage().getMax())
            .description("Max memory in bytes")
            .register(meterRegistry);

        // CPU metrics (using available processors as a placeholder since getSystemCpuLoad is not always available)
        Gauge.builder("rakcha.system.processors", this, monitor -> osBean.getAvailableProcessors())
            .description("Available processors")
            .register(meterRegistry);

        // Uptime metrics
        Gauge.builder("rakcha.jvm.uptime", this, monitor -> runtimeBean.getUptime())
            .description("JVM uptime in milliseconds")
            .register(meterRegistry);

        log.info("System metrics initialized");
    }

    /**
     * Start periodic monitoring tasks
     */
    private void startPeriodicMonitoring() {
        // Log system stats every 5 minutes
        scheduler.scheduleAtFixedRate(this::logSystemStats, 5, 5, TimeUnit.MINUTES);

        // Check for memory leaks every 10 minutes
        scheduler.scheduleAtFixedRate(this::checkMemoryUsage, 10, 10, TimeUnit.MINUTES);

        log.info("Periodic monitoring started");
    }

    /**
     * Record a request
     */
    public void recordRequest() {
        requestCounter.increment();
        requestCount.incrementAndGet();
    }

    /**
     * Record an error
     */
    public void recordError() {
        errorCounter.increment();
        errorCount.incrementAndGet();
    }

    /**
     * Record response time
     */
    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    /**
     * Stop timer and record response time
     */
    public void recordResponseTime(Timer.Sample sample) {
        sample.stop(responseTimer);
    }

    /**
     * Get current system statistics
     */
    public String getSystemStats() {
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

        return String.format(
            "=== System Performance Statistics ===\n" +
                "JVM Uptime: %d ms\n" +
                "Memory Usage: %.2f%% (%d MB / %d MB)\n" +
                "Available Processors: %d\n" +
                "Total Requests: %d\n" +
                "Total Errors: %d\n" +
                "Error Rate: %.2f%%\n" +
                "Average Response Time: %.2f ms\n",
            runtimeBean.getUptime(),
            memoryUsagePercent,
            usedMemory / (1024 * 1024),
            maxMemory / (1024 * 1024),
            osBean.getAvailableProcessors(),
            requestCount.get(),
            errorCount.get(),
            requestCount.get() > 0 ? (double) errorCount.get() / requestCount.get() * 100 : 0.0,
            responseTimer.mean(TimeUnit.MILLISECONDS)
        );
    }

    /**
     * Log system statistics
     */
    private void logSystemStats() {
        try {
            log.info("System Performance Stats:\n{}", getSystemStats());
        } catch (Exception e) {
            log.error("Error logging system stats", e);
        }
    }

    /**
     * Check memory usage and warn if high
     */
    private void checkMemoryUsage() {
        try {
            long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
            long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;

            if (memoryUsagePercent > 80) {
                log.warn("High memory usage detected: {:.2f}% ({} MB / {} MB)",
                    memoryUsagePercent,
                    usedMemory / (1024 * 1024),
                    maxMemory / (1024 * 1024));

                // Suggest garbage collection
                if (memoryUsagePercent > 90) {
                    log.warn("Critical memory usage! Suggesting garbage collection...");
                    System.gc();
                }
            }
        } catch (Exception e) {
            log.error("Error checking memory usage", e);
        }
    }

    /**
     * Get application health status
     */
    public String getHealthStatus() {
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        double errorRate = requestCount.get() > 0 ? (double) errorCount.get() / requestCount.get() * 100 : 0.0;

        String healthStatus = "HEALTHY";
        if (memoryUsagePercent > 90 || errorRate > 10) {
            healthStatus = "CRITICAL";
        } else if (memoryUsagePercent > 80 || errorRate > 5) {
            healthStatus = "WARNING";
        }

        return String.format(
            "Health Status: %s\n" +
                "Memory Usage: %.2f%%\n" +
                "Error Rate: %.2f%%\n" +
                "Uptime: %d ms\n",
            healthStatus,
            memoryUsagePercent,
            errorRate,
            runtimeBean.getUptime()
        );
    }

    /**
     * Reset all counters
     */
    public void resetCounters() {
        requestCount.set(0);
        errorCount.set(0);
        log.info("Performance counters reset");
    }

    /**
     * Get meter registry for custom metrics
     */
    public MeterRegistry getMeterRegistry() {
        return meterRegistry;
    }

    /**
     * Shutdown performance monitor
     */
    public void shutdown() {
        try {
            scheduler.shutdown();
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            log.info("Performance Monitor shutdown completed");
        } catch (Exception e) {
            log.error("Error during Performance Monitor shutdown", e);
        }
    }
}
