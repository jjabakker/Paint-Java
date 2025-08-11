package Paint.Loaders;

import static Paint.Constants.PaintConstants.*;

import Paint.Objects.*;
import PaintUtilities.ColumnValue;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;
import tech.tablesaw.api.Row;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import PaintUtilities.ExceptionUtils;
import static PaintUtilities.ExceptionUtils.friendlyMessage;
import Paint.Objects.TracksTable;
import Paint.Objects.SquaresTable;


/**
 * Responsible for validating and loading a single experiment from disk.
 * Aggregates errors instead of throwing immediately, so callers can decide how to handle failures.
 */
public final class ExperimentLoader {

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

    private ExperimentLoader() {
        // utility
    }

    public static Result loadExperiment(Path experimentPath, String experimentName, boolean matureProject) {

        // Validate structure
        List<String> errors = validateExperimentLayout(experimentPath, experimentName, matureProject);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        Experiment experiment = new Experiment();
        experiment.setExperimentName(experimentName);

        // Load recordings
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

        // Get Experiment Attributes from old style data
        experiment =  getExperimentAttributes(experiment, experimentPath, experimentName);

        // Read Tracks once
        TracksTable tracksTable = new TracksTable(experimentPath.resolve(TRACKS_CSV));
        experiment.setTracksTable(tracksTable);

        // Read Squares once
        SquaresTable squaresTable = new SquaresTable(experimentPath.resolve(SQUARES_CSV));
        experiment.setSquaresTable(squaresTable);


        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        return Result.success(experiment);
    }

    private static Table filterByRecording(Table table, String recordingName) {
        if (recordingName == null || recordingName.isEmpty()) {
            return table;
        }
        if (table.containsColumn(COL_EXT_RECORDING_NAME) && table.column(COL_EXT_RECORDING_NAME) instanceof StringColumn) {
            StringColumn col = table.stringColumn(COL_EXT_RECORDING_NAME);
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

    private static List<Recording> loadRecordings(Path experimentPath) {
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

        if (!table.columnNames().contains(COL_RECORDING_NAME)) {
            throw new IllegalStateException("Column '" + COL_RECORDING_NAME + "' is missing in '" + RECORDINGS_CSV + "'.");
        }

        // Build a PaintRecording per row, pulling values from the table
        List<Recording> recordings = new ArrayList<>(table.rowCount());
        for (Row row : table) {
            List<ColumnValue>  colValues = new ArrayList<>();
            ColumnValue colValuePair;

            for (int colIndex = 0; colIndex < table.columnCount(); colIndex++) {
                String columnName = table.column(colIndex).name();
                Object value = row.getObject(colIndex);
                colValues.add(new ColumnValue(columnName, (String) value));
                // System.out.println(columnName + " = " + value);
            }

            // Create a new PaintRecording with the values from the table passed as an List of ColumnValue objects
            Recording rec = new Recording(colValues);
            recordings.add(rec);
        }

        // If no rows, create a placeholder
        if (recordings.isEmpty()) {
            Recording placeholder = new Recording();
            placeholder.setRecordingName("Recording");
            return Collections.singletonList(placeholder);
        }

        return recordings;
    }

    private static Experiment getExperimentAttributes(Experiment experiment, Path experimentPath, String experimentName) {
        Table table;

        try {
            experimentPath = experimentPath.resolve(RECORDINGS_CSV);
            Table probe = Table.read().csv(experimentPath.toFile());
            ColumnType[] allString = new ColumnType[probe.columnCount()];
            Arrays.fill(allString, ColumnType.STRING);

            CsvReadOptions options = CsvReadOptions.builder(experimentPath.toFile())
                    .columnTypes(allString)
                    .build();

            table = Table.read().csv(options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Retrieve values from the file that are experiment rather than recording attributes
        String caseName = (String) getUniqueColumnValueOrExit(table, "Case");
        String maxFrameGap = (String) getUniqueColumnValueOrExit(table, "Max Frame Gap");
        String gapClosingMaxDistance = (String) getUniqueColumnValueOrExit(table, "Gap Closing Max Distance");
        String linkingMaxDistance = (String) getUniqueColumnValueOrExit(table, "Linking Max Distance");
        String medianFiltering = (String) getUniqueColumnValueOrExit(table, "Median Filtering");
        String minNumberOfSpotsInTrack = (String) getUniqueColumnValueOrExit(table, "Min Spots in Track");
        String minTracksForTau = (String) getUniqueColumnValueOrExit(table, "Min Tracks for Tau");
        String neighbourMode = (String) getUniqueColumnValueOrExit(table, "Neighbour Mode");

        // Update the Experiment toDo

        return experiment;
    }


    private static Object getUniqueColumnValueOrExit(Table table, String columnName) {
        Column<?> column = table.column(columnName);

        // If the column is empty
        if (column.isEmpty()) {
            System.err.println("Column '" + columnName + "' is empty.");
            System.exit(-1);
        }

        // Check if all values are identical
        if (column.unique().size() == 1) {
            return column.get(0); // return as Object (caller can cast)
        }

        // If values differ
        System.err.println("Not all rows have the same value in column: " + columnName);
        System.exit(-1);
        return null; // Unreachable, but needed for compiler
    }
}
