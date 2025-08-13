package paint.loaders;

import paint.objects.*;
import paint.utilities.ColumnValue;

import paint.utilities.ExceptionUtils;
import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static paint.constants.PaintConstants.*;

public final class ProjectDataLoader {

    private ProjectDataLoader() {}

    public static void main(String[] args) {
        Project project = null;
        List<Experiment> experiments;

        try {
            Path projectPath;

            if (args != null && args.length != 0) {
                projectPath = java.nio.file.Paths.get(args[0]);
            } else {
                System.out.println("Usage: java -cp <jar> paint.loaders.PainProjectLoader <project-root-path> [--mature|--legacy]");
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

        experiments = project.getExperiments();
        for (Experiment experiment : experiments) {
            System.out.println(experiment);
            List <Recording> recordings = experiment.getRecordings();
            for (Recording rec : recordings) {
                System.out.println(rec);
            }
        }

    }

    // ---------- Public API ----------


    public static Project loadProject(Path projectPath, boolean matureProject) {
        List<Experiment> experiments = new ArrayList<>();
        Set<String> experimentsToProcess = readExperimentsToProcess(projectPath);
        Project project;

        if (experimentsToProcess == null) {
            System.err.println("Warning: No Project_info.csv filter applied; loading all experiments.");
            try (DirectoryStream<Path> dirs = Files.newDirectoryStream(projectPath)) {
                for (Path expDir : dirs) {
                    if (!Files.isDirectory(expDir)) continue;
                    String experimentName = expDir.getFileName().toString();
                    loadAndAddExperiment(experiments, projectPath, experimentName, matureProject);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error loading project: " + e.getMessage(), e);
            }
        } else {
            for (String experimentName : experimentsToProcess) {
                Path expDir = projectPath.resolve(experimentName);
                if (!Files.isDirectory(expDir)) {
                    System.err.println("Warning: experiment folder not found: " + experimentName);
                    continue;
                }
                loadAndAddExperiment(experiments, projectPath, experimentName, matureProject);
            }
        }

        project = new Project(projectPath);
        project.setExperiments(experiments);
        project.setProjectName(projectPath.getFileName().toString());

        return project;
    }

    private static void loadAndAddExperiment(List<Experiment> experiments, Path projectPath,
                                             String experimentName, boolean matureProject) {
        Result result = null;
        try {
            result = loadExperiment(projectPath, experimentName, matureProject);
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }
        if (result.isSuccess() && result.experiment().isPresent()) {
            experiments.add(result.experiment().get());
        } else {
            System.err.println("Failed to load experiment: " + experimentName);
            for (String err : result.errors()) System.err.println(err);
        }
    }

    /** Load a single experiment; returns Result with Experiment or errors. */
    public static Result loadExperiment(Path projectPath, String experimentName, boolean matureProject) throws Exception {

        Path experimentPath = projectPath.resolve(experimentName);

        // Validate the structure
        List<String> errors = validateExperimentLayout(experimentPath, experimentName, matureProject);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        // Create the Experiment object so that it is available for populating
        Experiment experiment = new Experiment();
        experiment.setExperimentName(experimentName);

        // Get Experiment Attributes from old style data
        setExperimentAttributes(experiment, experimentPath, experimentName);

        // Load recordings, but do not bother with their squares and tracks yet.
        List<Recording> recordings;
        try {
            recordings = loadRecordings(experimentPath);
            for (Recording rec : recordings) {
                experiment.addRecording(rec);
            }
        } catch (Exception e) {
            errors.add("Failed to read '" + RECORDINGS_CSV + "': " + ExceptionUtils.friendlyMessage(e));
            return Result.failure(errors);
        }

        // Read the experiment 'All Squares' and assign the squares to each recording.
        Table squaresTable = readTableAsStrings(experimentPath.resolve(SQUARES_CSV));
        for (Recording recording : recordings) {
            String recordingName = recording.getRecordingName();
            String recordingNameColumn = "Recording Name";

            // The Code is a bit more complex because there is ambiguity in which column the name is to be found.
            // Later on when 'Ext Recording Name' is fully phased out, this code can be simplified.
            if (!squaresTable.containsColumn(COL_RECORDING_NAME) && squaresTable.containsColumn(COL_EXT_RECORDING_NAME)) {
                recordingNameColumn = COL_EXT_RECORDING_NAME;
            }
            else {
                System.err.println("No column named 'Recording Name' or 'Ext Recording Name' found in '" + SQUARES_CSV + "'.");
                System.exit(-1);
            }
            // End

            Table squaresOfRecording = squaresTable.where(
                    squaresTable.stringColumn(recordingNameColumn)
                            .matchesRegex("^" + recordingName + "(?:-threshold-\\d{1,3})?$")
            );

            // Create the Square objects for this recording
            for (Row row : squaresOfRecording) {
                List<ColumnValue> colValues = new ArrayList<>();

                for (int colIndex = 0; colIndex < squaresOfRecording.columnCount(); colIndex++) {
                    String columnName = squaresOfRecording.column(colIndex).name();
                    Object value = row.getObject(colIndex);
                    colValues.add(new ColumnValue(columnName, (String) value));
                }

                Square square  = new Square(colValues);
                recording.addSquare(square);
            }
        }

        // Read the experiment tracks and assign the tracks to each recording.
        Table exprimentTracksTable = loadTracks(experimentPath);
        int numberOfTracksInExperiment = exprimentTracksTable.rowCount();

        for (Recording recording : recordings) {
            String recordingName = recording.getRecordingName();
            String recordingNameColumn = COL_EXT_RECORDING_NAME;

            Table recordingTracksTable = exprimentTracksTable.where(
                    exprimentTracksTable.stringColumn(recordingNameColumn)
                            .matchesRegex("^" + recordingName + "(?:-threshold-\\d{1,3})?$"));
            int numberOfTracksInRecording = recordingTracksTable.rowCount();

            // Save the table
            if (recordingTracksTable.rowCount() == 0) {
                continue;
            }
            recording.setTracksTable(recordingTracksTable);

            // Here the Track objects are added (arguably you do one or the other)
            for (Row row1 : recordingTracksTable) {
                Track track = createTrackFromRow(row1);
                recording.addTrack(track);
            }

            // Now assign tracks to squares in each recording
            int cumulativeNumberOfTracksInSquares = 0;
            int colIndex;
            int rowIndex;
            int lastColIndex = 19;   // Todo
            int lastRowIndex = 19;

            for (Square square : recording.getSquares()) {
                double x0 = square.getX0();
                double y0 = square.getY0();
                double x1 = square.getX1();
                double y1 = square.getY1();
                colIndex = square.getColNumber();
                rowIndex = square.getRowNumber();

                Table squareTracksTable = filterTracksInSquare(recordingTracksTable, x0, y0, x1, y1, colIndex == lastColIndex,
                        rowIndex == lastRowIndex);
                // DEBUG int numberOfTracksInSquare = squareTracksTable.rowCount();
                // DEBUG System.out.printf("Recording %s - Square %3d: %5d cumulative %5d\n", recordingName, squareNr, numberOfTracksInSquare, cumulativeNumberOfTracksInSquares);
                if (squareTracksTable.rowCount() > 0) {
                    cumulativeNumberOfTracksInSquares += squareTracksTable.rowCount();
                    for (Row row : squareTracksTable) {
                        Track track = createTrackFromRow(row);
                        recording.addTrack(track);
                    }
                }
            }
            System.out.println("RecordingTracksTable size: " + recordingTracksTable.rowCount() + " --- " + " Cumulative Number of Tracks In Squares " + cumulativeNumberOfTracksInSquares);
        }

        return errors.isEmpty() ? Result.success(experiment) : Result.failure(errors);
    }

    /** Project-level CSV (all STRING columns). */
    public static Table loadProjectInfo(Path projectPath) throws Exception {
        return readTableAsStrings(projectPath.resolve(PROJECT_INFO_CSV));
    }

    /** Experiment info CSV (all STRING columns). */
    public static Table loadExperimentInfo(Path experimentPath) throws Exception {
        return readTableAsStrings(experimentPath.resolve(EXPERIMENT_INFO_CSV));
    }

    /** Squares CSV (all STRING columns). */
    public static Table loadSquares(Path experimentPath) throws Exception {
        return readTableAsStrings(experimentPath.resolve(SQUARES_CSV));
    }

    /** Tracks CSV (original columns). */
    public static Table loadTracks(Path experimentPath) throws Exception {
        return readTable(experimentPath.resolve(TRACKS_CSV));
    }

    /** Recordings CSV (all STRING columns). */
    public static Table loadRecordingsTable(Path experimentPath) throws Exception {
        return readTableAsStrings(experimentPath.resolve(RECORDINGS_CSV));
    }

    // ---------- Result type ----------

    public static final class Result {
        private final Experiment experiment;
        private final List<String> errors;

        private Result(Experiment experiment, List<String> errors) {
            this.experiment = experiment;
            this.errors = errors;
        }

        public static Result success(Experiment experiment) {
            return new Result(experiment, Collections.emptyList());
        }

        public static Result failure(List<String> errors) {
            return new Result(null, new ArrayList<>(errors));
        }

        public Optional<Experiment> experiment() {
            return Optional.ofNullable(experiment);
        }

        public List<String> errors() {
            return Collections.unmodifiableList(errors);
        }

        public boolean isSuccess() {
            return experiment != null && errors.isEmpty();
        }
    }

    // ---------- Implementation ----------


    /** A regular read, safe dor csv files that have been system-generated */
    private static Table readTable(Path csvPath) throws Exception {
        return Table.read().csv(csvPath.toFile());
    }

    /** Always read CSV with ALL columns forced to STRING. */
    private static Table readTableAsStrings(Path csvPath) throws Exception {
        String headerLine;
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            headerLine = br.readLine();
        }
        if (headerLine == null) {
            return Table.create(csvPath.getFileName().toString());
        }

        // simple split on comma; switch to a CSV parser if headers may contain commas in quotes
        int columnCount = headerLine.split(",", -1).length;

        ColumnType[] types = new ColumnType[columnCount];
        Arrays.fill(types, ColumnType.STRING);

        CsvReadOptions options = CsvReadOptions.builder(csvPath.toFile())
                .header(true)
                .columnTypes(types)
                .build();

        return Table.read().usingOptions(options);
    }

    private static List<String> validateExperimentLayout(Path experimentPath, String experimentName, boolean matureProject) {
        List<String> errors = new ArrayList<>();

        if (!Files.isDirectory(experimentPath)) {
            errors.add("Experiment directory does not exist: " + experimentName);
            return errors;
        }

        if (!Files.isRegularFile(experimentPath.resolve(RECORDINGS_CSV))) {
            errors.add("File '" + RECORDINGS_CSV + "' does not exist.");
        }
        if (!Files.isRegularFile(experimentPath.resolve(TRACKS_CSV))) {
            errors.add("File '" + TRACKS_CSV + "' does not exist.");
        }
        if (matureProject && !Files.isRegularFile(experimentPath.resolve(SQUARES_CSV))) {
            errors.add("File '" + SQUARES_CSV + "' does not exist.");
        }
        if (!Files.isDirectory(experimentPath.resolve(DIR_TRACKMATE_IMAGES))) {
            errors.add("Directory '" + DIR_TRACKMATE_IMAGES + "' does not exist.");
        }
        if (!Files.isDirectory(experimentPath.resolve(DIR_BRIGHTFIELD_IMAGES))) {
            errors.add("Directory '" + DIR_BRIGHTFIELD_IMAGES + "' does not exist.");
        }

        return errors;
    }

    private static List<Recording> loadRecordings(Path experimentPath)  {
        Table table = null;
        try {
            table = loadRecordingsTable(experimentPath);
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }

        if (!table.columnNames().contains(COL_RECORDING_NAME)) {
//            throw new IllegalStateException(
//                    "Column '" + COL_RECORDING_NAME + "' is missing in '" + RECORDINGS_CSV + "'.");
        }

        List<Recording> recordings = new ArrayList<>(table.rowCount());
        for (Row row : table) {
            List<ColumnValue> colValues = new ArrayList<>();
            for (int colIndex = 0; colIndex < table.columnCount(); colIndex++) {
                String columnName = table.column(colIndex).name();
                Object value = row.getObject(colIndex);
                colValues.add(new ColumnValue(columnName, String.valueOf(value)));
            }
            Recording rec = new Recording(colValues);
            recordings.add(rec);
        }

        if (recordings.isEmpty()) {
            Recording placeholder = new Recording();
            placeholder.setRecordingName("Recording");
            return Collections.singletonList(placeholder);
        }

        return recordings;
    }

    private static void setExperimentAttributes(Experiment experiment, Path experimentPath, String experimentName) {

        Table table;
        try {
            table = loadRecordingsTable(experimentPath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Pull unique values; exit if they vary
        experiment.setCaseName(String.valueOf(getUniqueColumnValueOrExit(table, "Case")));
        experiment.setMaxFrameGap(String.valueOf(getUniqueColumnValueOrExit(table, "Max Frame Gap")));
        experiment.setGapClosingMaxDistance(String.valueOf(getUniqueColumnValueOrExit(table, "Gap Closing Max Distance")));
        experiment.setLinkingMaxDistance(String.valueOf(getUniqueColumnValueOrExit(table, "Linking Max Distance")));
        experiment.setMedianFiltering(String.valueOf(getUniqueColumnValueOrExit(table, "Median Filtering")));
        experiment.setMinNumberOfSpotsInTrack(String.valueOf(getUniqueColumnValueOrExit(table, "Min Spots in Track")));
        experiment.setMinTracksForTau(String.valueOf(getUniqueColumnValueOrExit(table, "Min Tracks for Tau")));
        experiment.setNeighbourMode(String.valueOf(getUniqueColumnValueOrExit(table, "Neighbour Mode")));
    }

    private static Object getUniqueColumnValueOrExit(Table table, String columnName) {
        tech.tablesaw.columns.Column<?> column = table.column(columnName);

        if (column.isEmpty()) {
            System.err.println("Column '" + columnName + "' is empty.");
            System.exit(-1);
        }
        if (column.unique().size() == 1) {
            return column.get(0);
        }
        System.err.println("Not all rows have the same value in column: " + columnName);
        System.exit(-1);
        return null; // Unreachable but needed for compiler
    }

    private static Set<String> readExperimentsToProcess(Path projectPath) {
        Set<String> include = new HashSet<>();
        Path csvPath = projectPath.resolve(PROJECT_INFO_CSV);
        if (!Files.exists(csvPath)) {
            System.err.println("Warning: Project_info.csv not found; including all experiments.");
            return null; // null => include all (backward compatible)
        }

        try {
            Table t = readTableAsStrings(csvPath);

            if (!t.columnNames().contains("Experiment Name")) {
                System.err.println("Warning: Project_info.csv missing 'Experiment' column; including all.");
                return null;
            }
            if (!t.columnNames().contains("Process")) {
                System.err.println("Warning: Project_info.csv missing 'Process' column; including all.");
                return null;
            }

            // Iterate rows and collect experiments with Process == truthy
            for (int i = 0; i < t.rowCount(); i++) {
                String exp = t.stringColumn("Experiment Name").get(i);
                String proc = t.stringColumn("Process").get(i);
                if (exp != null && isTruthy(proc)) {
                    include.add(exp.trim());
                }
            }

            return include;
        } catch (Exception e) {
            System.err.printf("Warning: Failed to read %s: %s", PROJECT_INFO_CSV,e.getMessage());
            return null;
        }
    }

    private static boolean isTruthy(String v) {
        if (v == null) return false;
        String s = v.trim();
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("t")
                || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("y")
                || s.equals("1");
    }



    public static Table filterTracksInSquare(Table tracks, double x0, double y0, double x1, double y1,
                                             boolean isLastColumn, boolean isLastRow) {
        double left   = Math.min(x0, x1);
        double right  = Math.max(x0, x1);
        double top    = Math.min(y0, y1);
        double bottom = Math.max(y0, y1);

        DoubleColumn x = tracks.doubleColumn("Track X Location");
        DoubleColumn y = tracks.doubleColumn("Track Y Location");

        Selection sel = x.isGreaterThanOrEqualTo(left);
        if (isLastColumn)
            sel.and(x.isLessThanOrEqualTo(right));
        else
            sel.and(x.isLessThan(right));

        sel.and(y.isGreaterThanOrEqualTo(top));
        if (isLastRow)
            sel.and(y.isLessThanOrEqualTo(bottom));
        else
            sel.and(y.isLessThan(bottom));

        return tracks.where(sel);
    }

    private static Track createTrackFromRow(Row row) {

        Track track = new Track();
        track.setTrackLabel(row.getString("Track Label"));
        track.setTrackId(row.getInt("Track Id"));
        track.setTrackLabel(row.getString("Track Label"));
        track.setNumberSpots(row.getInt("Nr Spots"));
        track.setNumberGaps(row.getInt("Nr Gaps"));
        track.setLongestGap(row.getInt("Longest Gap"));
        track.setTrackDuration(row.getDouble("Track Duration"));
        track.setTrackXLocation(row.getDouble("Track X Location"));
        track.setTrackYLocation(row.getDouble("Track Y Location"));
        track.setTrackDisplacement(row.getDouble("Track Displacement"));
        track.setTrackMaxSpeed(row.getDouble("Track Max Speed"));
        track.setTrackMedianSpeed(row.getDouble("Track Median Speed"));
        track.setTrackMeanSpeed(row.getDouble("Track Mean Speed"));
        track.setTrackMaxSpeedCalc(row.getDouble("Track Max Speed Calc"));
        track.setTrackMedianSpeedCalc(row.getDouble("Track Median Speed Calc"));
        track.setTrackMeanSpeedCalc(row.getDouble("Track Mean Speed Calc"));
        track.setDiffusionCoefficient(row.getDouble("Diffusion Coefficient"));
        track.setDiffusionCoefficientExt(row.getDouble("Diffusion Coefficient Ext"));
        track.setTotalDistance(row.getDouble("Total Distance"));
        track.setConfinementRatio(row.getDouble("Confinement Ratio"));

        return track;
    }
}