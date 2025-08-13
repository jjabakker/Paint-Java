package generateSquares;

import com.google.gson.annotations.SerializedName;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Paths;

import paint.utilities.JsonConfig;
import paint.utilities.DirectoryClassifier;

public class GenerateSquareDialog {

    private JTextField nrSquaresField;
    private JTextField minTracksField;
    private JTextField minRSquaredField;
    private JTextField minDensityRatioField;
    private JTextField maxVariabilityField;

    private boolean hasChanges = false;  // Flag to track changes

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GenerateSquareDialog dialog = new GenerateSquareDialog();
            dialog.go();
        });
    }

    void go() {

        // Create the config reader and fetch the values
        JsonConfig config = new JsonConfig("src/main/resources/paint.json");

        int nrSquares = config.getInt("Generate Squares", "Nr of Squares in Row", 5);
        int minTracks = config.getInt("Generate Squares", "Min Tracks to Calculate Tau", 11);
        double minRSquared = config.getDouble("Generate Squares", "Min Required R Squared", 0.1);
        double minDensityRatio = config.getDouble("Generate Squares", "Min Required Density Ratio", 2.0);
        double maxVariability = config.getDouble("Generate Squares", "Max Allowable Variability", 10.0);

        // Load saved the directory path
        String lastUsedDirectory = config.getString("Generate Squares", "Last Used Directory", "");

        JFrame frame = new JFrame("Generate Squares");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // === Form Panel ===
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 8));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        nrSquaresField = createTightTextField(String.valueOf(nrSquares), new IntegerDocumentFilter());
        minTracksField = createTightTextField(String.valueOf(minTracks), new IntegerDocumentFilter());
        minRSquaredField = createTightTextField(String.valueOf(minRSquared), new FloatDocumentFilter());
        minDensityRatioField = createTightTextField(String.valueOf(minDensityRatio), new FloatDocumentFilter());
        maxVariabilityField = createTightTextField(String.valueOf(maxVariability), new FloatDocumentFilter());

        formPanel.add(new JLabel("Nr of Squares in Row"));
        formPanel.add(nrSquaresField);

        formPanel.add(new JLabel("Minimum Tracks to Calculate Tau"));
        formPanel.add(minTracksField);

        formPanel.add(new JLabel("Min Allowable R-squared"));
        formPanel.add(minRSquaredField);

        formPanel.add(new JLabel("Min Required Density Ratio"));
        formPanel.add(minDensityRatioField);

        formPanel.add(new JLabel("Max Allowed Variability"));
        formPanel.add(maxVariabilityField);

        // Attach DocumentListener to track changes as the user edits fields
        DocumentListener changeListener = new DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { hasChanges = true; }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { hasChanges = true; }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { hasChanges = true; }
        };

        nrSquaresField.getDocument().addDocumentListener(changeListener);
        minTracksField.getDocument().addDocumentListener(changeListener);
        minRSquaredField.getDocument().addDocumentListener(changeListener);
        minDensityRatioField.getDocument().addDocumentListener(changeListener);
        maxVariabilityField.getDocument().addDocumentListener(changeListener);

        // === Directory Selector Panel ===
        JPanel directoryPanel = new JPanel(new BorderLayout(5, 5));
        directoryPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JTextField directoryField = new JTextField();
        directoryField.setPreferredSize(new Dimension(600, 25));  // width can be adjusted
        if (!lastUsedDirectory.isEmpty()) {
            directoryField.setText(lastUsedDirectory);  // Set the saved path here
        }
        else {
            hasChanges = true;  // Then for sure the user will need to enter something
        }

        JButton browseButton = new JButton("Browse...");
        browseButton.setPreferredSize(new Dimension(100, 30));   // button size fixed

        directoryPanel.add(directoryField, BorderLayout.CENTER);
        directoryPanel.add(browseButton, BorderLayout.WEST);

        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = chooser.showOpenDialog(frame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        // === Button Panel ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {

            // Validate numeric inputs before proceeding
            try {
                int nrSquaresVal = Integer.parseInt(nrSquaresField.getText());
                int minTracksVal = Integer.parseInt(minTracksField.getText());
                double minRSquaredVal = Double.parseDouble(minRSquaredField.getText());
                double minDensityRatioVal = Double.parseDouble(minDensityRatioField.getText());
                double maxVariabilityVal = Double.parseDouble(maxVariabilityField.getText());

                DirectoryClassifier.ClassificationResult result = DirectoryClassifier.classifyDirectoryWork(Paths.get(directoryField.getText()));

                System.out.println("Maturity: " + result.maturity);
                System.out.println("Type: " + result.type);
                System.out.println("Feedback: " + result.feedback);

                if (result.type == DirectoryClassifier.DirectoryType.UNKNOWN) {
                    ImageIcon customIcon = new ImageIcon("/Users/hans/IdeaProjects/utilities/src/main/resources/paint.png");
                    String message = "<html><body style='width: 200px;'>" + result.feedback + "</body></html>";
                    JOptionPane.showMessageDialog(
                            null,                 // parent component (null centers on screen)
                            new JLabel(message),
                            "Warning",
                            JOptionPane.PLAIN_MESSAGE,
                            customIcon);
                } else if (result.type == DirectoryClassifier.DirectoryType.PROJECT) {
                    System.out.println("Nr of Squares in Row: " + nrSquaresField.getText());
                    System.out.println("Minimum Tracks to Calculate Tau: " + minTracksField.getText());
                    System.out.println("Min Allowable R-squared: " + minRSquaredField.getText());
                    System.out.println("Min Required Density Ratio: " + minDensityRatioField.getText());
                    System.out.println("Max Allowed Variability: " + maxVariabilityField.getText());
                    System.out.println("Selected Directory: " + directoryField.getText());
                }

                // Save config only if inputs are valid
                config.setInt("Generate Squares", "Nr of Squares in Row", nrSquaresVal);
                config.setInt("Generate Squares", "Min Tracks to Calculate Tau", minTracksVal);
                config.setDouble("Generate Squares", "Min Required R Squared", minRSquaredVal);
                config.setDouble("Generate Squares", "Min Required Density Ratio", minDensityRatioVal);
                config.setDouble("Generate Squares", "Max Allowable Variability", maxVariabilityVal);
                config.setString("Generate Squares", "Last Used Directory", directoryField.getText());

                if (hasChanges) {
                    config.save();
                    hasChanges = false;
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter valid numeric values in all fields.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Failed to classify directory: " + ex.getMessage(),
                        "IO Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // === Layout the Frame ===
        frame.setLayout(new BorderLayout());
        frame.add(formPanel, BorderLayout.NORTH);
        frame.add(directoryPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setSize(800, frame.getPreferredSize().height);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private JTextField createTightTextField(String text, DocumentFilter filter) {
        JTextField textField = new JTextField(text);
        textField.setPreferredSize(new Dimension(150, 20));
        textField.setMargin(new Insets(0, 2, 0, 2));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                new EmptyBorder(2, 4, 2, 4)
        ));
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(filter);
        return textField;
    }



    static class IntegerDocumentFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr)
                throws javax.swing.text.BadLocationException {
            if (string.matches("\\d+")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs)
                throws javax.swing.text.BadLocationException {
            if (text.matches("\\d*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    static class FloatDocumentFilter extends DocumentFilter {

        @Override
        public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr)
                throws javax.swing.text.BadLocationException {

            String existingText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String candidateText = new StringBuilder(existingText).insert(offset, string).toString();

            if (isValidFloat(candidateText)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs)
                throws javax.swing.text.BadLocationException {

            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = new StringBuilder(currentText).replace(offset, offset + length, text).toString();

            if (isValidFloat(newText)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

//        private boolean isValidFloat(String text) {
//            if (text.isEmpty()) return true;
//            if (text.equals("-")) return true;
//            if (text.equals(".")) return true;
//            if (text.equals("-.")) return true;
//            try {
//                Float.parseFloat(text);
//                return true;
//            } catch (NumberFormatException e) {
//                return false;
//            }
//        }

        private boolean isValidFloat(String text) {
            switch (text) {
                case "":
                case "-":
                case ".":
                case "-.":
                    return true;
                default:
                    try {
                        Float.parseFloat(text);
                        return true;
                    } catch (NumberFormatException e) {
                        return false;
                    }
            }
        }
    }
}