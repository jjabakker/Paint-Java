package Paint.Loaders;

import Paint.Objects.PaintExperiment;
import Paint.Objects.PaintRecording;
import Paint.Objects.PaintSquare;
import Paint.Objects.PaintTrack;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsible for validating and loading a single experiment from disk.
 * Aggregates errors instead of throwing immediately, so callers can decide how to handle failures.
 */
public final class PaintExperimentLoader {

    public static final class Result {
        private final PaintExperiment experiment;
        private final List<String> errors;

        private Result(PaintExperiment experiment, List<String> errors) {
            this.experiment = experiment;
            this.errors = errors;
        }

        public static Result success(PaintExperiment experiment) {
            return new Result(experiment, Collections.emptyList());
        }

        public static Result failure(List<String> errors) {
            return new Result(null, new ArrayList<>(errors));
        }

        public Optional<PaintExperiment> experiment() {
            return Optional.ofNullable(experiment);
        }

        public List<String> errors() {
            return Collections.unmodifiableList(errors);
        }

        public boolean isSuccess() {
            return experiment != null && errors.isEmpty();
        }
    }

    // Filenames and directories used by an experiment
    private static final String RECORDINGS_CSV = "All Recordings.csv";
    private static final String TRACKS_CSV = "All Tracks.csv";
    private static final String SQUARES_CSV = "All Squares.csv";
    private static final String DIR_TRACKMATE_IMAGES = "TrackMate Images";
    private static final String DIR_BRIGHTFIELD_IMAGES = "Brightfield Images";

    private PaintExperimentLoader() {
        // utility
    }

    public static Result loadExperiment(Path experimentPath, String experimentName, boolean matureProject) {

        //  Create a list of experiment that appear to meet the most obvious requirements
        List<String> errors = validateExperimentLayout(experimentPath, experimentName, matureProject);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        // Create the domain object
        PaintExperiment experiment = new PaintExperiment();
        experiment.setExperimentName(experimentName);

        // Load recordings
        List<PaintRecording> recordings;
        try {
            recordings = loadRecordings(experimentPath);
        } catch (Exception e) {
            errors.add("Failed to read '" + RECORDINGS_CSV + "': " + extractFriendlyMessage(e));
            return Result.failure(errors);
        }

        // Load tracks once (per experiment)
        List<PaintTrack> tracks;
        try {
            Path tracksCsv = experimentPath.resolve(TRACKS_CSV);
            tracks = PaintTrackLoader.loadAllTracks(tracksCsv);
        } catch (Exception e) {
            errors.add("Failed to load tracks from '" + TRACKS_CSV + "': " + extractFriendlyMessage(e));
            return Result.failure(errors);
        }

        // Load squares once (per experiment), only if mature project
        List<PaintSquare> squares = Collections.emptyList();
        if (matureProject) {
            try {
                Path squaresCsv = experimentPath.resolve(SQUARES_CSV);
                if (Files.isRegularFile(squaresCsv)) {
                    squares = PaintSquareLoader.loadAllSquares(squaresCsv);
                } else {
                    // Should have been validated earlier; keep defensive fallback
                    errors.add("Expected '" + SQUARES_CSV + "' file was not found.");
                    return Result.failure(errors);
                }
            } catch (Exception e) {
                errors.add("Failed to load squares from '" + SQUARES_CSV + "': " + extractFriendlyMessage(e));
                return Result.failure(errors);
            }
        }

        // Attach shared data to each recording and add to experiment
        for (PaintRecording rec : recordings) {
            rec.setTracks(tracks);
            if (matureProject) {
                rec.setSquares(squares);
            }
            experiment.addRecording(rec);
        }

        return Result.success(experiment);
    }

    private static List<String> validateExperimentLayout(Path experimentPath, String experimentName, boolean matureProject) {
        List<String> errors = new ArrayList<>();

        if (!Files.isDirectory(experimentPath)) {
            errors.add("Experiment directory does not exist: " + experimentName);
            return errors;
        }

        Path recordingsPath = experimentPath.resolve(RECORDINGS_CSV);
        if (!Files.isRegularFile(recordingsPath)) {
            errors.add("File '" + RECORDINGS_CSV + "' does not exist.");
        }

        Path tracksPath = experimentPath.resolve(TRACKS_CSV);
        if (!Files.isRegularFile(tracksPath)) {
            errors.add("File '" + TRACKS_CSV + "' does not exist.");
        }

        if (matureProject) {
            Path squaresPath = experimentPath.resolve(SQUARES_CSV);
            if (!Files.isRegularFile(squaresPath)) {
                errors.add("File '" + SQUARES_CSV + "' does not exist.");
            }
        }

        Path trackMateImagesPath = experimentPath.resolve(DIR_TRACKMATE_IMAGES);
        if (!Files.isDirectory(trackMateImagesPath)) {
            errors.add("Directory '" + DIR_TRACKMATE_IMAGES + "' does not exist.");
        }

        Path brightfieldImagesPath = experimentPath.resolve(DIR_BRIGHTFIELD_IMAGES);
        if (!Files.isDirectory(brightfieldImagesPath)) {
            errors.add("Directory '" + DIR_BRIGHTFIELD_IMAGES + "' does not exist.");
        }

        return errors;
    }

    private static List<PaintRecording> loadRecordings(Path experimentPath) {
        Path filePath = experimentPath.resolve(RECORDINGS_CSV);

        // Read as all-strings to prevent type inference issues
        Table table;
        try {
            Table probe = Table.read().csv(filePath.toFile());
            ColumnType[] allString = new ColumnType[probe.columnCount()];
            Arrays.fill(allString, ColumnType.STRING);

            CsvReadOptions options = CsvReadOptions.builder(filePath.toFile())
                    .columnTypes(allString)
                    .build();

            table = Table.read().csv(options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (!table.columnNames().contains("Recording Name")) {
            throw new IllegalStateException("Column 'Recording Name' is missing in '" + RECORDINGS_CSV + "'.");
        }

        // Create a PaintRecording per row (or unique names if preferred)
        List<String> names = table.stringColumn("Recording Name")
                .asList()
                .stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (names.isEmpty()) {
            // Still create a single placeholder recording to reflect the experiment presence
            PaintRecording placeholder = new PaintRecording();
            placeholder.setRecordingName("Recording");
            return Collections.singletonList(placeholder);
        }

        List<PaintRecording> recordings = new ArrayList<>(names.size());
        for (String name : names) {
            PaintRecording rec = new PaintRecording();
            rec.setRecordingName(name);
            recordings.add(rec);
        }
        return recordings;
    }

    private static String extractFriendlyMessage(Exception e) {
        String m = e.toString();
        int colon = m.lastIndexOf(':');
        return (colon != -1) ? m.substring(colon + 1).trim() : m;
    }
}
