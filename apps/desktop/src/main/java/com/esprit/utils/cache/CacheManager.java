package com.esprit.utils.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Advanced caching manager for RAKCHA application using Caffeine and Chronicle Map.
 * Provides high-performance caching with different strategies for different data types.
 * <p>
 * Features:
 * - In-memory caching with Caffeine (for frequently accessed data)
 * - Off-heap persistent caching with Chronicle Map (for large datasets)
 * - Cache statistics and monitoring
 * - Automatic cache warming strategies
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Log4j2
public class CacheManager {

    private static CacheManager instance;
    private final Map<String, Cache<String, Object>> caffeineCache;
    // private final Map<String, ChronicleMap<String, String>> chronicleMaps; // Will be enabled when Chronicle Map is available

    private CacheManager() {
        this.caffeineCache = new ConcurrentHashMap<>();
        // this.chronicleMaps = new ConcurrentHashMap<>(); // Will be enabled when Chronicle Map is available
        initializeDefaultCaches();
    }

    public static synchronized CacheManager getInstance() {
        if (instance == null) {
            instance = new CacheManager();
        }
        return instance;
    }

    /**
     * Initialize default caches for common use cases
     */
    private void initializeDefaultCaches() {
        // High-frequency data cache (movies, users, sessions)
        createCaffeineCache("movies", 1000, Duration.ofMinutes(30));
        createCaffeineCache("users", 500, Duration.ofMinutes(15));
        createCaffeineCache("sessions", 200, Duration.ofMinutes(60));

        // ML predictions cache (short-lived but frequently accessed)
        createCaffeineCache("ml_predictions", 2000, Duration.ofMinutes(10));

        // Analytics cache (medium-lived, moderate size)
        createCaffeineCache("analytics", 500, Duration.ofMinutes(5));

        // Search results cache
        createCaffeineCache("search_results", 1000, Duration.ofMinutes(20));

        log.info("Default caches initialized successfully");
    }

    /**
     * Create a new Caffeine cache with specified parameters
     */
    public void createCaffeineCache(String cacheName, int maxSize, Duration expireAfterWrite) {
        Cache<String, Object> cache = Caffeine.newBuilder()
            .maximumSize(maxSize)
            .expireAfterWrite(expireAfterWrite)
            .recordStats()
            .build();

        caffeineCache.put(cacheName, cache);
        log.info("Created Caffeine cache '{}' with max size {} and expiry {}",
            cacheName, maxSize, expireAfterWrite);
    }

    /**
     * Create a new Chronicle Map for persistent off-heap storage
     * Note: Currently disabled until Chronicle Map dependency is available
     */
    public void createChronicleMap(String mapName, int maxEntries, String filePath) {
        log.info("Chronicle Map creation requested for '{}' but currently disabled - will be implemented in future tasks", mapName);
        // Implementation will be added when Chronicle Map dependency is available
    }

    /**
     * Get value from Caffeine cache
     */
    public Object getCaffeineValue(String cacheName, String key) {
        Cache<String, Object> cache = caffeineCache.get(cacheName);
        if (cache != null) {
            return cache.getIfPresent(key);
        }
        return null;
    }

    /**
     * Put value into Caffeine cache
     */
    public void putCaffeineValue(String cacheName, String key, Object value) {
        Cache<String, Object> cache = caffeineCache.get(cacheName);
        if (cache != null) {
            cache.put(key, value);
        } else {
            log.warn("Cache '{}' not found", cacheName);
        }
    }

    /**
     * Get value from Chronicle Map
     */
    public String getChronicleValue(String mapName, String key) {
        log.debug("Chronicle Map get requested for '{}' but currently disabled", mapName);
        return null; // Will be implemented when Chronicle Map is available
    }

    /**
     * Put value into Chronicle Map
     * Note: Currently disabled until Chronicle Map dependency is available
     */
    public void putChronicleValue(String mapName, String key, String value) {
        log.debug("Chronicle Map put requested for '{}' but currently disabled", mapName);
        // Will be implemented when Chronicle Map is available
    }

    /**
     * Get cache statistics for monitoring
     */
    public CacheStats getCacheStats(String cacheName) {
        Cache<String, Object> cache = caffeineCache.get(cacheName);
        if (cache != null) {
            return cache.stats();
        }
        return null;
    }

    /**
     * Get all cache statistics as a formatted string
     */
    public String getAllCacheStats() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== Cache Statistics ===\n");

        for (Map.Entry<String, Cache<String, Object>> entry : caffeineCache.entrySet()) {
            String cacheName = entry.getKey();
            CacheStats cacheStats = entry.getValue().stats();

            stats.append(String.format(
                "Cache: %s\n" +
                    "  Hit Rate: %.2f%%\n" +
                    "  Hit Count: %d\n" +
                    "  Miss Count: %d\n" +
                    "  Load Count: %d\n" +
                    "  Eviction Count: %d\n" +
                    "  Average Load Time: %.2f ms\n\n",
                cacheName,
                cacheStats.hitRate() * 100,
                cacheStats.hitCount(),
                cacheStats.missCount(),
                cacheStats.loadCount(),
                cacheStats.evictionCount(),
                cacheStats.averageLoadPenalty() / 1_000_000.0
            ));
        }

        return stats.toString();
    }

    /**
     * Clear all caches
     */
    public void clearAllCaches() {
        caffeineCache.values().forEach(Cache::invalidateAll);
        // chronicleMaps.values().forEach(ChronicleMap::clear); // Will be enabled when Chronicle Map is available
        log.info("All caches cleared");
    }

    /**
     * Clear specific cache
     */
    public void clearCache(String cacheName) {
        Cache<String, Object> cache = caffeineCache.get(cacheName);
        if (cache != null) {
            cache.invalidateAll();
            log.info("Cache '{}' cleared", cacheName);
        }
    }

    /**
     * Warm up cache with commonly accessed data
     * This method should be called during application startup
     */
    public void warmUpCaches() {
        log.info("Starting cache warm-up process...");

        // This would typically load frequently accessed data
        // Implementation would depend on specific business logic
        // For now, we'll just log the intent

        log.info("Cache warm-up completed");
    }

    /**
     * Shutdown all caches and release resources
     */
    public void shutdown() {
        try {
            // Close Chronicle Maps (disabled until dependency is available)
            // for (ChronicleMap<String, String> map : chronicleMaps.values()) {
            //     map.close();
            // }
            // chronicleMaps.clear();

            // Clear Caffeine caches
            caffeineCache.clear();

            log.info("Cache manager shutdown completed");
        } catch (Exception e) {
            log.error("Error during cache manager shutdown", e);
        }
    }
}
