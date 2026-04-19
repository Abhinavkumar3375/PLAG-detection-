package com.plagiarism;

import com.plagiarism.ui.MainFrame;
import com.plagiarism.detector.PlagiarismCheck;
import com.plagiarism.detector.AnalysisResult;
import com.plagiarism.utils.FileHelper;

import javax.swing.*;
import java.util.Scanner;

/**
 * Main entry point for the application
 */
public class Main {
    
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
        
        // Check command line arguments
        if (args.length > 0) {
            // Run in command line mode
            runCommandLineMode(args);
        } else {
            // Run in GUI mode
            runGUIMode();
        }
    }
    
    /**
     * Run application in GUI mode
     */
    private static void runGUIMode() {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
            
            // Show welcome message
            JOptionPane.showMessageDialog(frame,
                """
                Welcome to AI Plagiarism Detection System!
                
                This tool helps identify AI-generated content using:
                • Perplexity Analysis
                • Burstiness Detection
                • Pattern Recognition
                • Vocabulary Diversity Assessment
                
                Simply paste your text, load a file, or drag and drop
                to begin the analysis.
                """,
                "Welcome",
                JOptionPane.INFORMATION_MESSAGE
            );
        });
    }
    
    /**
     * Run application in command line mode
     */
    private static void runCommandLineMode(String[] args) {
        System.out.println("========================================");
        System.out.println("   AI PLAGIARISM DETECTION SYSTEM");
        System.out.println("========================================\n");
        
        String content = null;
        
        if (args[0].equals("-f") || args[0].equals("--file")) {
            // Read from file
            if (args.length < 2) {
                System.err.println("Error: Please specify a file path");
                return;
            }
            
            content = FileHelper.readFileContent(args[1]);
            if (content == null) {
                System.err.println("Error: Could not read file: " + args[1]);
                return;
            }
            
            System.out.println("Analyzing file: " + args[1]);
            
        } else if (args[0].equals("-t") || args[0].equals("--text")) {
            // Read from arguments
            StringBuilder textBuilder = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                textBuilder.append(args[i]).append(" ");
            }
            content = textBuilder.toString().trim();
            
            if (content.isEmpty()) {
                System.err.println("Error: No text provided");
                return;
            }
            
        } else {
            // Interactive mode
            System.out.println("Enter the text to analyze (press Enter twice to finish):\n");
            Scanner scanner = new Scanner(System.in);
            StringBuilder inputBuilder = new StringBuilder();
            String line;
            int emptyLineCount = 0;
            
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                if (line.trim().isEmpty()) {
                    emptyLineCount++;
                    if (emptyLineCount >= 2) break;
                } else {
                    emptyLineCount = 0;
                }
                inputBuilder.append(line).append("\n");
            }
            content = inputBuilder.toString().trim();
            scanner.close();
        }
        
        // Perform analysis
        if (content != null && !content.isEmpty()) {
            System.out.println("\nAnalyzing content...\n");
            
            PlagiarismCheck detector = new PlagiarismCheck();
            AnalysisResult result = detector.analyzeContent(content);
            
            System.out.println(result);
            
            // Save option
            if (args.length > 0 && (args[0].equals("-f") || args[0].equals("--file"))) {
                String outputPath = args[1] + "_analysis_report.txt";
                if (FileHelper.saveAnalysisReport(outputPath, result.toString())) {
                    System.out.println("\nReport saved to: " + outputPath);
                }
            }
            
        } else {
            System.err.println("Error: No content to analyze");
        }
    }
}