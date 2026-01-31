package com.esprit.models.ml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.Map;

@Log4j2
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Classification {

    private String entityType; // "user", "film", "session"
    private Long entityId;
    private String classLabel;
    private Double confidence;
    private String algorithm; // "naive_bayes", "decision_tree", "svm"
    private LocalDateTime classifiedAt;
    private Long id;
    private Map<String, Double> classProbabilities;
    private String features; // JSON string of features used

    /**
     * Constructor for creating a new classification.
     *
     * @param entityType the type of entity being classified
     * @param entityId   the ID of the entity
     * @param classLabel the predicted class
     * @param confidence the confidence score (0.0 to 1.0)
     * @param algorithm  the algorithm used for classification
     */
    public Classification(String entityType, Long entityId, String classLabel,
                          Double confidence, String algorithm) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.classLabel = classLabel;
        this.confidence = confidence;
        this.algorithm = algorithm;
        this.classifiedAt = LocalDateTime.now();
    }

    /**
     * Get the classification quality based on confidence score.
     *
     * @return quality rating as string
     */
    public String getQualityRating() {
        if (confidence >= 0.9) {
            return "Very High";
        } else if (confidence >= 0.7) {
            return "High";
        } else if (confidence >= 0.5) {
            return "Medium";
        } else {
            return "Low";
        }
    }

    /**
     * Check if this classification is reliable (confidence > 0.7).
     *
     * @return true if reliable, false otherwise
     */
    public boolean isReliable() {
        return confidence != null && confidence > 0.7;
    }
}
