package com.plagiarism.detector;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Core detection engine for AI-generated content
 */
public class PlagiarismCheck {
    
    // Common patterns found in AI-generated text
    private static final String[] AI_PATTERNS = {
    // Logical connectors
    "however", "therefore", "furthermore", "moreover", "consequently",
    "thus", "hence", "accordingly", "nevertheless", "nonetheless",

    // Structured phrases
    "in conclusion", "in summary", "to summarize", "overall",
    "in light of", "with regard to", "in terms of",
    "it is important to note", "it is worth mentioning",
    "one could argue", "this suggests that",
    "it can be observed that", "this indicates that",

    // Academic/formal tone
    "the aforementioned", "as previously mentioned",
    "as discussed earlier", "the above points",
    "from this perspective", "in this context",
    "a key factor is", "an important aspect is",

    // Balanced/neutral framing
    "on the other hand", "on one hand",
    "while it is true that", "although it may seem",
    "this highlights the importance of",

    // Generic explanation style
    "this can be understood as", "this refers to",
    "this is because", "this occurs when",
    "it is generally accepted that",

    // AI-style redundancy patterns
    "overall, it can be said that",
    "in many cases", "in most cases",
    "a key factor is", "an important aspect is"
};
    private static final String[] HUMAN_PATTERNS = {
        "um", "uh", "like", "you know", "i mean", "actually", "basically",
        "honestly", "literally", "seriously", "anyway", "so anyway"
    };
    
    private static final Pattern SENTENCE_PATTERN = Pattern.compile("[.!?]+\\s+");
    
    /**
     * Main analysis method
     */
    public AnalysisResult analyzeContent(String content) {
        AnalysisResult result = new AnalysisResult();
        
        if (content == null || content.trim().isEmpty()) {
            result.setVerdict("INVALID INPUT");
            result.setDetailedAnalysis("No content provided for analysis.");
            return result;
        }
        
        // Tokenize content
        List<String> tokens = tokenize(content);
        result.setTotalTokens(tokens.size());
        result.setUniqueTokens(new HashSet<>(tokens).size());
        
        // Calculate metrics
        double perplexity = calculatePerplexity(content, tokens);
        double burstiness = calculateBurstiness(content);
        double patternScore = analyzePatterns(content.toLowerCase());
        
        result.setPerplexity(perplexity);
        result.setBurstiness(burstiness);
        
        // Determine AI vs Human probability
        double aiScore = calculateAIScore(perplexity, burstiness, patternScore);
        double humanScore = 1.0 - aiScore;
        
        result.setAiProbability(aiScore);
        result.setHumanProbability(humanScore);
        
        // Set verdict
        if (aiScore >= 0.7) {
            result.setVerdict("LIKELY AI-GENERATED");
        } else if (aiScore >= 0.5) {
            result.setVerdict("POSSIBLY AI-GENERATED");
        } else if (aiScore >= 0.3) {
            result.setVerdict("LIKELY AI-GENERATED");
        } else {
            result.setVerdict("HIGHLY LIKELY HUMAN-WRITTEN");
        }
        
        // Generate detailed analysis
        result.setDetailedAnalysis(generateDetailedAnalysis(result, tokens));
        
        return result;
    }
    
