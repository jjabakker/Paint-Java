package generateSquares;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DocumentFilter;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import paint.utilities.JsonConfig;
import paint.utilities.DirectoryClassifier;

public class GenerateSquareDialog {

    private JsonConfig config = null;
    private JTextField nrSquaresField;
    private JTextField minTracksField;
    private JTextField minRSquaredField;
    private JTextField minDensityRatioField;
    private JTextField maxVariabilityField;
    private JTextField directoryField;

    private JPanel extraPanel;          // hidden until expanded
    private List<JCheckBox> checkBoxes; // track checkboxes for save/restore
    private boolean userChangedInput = false;

    private JFrame frame;
    private JButton okButton;
    private JButton cancelButton;

    private File projectDir = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GenerateSquareDialog dialog = new GenerateSquareDialog();
            dialog.go();
        });
    }

    void go() {

        frame = new JFrame("Generate Squares");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // === Directory Selector ===
        JPanel directoryPanel = new JPanel(new BorderLayout(5, 5));
        directoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        directoryField = new JTextField();
        directoryField.setPreferredSize(new Dimension(600, 25));

        JButton browseButton = new JButton("Browse...");
        browseButton.setPreferredSize(new Dimension(100, 30));
        directoryPanel.add(browseButton, BorderLayout.WEST);
        directoryPanel.add(directoryField, BorderLayout.CENTER);

        // === Extra Panel (hidden initially) ===
        extraPanel = new JPanel();
        extraPanel.setLayout(new BoxLayout(extraPanel, BoxLayout.Y_AXIS));
        extraPanel.setVisible(false);

        // === Button Panel (always visible) ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        frame.add(directoryPanel, BorderLayout.NORTH);
        frame.add(extraPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // === Browse Action ===
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedDir = chooser.getSelectedFile();
                directoryField.setText(selectedDir.getAbsolutePath());
            }
        });

        // === Cancel Action for the first dialog box ===
        cancelButton.addActionListener(e -> {

            if (userChangedInput) {
                saveConfig(config);
            }

            frame.dispose();
            System.exit(0);
        });

        // === Ok Action for the first dialog box ===
        okButton.addActionListener(e -> {
            projectDir = new File(directoryField.getText());
            if (projectDir.toString().trim().isEmpty()) {
                System.err.println("Directory cannot be empty.");
            }
            else if (!projectDir.exists() || !projectDir.isDirectory()) {
                System.out.println("Directory does not exist.");
            }
            else { // It seems like we may have a valid directory
                // Try to read in the config file
                config = new JsonConfig(Paths.get(projectDir.getAbsolutePath(), "paint.json"));

                // Expand and populate the dialog
                populateExtraPanel(new File(directoryField.getText()));
                expandDialog();

                // Change the OK button handler
                for (ActionListener al : okButton.getActionListeners()) {
                    okButton.removeActionListener(al);
                }
                okButton.addActionListener(e1 -> {
                    performCalculations();
                });

                // Change the Cancel button handler
                for (ActionListener al : cancelButton.getActionListeners()) {
                    cancelButton.removeActionListener(al);
                }
                cancelButton.addActionListener(e1 -> {
                    if (userChangedInput) {
                        saveConfig(config);
                    }
                    collapseDialog();
                });
            }
        });


        // Initial size: compact
        frame.pack();
        frame.setSize(600, 120);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void expandDialog() {
        extraPanel.setVisible(true);
        frame.setSize(800, 600); // expanded size
        frame.revalidate();
    }

    private void collapseDialog() {
        extraPanel.setVisible(false);
        frame.setSize(600, 120);
        frame.revalidate();

        for (ActionListener al : cancelButton.getActionListeners()) {
            cancelButton.removeActionListener(al);
        }
        cancelButton.addActionListener(e1 -> {
            frame.dispose();
            System.exit(0);
        });
    }

    private void populateExtraPanel(File directory) {
        extraPanel.removeAll();

        // === Form Panel ===
        int nrSquares = config.getInt("Generate Squares", "Nr of Squares in Row", 20);
        int minTracks = config.getInt("Generate Squares", "Min Tracks to Calculate Tau", 20);
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

        DocumentListener changeListener = new SimpleChangeListener(() -> userChangedInput = true);
        nrSquaresField.getDocument().addDocumentListener(changeListener);
        minTracksField.getDocument().addDocumentListener(changeListener);
        minRSquaredField.getDocument().addDocumentListener(changeListener);
        minDensityRatioField.getDocument().addDocumentListener(changeListener);
        maxVariabilityField.getDocument().addDocumentListener(changeListener);

        extraPanel.add(formPanel);

        // === Checkbox Panel ===
        checkBoxes = new ArrayList<>();
        // holds dynamic checkboxes
        JPanel checkboxPanel = new JPanel();
        checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));

        File[] subs = directory.listFiles();
        if (subs != null) {
            for (File sub : subs) {
                if (sub.isDirectory()) {
                     JCheckBox cb = new JCheckBox(sub.getName());
                    boolean savedState = config.getBoolean("Generate Squares",
                            "Checkbox States." + sub.getName(), false);
                    cb.setSelected(savedState);
                    cb.addItemListener(e -> userChangedInput = true);
                    checkboxPanel.add(cb);
                    checkBoxes.add(cb);
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(checkboxPanel);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);


        extraPanel.add(scrollPane);

        extraPanel.revalidate();
        extraPanel.repaint();
    }

    private void saveConfig(JsonConfig config) {
        try {
            int nrSquaresVal = Integer.parseInt(nrSquaresField.getText());
            int minTracksVal = Integer.parseInt(minTracksField.getText());
            double minRSquaredVal = Double.parseDouble(minRSquaredField.getText());
            double minDensityRatioVal = Double.parseDouble(minDensityRatioField.getText());
            double maxVariabilityVal = Double.parseDouble(maxVariabilityField.getText());

            DirectoryClassifier.ClassificationResult result =
                    DirectoryClassifier.classifyDirectoryWork(Paths.get(directoryField.getText()));


            // Console output for debugging
            System.out.println("Nr of Squares in Row: " + nrSquaresField.getText());
            System.out.println("Minimum Tracks to Calculate Tau: " + minTracksField.getText());
            System.out.println("Min Allowable R-squared: " + minRSquaredField.getText());
            System.out.println("Min Required Density Ratio: " + minDensityRatioField.getText());
            System.out.println("Max Allowed Variability: " + maxVariabilityField.getText());
            System.out.println("Selected Directory: " + directoryField.getText());

            // Save config
            if (userChangedInput) {
                config.setInt("Generate Squares", "Nr of Squares in Row", nrSquaresVal);
                config.setInt("Generate Squares", "Min Tracks to Calculate Tau", minTracksVal);
                config.setDouble("Generate Squares", "Min Required R Squared", minRSquaredVal);
                config.setDouble("Generate Squares", "Min Required Density Ratio", minDensityRatioVal);
                config.setDouble("Generate Squares", "Max Allowable Variability", maxVariabilityVal);
                config.setString("Generate Squares", "Last Used Directory", directoryField.getText());

                // config.removeAllCheckboxStates("Generate Squares");
                config.removeWithPrefix("Generate Squares", "Checkbox States.");

                for (JCheckBox cb : checkBoxes) {
                    config.setBoolean("Generate Squares",
                            "Checkbox States." + cb.getText(), cb.isSelected());
                }
                config.save();
                userChangedInput = false;
            }



        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Please enter valid numeric values.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame,
                    "Failed to classify directory: " + ex.getMessage(),
                    "IO Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performCalculations() {
        for (JCheckBox cb : checkBoxes) {
            if (cb.isSelected()) {
                System.out.printf("Starting calculations for project %s - experiment: %s.\n", directoryField.getText(),  cb.getText() );
            }
        }
        System.out.println(">>> Starting calculations with directory: " + directoryField.getText());
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

    // === DocumentFilter helpers ===
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

    // === Simple listener helper ===
    static class SimpleChangeListener implements DocumentListener {
        private final Runnable callback;
        SimpleChangeListener(Runnable callback) { this.callback = callback; }
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { callback.run(); }
    }
}