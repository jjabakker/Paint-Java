package paint.loaders;

import paint.calculations.CalculateTauResult;
import paint.io.TrackTableIO;
import paint.csv.TrackToTable;
import paint.objects.*;
import paint.utilities.ColumnValue;

import paint.utilities.ExceptionUtils;
import tech.tablesaw.api.*;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static paint.calculations.CalculateTau.calculateTau;
import static paint.constants.PaintConstants.*;
import static paint.csv.TrackCSVWriter.writeTracksTableToCSV;
import static paint.csv.TrackCSVWriter.writeTracksToCSV;
import static paint.csv.TrackToTable.toTable;

import paint.utilities.JsonConfig;

public final class ProjectDataLoader {

    private ProjectDataLoader() {}

    public static void main(String[] args) {
        Project project = null;

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
                } else if ("--mature".equalsIgnoreCase(args[1])) {
                    matureProject = true;
                } else {
                    System.err.println("Unknown option: " + args[1]);
                    System.out.println("Use --mature or --legacy (default is --mature).");
                    System.exit(2);
                }
            }

            System.out.println("Project: " + projectPath.getFileName().toString());

            // This is the actual call to read in a project
            project = loadProject(projectPath, matureProject);

        } catch (Exception e) {
            System.err.println("Failed to load project: " + e.getMessage());
            System.exit(1);
        }

        System.out.println(project);

        //
        //
        // At this point the project has been read in and we can inspect it
        //
        //

        // Get the first recording from the first experiment and look for the squares that have sufficient tracks
        Experiment exp = project.getExperiments().get(0);                                                 // Get the first experiment in the project
        Recording rec = exp.getRecordings().get(0);                                                       // Get the first recording in the experiment
        List<Square> squares = rec.getSquares();                                                          // Get a list of all the squares in the recording
        int minNumberOfTracksForTau = 0;
        double minRequiredSQuared = 0.9;
        int index = 0;
        Table tracksTable;
        Table experimentTracksTable = TrackToTable.emptyTrackTable();
        for (Square sq : squares) {                                                                       // Iterate through the list of squares
            if (sq.getNumberTracks() >= minNumberOfTracksForTau) {                                        // Only if the square has more than the min number of tracks
                List<Track> tracks = sq.getTracks();                                                      // Get a list of all the tracks in the square
                CalculateTauResult result = calculateTau(tracks, minNumberOfTracksForTau, minRequiredSQuared);  // Calculate the Tau
                if (result.getStatus() == CalculateTauResult.Status.TAU_SUCCESS) {
                    System.out.printf("Status: %-30s Tau: %6.1f  R_Squared : %3.6f%n", result.getStatus(), result.getTau(), result.getRSquared());
                    try {
                        writeTracksToCSV(tracks, "/Users/hans/Downloads/test_tracks.csv");
                    } catch (IOException e) {
                        System.err.println("Failed to write tracks to CSV: " + e.getMessage());
                    }
                    tracksTable = toTable(tracks);
                    experimentTracksTable.append(tracksTable);
                    int i = 0;
                }
                index += 1;
            }
        }
        try {
            writeTracksTableToCSV(experimentTracksTable, "/Users/hans/Downloads/experiment_tracks.csv");
        } catch (IOException e) {
            System.err.println("Failed to write tracks table to CSV: " + e.getMessage());
        }
        System.out.println("Number of squares with more than or equal to " + minNumberOfTracksForTau + " tracks: " + index);

        //
        //
         // Here we start with the new IO
        //
        //

        Table projectTracksTable = TrackToTable.emptyTrackTable();

        int numberTracksInRecording = 0;
        int numberTracksInExperiment = 0;
        int numberTracksInProject = 0;


        int table_counter = 0;
        Table tracksTable0 = null;
        Table tracksTable1 = null;
        Table tracksTable2 = null;

        TrackTableIO trackTableIO = new TrackTableIO();                          // Create a new TrackTableIO object
        Table tracksCombined = trackTableIO.emptyTable();                        // Create a new empty table to hold the combined tracks

        List<Experiment> experiments = project.getExperiments();
        for (Experiment experiment : experiments) {                         // Loop through the experiments
            List<Recording> recordings = experiment.getRecordings();
            for (Recording recording : recordings) {                        // Loop through the recordings
                List<Square> squaresInRecording = recording.getSquares();
                numberTracksInRecording = 0;
                for (Square square : squaresInRecording) {                  // Loop through the squares in the recording
                    List<Track> tracksInSquare = square.getTracks();        // Get the list of tracks for each square
                    tracksTable = trackTableIO.toTable(tracksInSquare);     // Convert the tracks to a table

                    switch (table_counter) {
                        case 0:
                            tracksTable0 = tracksTable.copy();
                            break;
                        case 1:
                            tracksTable1 = tracksTable.copy();
                            break;
                        case 2:
                            tracksTable2 = tracksTable.copy();
                            break;
                    }
                    table_counter += 1;

                    if (tracksTable.rowCount() != 0) {
                        projectTracksTable.append(tracksTable);
                        System.out.printf("Added %3d tracks or square %3d of recording %s of experiment %s%n",
                                tracksTable.rowCount(),
                                square.getSquareNumber(),
                                recording.getRecordingName(),
                                experiment.getExperimentName());
                        numberTracksInRecording += tracksTable.rowCount();
                        numberTracksInExperiment += tracksTable.rowCount();
                        numberTracksInProject += tracksTable.rowCount();
                    }
                }
                System.out.printf("\n\n\nThe number of tracks in %s is %s\n\n\n", recording.getRecordingName(), numberTracksInRecording);
            }
        }
        try {
            System.out.println("Number of tracks in project: " + numberTracksInProject);
            System.out.println("Number of tracks in project: " + projectTracksTable.rowCount());
            writeTracksTableToCSV(projectTracksTable, "/Users/hans/Downloads/test_tracks.csv");
        } catch (IOException e) {
            System.err.println("Failed to write tracks to CSV: " + e.getMessage());
        }


        //
        //
        // Now append the tables to the combined table and write to disk
        //
        //

        tracksCombined = trackTableIO.appended(tracksCombined, tracksTable0);
        tracksCombined = trackTableIO.appended(tracksCombined, tracksTable1);
        tracksCombined = trackTableIO.appended(tracksCombined, tracksTable2);
        try {
            trackTableIO.writeCsv(tracksCombined, "/Users/hans/Downloads/test_tracks_combined.csv");
        } catch (IOException e) {
            System.err.println("Failed to write tracks to CSV: " + e.getMessage());
        }


    }

    // ---------- Public API ----------


    public static Project loadProject(Path projectPath, boolean matureProject) {
        List<Experiment> experiments = new ArrayList<>();
        Set<String> experimentsToProcess = readExperimentsToProcess(projectPath);
        Project project;

        Context context = loadContext(projectPath);
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
        project.setContext(context);
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
        Context context = getProjectContext(experiment, experimentPath, experimentName);

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
        Table experimentTracksTable = loadTracks(experimentPath);
        int numberOfTracksInExperiment = experimentTracksTable.rowCount();

        for (Recording recording : recordings) {
            String recordingName = recording.getRecordingName();
            String recordingNameColumn = COL_EXT_RECORDING_NAME;

            Table recordingTracksTable = experimentTracksTable.where(
                    experimentTracksTable.stringColumn(recordingNameColumn)
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
            int lastColIndex = 19;   // ToDo
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
                        square.addTrack(track);
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
    public static Table loadTracks(Path experimentPath) throws Exception {     // @@@

        String tracksPath = experimentPath.resolve(TRACKS_CSV).toString();
         // Detect all column types first
        Table temp = Table.read().csv(tracksPath);
        ColumnType[] detected = temp.columnTypes();

        // Find and override just the one column you care about
        int colIndex = temp.columnIndex("Label Nr");
        detected[colIndex] = ColumnType.INTEGER;

        // Read again with forced column type
        CsvReadOptions options = CsvReadOptions.builder(tracksPath)
                .columnTypes(detected)
                .build();
        return Table.read().usingOptions(options);
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

    private static Context getProjectContext(Experiment experiment, Path experimentPath, String experimentName) {

        Table table;
//        try {
//            table = loadRecordingsTable(experimentPath);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        // Pull unique values; exit if they vary
          Context context = new Context();
//        // context.setCaseName(String.valueOf(getUniqueColumnValueOrExit(table, "Case")));
//        context.setNumberOfSquaresInRecordingSpecifiedByRow(String.valueOf(getUniqueColumnValueOrExit(table, "Nr of Squares in Row")));
//        context.setMaxFrameGap(String.valueOf(getUniqueColumnValueOrExit(table, "Max Frame Gap")));
//        context.setGapClosingMaxDistance(String.valueOf(getUniqueColumnValueOrExit(table, "Gap Closing Max Distance")));
//        context.setLinkingMaxDistance(String.valueOf(getUniqueColumnValueOrExit(table, "Linking Max Distance")));
//        context.setMedianFiltering(String.valueOf(getUniqueColumnValueOrExit(table, "Median Filtering")));
//        context.setMinNumberOfSpotsInTrack(String.valueOf(getUniqueColumnValueOrExit(table, "Min Spots in Track")));
//        context.setMinTracksForTau(String.valueOf(getUniqueColumnValueOrExit(table, "Min Tracks for Tau")));
//        context.setNeighbourMode(String.valueOf(getUniqueColumnValueOrExit(table, "Neighbour Mode")));
//        context.setMaxAllowableVariability(String.valueOf(getUniqueColumnValueOrExit(table, "Max Allowable Variability")));
//        context.setMinRequiredDensityRatio(String.valueOf(getUniqueColumnValueOrExit(table, "Min Required Density Ratio")));
//        context.setMinRequiredRSquared(String.valueOf(getUniqueColumnValueOrExit(table, "Min Required R Squared")));
        return context;
    }

    private static Object getUniqueColumnValueOrExit(Table table, String columnName) {
        // 1) Existence check BEFORE calling table.column(...)
        if (!table.columnNames().contains(columnName)) {
            System.err.println("Column '" + columnName + "' does not exist.");
            System.err.println("Available columns: " + table.columnNames());
            System.exit(-1);
        }

        // 2) Now it's safe to fetch the column
        Column col = table.column(columnName);

        if (col.size() == 0) {
            System.err.println("Column '" + columnName + "' is empty.");
            System.exit(-1);
        }

        // 3) Verify all rows hold the same value
        Object first = col.get(0);
        if (col instanceof NumberColumn) {
            NumberColumn nc = (NumberColumn) col;
            double d0 = nc.getDouble(0);
            for (int i = 1; i < nc.size(); i++) {
                double di = nc.getDouble(i);
                // Treat NaN == NaN as equal; else use exact double compare
                if (!(Double.isNaN(d0) && Double.isNaN(di)) && Double.compare(d0, di) != 0) {
                    System.err.println("Not all rows have the same value in numeric column: " + columnName);
                    System.exit(-1);
                }
            }
            return first; // same as col.get(0)
        } else {
            for (int i = 1; i < col.size(); i++) {
                Object v = col.get(i);
                if (!equalsNullSafe(first, v)) {
                    System.err.println("Not all rows have the same value in column: " + columnName);
                    System.exit(-1);
                }
            }
            return first;
        }
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

    // Null-safe equality with String trimming option if you want it
    private static boolean equalsNullSafe(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
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
        track.setUniqueKey(row.getString("Unique Key"));
        track.setRecordingName(row.getString("Ext Recording Name"));
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
        track.setSquareNumber(row.getInt("Square Nr"));
        try {
            int Temp = row.getInt("Label Nr");
        }
        catch  (Exception e){
            System.out.println("Error: Label Nr is not an integer");
        }
        track.setLabelNumber(row.getInt("Label Nr"));


        return track;
    }

    private static Context loadContext(Path projectPath) {

        Context context = new Context();

        int numberOfSquaresInRecording = 3;
        double minRequiredRSquared = 0.1;
        double maxAllowableVariability = 10;
        double minRequiredDensityRatio = 0.1;
        int minTracksForTau = 20;
        int maxFrameGap = 3;
        double gapClosingMaxDistance = 1.2;
        double linkingMaxDistance = 0.6;
        boolean medianFiltering = false;
        int minNumberOfSpotsInTrack = 3;
        String neighbourMode = "Free";

        // Create the config reader and fetch the values
        JsonConfig config = new JsonConfig(projectPath.resolve(PAINT_JSON));

        try {
            numberOfSquaresInRecording = config.getInt("Generate Squares", "Nr of Squares in Recording", 400);
            minRequiredRSquared = config.getDouble("Generate Squares", "Min Required R Squared", 0.1);
            maxAllowableVariability = config.getDouble("Generate Squares", "Max Allowable Variability", 10.0);
            minRequiredDensityRatio = config.getDouble("Generate Squares", "Min Required Density Ratio", 2.0);
            minTracksForTau = config.getInt("Generate Squares", "Min Tracks to Calculate Tau", 20);
            maxFrameGap = config.getInt("TrackMate", "MAX_FRAME_GAP", 3);
            gapClosingMaxDistance = config.getDouble("TrackMate", "GAP_CLOSING_MAX_DISTANCE", 1.2);
            linkingMaxDistance = config.getDouble("TrackMate", "LINKING_MAX_DISTANCE", 0.6);
            medianFiltering = config.getBoolean("TrackMate", "DO_MEDIAN_FILTERING", false);
            minNumberOfSpotsInTrack = config.getInt("TrackMate", "MIN_NR_SPOTS_IN_TRACK", 3);
            neighbourMode = config.getString("Generate Squares", "NEIGHBOUR_MODE", "Free");

        }
        catch (Exception e) {
            System.err.println("Failed to read config file: " + e.getMessage());
            System.exit(-1);
        }
        try {
            context.setNumberOfSquaresInRecording(numberOfSquaresInRecording);
            context.setMinRequiredRSquared(minRequiredRSquared);
            context.setMaxAllowableVariability(maxAllowableVariability);
            context.setMinRequiredDensityRatio(minRequiredDensityRatio);
            context.setMinTracksForTau(minTracksForTau);
            context.setMaxFrameGap(maxFrameGap);
            context.setGapClosingMaxDistance(gapClosingMaxDistance);
            context.setLinkingMaxDistance(linkingMaxDistance);
            context.setMedianFiltering(medianFiltering);
            context.setMinNumberOfSpotsInTrack(minNumberOfSpotsInTrack);
            context.setNeighbourMode(neighbourMode);
        }
        catch (Exception e) {
            System.err.println("Failed set context values.");
            System.exit(-1);
        }
        return context;
    }
}