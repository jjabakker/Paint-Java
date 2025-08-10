package Paint.Loaders;

import Paint.Constants.PaintConstants;
import Paint.Objects.PaintExperiment;
import Paint.Objects.PaintRecording;
import Paint.Objects.PaintSquare;
import Paint.Objects.PaintTrack;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
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

    private PaintExperimentLoader() {
        // utility
    }

    public static Result loadExperiment(Path experimentPath, String experimentName, boolean matureProject) {

        // Validate structure
        List<String> errors = validateExperimentLayout(experimentPath, experimentName, matureProject);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        PaintExperiment experiment = new PaintExperiment();
        experiment.setExperimentName(experimentName);

        // Load recordings
        List<PaintRecording> recordings;
        try {
            recordings = loadRecordings(experimentPath);
        } catch (Exception e) {
            errors.add("Failed to read '" + PaintConstants.RECORDINGS_CSV + "': " + extractFriendlyMessage(e));
            return Result.failure(errors);
        }

        // Read once
        Table tracksTable;
        try {
            tracksTable = Table.read().csv(experimentPath.resolve(PaintConstants.TRACKS_CSV).toFile());
        } catch (Exception e) {
            errors.add("Failed to load tracks from '" + PaintConstants.TRACKS_CSV + "': " + extractFriendlyMessage(e));
            return Result.failure(errors);
        }

        Table squaresTable = null;
        if (matureProject) {
            Path squaresCsv = experimentPath.resolve(PaintConstants.SQUARES_CSV);
            if (!Files.isRegularFile(squaresCsv)) {
                errors.add("Expected '" + PaintConstants.SQUARES_CSV + "' file was not found.");
                return Result.failure(errors);
            }
            try {
                squaresTable = Table.read().csv(squaresCsv.toFile());
            } catch (Exception e) {
                errors.add("Failed to load squares from '" + PaintConstants.SQUARES_CSV + "': " + extractFriendlyMessage(e));
                return Result.failure(errors);
            }
        }

        // Filter and delegate parsing to loaders
        for (PaintRecording rec : recordings) {
            String recordingName = rec.getRecordingName();

            try {
                Table filteredTracks = filterByRecording(tracksTable, recordingName);
                List<PaintTrack> tracksForRecording = PaintTrackLoader.fromTable(filteredTracks);
                rec.setTracks(tracksForRecording);
            } catch (Exception e) {
                errors.add("Failed to build tracks for recording '" + recordingName + "': " + extractFriendlyMessage(e));
            }

            if (matureProject && squaresTable != null) {
                try {
                    Table filteredSquares = filterByRecording(squaresTable, recordingName);
                    List<PaintSquare> squaresForRecording = PaintSquareLoader.fromTable(filteredSquares);
                    rec.setSquares(squaresForRecording);
                } catch (Exception e) {
                    errors.add("Failed to build squares for recording '" + recordingName + "': " + extractFriendlyMessage(e));
                }
            }

            experiment.addRecording(rec);
        }

        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        return Result.success(experiment);
    }

    private static Table filterByRecording(Table table, String recordingName) {
        if (recordingName == null || recordingName.isEmpty()) {
            return table;
        }
        if (table.containsColumn(PaintConstants.COL_EXT_RECORDING_NAME) && table.column(PaintConstants.COL_EXT_RECORDING_NAME) instanceof StringColumn) {
            StringColumn col = table.stringColumn(PaintConstants.COL_EXT_RECORDING_NAME);
            return table.where(col.isEqualTo(recordingName));
        }
        return table.emptyCopy();
    }

    private static List<String> validateExperimentLayout(Path experimentPath, String experimentName, boolean matureProject) {
        List<String> errors = new ArrayList<>();

        if (!Files.isDirectory(experimentPath)) {
            errors.add("Experiment directory does not exist: " + experimentName);
            return errors;
        }

        Path recordingsPath = experimentPath.resolve(PaintConstants.RECORDINGS_CSV);
        if (!Files.isRegularFile(recordingsPath)) {
            errors.add("File '" + PaintConstants.RECORDINGS_CSV + "' does not exist.");
        }

        Path tracksPath = experimentPath.resolve(PaintConstants.TRACKS_CSV);
        if (!Files.isRegularFile(tracksPath)) {
            errors.add("File '" + PaintConstants.TRACKS_CSV + "' does not exist.");
        }

        if (matureProject) {
            Path squaresPath = experimentPath.resolve(PaintConstants.SQUARES_CSV);
            if (!Files.isRegularFile(squaresPath)) {
                errors.add("File '" + PaintConstants.SQUARES_CSV + "' does not exist.");
            }
        }

        Path trackMateImagesPath = experimentPath.resolve(PaintConstants.DIR_TRACKMATE_IMAGES);
        if (!Files.isDirectory(trackMateImagesPath)) {
            errors.add("Directory '" + PaintConstants.DIR_TRACKMATE_IMAGES + "' does not exist.");
        }

        Path brightfieldImagesPath = experimentPath.resolve(PaintConstants.DIR_BRIGHTFIELD_IMAGES);
        if (!Files.isDirectory(brightfieldImagesPath)) {
            errors.add("Directory '" + PaintConstants.DIR_BRIGHTFIELD_IMAGES + "' does not exist.");
        }

        return errors;
    }

    private static List<PaintRecording> loadRecordings(Path experimentPath) {
        Path filePath = experimentPath.resolve(PaintConstants.RECORDINGS_CSV);

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

        if (!table.columnNames().contains(PaintConstants.COL_RECORDING_NAME)) {
            throw new IllegalStateException("Column '" + PaintConstants.COL_RECORDING_NAME + "' is missing in '" + PaintConstants.RECORDINGS_CSV + "'.");
        }

        List<String> names = table.stringColumn(PaintConstants.COL_RECORDING_NAME)
                .asList()
                .stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (names.isEmpty()) {
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