    /**
     * Tokenize text into words
     */
    private List<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase()
            .replaceAll("[^a-zA-Z\\s]", " ")
            .trim()
            .split("\\s+"))
            .filter(word -> word.length() > 1)
            .toList();
    }
    
    /**
     * Calculate text perplexity (measure of predictability)
     */
    private double calculatePerplexity(String text, List<String> tokens) {
        if (tokens.size() < 10) return 0.5;
        
        // Calculate word frequency distribution
        Map<String, Integer> wordFreq = new HashMap<>();
        for (String token : tokens) {
            wordFreq.put(token, wordFreq.getOrDefault(token, 0) + 1);
        }
        
        // Calculate entropy
        double entropy = 0.0;
        int totalWords = tokens.size();
        
        for (int freq : wordFreq.values()) {
            double probability = (double) freq / totalWords;
            entropy -= probability * Math.log(probability) / Math.log(2);
        }
        
        // Normalize entropy to 0-1 range (lower entropy = more predictable = more likely AI)
        double maxEntropy = Math.log(Math.min(totalWords, wordFreq.size())) / Math.log(2);
        double normalizedEntropy = maxEntropy > 0 ? entropy / maxEntropy : 0.5;
        
        // Convert to perplexity-like score (lower = more AI-like)
        return 1.0 - normalizedEntropy;
    }
    
    /**
     * Calculate burstiness (variation in sentence length)
     */
    private double calculateBurstiness(String text) {
        String[] sentences = SENTENCE_PATTERN.split(text);
        if (sentences.length < 3) return 0.5;
        
        List<Integer> sentenceLengths = new ArrayList<>();
        for (String sentence : sentences) {
            if (!sentence.trim().isEmpty()) {
                sentenceLengths.add(sentence.trim().split("\\s+").length);
            }
        }
        
        if (sentenceLengths.size() < 2) return 0.5;
        
        // Calculate variance
        double mean = sentenceLengths.stream().mapToInt(Integer::intValue).average().orElse(0);
        double variance = sentenceLengths.stream()
            .mapToDouble(len -> Math.pow(len - mean, 2))
            .average()
            .orElse(0);
        
        // Normalize burstiness (higher variance = more burstiness = more human-like)
        double burstiness = Math.sqrt(variance) / (mean + 1);
        return Math.min(burstiness, 1.0);
    }
    
    /**
     * Analyze patterns indicative of AI or human writing
     */
    private double analyzePatterns(String text) {
        int aiPatternCount = 0;
        int humanPatternCount = 0;
        
        // Check AI patterns
        for (String pattern : AI_PATTERNS) {
            if (text.contains(pattern)) {
                aiPatternCount += countOccurrences(text, pattern);
            }
        }
        
        // Check human patterns
        for (String pattern : HUMAN_PATTERNS) {
            if (text.contains(pattern)) {
                humanPatternCount += countOccurrences(text, pattern);
            }
        }
        
        // Calculate pattern ratio
        double totalPatterns = aiPatternCount + humanPatternCount;
        if (totalPatterns > 0) {
            return aiPatternCount / totalPatterns;
        }
        
        return 0.5; // Neutral if no patterns found
    }
    
    /**
     * Count occurrences of a substring
     */
    private int countOccurrences(String text, String pattern) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(pattern, index)) != -1) {
            count++;
            index += pattern.length();
        }
        return count;
    }
    
    /**
     * Calculate final AI score
     */
    private double calculateAIScore(double perplexity, double burstiness, double patternScore) {
        // Weighted combination of metrics
        double perplexityWeight = 0.5;
        double burstinessWeight = 0.3;
        double patternWeight = 0.2;
        
        // Invert burstiness (AI text tends to have lower burstiness)
        double invertedBurstiness = 1.0 - burstiness;
        
        return (perplexity * perplexityWeight) +
               (invertedBurstiness * burstinessWeight) +
               (patternScore * patternWeight);
    }
    
    /**
     * Generate detailed analysis description
     */
    private String generateDetailedAnalysis(AnalysisResult result, List<String> tokens) {
        StringBuilder analysis = new StringBuilder();
        
        // Analyze perplexity
        if (result.getPerplexity() < 0.4) {
            analysis.append("• Text shows low perplexity (high predictability), characteristic of AI-generated content.\n");
        } else if (result.getPerplexity() > 0.6) {
            analysis.append("• Text shows high perplexity (low predictability), characteristic of human writing.\n");
        } else {
            analysis.append("• Text shows moderate perplexity levels.\n");
        }
        
        // Analyze burstiness
        if (result.getBurstiness() < 0.4) {
            analysis.append("• Low burstiness detected - uniform sentence structure typical of AI text.\n");
        } else if (result.getBurstiness() > 0.6) {
            analysis.append("• High burstiness detected - varied sentence lengths typical of human writing.\n");
        } else {
            analysis.append("• Moderate sentence variation detected.\n");
        }
        
        // Vocabulary diversity
        double diversity = result.getVocabularyDiversity();
        if (diversity < 0.3) {
            analysis.append("• Limited vocabulary range detected, often seen in AI-generated text.\n");
        } else if (diversity > 0.5) {
            analysis.append("• Rich vocabulary diversity, characteristic of human writing.\n");
        }
        
        // Confidence note
        analysis.append("\nNote: This analysis is probabilistic and should be used as guidance only.");
        
        return analysis.toString();
    }
}
