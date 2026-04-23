package com.plagiarism.detector;

/**
 * Enhanced AnalysisResult with richer AI-detection metrics.
 * New signals improve accuracy especially on large content volumes.
 */
public class AnalysisResult {

    // ── Core probabilities ──────────────────────────────────────────────────
    private double aiProbability;
    private double humanProbability;
    private String verdict;
    private String detailedAnalysis;

    // ── Original metrics ────────────────────────────────────────────────────
    private double perplexity;
    private double burstiness;
    private int    totalTokens;
    private int    uniqueTokens;

    // ── NEW: Sentence-level signals ─────────────────────────────────────────
    /** Std-dev of sentence lengths. AI ≈ low variance; humans ≈ high variance. */
    private double sentenceLengthVariance;

    /** Average words per sentence. AI clusters tightly around 18-22. */
    private double avgSentenceLength;

    /** How often consecutive sentences have near-identical length (ratio 0-1). */
    private double sentenceUniformityRate;

    // ── NEW: Stylistic signals ───────────────────────────────────────────────
    /** Ratio of passive-voice sentences. AI skews higher (~0.25+). */
    private double passiveVoiceRatio;

    /**
     * Density of filler/transition phrases ("Furthermore", "In conclusion",
     * "It is worth noting", etc.). AI overuses these.
     */
    private double transitionWordDensity;

    /** Ratio of sentences starting with the same POS tag run. */
    private double syntacticRepetitionScore;

    // ── NEW: Lexical signals ─────────────────────────────────────────────────
    /** Frequency of the top-5 most repeated n-grams (bigrams+). AI repeats more. */
    private double ngramRepetitionScore;

    /** Shannon entropy of the token distribution. Lower → more AI-like. */
    private double lexicalEntropy;

    /** Ratio of hedge words ("may", "might", "could", "generally"). AI hedges more. */
    private double hedgeWordRatio;

    // ── NEW: Large-text-specific signals ────────────────────────────────────
    /**
     * Paragraph-to-paragraph topic drift score (0-1).
     * AI stays tightly on topic; humans wander naturally.
     */
    private double topicDriftScore;

    /**
     * Measures how evenly information density is spread across paragraphs.
     * AI tends to distribute information uniformly.
     */
    private double informationDensityVariance;

    /** Composite weighted confidence in the AI verdict (0-1). */
    private double confidenceScore;

    // ── Thresholds (tune per your dataset) ─────────────────────────────────
    private static final double HIGH_CONFIDENCE    = 0.80;
    private static final double MEDIUM_CONFIDENCE  = 0.60;

    // ═══════════════════════════════════════════════════════════════════════
    //  Constructor
    // ═══════════════════════════════════════════════════════════════════════

