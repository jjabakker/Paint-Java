package Paint.Loaders;

import Paint.Objects.PaintExperiment;
import Paint.Objects.PaintProject;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import com.opencsv.CSVReader; // add import
import java.io.FileReader;   // add import

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class PainProjectLoader {

    public static void main(String[] args) {
        try {
            Path projectPath;

            if (args != null && args.length != 0) {
                projectPath = Paths.get(args[0]);
            } else {
                projectPath = Paths.get("/Users/hans/Downloads/Paint Data - v38/Regular Probes/Paint Regular Probes - 20 Squares"); // Replace or pass via args
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

        PaintProject project = new PaintProject(projectPath);

        List<String> allErrors = new ArrayList<>();
        for (String experimentName : experimentNames) {
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
        try (CSVReader reader = new CSVReader(new FileReader(csvPath.toFile()))) {
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