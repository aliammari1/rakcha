package com.esprit.services.ml;

import com.esprit.models.ml.Classification;
import com.esprit.services.IService;
import com.esprit.utils.Page;
import com.esprit.utils.PageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.log4j.Log4j2;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Weka-based classification service implementing IService pattern.
 * Provides fallback ML capabilities using Weka algorithms.
 * Follows existing RAKCHA service architecture patterns.
 */

@Log4j2
public class WekaClassificationService implements IService<Classification> {

    // Model paths
    private static final String NAIVE_BAYES_MODEL_PATH = "models/naive_bayes.model";
    private static final String DECISION_TREE_MODEL_PATH = "models/decision_tree.model";
    private static final String TRAINING_DATA_PATH = "data/training_data.arff";
    private final com.esprit.utils.DataSource dataSource;
    private final ExecutorService executorService;
    private final ObjectMapper objectMapper;
    // Caching for performance
    private final Cache<String, Classification> classificationCache;
    // Weka classifiers
    private Classifier naiveBayesClassifier;
    private Classifier decisionTreeClassifier;
    private Instances trainingData;

    public WekaClassificationService() {
        this.dataSource = com.esprit.utils.DataSource.getInstance();
        this.executorService = Executors.newFixedThreadPool(2);
        this.objectMapper = new ObjectMapper();

        // Initialize caching
        this.classificationCache = Caffeine.newBuilder()
            .maximumSize(5_000)
            .expireAfterWrite(Duration.ofMinutes(15))
            .build();

        // Initialize Weka models
        initializeModels();
    }

    /**
     * Initialize Weka classifiers and training data.
     */
    private void initializeModels() {
        try {
            // Initialize classifiers
            naiveBayesClassifier = new NaiveBayes();
            decisionTreeClassifier = new J48();

            // Try to load pre-trained models
            loadPreTrainedModels();

            // If no pre-trained models, create training data and train
            if (trainingData == null) {
                createTrainingData();
                trainModels();
            }

            log.info("Weka classification models initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Weka models", e);
        }
    }

    /**
     * Load pre-trained Weka models from disk.
     */
    private void loadPreTrainedModels() {
        try {
            File nbModelFile = new File(NAIVE_BAYES_MODEL_PATH);
            File dtModelFile = new File(DECISION_TREE_MODEL_PATH);
            File trainingDataFile = new File(TRAINING_DATA_PATH);

            if (nbModelFile.exists() && dtModelFile.exists() && trainingDataFile.exists()) {
                // Load models
                naiveBayesClassifier = (Classifier) weka.core.SerializationHelper.read(NAIVE_BAYES_MODEL_PATH);
                decisionTreeClassifier = (Classifier) weka.core.SerializationHelper.read(DECISION_TREE_MODEL_PATH);

                // Load training data structure
                weka.core.converters.ConverterUtils.DataSource source = new weka.core.converters.ConverterUtils.DataSource(TRAINING_DATA_PATH);
                trainingData = source.getDataSet();
                if (trainingData.classIndex() == -1) {
                    trainingData.setClassIndex(trainingData.numAttributes() - 1);
                }

                log.info("Pre-trained Weka models loaded successfully");
            }
        } catch (Exception e) {
            log.warn("Could not load pre-trained models, will create new ones", e);
        }
    }

    /**
     * Create training data from existing database records.
     */
    private void createTrainingData() {
        try {
            // Define attributes for user classification
            ArrayList<Attribute> attributes = new ArrayList<>();

            // User features
            attributes.add(new Attribute("age"));
            attributes.add(new Attribute("total_tickets"));
            attributes.add(new Attribute("avg_rating_given"));
            attributes.add(new Attribute("preferred_genre_diversity"));
            attributes.add(new Attribute("avg_ticket_price"));
            attributes.add(new Attribute("weekend_preference"));
            attributes.add(new Attribute("evening_preference"));

            // Class attribute (user type)
            List<String> classValues = Arrays.asList("casual", "regular", "enthusiast", "premium");
            attributes.add(new Attribute("user_type", classValues));

            // Create dataset
            trainingData = new Instances("UserClassification", attributes, 0);
            trainingData.setClassIndex(trainingData.numAttributes() - 1);

            // Populate with data from database
            populateTrainingData();

            log.info("Training data created with " + trainingData.numInstances() + " instances");
        } catch (Exception e) {
            log.error("Error creating training data", e);
        }
    }

