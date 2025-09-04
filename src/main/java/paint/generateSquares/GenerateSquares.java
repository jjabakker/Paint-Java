package paint.generateSquares;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import paint.utilities.DirectoryClassifier;
import paint.utilities.JsonConfig;

class ProjectDirectoryDialog {

    private JFrame frame;
    private JTextField directoryField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProjectDirectoryDialog::new);
    }

    public ProjectDirectoryDialog() {
        frame = new JFrame("Select Project Directory");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        directoryField = new JTextField(40);
        JButton browseButton = new JButton("Browse...");
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                directoryField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        okButton.addActionListener(e -> {
            File selectedDir = new File(directoryField.getText());
            if (selectedDir.exists() && selectedDir.isDirectory()) {
                new GenerateSquareDialog(selectedDir).show();
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Please select a valid project directory.",
                        "Invalid Directory",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> frame.dispose());

        JPanel dirPanel = new JPanel(new BorderLayout(5, 5));
        dirPanel.add(browseButton, BorderLayout.WEST);
        dirPanel.add(directoryField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(dirPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

// ======================================================================
// Second dialog: full GenerateSquare UI
// ======================================================================

class GenerateSquareDialog {

    private JFrame frame;
    private File projectDir;

    private JTextField nrSquaresField;
    private JTextField minTracksField;
    private JTextField minRSquaredField;
    private JTextField minDensityRatioField;
    private JTextField maxVariabilityField;

    private JPanel checkboxPanel;
    private List<JCheckBox> checkBoxes = new ArrayList<>();
    private boolean hasChanges = false;
    private boolean checkBoxChanged = false;

    public GenerateSquareDialog(File projectDir) {
        this.projectDir = projectDir;
        frame = new JFrame("Generate Squares - " + projectDir.getName());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JsonConfig config = new JsonConfig(Paths.get("src/main/resources/paint.json"));

        // === Form Panel ===
        int nrSquares = config.getInt("Generate Squares", "Nr of Squares in Row", 5);
        int minTracks = config.getInt("Generate Squares", "Min Tracks to Calculate Tau", 11);
        double minRSquared = config.getDouble("Generate Squares", "Min Required R Squared", 0.1);
        double minDensityRatio = config.getDouble("Generate Squares", "Min Required Density Ratio", 2.0);
        double maxVariability = config.getDouble("Generate Squares", "Max Allowable Variability", 10.0);

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

        DocumentListener changeListener = new SimpleChangeListener(() -> hasChanges = true);
        nrSquaresField.getDocument().addDocumentListener(changeListener);
        minTracksField.getDocument().addDocumentListener(changeListener);
        minRSquaredField.getDocument().addDocumentListener(changeListener);
        minDensityRatioField.getDocument().addDocumentListener(changeListener);
        maxVariabilityField.getDocument().addDocumentListener(changeListener);

        frame.add(formPanel, BorderLayout.NORTH);

        // === Checkbox Panel ===
        checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        populateCheckboxes(config);

        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        frame.add(scrollPane, BorderLayout.CENTER);

        // === Button Panel ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> handleOk(config));
        cancelButton.addActionListener(e -> frame.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void handleOk(JsonConfig config) {
        try {
            int nrSquaresVal = Integer.parseInt(nrSquaresField.getText());
            int minTracksVal = Integer.parseInt(minTracksField.getText());
            double minRSquaredVal = Double.parseDouble(minRSquaredField.getText());
            double minDensityRatioVal = Double.parseDouble(minDensityRatioField.getText());
            double maxVariabilityVal = Double.parseDouble(maxVariabilityField.getText());


            System.out.println("Nr of Squares in Row: " + nrSquaresVal);
            System.out.println("Minimum Tracks to Calculate Tau: " + minTracksVal);
            System.out.println("Min Allowable R-squared: " + minRSquaredVal);
            System.out.println("Min Required Density Ratio: " + minDensityRatioVal);
            System.out.println("Max Allowed Variability: " + maxVariabilityVal);
            System.out.println("Selected Directory: " + projectDir.getAbsolutePath());

            for (JCheckBox cb : checkBoxes) {
                if (cb.isSelected()) {
                    File expDir = new File(projectDir, cb.getText());
                    if (!expDir.isDirectory()) {
                        continue;
                    }
                    else if (!DirectoryClassifier.isExperimentDirectory(expDir.toPath()).valid) {
                        System.out.println("Skipping non-experiment directory: " + expDir);
                        System.out.println();
                    }
                    else {
                        System.out.println("Running calculations for: " + expDir);
                        // processExperiment(expDir, nrSquaresVal, minTracksVal, ...);
                    }
                }
            }

            // Save config
            saveConfig(config);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter valid numeric values.",
                    "Calculation Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateCheckboxes(JsonConfig config) {
        checkboxPanel.removeAll();
        checkBoxes.clear();

        File[] subs = projectDir.listFiles();
        if (subs != null) {
            for (File sub : subs) {
                if (sub.isDirectory()) {
                    JCheckBox cb = new JCheckBox(sub.getName());
                    boolean savedState = config.getBoolean("Generate Squares",
                            "Checkbox States." + sub.getName(), false);
                    cb.setSelected(savedState);
                    cb.addItemListener(e -> checkBoxChanged = true);
                    checkboxPanel.add(cb);
                    checkBoxes.add(cb);
                }
            }
        }
        checkboxPanel.revalidate();
        checkboxPanel.repaint();
    }

    private void saveConfig(JsonConfig config) {
        try {
            int nrSquaresVal = Integer.parseInt(nrSquaresField.getText());
            int minTracksVal = Integer.parseInt(minTracksField.getText());
            double minRSquaredVal = Double.parseDouble(minRSquaredField.getText());
            double minDensityRatioVal = Double.parseDouble(minDensityRatioField.getText());
            double maxVariabilityVal = Double.parseDouble(maxVariabilityField.getText());

//            DirectoryClassifier.ClassificationResult result =
//                    DirectoryClassifier.classifyDirectoryWork(Path.of(projectDir.getAbsolutePath()));
//
//            if (result.type == DirectoryClassifier.DirectoryType.UNKNOWN) {
//                JOptionPane.showMessageDialog(frame,
//                        "Unknown directory type: " + result.feedback,
//                        "Warning", JOptionPane.WARNING_MESSAGE);
//            }

            config.setInt("Generate Squares", "Nr of Squares in Row", nrSquaresVal);
            config.setInt("Generate Squares", "Min Tracks to Calculate Tau", minTracksVal);
            config.setDouble("Generate Squares", "Min Required R Squared", minRSquaredVal);
            config.setDouble("Generate Squares", "Min Required Density Ratio", minDensityRatioVal);
            config.setDouble("Generate Squares", "Max Allowable Variability", maxVariabilityVal);
            config.setString("Generate Squares", "Last Used Directory", projectDir.getAbsolutePath());

            config.removeWithPrefix("Generate Squares", "Checkbox");
            for (JCheckBox cb : checkBoxes) {
                config.setBoolean("Generate Squares",
                        "Checkbox States." + cb.getText(), cb.isSelected());
            }

            if (hasChanges || checkBoxChanged) {
                config.save();
                hasChanges = false;
                checkBoxChanged = false;
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter valid numeric values.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to save config: " + ex.getMessage(),
                    "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void show() {
        frame.pack();
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // === Helper methods ===
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
        private boolean isValidFloat(String text) {
            if (text.isEmpty() || text.equals("-") || text.equals(".") || text.equals("-.")) return true;
            try {
                Float.parseFloat(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    static class SimpleChangeListener implements DocumentListener {
        private final Runnable callback;
        SimpleChangeListener(Runnable callback) { this.callback = callback; }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
    }


}


public class GenerateSquares {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(ProjectDirectoryDialog::new);
    }
}