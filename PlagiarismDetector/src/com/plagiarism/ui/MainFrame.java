package com.plagiarism.ui;

import com.plagiarism.detector.PlagiarismCheck;
import com.plagiarism.detector.AnalysisResult;
import com.plagiarism.utils.FileHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Clean, Simple UI for AI Content Detection
 */
public class MainFrame extends JFrame {
    
    private JTextArea inputTextArea;
    private JTextArea resultTextArea;
    private JProgressBar progressBar;
    private JLabel statusLabel;
    private PlagiarismCheck detector;
    private JButton analyzeButton;
    private JButton clearButton;
    private JButton pasteButton;
    private JButton loadFileButton;
    private JButton saveReportButton;
    private JLabel wordCountLabel;
    private JLabel charCountLabel;
    private AnalysisResult currentResult;
    
    // Result display components
    private JPanel resultCard;
    private JLabel verdictLabel;
    private JLabel aiScoreLabel;
    private JLabel humanScoreLabel;
    private JProgressBar aiProgressBar;
    private JProgressBar humanProgressBar;
    private JLabel perplexityLabel;
    private JLabel burstinessLabel;
    private JLabel diversityLabel;
    
    // Modern Color Scheme
    private static final Color BG_DARK = new Color(22, 26, 30);
    private static final Color BG_CARD = new Color(32, 37, 42);
    private static final Color BG_INPUT = new Color(28, 33, 38);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);
    private static final Color ACCENT_RED = new Color(239, 68, 68);
    private static final Color ACCENT_YELLOW = new Color(234, 179, 8);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color BORDER_COLOR = new Color(51, 65, 85);
    private static final Color BTN_BG = new Color(51, 65, 85);
    private static final Color BTN_HOVER = new Color(71, 85, 105);
    
    public MainFrame() {
        detector = new PlagiarismCheck();
        initializeUI();
        setupDragAndDrop();
        setAppIcon();
    }
    
    private void initializeUI() {
        setTitle("AI PLAG Detector");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(850, 600));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BG_DARK);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
    }
    
    private void setAppIcon() {
        try {
            BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = icon.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(ACCENT_BLUE);
            g2d.fillRoundRect(0, 0, 64, 64, 15, 15);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawOval(18, 18, 28, 28);
            g2d.drawLine(38, 38, 52, 52);
            g2d.dispose();
            setIconImage(icon);
        } catch (Exception e) {}
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 25, 20, 25)
        ));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("AI PLAG Detector");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel subtitleLabel = new JLabel("Instantly detect AI-generated text with confidence");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(4));
        leftPanel.add(subtitleLabel);
        
        panel.add(leftPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createCenterPanel() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(320);
        splitPane.setResizeWeight(0.45);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_DARK);
        
        splitPane.setTopComponent(createInputPanel());
        splitPane.setBottomComponent(createOutputPanel());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Header with title and stats
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel inputLabel = new JLabel("Input Content");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        inputLabel.setForeground(TEXT_PRIMARY);
        
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);
        
        charCountLabel = new JLabel("Characters: 0");
        charCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        charCountLabel.setForeground(TEXT_SECONDARY);
        
        wordCountLabel = new JLabel("Words: 0");
        wordCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        wordCountLabel.setForeground(TEXT_SECONDARY);
        
        statsPanel.add(charCountLabel);
        statsPanel.add(wordCountLabel);
        
        headerPanel.add(inputLabel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);
        
        // Button toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 10));
        toolbar.setOpaque(false);
        
        pasteButton = createButton("📋 Paste", "Paste from clipboard");
        pasteButton.addActionListener(e -> pasteFromClipboard());
        
        loadFileButton = createButton("📂 Open", "Open text file");
        loadFileButton.addActionListener(e -> loadFromFile());
        
        clearButton = createButton("🗑️ Clear", "Clear text");
        clearButton.addActionListener(e -> {
            inputTextArea.setText("");
            updateStats();
        });
        
        toolbar.add(pasteButton);
        toolbar.add(loadFileButton);
        toolbar.add(clearButton);
        
        // Text area
        inputTextArea = new JTextArea();
        inputTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        inputTextArea.setBackground(BG_INPUT);
        inputTextArea.setForeground(TEXT_PRIMARY);
        inputTextArea.setCaretColor(TEXT_PRIMARY);
        inputTextArea.setLineWrap(true);
        inputTextArea.setWrapStyleWord(true);
        inputTextArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        inputTextArea.setText("Paste or type your text here...");
        
        inputTextArea.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (inputTextArea.getText().equals("Paste or type your text here...")) {
                    inputTextArea.setText("");
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(inputTextArea);
        scrollPane.setBackground(BG_INPUT);
        scrollPane.getViewport().setBackground(BG_INPUT);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        
        // Analyze button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 5, 0));
        
        analyzeButton = new JButton("🔍 ANALYZE TEXT");
        analyzeButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        analyzeButton.setBackground(ACCENT_BLUE);
        analyzeButton.setForeground(Color.RED);
        analyzeButton.setFocusPainted(false);
        analyzeButton.setBorder(new EmptyBorder(12, 30, 12, 30));
        analyzeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        analyzeButton.addActionListener(e -> analyzeContent());
        
        analyzeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                analyzeButton.setBackground(ACCENT_BLUE.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                analyzeButton.setBackground(ACCENT_BLUE);
            }
        });
        
        buttonPanel.add(analyzeButton);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(toolbar, BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        inputTextArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateStats(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateStats(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { updateStats(); }
        });
        
        return panel;
    }
    
    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel outputLabel = new JLabel("Analysis Results");
        outputLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        outputLabel.setForeground(TEXT_PRIMARY);
        
        saveReportButton = createButton("💾 Save", "Save report");
        saveReportButton.addActionListener(e -> saveReport());
        saveReportButton.setEnabled(false);
        
        headerPanel.add(outputLabel, BorderLayout.WEST);
        headerPanel.add(saveReportButton, BorderLayout.EAST);
        
        // Main result card
        resultCard = new JPanel();
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        resultCard.setBackground(BG_INPUT);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Verdict - Large, clear text
        verdictLabel = new JLabel("Ready to Analyze");
        verdictLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        verdictLabel.setForeground(TEXT_SECONDARY);
        verdictLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        resultCard.add(verdictLabel);
        resultCard.add(Box.createVerticalStrut(20));
        
        // AI vs Human scores
        JPanel scorePanel = new JPanel(new GridLayout(1, 2, 30, 0));
        scorePanel.setOpaque(false);
        
        // AI Score
        JPanel aiPanel = new JPanel();
        aiPanel.setLayout(new BoxLayout(aiPanel, BoxLayout.Y_AXIS));
        aiPanel.setOpaque(false);
        
        JLabel aiTitle = new JLabel("AI Generated");
        aiTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        aiTitle.setForeground(ACCENT_RED);
        aiTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        aiScoreLabel = new JLabel("0%");
        aiScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        aiScoreLabel.setForeground(ACCENT_RED);
        aiScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        aiProgressBar = new JProgressBar(0, 100);
        aiProgressBar.setValue(0);
        aiProgressBar.setForeground(ACCENT_RED);
        aiProgressBar.setBackground(new Color(239, 68, 68, 30));
        aiProgressBar.setBorder(null);
        aiProgressBar.setPreferredSize(new Dimension(150, 6));
        aiProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        
        aiPanel.add(aiTitle);
        aiPanel.add(Box.createVerticalStrut(10));
        aiPanel.add(aiScoreLabel);
        aiPanel.add(Box.createVerticalStrut(8));
        aiPanel.add(aiProgressBar);
        
        // Human Score
        JPanel humanPanel = new JPanel();
        humanPanel.setLayout(new BoxLayout(humanPanel, BoxLayout.Y_AXIS));
        humanPanel.setOpaque(false);
        
        JLabel humanTitle = new JLabel("Human Written");
        humanTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        humanTitle.setForeground(ACCENT_GREEN);
        humanTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        humanScoreLabel = new JLabel("0%");
        humanScoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        humanScoreLabel.setForeground(ACCENT_GREEN);
        humanScoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        humanProgressBar = new JProgressBar(0, 100);
        humanProgressBar.setValue(0);
        humanProgressBar.setForeground(ACCENT_GREEN);
        humanProgressBar.setBackground(new Color(34, 197, 94, 30));
        humanProgressBar.setBorder(null);
        humanProgressBar.setPreferredSize(new Dimension(150, 6));
        humanProgressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 6));
        
        humanPanel.add(humanTitle);
        humanPanel.add(Box.createVerticalStrut(10));
        humanPanel.add(humanScoreLabel);
        humanPanel.add(Box.createVerticalStrut(8));
        humanPanel.add(humanProgressBar);
        
        scorePanel.add(aiPanel);
        scorePanel.add(humanPanel);
        
        resultCard.add(scorePanel);
        resultCard.add(Box.createVerticalStrut(20));
        
        // Metrics row
        JPanel metricsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        metricsPanel.setOpaque(false);
        
        perplexityLabel = createMetricLabel("Perplexity", "0.000");
        burstinessLabel = createMetricLabel("Burstiness", "0.000");
        diversityLabel = createMetricLabel("Diversity", "1.000");
        
        metricsPanel.add(perplexityLabel.getParent());
        metricsPanel.add(burstinessLabel.getParent());
        metricsPanel.add(diversityLabel.getParent());
        
        resultCard.add(metricsPanel);
        
        // Detailed result area (collapsible/minimal)
        resultTextArea = new JTextArea();
        resultTextArea.setFont(new Font("Consolas", Font.PLAIN, 11));
        resultTextArea.setBackground(BG_INPUT);
        resultTextArea.setForeground(TEXT_SECONDARY);
        resultTextArea.setEditable(false);
        resultTextArea.setLineWrap(true);
        resultTextArea.setWrapStyleWord(true);
        resultTextArea.setBorder(new EmptyBorder(10, 0, 0, 0));
        resultTextArea.setVisible(false); // Hidden by default
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(resultCard, BorderLayout.CENTER);
        panel.add(resultTextArea, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel createMetricLabel(String name, String value) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Consolas", Font.BOLD, 18));
        valueLabel.setForeground(ACCENT_BLUE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        nameLabel.setForeground(TEXT_SECONDARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        panel.add(valueLabel);
        panel.add(Box.createVerticalStrut(4));
        panel.add(nameLabel);
        
        return valueLabel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BG_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 20, 10, 20)
        ));
        
        statusLabel = new JLabel("● Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(ACCENT_GREEN);
        
        progressBar = new JProgressBar();
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(100, 3));
        progressBar.setForeground(ACCENT_BLUE);
        
        JLabel versionLabel = new JLabel("v1.0.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(TEXT_SECONDARY);
        
        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(versionLabel, BorderLayout.EAST);
        panel.add(progressBar, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setBackground(BTN_BG);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new EmptyBorder(7, 14, 7, 14)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(tooltip);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BTN_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BTN_BG);
            }
        });
        
        return button;
    }
    
    private void updateStats() {
        String text = inputTextArea.getText();
        if (text.equals("Paste or type your text here...")) {
            charCountLabel.setText("Characters: 0");
            wordCountLabel.setText("Words: 0");
            return;
        }
        
        int charCount = text.length();
        int wordCount = text.trim().isEmpty() ? 0 : text.trim().split("\\s+").length;
        
        charCountLabel.setText("Characters: " + charCount);
        wordCountLabel.setText("Words: " + wordCount);
    }
    
    private void setupDragAndDrop() {
        new DropTarget(inputTextArea, new DropTargetAdapter() {
            @Override
            @SuppressWarnings("unchecked")
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> files = (List<File>) dtde.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    
                    if (!files.isEmpty()) {
                        String content = FileHelper.readFileContent(files.get(0).getAbsolutePath());
                        if (content != null) {
                            inputTextArea.setText(content);
                            statusLabel.setText("● File loaded: " + files.get(0).getName());
                            statusLabel.setForeground(ACCENT_GREEN);
                            updateStats();
                        }
                    }
                    dtde.dropComplete(true);
                } catch (Exception e) {
                    showError("Failed to load file");
                }
            }
        });
    }
    
    private void analyzeContent() {
        String content = inputTextArea.getText().trim();
        
        if (content.isEmpty() || content.equals("Paste or type your text here...")) {
            showError("Please enter text to analyze");
            return;
        }
        
        analyzeButton.setEnabled(false);
        analyzeButton.setText("⏳ ANALYZING...");
        progressBar.setVisible(true);
        statusLabel.setText("● Analyzing...");
        statusLabel.setForeground(ACCENT_YELLOW);
        
        SwingWorker<AnalysisResult, Void> worker = new SwingWorker<>() {
            @Override
            protected AnalysisResult doInBackground() {
                return detector.analyzeContent(content);
            }
            
            @Override
            protected void done() {
                try {
                    currentResult = get();
                    displayResults(currentResult);
                    statusLabel.setText("● Analysis complete");
                    statusLabel.setForeground(ACCENT_GREEN);
                    saveReportButton.setEnabled(true);
                } catch (Exception e) {
                    showError("Analysis failed");
                } finally {
                    analyzeButton.setEnabled(true);
                    analyzeButton.setText("🔍 ANALYZE TEXT");
                    progressBar.setVisible(false);
                }
            }
        };
        
        worker.execute();
    }
    
    private void displayResults(AnalysisResult result) {
        int aiProb = (int) (result.getAiProbability() * 100);
        int humanProb = (int) (result.getHumanProbability() * 50);
        
        // Set verdict with clear, simple text
        String verdict = result.getVerdict();
        verdictLabel.setText(verdict);
        
        if (verdict.contains("AI")) {
            verdictLabel.setForeground(ACCENT_RED);
        } else if (verdict.contains("HUMAN")) {
            verdictLabel.setForeground(ACCENT_GREEN);
        } else {
            verdictLabel.setForeground(ACCENT_YELLOW);
        }
        
        // Update scores
        aiScoreLabel.setText(aiProb + "%");
        humanScoreLabel.setText(humanProb + "%");
        aiProgressBar.setValue(aiProb);
        humanProgressBar.setValue(humanProb);
        
        // Update metrics
        perplexityLabel.setText(String.format("%.3f", result.getPerplexity()));
        burstinessLabel.setText(String.format("%.3f", result.getBurstiness()));
        diversityLabel.setText(String.format("%.3f", result.getVocabularyDiversity()));
    }
    
    private void pasteFromClipboard() {
        try {
            String text = (String) Toolkit.getDefaultToolkit()
                .getSystemClipboard().getData(DataFlavor.stringFlavor);
            if (text != null && !text.isEmpty()) {
                inputTextArea.setText(text);
                statusLabel.setText("● Pasted from clipboard");
                statusLabel.setForeground(ACCENT_GREEN);
                updateStats();
            }
        } catch (Exception e) {
            showError("Failed to paste");
        }
    }
    
    private void loadFromFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Text Files", "txt", "text", "md"));
        
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String content = FileHelper.readFileContent(chooser.getSelectedFile().getAbsolutePath());
            if (content != null) {
                inputTextArea.setText(content);
                statusLabel.setText("● Loaded: " + chooser.getSelectedFile().getName());
                statusLabel.setForeground(ACCENT_GREEN);
                updateStats();
            } else {
                showError("Failed to read file");
            }
        }
    }
    
    private void saveReport() {
        if (currentResult == null) {
            showError("No results to save");
            return;
        }
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
            "Text Files", "txt"));
        chooser.setSelectedFile(new File("ai_detection_report.txt"));
        
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            if (!path.endsWith(".txt")) path += ".txt";
            
            if (FileHelper.writeFileContent(path, currentResult.toString())) {
                statusLabel.setText("● Report saved");
                statusLabel.setForeground(ACCENT_GREEN);
            } else {
                showError("Failed to save");
            }
        }
    }
    
    private void showError(String message) {
        statusLabel.setText("● " + message);
        statusLabel.setForeground(ACCENT_RED);
    }
}
