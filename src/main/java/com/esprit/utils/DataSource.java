package com.esprit.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class providing helper methods for the RAKCHA application. Contains
 * reusable functionality and common operations. Supports multiple database
 * types
 * including MySQL, SQLite, PostgreSQL, and H2.
 *
 * @author RAKCHA Team
 * @version 1.0.0
 * @since 1.0.0
 */

@Log4j2
public class DataSource {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static DataSource instance;
    private final String url;
    private final String user;
    private final String password;
    private final boolean useConnectionPooling;
    private Connection connection;
    private HikariDataSource hikariDataSource;

    private DataSource() {
        // Set default URL based on a database type
        this.url = System.getProperty("db.url", dotenv.get("DB_URL", ""));
        this.user = System.getProperty("db.user", dotenv.get("DB_USER", "root"));
        this.password = System.getProperty("db.password", dotenv.get("DB_PASSWORD", ""));

        // Enable connection pooling for production databases (MySQL, PostgreSQL)
        // Use simple connection for SQLite and H2 (embedded databases)
        this.useConnectionPooling = !url.toUpperCase().contains("SQLITE") &&
            !url.toUpperCase().contains("H2") &&
            !url.isEmpty();

        // Create a data directory for SQLite if needed
        if (url.toUpperCase().contains("SQLITE")) {
            createDataDirectoryIfNeeded();
        }

        try {
            if (useConnectionPooling) {
                initializeConnectionPool();
                log.info("Database connection pool established successfully with HikariCP");
            } else {
                this.connection = DriverManager.getConnection(this.url, this.user, this.password);
                log.info("Database connection established successfully (direct connection)");
            }
        } catch (final SQLException e) {
            log.error("Failed to establish database connection", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }


    /**
     * @return DataSource
     */
    public static DataSource getInstance() {
        if (null == DataSource.instance) {
            DataSource.instance = new DataSource();
        }

        return DataSource.instance;
    }


    /**
     * Initialize HikariCP connection pool for production databases
     */
    private void initializeConnectionPool() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(this.url);
        config.setUsername(this.user);
        config.setPassword(this.password);

        // HikariCP performance optimizations
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        config.setLeakDetectionThreshold(60000); // 1 minute

        // Database-specific optimizations
        if (url.toUpperCase().contains("MYSQL")) {
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
        } else if (url.toUpperCase().contains("POSTGRESQL")) {
            config.setDriverClassName("org.postgresql.Driver");
            config.addDataSourceProperty("prepareThreshold", "1");
            config.addDataSourceProperty("preparedStatementCacheQueries", "256");
            config.addDataSourceProperty("preparedStatementCacheSizeMiB", "5");
            config.addDataSourceProperty("databaseMetadataCacheFields", "65536");
            config.addDataSourceProperty("databaseMetadataCacheFieldsMiB", "5");
        }

        this.hikariDataSource = new HikariDataSource(config);
    }

    /**
     * @return Connection
     */
    public Connection getConnection() {
        try {
            if (useConnectionPooling && hikariDataSource != null) {
                return hikariDataSource.getConnection();
            } else {
                if (this.connection == null || this.connection.isClosed()) {
                    this.connection = DriverManager.getConnection(this.url, this.user, this.password);
                }
                return this.connection;
            }
        } catch (SQLException e) {
            log.error("Failed to get database connection", e);
            throw new RuntimeException("Failed to get database connection", e);
        }
    }


    /**
     * Close the database connection or connection pool
     */
    public void closeConnection() {
        if (useConnectionPooling && hikariDataSource != null) {
            try {
                hikariDataSource.close();
                log.info("HikariCP connection pool closed successfully");
            } catch (Exception e) {
                log.warn("Error closing HikariCP connection pool", e);
            }
        } else if (this.connection != null) {
            try {
                this.connection.close();
                log.info("Direct database connection closed successfully");
            } catch (SQLException e) {
                log.warn("Error closing database connection", e);
            }
        }
    }

    /**
     * Get connection pool statistics (for monitoring)
     */
    public String getConnectionPoolStats() {
        if (useConnectionPooling && hikariDataSource != null) {
            return String.format(
                "Pool Stats - Active: %d, Idle: %d, Total: %d, Waiting: %d",
                hikariDataSource.getHikariPoolMXBean().getActiveConnections(),
                hikariDataSource.getHikariPoolMXBean().getIdleConnections(),
                hikariDataSource.getHikariPoolMXBean().getTotalConnections(),
                hikariDataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
            );
        }
        return "Connection pooling not enabled";
    }

    /**
     * Check if connection pooling is enabled
     */
    public boolean isConnectionPoolingEnabled() {
        return useConnectionPooling;
    }


    /**
     * Create data directory for SQLite database if it doesn't exist
     */
    private void createDataDirectoryIfNeeded() {
        try {
            java.nio.file.Path dataDir = java.nio.file.Paths.get(System.getProperty("user.dir"), "data");
            if (!java.nio.file.Files.exists(dataDir)) {
                java.nio.file.Files.createDirectories(dataDir);
                log.info("Created data directory for SQLite: " + dataDir.toAbsolutePath());
            }

        } catch (Exception e) {
            log.warn("Failed to create data directory for SQLite", e);
        }

    }


    /**
     * Get database URL being used
     */
    public String getDatabaseUrl() {
        return this.url;
    }


    /**
     * Creates all required tables if they don't exist.
     * This method ensures database schema compatibility across different databases.
     */
    public void createTablesIfNotExists() {
        try {
            TableCreator tableCreator = new TableCreator(this.getConnection());
            tableCreator.createAllTablesIfNotExists();
            log.info("All tables created successfully or already exist");
        } catch (Exception e) {
            log.error("Error creating database tables", e);
            // Don't throw exception here to avoid breaking the application if tables
            // already exist
        }

    }

}

