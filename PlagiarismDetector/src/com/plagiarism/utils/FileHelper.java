package com.plagiarism.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for file operations
 */
public class FileHelper {
    
    /**
     * Read file content with automatic encoding detection
     */
    public static String readFileContent(String filePath) {
        try {
            Path path = Paths.get(filePath);
            
            if (!Files.exists(path)) {
                System.err.println("File does not exist: " + filePath);
                return null;
            }
            
            // Try UTF-8 first
            try {
                return Files.readString(path, StandardCharsets.UTF_8);
            } catch (IOException e) {
                // Fall back to system default encoding
                return new String(Files.readAllBytes(path));
            }
            
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Write content to file
     */
    public static boolean writeFileContent(String filePath, String content) {
        try {
            Files.writeString(Paths.get(filePath), content, StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            System.err.println("Error writing file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Save analysis results to file
     */
    public static boolean saveAnalysisReport(String filePath, String report) {
        return writeFileContent(filePath, report);
    }
    
    /**
     * Get file extension
     */
    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
    
    /**
     * Check if file is a supported text format
     */
    public static boolean isSupportedTextFile(String fileName) {
        String ext = getFileExtension(fileName);
        return ext.matches("txt|text|md|rtf|java|py|cpp|c|html|css|js|json|xml");
    }
    
    /**
     * Get file size in readable format
     */
    public static String getReadableFileSize(long bytes) {
        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = bytes;
        
        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }
        
        return String.format("%.2f %s", size, units[unitIndex]);
    }
}