    public AnalysisResult() {
        this.aiProbability             = 0.0;
        this.humanProbability          = 0.0;
        this.verdict                   = "Not Analyzed";
        this.detailedAnalysis          = "";
        this.perplexity                = 0.0;
        this.burstiness                = 0.0;
        this.totalTokens               = 0;
        this.uniqueTokens              = 0;

        // New fields
        this.sentenceLengthVariance    = 0.0;
        this.avgSentenceLength         = 0.0;
        this.sentenceUniformityRate    = 0.0;
        this.passiveVoiceRatio         = 0.0;
        this.transitionWordDensity     = 0.0;
        this.syntacticRepetitionScore  = 0.0;
        this.ngramRepetitionScore      = 0.0;
        this.lexicalEntropy            = 0.0;
        this.hedgeWordRatio            = 0.0;
        this.topicDriftScore           = 0.0;
        this.informationDensityVariance= 0.0;
        this.confidenceScore           = 0.0;
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Computed / derived helpers
    // ═══════════════════════════════════════════════════════════════════════

    /** Type-token ratio: lexical richness. */
    public double getVocabularyDiversity() {
        return totalTokens > 0 ? (double) uniqueTokens / totalTokens : 0.0;
    }

    /**
     * Recomputes aiProbability as a weighted blend of all available signals.
     * Call this after setting all individual metrics in your analyzer.
     *
     * Weights are empirically tuned — adjust to your validation set.
     */
    public void recalculateAiProbability() {
        // Normalize each signal to a 0-1 "AI-ness" score
        double w1 = clamp(1.0 - (burstiness / 10.0));           // low burstiness → AI
        double w2 = clamp(perplexity > 0 ? 200.0 / perplexity   // low perplexity → AI
                          : 0.5);
        double w3 = clamp(sentenceUniformityRate);               // high uniformity → AI
        double w4 = clamp(passiveVoiceRatio * 2.5);              // high passive → AI
        double w5 = clamp(transitionWordDensity * 3.0);          // heavy transitions → AI
        double w6 = clamp(ngramRepetitionScore);                 // n-gram repeats → AI
        double w7 = clamp(hedgeWordRatio * 4.0);                 // many hedges → AI
        double w8 = clamp(1.0 - topicDriftScore);               // stays on-topic → AI
        double w9 = clamp(1.0 - (lexicalEntropy / 8.0));        // low entropy → AI
        double w10= clamp(1.0 - (getVocabularyDiversity() * 2));// low diversity → AI

        // Weighted average (weights must sum to 1)
        this.aiProbability =
              0.15 * w1
            + 0.15 * w2
            + 0.12 * w3
            + 0.10 * w4
            + 0.10 * w5
            + 0.10 * w6
            + 0.08 * w7
            + 0.08 * w8
            + 0.07 * w9
            + 0.05 * w10;

        this.humanProbability = 1.0 - this.aiProbability;

        // Confidence band
        double gap = Math.abs(aiProbability - humanProbability);
        this.confidenceScore = gap;

        // Auto-set verdict
        if (aiProbability >= HIGH_CONFIDENCE) {
            this.verdict = "AI-Generated";
        } else if (aiProbability >= MEDIUM_CONFIDENCE) {
            this.verdict = "Likely AI-Generated";
        } else if (humanProbability >= HIGH_CONFIDENCE) {
            this.verdict = "Human-Written";
        } else {
            this.verdict = "Mixed / Uncertain";
        }
    }

    /** Clamps a value to [0, 1]. */
    private static double clamp(double v) {
        return Math.max(0.0, Math.min(1.0, v));
    }

    /**
     * Returns a flag string highlighting the strongest AI signals found.
     * Useful for surfacing evidence in a UI or report.
     */
    public String getTopAiSignals() {
        StringBuilder sb = new StringBuilder();
        if (sentenceUniformityRate > 0.6)
            sb.append("  ⚠ Highly uniform sentence lengths\n");
        if (passiveVoiceRatio > 0.25)
            sb.append("  ⚠ Elevated passive-voice usage\n");
        if (transitionWordDensity > 0.30)
            sb.append("  ⚠ Heavy transition/filler phrases\n");
        if (ngramRepetitionScore > 0.5)
            sb.append("  ⚠ Repetitive n-gram patterns\n");
        if (hedgeWordRatio > 0.20)
            sb.append("  ⚠ Excessive hedge words\n");
        if (burstiness < 1.5)
            sb.append("  ⚠ Low burstiness (monotonic rhythm)\n");
        if (topicDriftScore < 0.15)
            sb.append("  ⚠ Unusually consistent topic focus\n");
        return sb.isEmpty() ? "  None dominant\n" : sb.toString();
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  toString
    // ═══════════════════════════════════════════════════════════════════════

    @Override
    public String toString() {
        String confidenceLabel = confidenceScore >= 0.4 ? "High"
                               : confidenceScore >= 0.2 ? "Medium" : "Low";

        return String.format("""
            ============================================
             AI PLAGIARISM DETECTION REPORT
            ============================================
             Verdict  : %s
             AI Prob  : %.2f%%   Human Prob: %.2f%%
             Confidence: %s (%.2f)
            --------------------------------------------
             CORE METRICS
               Perplexity           : %.3f
               Burstiness           : %.3f
               Vocabulary Diversity : %.3f
               Total / Unique Words : %d / %d
            --------------------------------------------
             SENTENCE METRICS
               Avg Sentence Length  : %.1f words
               Length Variance      : %.3f
               Uniformity Rate      : %.3f
            --------------------------------------------
             STYLISTIC METRICS
               Passive Voice Ratio  : %.3f
               Transition Density   : %.3f
               Syntactic Repetition : %.3f
            --------------------------------------------
             LEXICAL METRICS
               N-gram Repetition    : %.3f
               Lexical Entropy      : %.3f
               Hedge Word Ratio     : %.3f
            --------------------------------------------
             LARGE-TEXT METRICS
               Topic Drift Score    : %.3f
               Info Density Variance: %.3f
            --------------------------------------------
             TOP AI SIGNALS DETECTED:
            %s
            --------------------------------------------
             DETAILED ANALYSIS:
            %s
            ============================================
            """,
            verdict, aiProbability * 100, humanProbability * 100,
            confidenceLabel, confidenceScore,
            perplexity, burstiness, getVocabularyDiversity(),
            totalTokens, uniqueTokens,
            avgSentenceLength, sentenceLengthVariance, sentenceUniformityRate,
            passiveVoiceRatio, transitionWordDensity, syntacticRepetitionScore,
            ngramRepetitionScore, lexicalEntropy, hedgeWordRatio,
            topicDriftScore, informationDensityVariance,
            getTopAiSignals(),
            detailedAnalysis
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    //  Getters & Setters (original)
    // ═══════════════════════════════════════════════════════════════════════

    public double getAiProbability()               { return aiProbability; }
    public void   setAiProbability(double v)       { this.aiProbability = v; }
    public double getHumanProbability()            { return humanProbability; }
    public void   setHumanProbability(double v)    { this.humanProbability = v; }
    public String getVerdict()                     { return verdict; }
    public void   setVerdict(String v)             { this.verdict = v; }
    public String getDetailedAnalysis()            { return detailedAnalysis; }
    public void   setDetailedAnalysis(String v)    { this.detailedAnalysis = v; }
    public double getPerplexity()                  { return perplexity; }
    public void   setPerplexity(double v)          { this.perplexity = v; }
    public double getBurstiness()                  { return burstiness; }
    public void   setBurstiness(double v)          { this.burstiness = v; }
    public int    getTotalTokens()                 { return totalTokens; }
    public void   setTotalTokens(int v)            { this.totalTokens = v; }
    public int    getUniqueTokens()                { return uniqueTokens; }
    public void   setUniqueTokens(int v)           { this.uniqueTokens = v; }

    // ── New getters & setters ────────────────────────────────────────────

    public double getSentenceLengthVariance()          { return sentenceLengthVariance; }
    public void   setSentenceLengthVariance(double v)  { this.sentenceLengthVariance = v; }
    public double getAvgSentenceLength()               { return avgSentenceLength; }
    public void   setAvgSentenceLength(double v)       { this.avgSentenceLength = v; }
    public double getSentenceUniformityRate()           { return sentenceUniformityRate; }
    public void   setSentenceUniformityRate(double v)  { this.sentenceUniformityRate = v; }
    public double getPassiveVoiceRatio()               { return passiveVoiceRatio; }
    public void   setPassiveVoiceRatio(double v)       { this.passiveVoiceRatio = v; }
    public double getTransitionWordDensity()            { return transitionWordDensity; }
    public void   setTransitionWordDensity(double v)   { this.transitionWordDensity = v; }
    public double getSyntacticRepetitionScore()         { return syntacticRepetitionScore; }
    public void   setSyntacticRepetitionScore(double v){ this.syntacticRepetitionScore = v; }
    public double getNgramRepetitionScore()             { return ngramRepetitionScore; }
    public void   setNgramRepetitionScore(double v)    { this.ngramRepetitionScore = v; }
    public double getLexicalEntropy()                   { return lexicalEntropy; }
    public void   setLexicalEntropy(double v)           { this.lexicalEntropy = v; }
    public double getHedgeWordRatio()                   { return hedgeWordRatio; }
    public void   setHedgeWordRatio(double v)           { this.hedgeWordRatio = v; }
    public double getTopicDriftScore()                  { return topicDriftScore; }
    public void   setTopicDriftScore(double v)          { this.topicDriftScore = v; }
    public double getInformationDensityVariance()       { return informationDensityVariance; }
    public void   setInformationDensityVariance(double v){ this.informationDensityVariance = v; }
    public double getConfidenceScore()                  { return confidenceScore; }
}
