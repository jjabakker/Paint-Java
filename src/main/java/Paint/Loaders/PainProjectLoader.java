package Paint.Loaders;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        List<String> rawExperimentNames;
        try {
            rawExperimentNames = table.stringColumn("Experiment Name")
                    .unique()
                    .asList();
        } catch (Exception e) {
            throw new IllegalStateException("Column 'Experiment Name' is missing or unreadable in 'All Recordings.csv'.", e);
        }

        // Normalize names to avoid trailing/leading spaces from CSV
        List<String> experimentNames = new ArrayList<>(rawExperimentNames.size());
        for (String name : rawExperimentNames) {
            experimentNames.add(name == null ? "" : name.trim());
        }

        // Validate per-experiment directories and required CSVs
        List<String> errors = new ArrayList<>();
        for (String experimentName : experimentNames) {
            if (experimentName.isEmpty()) {
                errors.add("Encountered empty experiment name in top level 'All Recordings.csv'.");
                continue;
            }

            Path experimentPath = projectPath.resolve(experimentName);
            if (!Files.isDirectory(experimentPath)) {
                errors.add("Experiment directory does not exist: " + experimentName);
                continue;
            }

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
}