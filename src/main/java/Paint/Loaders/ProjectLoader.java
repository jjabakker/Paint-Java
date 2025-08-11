package Paint.Loaders;

import static Paint.Constants.PaintConstants.*;
import Paint.Objects.Experiment;
import Paint.Objects.Project;
import Paint.Objects.Recording;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import com.opencsv.CSVReader;
import java.io.FileReader;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import PaintUtilities.ExceptionUtils;

public class ProjectLoader {

    public static void main(String[] args) {
        Project project = null;

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
                if ("--legacy".equalsIgnoreCase(args[1])) {
                    matureProject = false;
                }
                else if ("--mature".equalsIgnoreCase(args[1])) {
                    matureProject = true;
                }
                else {
                    System.err.println("Unknown option: " + args[1]);
                    System.out.println("Use --mature or --legacy (default is --mature).");
                    System.exit(2);
                }
            }


            System.out.println("Project: " + projectPath.getFileName().toString());
            project = loadProject(projectPath, matureProject);

        } catch (Exception e) {
            System.err.println("Failed to load project: " + e.getMessage());
            System.exit(1);
        }

        if (project != null) {
            List <Experiment> experiments = project.getExperiments();
            for (Experiment experiment : experiments) {
                System.out.println(experiment);
                for (Recording rec : experiment.getRecordings()) {
                    System.out.println(rec);
                }
            }
        }
    }

    public static Project loadProject(Path projectPath, boolean matureProject) {
        Path filePath = projectPath.resolve(PROJECT_INFO_CSV);
        Table table;

        try {
            ColumnType[] allString = buildAllStringTypesFromHeader(filePath);
            CsvReadOptions options = CsvReadOptions.builder(filePath.toFile())
                    .columnTypes(allString)
                    .build();
            table = Table.read().csv(options);
        } catch (Exception e) {
            String message = ExceptionUtils.friendlyMessage(e);
            throw new RuntimeException("Failed to read top-level " + PROJECT_INFO_CSV + ":" + message, e);
        }

        // There needs to be a column named 'Experiment Name'
        if (!table.columnNames().contains("Experiment Name")) {
            throw new IllegalStateException("Column 'Experiment Name' is missing in " + PROJECT_INFO_CSV + ".");
        }

        // Unique experiment names listed in PROJECT_INFO
        List<String> allExperimentNames = table.stringColumn("Experiment Name")
                .unique()
                .asList()
                .stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // Only include experiments for which the Process flag is set (truthy).
        // Create a list of experiments to load and a list of experiments skipped.
        List<String> experimentsToLoad = new ArrayList<>();
        List<String> experimentsSkipped = new ArrayList<>();

        boolean hasProcessColumn = table.columnNames().contains("Process");
        if (hasProcessColumn) {
            Set<String> yesValues = new HashSet<>(Arrays.asList("y", "ye", "yes", "ok", "true", "t", "1"));

            Set<String> allowed = new HashSet<>();
            List<String> experimentNameColumn = table.stringColumn("Experiment Name").asList();
            List<String> processColumn = table.stringColumn("Process").asList();

            for (int i = 0; i < table.rowCount(); i++) {
                String exp = experimentNameColumn.get(i);
                String p = processColumn.get(i);
                if (exp == null)
                    continue;
                if (p != null && yesValues.contains(p.trim().toLowerCase())) {
                    allowed.add(exp.trim());
                }
            }

            for (String exp : allExperimentNames) {
                if (allowed.contains(exp)) {
                    experimentsToLoad.add(exp);
                } else {
                    experimentsSkipped.add(exp);
                }
            }
        } else {
            experimentsToLoad.addAll(allExperimentNames);
        }

        // Create the Project object
        Project project = new Project(projectPath);

        List<String> allErrors = new ArrayList<>();
        for (String experimentName : experimentsToLoad) {
            // Path experimentPath = projectPath.resolve(experimentName);

            ExperimentLoader.Result result =
                    ExperimentLoader.loadExperiment(projectPath, experimentName, matureProject);

            // Experiment exp = new Experiment(experimentName, projectPath);

            if (result.isSuccess()) {
                Experiment experiment = result.experiment().get();
                project.addExperiment(experiment);
            } else {
                for (String err : result.errors()) {
                    allErrors.add("[" + experimentName + "] " + err);
                }
            }
        }

        // Notify the user about experiments processed due to a positive  Process flag
        if (!experimentsToLoad.isEmpty()) {
            System.out.println(String.format("\nProcessed %d experiment%s", experimentsToLoad.size(), experimentsToLoad.size() == 1 ? "" : "s"));
            for (String name : experimentsToLoad) {
                System.out.println("- " + name);
            }
        }

        // Notify user about experiments skipped due to the Process flag
        if (!experimentsSkipped.isEmpty()) {
            System.out.println(String.format("\nSkipped %d experiment%s because Process flag is not set in %s", experimentsSkipped.size(), experimentsSkipped.size() == 1 ? "" : "s", PROJECT_INFO_CSV));
            for (String name : experimentsSkipped) {
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
}
