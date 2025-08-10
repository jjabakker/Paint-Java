package Paint.Loaders;

import Paint.Objects.PaintExperiment;
import Paint.Objects.PaintProject;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PainProjectLoader {

    public static void main(String[] args) {
        try {
            Path projectPath;

            if (args != null && args.length != 0) {
                projectPath = java.nio.file.Paths.get(args[0]);
            } else {
                System.out.println("Usage: java -cp <jar> Paint.Loaders.PainProjectLoader <project-root-path> [--mature|--legacy]");
                System.out.println("  <project-root-path>  Path containing Experiment Info.csv and experiment directories");
                System.out.println("  --mature             Expect squares file (All Squares.csv) in experiments (default)");
                System.out.println("  --legacy             Do not require squares file");
                return;
            }

            boolean matureProject = true;

            if (args.length > 1) {
                if ("--legacy".equalsIgnoreCase(args[1])) matureProject = false;
                else if ("--mature".equalsIgnoreCase(args[1])) matureProject = true;
                else {
                    System.err.println("Unknown option: " + args[1]);
                    System.out.println("Use --mature or --legacy (default is --mature).");
                    System.exit(2);
                }
            }

            PaintProject project = loadProject(projectPath, matureProject);
            System.out.println("Project: " + project.getProjectName());
            System.out.println("Processed experiments count: " + project.getExperiments().size());

        } catch (Exception e) {
            System.err.println("Failed to load project: " + e.getMessage());
            System.exit(1);
        }
    }

    public static PaintProject loadProject(Path projectPath, boolean matureProject) {
        Path filePath = projectPath.resolve("Experiment Info.csv");
        Table table;

        try {
            ColumnType[] allString = buildAllStringTypesFromHeader(filePath);
            CsvReadOptions options = CsvReadOptions.builder(filePath.toFile())
                    .columnTypes(allString)
                    .build();
            table = Table.read().csv(options);
        } catch (Exception e) {
            String message = extractFriendlyMessage(e);
            throw new RuntimeException("Failed to read top-level 'All Recordings.csv': " + message, e);
        }

        if (!table.columnNames().contains("Experiment Name")) {
            throw new IllegalStateException("Column 'Experiment Name' is missing in 'All Recordings.csv'.");
        }

        List<String> experimentNames = table.stringColumn("Experiment Name")
                .unique()
                .asList()
                .stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Determine which experiments have at least one row with Process flag set to a truthy value
        Map<String, Boolean> experimentHasProcessYes = new HashMap<>();
        List<String> skippedByProcess = new ArrayList<>();
        Set<String> yesValues = new HashSet<>(Arrays.asList("y", "ye", "yes", "ok", "true", "t", "1"));

        boolean hasProcessColumn = table.columnNames().contains("Process");
        if (hasProcessColumn) {
            // Initialize all to false
            for (String exp : experimentNames) {
                experimentHasProcessYes.put(exp, false);
            }
            List<String> expCol = table.stringColumn("Experiment Name").asList();
            List<String> procCol = table.stringColumn("Process").asList();

            for (int i = 0; i < table.rowCount(); i++) {
                String exp = expCol.get(i);
                if (exp == null) continue;
                String p = procCol.get(i);
                boolean isYes = p != null && yesValues.contains(p.trim().toLowerCase());
                if (isYes) {
                    experimentHasProcessYes.put(exp.trim(), true);
                }
            }
        }

        PaintProject project = new PaintProject(projectPath);

        List<String> allErrors = new ArrayList<>();
        for (String experimentName : experimentNames) {
            // If Process column exists and no row for this experiment has Process=true, skip and record
            if (hasProcessColumn && !experimentHasProcessYes.getOrDefault(experimentName, false)) {
                skippedByProcess.add(experimentName);
                continue;
            }

            Path experimentPath = projectPath.resolve(experimentName);

            PaintExperimentLoader.Result result =
                    PaintExperimentLoader.loadExperiment(experimentPath, experimentName, matureProject);

            if (result.isSuccess()) {
                PaintExperiment experiment = result.experiment().get();
                project.addExperiment(experiment);
            } else {
                for (String err : result.errors()) {
                    allErrors.add("[" + experimentName + "] " + err);
                }
            }
        }

        // Inform user about experiments skipped due to the Process flag
        if (!skippedByProcess.isEmpty()) {
            System.out.println("Note: The following experiment(s) were skipped because the Process flag is not set:");
            for (String name : skippedByProcess) {
                System.out.println("- " + name);
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

        if (!allErrors.isEmpty()) {
            System.err.println("Some experiments were skipped due to validation errors:");
            for (String err : allErrors) {
                System.err.println("- " + err);
            }
        }

        return project;
    }

    // Build an all-STRING ColumnType[] using only the header row (fast, no full parse)
    private static ColumnType[] buildAllStringTypesFromHeader(Path csvPath) throws Exception {
        try (com.opencsv.CSVReader reader = new com.opencsv.CSVReader(new java.io.FileReader(csvPath.toFile()))) {
            String[] header = reader.readNext();
            if (header == null || header.length == 0) {
                throw new IllegalStateException("CSV has no header: " + csvPath.getFileName());
            }
            ColumnType[] types = new ColumnType[header.length];
            Arrays.fill(types, ColumnType.STRING);
            return types;
        }
    }

    private static String extractFriendlyMessage(Exception e) {
        String errorMsg = e.toString();
        int colonIndex = errorMsg.lastIndexOf(":");
        return (colonIndex != -1) ? errorMsg.substring(colonIndex + 1).trim() : errorMsg;
    }
}