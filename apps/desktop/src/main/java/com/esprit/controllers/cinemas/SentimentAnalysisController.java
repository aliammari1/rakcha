package com.esprit.controllers.cinemas;

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.analyzer.SentimentPolarities;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;

@Log4j2
public class SentimentAnalysisController {

    /**
     * Construct a SentimentAnalysisController.
     */
    public SentimentAnalysisController() {
        // No initialization needed - using static methods
    }

    /**
     * Classifies the sentiment of the given text as Positive, Negative, or Neutral.
     *
     * @param text the input text to analyze
     * @return `Positive`, `Negative`, or `Neutral` based on the text's sentiment
     * @throws IllegalArgumentException if text is null
     * @since 2.0
     */
    public String analyzeSentiment(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        try {
            // Analyze using static method
            SentimentPolarities polarities = SentimentAnalyzer.getScoresFor(text);
            final double compound = polarities.getCompoundPolarity();

            if (compound >= 0.05) {
                return "Positive";
            } else if (compound <= -0.05) {
                return "Negative";
            } else {
                return "Neutral";
            }

        } catch (Exception e) {
            throw new RuntimeException("Error analyzing sentiment", e);
        }
    }

    /**
     * Return a formatted sentiment label and polarity scores for the given text.
     *
     * @param text the text to analyze; must not be null
     * @return a string formatted as "<Sentiment> (Compound: <compound>, Pos: <positive>, Neg: <negative>, Neu: <neutral>)" with numeric scores rounded to three decimals
     * @throws IllegalArgumentException if {@code text} is null
     * @throws RuntimeException         if an error occurs during analysis
     */
    public String analyzeSentimentDetailed(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        try {
            // Analyze using static method
            SentimentPolarities polarities = SentimentAnalyzer.getScoresFor(text);
            final double compound = polarities.getCompoundPolarity();
            final double positive = polarities.getPositivePolarity();
            final double negative = polarities.getNegativePolarity();
            final double neutral = polarities.getNeutralPolarity();

            String sentiment;
            if (compound >= 0.05) {
                sentiment = "Positive";
            } else if (compound <= -0.05) {
                sentiment = "Negative";
            } else {
                sentiment = "Neutral";
            }

            return String.format("%s (Compound: %.3f, Pos: %.3f, Neg: %.3f, Neu: %.3f)",
                sentiment, compound, positive, negative, neutral);
        } catch (Exception e) {
            throw new RuntimeException("Error analyzing detailed sentiment", e);
        }
    }

    /**
     * Retrieve the compound sentiment score for the input text.
     *
     * @param text the input text to analyze; must not be null
     * @return the compound score between -1.0 (negative) and 1.0 (positive)
     * @throws IllegalArgumentException if {@code text} is null
     * @throws RuntimeException         if an error occurs during analysis
     */
    public double getSentimentScore(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        try {
            // Analyze using static method
            SentimentPolarities polarities = SentimentAnalyzer.getScoresFor(text);
            return polarities.getCompoundPolarity();
        } catch (Exception e) {
            throw new RuntimeException("Error getting sentiment score", e);
        }
    }

    /**
     * Obtain sentiment polarity scores for the provided text.
     *
     * @param text the input text to analyze; must not be null
     * @return a map with keys "compound", "positive", "negative", and "neutral" mapped to their respective float scores
     * @throws IllegalArgumentException if text is null
     * @throws RuntimeException         if an error occurs while performing sentiment analysis
     */
    public HashMap<String, Float> getAllSentimentScores(final String text) {
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        try {
            // Analyze using static method
            SentimentPolarities polarities = SentimentAnalyzer.getScoresFor(text);

            HashMap<String, Float> scores = new HashMap<>();
            scores.put("compound", polarities.getCompoundPolarity());
            scores.put("positive", polarities.getPositivePolarity());
            scores.put("negative", polarities.getNegativePolarity());
            scores.put("neutral", polarities.getNeutralPolarity());

            return scores;
        } catch (Exception e) {
            throw new RuntimeException("Error getting all sentiment scores", e);
        }
    }
}
