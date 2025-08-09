package Paint.Loaders;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PainProjectLoader {

    public static void main(String[] args) {
        try {
            Path projectPath;

            if (args != null && args.length != 0) {
                // System.err.println("Usage: java Paint.Loaders.PainProjectLoader <project-directory>");
                // System.err.println("Example: java Paint.Loaders.PainProjectLoader /path/to/PaintProject");
                // System.exit(1);
                projectPath = Paths.get(args[0]);
            }
            else {
                projectPath = Paths.get("/Users/hans/Downloads/Paint Data - v38/Regular Probes/Paint Regular Probes - 20 Squares");
            }

            List<String> experimentNames = loadProject(projectPath, true);
            System.out.println("Experiments count: " + experimentNames.size());

        } catch (Exception e) {
            System.err.println("Failed to load project: " + e.getMessage());
            System.exit(1);
        }
    }

    public static List<String> loadProject(Path projectPath, boolean matureProject) {
        // Read the top-level "All Recordings.csv" as strings
        Path filePath = projectPath.resolve("All Recordings.csv");
        Table table;

        try {
            // Probe to get column count
            Table temp = Table.read().csv(filePath.toFile());
            ColumnType[] allString = new ColumnType[temp.columnCount()];
            Arrays.fill(allString, ColumnType.STRING);

            CsvReadOptions options = CsvReadOptions.builder(filePath.toFile())
                    .columnTypes(allString)
                    .build();

            table = Table.read().csv(options);
        } catch (Exception e) {
            String errorMsg = e.toString();
            int colonIndex = errorMsg.lastIndexOf(":");
            String messageAfterColon = (colonIndex != -1) ? errorMsg.substring(colonIndex + 1).trim() : errorMsg;
            throw new RuntimeException("Failed to read top-level 'All Recordings.csv': " + messageAfterColon, e);
        }

        if (!table.columnNames().contains("Experiment Name")) {
            throw new IllegalStateException("Column 'Experiment Name' is missing in 'All Recordings.csv'.");
        }

        // Collect ALL unique experiment names (no Process filtering), while trimming whitespace
        List<String> rawNames = table.stringColumn("Experiment Name").unique().asList();
        Set<String> uniqueTrimmed = new LinkedHashSet<>();
        for (String name : rawNames) {
            if (name != null) {
                String trimmed = name.trim();
                if (!trimmed.isEmpty()) {
                    uniqueTrimmed.add(trimmed);
                }
            }
        }
        List<String> experimentNames = new ArrayList<>(uniqueTrimmed);

        // Validate per-experiment requirements via helper
        List<String> errors = new ArrayList<>();
        for (String experimentName : experimentNames) {
            Path experimentPath = projectPath.resolve(experimentName);
            if (!Files.isDirectory(experimentPath)) {
                errors.add("Experiment directory does not exist: " + experimentName);
                continue;
            }
            errors.addAll(collectExperimentValidationErrors(experimentPath, experimentName, matureProject));
        }

        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Project validation failed:");
            for (String err : errors) {
                sb.append(System.lineSeparator()).append("- ").append(err);
            }
            throw new IllegalStateException(sb.toString());
        }

        return experimentNames;
    }

    // New helper that encapsulates all experiment-level checks
    private static List<String> collectExperimentValidationErrors(Path experimentPath, String experimentName, boolean matureProject) {
        List<String> errors = new ArrayList<>();

        Path recordingsPath = experimentPath.resolve("All Recordings.csv");
        if (!Files.isRegularFile(recordingsPath)) {
            errors.add("File 'All Recordings.csv' does not exist in experiment: " + experimentName);
        }

        Path tracksPath = experimentPath.resolve("All Tracks.csv");
        if (!Files.isRegularFile(tracksPath)) {
            errors.add("File 'All Tracks.csv' does not exist in experiment: " + experimentName);
        }

        if (matureProject) {
            Path squaresPath = experimentPath.resolve("All Squares.csv");
            if (!Files.isRegularFile(squaresPath)) {
                errors.add("File 'All Squares.csv' does not exist in experiment: " + experimentName);
            }
        }

        Path trackMateImagesPath = experimentPath.resolve("TrackMate Images");
        if (!Files.isDirectory(trackMateImagesPath)) {
            errors.add("Directory 'TrackMate Images' does not exist in experiment: " + experimentName);
        }

        Path brightfieldImagesPath = experimentPath.resolve("Brightfield Images");
        if (!Files.isDirectory(brightfieldImagesPath)) {
            errors.add("Directory 'Brightfield Images' does not exist in experiment: " + experimentName);
        }

        return errors;
    }
}