    /**
     * Populate training data from database.
     */
    private void populateTrainingData() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                SELECT
                    u.id,
                    YEAR(CURDATE()) - YEAR(u.birth_date) as age,
                    COUNT(t.id) as total_tickets,
                    AVG(COALESCE(r.rating, 3)) as avg_rating_given,
                    COUNT(DISTINCT c.name) as genre_diversity,
                    AVG(t.price_paid) as avg_ticket_price,
                    SUM(CASE WHEN DAYOFWEEK(ms.start_time) IN (1,7) THEN 1 ELSE 0 END) / COUNT(*) as weekend_preference,
                    SUM(CASE WHEN HOUR(ms.start_time) >= 18 THEN 1 ELSE 0 END) / COUNT(*) as evening_preference
                FROM users u
                LEFT JOIN tickets t ON u.id = t.client_id
                LEFT JOIN movie_sessions ms ON t.movie_session_id = ms.id
                LEFT JOIN films f ON ms.film_id = f.id
                LEFT JOIN film_categories fc ON f.id = fc.film_id
                LEFT JOIN categories c ON fc.category_id = c.id
                LEFT JOIN reviews r ON u.id = r.user_id AND f.id = r.film_id
                WHERE u.role = 'CLIENT'
                GROUP BY u.id
                HAVING total_tickets > 0
                """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    DenseInstance instance = new DenseInstance(trainingData.numAttributes());
                    instance.setDataset(trainingData);

                    // Set feature values
                    instance.setValue(0, rs.getDouble("age"));
                    instance.setValue(1, rs.getDouble("total_tickets"));
                    instance.setValue(2, rs.getDouble("avg_rating_given"));
                    instance.setValue(3, rs.getDouble("genre_diversity"));
                    instance.setValue(4, rs.getDouble("avg_ticket_price"));
                    instance.setValue(5, rs.getDouble("weekend_preference"));
                    instance.setValue(6, rs.getDouble("evening_preference"));

                    // Determine user type based on behavior
                    String userType = determineUserType(
                        rs.getInt("total_tickets"),
                        rs.getDouble("avg_ticket_price"),
                        rs.getInt("genre_diversity")
                    );
                    instance.setValue(7, userType);

                    trainingData.add(instance);
                }
            }
        } catch (SQLException e) {
            log.error("Error populating training data", e);
        }
    }

    /**
     * Determine user type based on behavior patterns.
     */
    private String determineUserType(int totalTickets, double avgPrice, int genreDiversity) {
        if (totalTickets >= 20 && avgPrice >= 15.0 && genreDiversity >= 4) {
            return "premium";
        } else if (totalTickets >= 10 && genreDiversity >= 3) {
            return "enthusiast";
        } else if (totalTickets >= 5) {
            return "regular";
        } else {
            return "casual";
        }
    }

    /**
     * Train Weka models with the training data.
     */
    private void trainModels() {
        try {
            if (trainingData != null && trainingData.numInstances() > 0) {
                // Train Naive Bayes
                naiveBayesClassifier.buildClassifier(trainingData);

                // Train Decision Tree
                decisionTreeClassifier.buildClassifier(trainingData);

                // Save models to disk
                saveModels();

                log.info("Weka models trained successfully");
            }
        } catch (Exception e) {
            log.error("Error training Weka models", e);
        }
    }

    /**
     * Save trained models to disk.
     */
    private void saveModels() {
        try {
            // Create models directory if it doesn't exist
            File modelsDir = new File("models");
            if (!modelsDir.exists()) {
                modelsDir.mkdirs();
            }

            // Save classifiers
            weka.core.SerializationHelper.write(NAIVE_BAYES_MODEL_PATH, naiveBayesClassifier);
            weka.core.SerializationHelper.write(DECISION_TREE_MODEL_PATH, decisionTreeClassifier);

            // Save training data structure
            weka.core.converters.ArffSaver saver = new weka.core.converters.ArffSaver();
            saver.setInstances(trainingData);
            saver.setFile(new File(TRAINING_DATA_PATH));
            saver.writeBatch();

            log.info("Weka models saved successfully");
        } catch (Exception e) {
            log.error("Error saving Weka models", e);
        }
    }

    /**
     * Classify a user asynchronously.
     *
     * @param userId the user ID to classify
     * @return CompletableFuture with classification result
     */
    public CompletableFuture<Classification> classifyUserAsync(Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            String cacheKey = "user_" + userId;

            // Check cache first
            Classification cached = classificationCache.getIfPresent(cacheKey);
            if (cached != null) {
                return cached;
            }

            // Perform classification
            Classification classification = classifyUser(userId);

            // Cache and store result
            if (classification != null) {
                classificationCache.put(cacheKey, classification);
                create(classification);
            }

            return classification;
        }, executorService);
    }

    /**
     * Classify a user using Weka algorithms.
     */
    private Classification classifyUser(Long userId) {
        try {
            if (trainingData == null || naiveBayesClassifier == null) {
                log.warn("Models not initialized, cannot classify user");
                return null;
            }

            // Get user features
            Map<String, Double> userFeatures = getUserFeatures(userId);
            if (userFeatures.isEmpty()) {
                return null;
            }

            // Create instance for classification
            DenseInstance instance = new DenseInstance(trainingData.numAttributes());
            instance.setDataset(trainingData);

            // Set feature values
            instance.setValue(0, userFeatures.getOrDefault("age", 25.0));
            instance.setValue(1, userFeatures.getOrDefault("total_tickets", 0.0));
            instance.setValue(2, userFeatures.getOrDefault("avg_rating_given", 3.0));
            instance.setValue(3, userFeatures.getOrDefault("genre_diversity", 1.0));
            instance.setValue(4, userFeatures.getOrDefault("avg_ticket_price", 10.0));
            instance.setValue(5, userFeatures.getOrDefault("weekend_preference", 0.5));
            instance.setValue(6, userFeatures.getOrDefault("evening_preference", 0.5));

            // Classify using Naive Bayes (primary)
            double[] distribution = naiveBayesClassifier.distributionForInstance(instance);
            int predictedClass = (int) naiveBayesClassifier.classifyInstance(instance);

            String classLabel = trainingData.classAttribute().value(predictedClass);
            double confidence = distribution[predictedClass];

            // Create classification probabilities map
            Map<String, Double> classProbabilities = new HashMap<>();
            for (int i = 0; i < distribution.length; i++) {
                String className = trainingData.classAttribute().value(i);
                classProbabilities.put(className, distribution[i]);
            }

            // Create classification result
            Classification classification = new Classification(
                "user", userId, classLabel, confidence, "naive_bayes"
            );
            classification.setClassProbabilities(classProbabilities);
            classification.setFeatures(objectMapper.writeValueAsString(userFeatures));

            return classification;

        } catch (Exception e) {
            log.error("Error classifying user: " + userId, e);
            return null;
        }
    }

    /**
     * Get user features for classification.
     */
    private Map<String, Double> getUserFeatures(Long userId) {
        Map<String, Double> features = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                SELECT
                    YEAR(CURDATE()) - YEAR(u.birth_date) as age,
                    COUNT(t.id) as total_tickets,
                    AVG(COALESCE(r.rating, 3)) as avg_rating_given,
                    COUNT(DISTINCT c.name) as genre_diversity,
                    AVG(t.price_paid) as avg_ticket_price,
                    SUM(CASE WHEN DAYOFWEEK(ms.start_time) IN (1,7) THEN 1 ELSE 0 END) / GREATEST(COUNT(*), 1) as weekend_preference,
                    SUM(CASE WHEN HOUR(ms.start_time) >= 18 THEN 1 ELSE 0 END) / GREATEST(COUNT(*), 1) as evening_preference
                FROM users u
                LEFT JOIN tickets t ON u.id = t.client_id
                LEFT JOIN movie_sessions ms ON t.movie_session_id = ms.id
                LEFT JOIN films f ON ms.film_id = f.id
                LEFT JOIN film_categories fc ON f.id = fc.film_id
                LEFT JOIN categories c ON fc.category_id = c.id
                LEFT JOIN reviews r ON u.id = r.user_id AND f.id = r.film_id
                WHERE u.id = ?
                GROUP BY u.id
                """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, userId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    features.put("age", rs.getDouble("age"));
                    features.put("total_tickets", rs.getDouble("total_tickets"));
                    features.put("avg_rating_given", rs.getDouble("avg_rating_given"));
                    features.put("genre_diversity", rs.getDouble("genre_diversity"));
                    features.put("avg_ticket_price", rs.getDouble("avg_ticket_price"));
                    features.put("weekend_preference", rs.getDouble("weekend_preference"));
                    features.put("evening_preference", rs.getDouble("evening_preference"));
                }
            }
        } catch (SQLException e) {
            log.error("Error getting user features", e);
        }

        return features;
    }

    /**
     * Retrain models with new data.
     */
    public CompletableFuture<Void> retrainModelsAsync() {
        return CompletableFuture.runAsync(() -> {
            log.info("Starting model retraining...");
            createTrainingData();
            trainModels();
            log.info("Model retraining completed");
        }, executorService);
    }

    // IService implementation methods
    @Override
    public void create(Classification classification) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                INSERT INTO classifications
                (entity_type, entity_id, class_label, confidence, algorithm, classified_at, features)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, classification.getEntityType());
                stmt.setLong(2, classification.getEntityId());
                stmt.setString(3, classification.getClassLabel());
                stmt.setDouble(4, classification.getConfidence());
                stmt.setString(5, classification.getAlgorithm());
                stmt.setObject(6, classification.getClassifiedAt());
                stmt.setString(7, classification.getFeatures());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error creating classification", e);
        }
    }

    @Override
    public Page<Classification> read(PageRequest pageRequest) {
        List<Classification> classifications = new ArrayList<>();
        int totalCount = 0;

        try (Connection conn = dataSource.getConnection()) {
            // Get total count
            String countSql = "SELECT COUNT(*) FROM classifications";
            try (PreparedStatement stmt = conn.prepareStatement(countSql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalCount = rs.getInt(1);
                }
            }

            // Get paginated results
            String sql = """
                SELECT * FROM classifications
                ORDER BY classified_at DESC
                LIMIT ? OFFSET ?
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, pageRequest.size());
                stmt.setInt(2, pageRequest.page() * pageRequest.size());
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Classification classification = mapResultSetToClassification(rs);
                    classifications.add(classification);
                }
            }
        } catch (SQLException e) {
            log.error("Error reading classifications", e);
        }

        return new Page<>(classifications, pageRequest.page(), pageRequest.size(), totalCount);
    }

    @Override
    public void update(Classification classification) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                UPDATE classifications
                SET class_label = ?, confidence = ?, algorithm = ?, features = ?
                WHERE id = ?
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, classification.getClassLabel());
                stmt.setDouble(2, classification.getConfidence());
                stmt.setString(3, classification.getAlgorithm());
                stmt.setString(4, classification.getFeatures());
                stmt.setLong(5, classification.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error updating classification", e);
        }
    }

    @Override
    public void delete(Classification classification) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "DELETE FROM classifications WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, classification.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            log.error("Error deleting classification", e);
        }
    }

    @Override
    public int count() {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT COUNT(*) FROM classifications";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            log.error("Error counting classifications", e);
        }
        return 0;
    }

    @Override
    public Classification getById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM classifications WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return mapResultSetToClassification(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error getting classification by id", e);
        }
        return null;
    }

    @Override
    public List<Classification> getAll() {
        List<Classification> classifications = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT * FROM classifications ORDER BY classified_at DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    classifications.add(mapResultSetToClassification(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error getting all classifications", e);
        }
        return classifications;
    }

    @Override
    public List<Classification> search(String query) {
        List<Classification> classifications = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            String sql = """
                SELECT * FROM classifications
                WHERE class_label LIKE ? OR algorithm LIKE ? OR entity_type LIKE ?
                ORDER BY classified_at DESC
                """;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String searchPattern = "%" + query + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    classifications.add(mapResultSetToClassification(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Error searching classifications", e);
        }
        return classifications;
    }

    @Override
    public boolean exists(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            String sql = "SELECT 1 FROM classifications WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setLong(1, id);
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Error checking if classification exists", e);
        }
        return false;
    }

    /**
     * Map ResultSet to Classification object.
     */
    private Classification mapResultSetToClassification(ResultSet rs) throws SQLException {
        Classification classification = new Classification();
        classification.setId(rs.getLong("id"));
        classification.setEntityType(rs.getString("entity_type"));
        classification.setEntityId(rs.getLong("entity_id"));
        classification.setClassLabel(rs.getString("class_label"));
        classification.setConfidence(rs.getDouble("confidence"));
        classification.setAlgorithm(rs.getString("algorithm"));
        classification.setClassifiedAt(rs.getTimestamp("classified_at").toLocalDateTime());
        classification.setFeatures(rs.getString("features"));

        return classification;
    }

    /**
     * Clean up resources.
     */
    public void shutdown() {
        executorService.shutdown();
    }
}
