package Paint.Loaders;

import Paint.Objects.PaintExperiment;
import Paint.Objects.PaintProject;
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
                projectPath = Paths.get(args[0]);
            } else {
                projectPath = Paths.get("/Users/hans/Downloads/Paint Data - v38/Regular Probes/Paint Regular Probes - 20 Squares");
            }

            PaintProject project = loadProject(projectPath, true);
            System.out.println("Project: " + project.getProjectName());
            System.out.println("Experiments count: " + project.getExperiments().size());

        } catch (Exception e) {
            System.err.println("Failed to load project: " + e.getMessage());
            System.exit(1);
        }
    }

    public static PaintProject loadProject(Path projectPath, boolean matureProject) {

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
            throw new RuntimeException("Failed to project information in read top-level 'All Recordings.csv': " + messageAfterColon, e);
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

        PaintProject project = new PaintProject(projectPath);

        // Validate per-experiment and create objects for those that pass
        List<String> allErrors = new ArrayList<>();
        for (String experimentName : experimentNames) {
            Path experimentPath = projectPath.resolve(experimentName);
            List<String> errors = new ArrayList<>();

            if (!Files.isDirectory(experimentPath)) {
                errors.add("Experiment directory does not exist: " + experimentName);
            } else {
                errors.addAll(collectExperimentValidationErrors(experimentPath, experimentName, matureProject));
            }

            if (errors.isEmpty()) {
                // Create a minimal PaintExperiment and add to the project
                PaintExperiment experiment = new PaintExperiment();
                experiment.setExperimentName(experimentName);
                // Later: we can enrich the experiment with recordings/tracks/etc.
                project.addExperiment(experiment);
            } else {
                // Accumulate errors but do not stop loading other experiments
                for (String err : errors) {
                    allErrors.add("[" + experimentName + "] " + err);
                }
            }
        }

        if (project.getExperiments().isEmpty()) {
            StringBuilder sb = new StringBuilder("No valid experiments found in project: ")
                    .append(projectPath.getFileName());
            if (!allErrors.isEmpty()) {
                sb.append(System.lineSeparator()).append("Errors:");
                for (String err : allErrors) {
                    sb.append(System.lineSeparator()).append("- ").append(err);
                }
            }
            throw new IllegalStateException(sb.toString());
        }

        // Optionally log warnings for skipped experiments (non-fatal)
        if (!allErrors.isEmpty()) {
            System.err.println("Some experiments were skipped due to validation errors:");
            for (String err : allErrors) {
                System.err.println("- " + err);
            }
        }

        return project;
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