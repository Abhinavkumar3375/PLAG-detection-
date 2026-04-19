package com.plagiarism.detector;

/**
 * Class to store and manage plagiarism analysis results
 */
public class AnalysisResult {
    private double aiProbability;
    private double humanProbability;
    private String verdict;
    private String detailedAnalysis;
    private double perplexity;
    private double burstiness;
    private int totalTokens;
    private int uniqueTokens;
    
    public AnalysisResult() {
        this.aiProbability = 0.0;
        this.humanProbability = 0.0;
        this.verdict = "Not Analyzed";
        this.detailedAnalysis = "";
        this.perplexity = 0.0;
        this.burstiness = 0.0;
        this.totalTokens = 0;
        this.uniqueTokens = 0;
    }
    
    // Getters and Setters
    public double getAiProbability() {
        return aiProbability;
    }
    
    public void setAiProbability(double aiProbability) {
        this.aiProbability = aiProbability;
    }
    
    public double getHumanProbability() {
        return humanProbability;
    }
    
    public void setHumanProbability(double humanProbability) {
        this.humanProbability = humanProbability;
    }
    
    public String getVerdict() {
        return verdict;
    }
    
    public void setVerdict(String verdict) {
        this.verdict = verdict;
    }
    
    public String getDetailedAnalysis() {
        return detailedAnalysis;
    }
    
    public void setDetailedAnalysis(String detailedAnalysis) {
        this.detailedAnalysis = detailedAnalysis;
    }
    
    public double getPerplexity() {
        return perplexity;
    }
    
    public void setPerplexity(double perplexity) {
        this.perplexity = perplexity;
    }
    
    public double getBurstiness() {
        return burstiness;
    }
    
    public void setBurstiness(double burstiness) {
        this.burstiness = burstiness;
    }
    
    public int getTotalTokens() {
        return totalTokens;
    }
    
    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }
    
    public int getUniqueTokens() {
        return uniqueTokens;
    }
    
    public void setUniqueTokens(int uniqueTokens) {
        this.uniqueTokens = uniqueTokens;
    }
    
    public double getVocabularyDiversity() {
        if (totalTokens > 0) {
            return (double) uniqueTokens / totalTokens;
        }
        return 0.0;
    }
    
    @Override
    public String toString() {
        return String.format("""
            ========================================
            AI PLAGIARISM DETECTION REPORT
            ========================================
            Verdict: %s
            AI-Generated Probability: %.2f%%
            Human-Written Probability: %.2f%%
            
            Metrics:
            - Perplexity Score: %.3f
            - Burstiness Score: %.3f
            - Vocabulary Diversity: %.3f
            - Total Words: %d
            - Unique Words: %d
            
            Detailed Analysis:
            %s
            ========================================
            """,
            verdict, aiProbability * 100, humanProbability * 100,
            perplexity, burstiness, getVocabularyDiversity(),
            totalTokens, uniqueTokens, detailedAnalysis
        );
    }
}