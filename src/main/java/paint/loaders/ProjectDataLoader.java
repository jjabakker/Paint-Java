package paint.loaders;

import paint.io.RecordingTableIO;
import paint.io.TrackTableIO;

import paint.io.SquareTableIO;
import paint.objects.*;

import paint.utilities.ExceptionUtils;
import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.selection.Selection;

import java.io.BufferedReader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static paint.calculations.CalculateRecording.calculateAverageTrackCountOfBackground;
import static paint.constants.PaintConstants.*;

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
                return;
            }

            boolean matureProject = false;

            if (args.length > 1) {
                if ("--mature".equalsIgnoreCase(args[1])) {
                    matureProject = true;
                } else {
                    System.err.println("Unknown option: " + args[1]);
                    System.out.println("Use --mature.");
                    System.exit(2);
                }
            }

            project = loadProject(projectPath, matureProject);

        } catch (Exception e) {
            System.err.println("Failed to load project: " + e.getMessage());
            System.exit(1);
        }

        System.out.println(project);

        // evaluateProject(project);

        System.out.println("\n\n\n\n");
        cycleThroughProject(project);
    }


    // ---------- Public API ----------

    public static void cycleThroughProject(Project project) {


        int numberOfTracksInExperiment = 0;
        int numberOfTracksInProject = 0;

        System.out.printf("Project: %s has the following context:\n",project.getProjectName());
        System.out.println(project.getContext());
        System.out.printf("Project %s has %d experiments:\n", project.getProjectName(), project.getExperiments().size());

        for (Experiment exp : project.getExperiments()) {
            System.out.printf("\t%s\n", exp.getExperimentName());
        }
        System.out.println();

        for (Experiment exp : project.getExperiments()) {
            numberOfTracksInExperiment = 0;
            System.out.printf("\n\nExperiment: %s has %d recordings.\n", exp.getExperimentName(), exp.getRecordings().size());
            for (Recording rec : exp.getRecordings()) {
                System.out.printf("\t%s\n", rec.getRecordingName());
            }

            for (Recording rec : exp.getRecordings()) {
                System.out.printf("\n");
                System.out.printf("Recording: %s\n", rec.getRecordingName());
                System.out.println(rec);

                int numberOfTracksInSquare = 0;
                for (Square square: rec.getSquares()) {
                    // calcSquare(square);
                    if (square.getTracks().size() != 0) {
                        numberOfTracksInSquare += square.getTracks().size();
                    }
                }

                numberOfTracksInProject += numberOfTracksInSquare;
                numberOfTracksInExperiment += numberOfTracksInSquare;

                calculateAverageTrackCountOfBackground(rec, 60);
            }
            System.out.printf("Number of tracks in experiment: %d\n", numberOfTracksInExperiment);
        }
        System.out.printf("\nNumber of tracks in project: %d\n", numberOfTracksInProject);

   }


    public static Project loadProject(Path projectPath, boolean matureProject) {
        List<Experiment> experiments = new ArrayList<>();
        Set<String> experimentsToProcess;

        // Read the context information from the Paint Configuration.json file
        Context context = loadContextFromJsonConfig(projectPath);

        // Read Paint Project Info.csv to determine which experiments to load.
        experimentsToProcess = readListOfExperimentsToProcess(projectPath);
        if (experimentsToProcess == null) {
            System.err.println("No '" + PROJECT_INFO_CSV + "' file found in project folder. ");
        } else {
            // Process all experiments that are listed in the Paint Project Info.csv file.
            for (String experimentName : experimentsToProcess) {
                Path expDir = projectPath.resolve(experimentName);
                if (!Files.isDirectory(expDir)) {
                    System.err.println("Warning: experiment folder not found: " + experimentName);
                    continue;
                }
                loadAndAddExperiment(experiments, projectPath, experimentName, context, matureProject);
            }
        }

        // Create and return the Project object
        return new Project(projectPath.getFileName().toString(), projectPath, context, experiments);
    }

    private static void loadAndAddExperiment(List<Experiment> experiments, Path projectPath,
                                             String experimentName, Context context, boolean matureProject) {

        Result result = null;
        try {
            result = loadExperiment(projectPath, experimentName, context, matureProject);
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }

        if (result != null && result.isSuccess() && result.experiment().isPresent()) {
            experiments.add(result.experiment().get());
        } else {
            System.err.println("Failed to load experiment: " + experimentName);
            if (result != null) {
                for (String err : result.errors()) {
                    System.err.println(err);
                }
            }
        }
    }



    /**
     * Load a single experiment; returns Result with Experiment or errors.
     */

    public static Experiment loadExperiment1(Path projectPath, String experimentName, Context context) throws Exception {
        Result result = loadExperiment(projectPath, experimentName, context, false);
        return result.isSuccess() ? result.experiment().orElse(null) : null;
    }

    public static Result loadExperiment(Path projectPath, String experimentName,
                                        Context context, boolean matureProject) throws Exception {

        Path experimentPath = projectPath.resolve(experimentName);

        // Validate if the expected files and directories are present in the experiment folder.
        List<String> errors = validateExperimentLayout(experimentPath, experimentName, matureProject);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        // Create the Experiment object so that it is available for populating
        Experiment experiment = new Experiment(experimentName);

        // Load recordings, but do not bother with their squares and tracks yet.
        List<Recording> recordings;
        try {
            RecordingTableIO recordingsTableIO  = new RecordingTableIO();
            Table recordingsTable = recordingsTableIO.readCsv(experimentPath.resolve(RECORDINGS_CSV));
            recordings = recordingsTableIO.toEntities(recordingsTable);
            for (Recording rec : recordings) {
                experiment.addRecording(rec);
            }
        } catch (Exception e) {
            errors.add("Failed to read '" + RECORDINGS_CSV + "': " + ExceptionUtils.friendlyMessage(e));
            return Result.failure(errors);
        }

        // Read the experiment 'All Squares' file
        SquareTableIO squareTableIO = new SquareTableIO();
        Table squaresTable = squareTableIO.readCsv(experimentPath.resolve(SQUARES_CSV));

        // Read the experiment 'All Tracks' file
        TrackTableIO trackTableIO = new TrackTableIO();
        Table tracksTable = trackTableIO.readCsv(experimentPath.resolve(TRACKS_CSV));

        // Assign the squares to each recording.
        for (Recording recording : recordings) {

            // Find the square records for this recording
            Table squaresOfRecording = squaresTable.where(
                    squaresTable.stringColumn(COL_RECORDING_NAME)
                            .matchesRegex("^" + recording.getRecordingName() + "(?:-threshold-\\d{1,3})?$"));

            // Create the Square objects for this recording and add the Square objects to the recording
            List<Square> squares = squareTableIO.toEntities(squaresOfRecording);
            recording.addSquares(squares);

            // Find the track records for this recording
            Table tracksOfRecording = tracksTable.where(
                    tracksTable.stringColumn(COL_RECORDING_NAME)
                            .matchesRegex("^" + recording.getRecordingName() + "(?:-threshold-\\d{1,3})?$"));

            // Create the Tracks objects for this recording and add the Tracks objects to the recording
            List<Track> tracks = trackTableIO.toEntities(tracksOfRecording);
            recording.setTracks(tracks);  // ToDo - be consistent, use the ame call as for squares above
            recording.setTracksTable(tracksOfRecording);

            // Assign the Tracks to specific squares in each recording
            int cumulativeNumberOfTracksInSquares = 0;
            int lastRowCol = context.getNumberOfSquaresInRow() - 1;

            for (Square square : recording.getSquares()) {

                Table squareTracksTable = filterTracksInSquare(tracksOfRecording, square, lastRowCol);
                tracks = trackTableIO.toEntities(squareTracksTable);
                square.setTracks(tracks);
            }
        }

        return errors.isEmpty() ? Result.success(experiment) : Result.failure(errors);
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
        if (!Files.isDirectory(experimentPath.resolve(DIR_TRACKMATE_IMAGES))) {
            errors.add("Directory '" + DIR_TRACKMATE_IMAGES + "' does not exist.");
        }
        if (!Files.isDirectory(experimentPath.resolve(DIR_BRIGHTFIELD_IMAGES))) {
            errors.add("Directory '" + DIR_BRIGHTFIELD_IMAGES + "' does not exist.");
        }

        // If the experiment is marked as 'Mature' there needs to be an 'All Squares' CSV file.
        if (matureProject && !Files.isRegularFile(experimentPath.resolve(SQUARES_CSV))) {
            errors.add("File '" + SQUARES_CSV + "' does not exist.");
        }

        return errors;
    }


    private static Set<String> readListOfExperimentsToProcess(Path projectPath) {
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
        if (v == null) {
            return false;
        }
        String s = v.trim();
        return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("t")
                || s.equalsIgnoreCase("yes") || s.equalsIgnoreCase("y")
                || s.equals("1");
    }

    /*
    Selexct the tracks that are within the square's bounding box.
     */

    public static Table filterTracksInSquare(Table tracks, Square square, int lastRowCol) {

        double x0 = square.getX0();
        double y0 = square.getY0();
        double x1 = square.getX1();
        double y1 = square.getY1();

        boolean isLastColumn = square.getColNumber() == lastRowCol;
        boolean isLastRow = square.getRowNumber() == lastRowCol;

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


    /*
    Load parameters that are kept constant for the whole project from the JSON file
     */

    private static Context loadContextFromJsonConfig(Path projectPath) {

        Context context = new Context();

        int numberOfSquaresInRecording = 400;
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
            numberOfSquaresInRecording = config.getInt("Generate Squares", "Nr of Squares in Recording", numberOfSquaresInRecording);
            minRequiredRSquared = config.getDouble("Generate Squares", "Min Required R Squared", minRequiredRSquared);
            maxAllowableVariability = config.getDouble("Generate Squares", "Max Allowable Variability", maxAllowableVariability);
            minRequiredDensityRatio = config.getDouble("Generate Squares", "Min Required Density Ratio", minRequiredDensityRatio);
            minTracksForTau = config.getInt("Generate Squares", "Min Tracks to Calculate Tau", minTracksForTau);
            maxFrameGap = config.getInt("TrackMate", "MAX_FRAME_GAP", maxFrameGap);
            gapClosingMaxDistance = config.getDouble("TrackMate", "GAP_CLOSING_MAX_DISTANCE", gapClosingMaxDistance);
            linkingMaxDistance = config.getDouble("TrackMate", "LINKING_MAX_DISTANCE", linkingMaxDistance);
            medianFiltering = config.getBoolean("TrackMate", "DO_MEDIAN_FILTERING", medianFiltering);
            minNumberOfSpotsInTrack = config.getInt("TrackMate", "MIN_NR_SPOTS_IN_TRACK", minNumberOfSpotsInTrack);
            neighbourMode = config.getString("Generate Squares", "NEIGHBOUR_MODE", neighbourMode);

        }
        catch (Exception e) {
            System.err.println("Failed to read context values from config file: " + e.getMessage());
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
            System.err.println("Failed to set context values: " + e.getMessage());
            System.exit(-1);
        }
        return context;
    }
}