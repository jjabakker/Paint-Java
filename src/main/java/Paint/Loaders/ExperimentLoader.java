package Paint.Loaders;

import static Paint.Constants.PaintConstants.*;
import static PaintUtilities.CSVHandling.readCSV;

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




/**
 * Responsible for validating and loading a single experiment from disk.
 * Aggregates errors instead of throwing immediately, so callers can decide how to handle failures.
 */
public final class ExperimentLoader {

    /*
    Loads the complete experiment information and is called once for each experiment in a project.
     */

    public static Result loadExperiments(Path projectPath, String experimentName, boolean matureProject) {

        Path experimentPath = projectPath.resolve(experimentName);

        // Validate the structure
        List<String> errors = validateExperimentLayout(experimentPath, experimentName, matureProject);
        if (!errors.isEmpty()) {
            return Result.failure(errors);
        }

        // Crate the Experiment object so that it is available for populating
        Experiment experiment = new Experiment();
        experiment.setExperimentName(experimentName);

        // Get Experiment Attributes from old style data
        experiment =  getExperimentAttributes(experiment, experimentPath, experimentName);

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
        Table squaresTable = readCSV(experimentPath.resolve(SQUARES_CSV));
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
                ColumnValue colValuePair;

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
        Table tracksTable = readCSV(experimentPath.resolve(TRACKS_CSV));
        for (Recording recording : recordings) {
            String recordingName = recording.getRecordingName();
            String recordingNameColumn = COL_RECORDING_NAME;

            // The Code is a bit more complex because there is ambiguity in which column the name is to be found.
            // Later on when 'Ext Recording Name' is fully phased out, this code can be simplified.
            if (!tracksTable.containsColumn(COL_RECORDING_NAME) && tracksTable.containsColumn(COL_EXT_RECORDING_NAME)) {
                recordingNameColumn = COL_EXT_RECORDING_NAME;
            }
            else {
                System.err.println("No column named 'Recording Name' or 'Ext Recording Name' found in '" + TRACKS_CSV + "'.");
                System.exit(-1);
            }
            // End

            Table tracksOfRecording = tracksTable.where(
                    tracksTable.stringColumn(recordingNameColumn)
                            .matchesRegex("^" + recordingName + "(?:-threshold-\\d{1,3})?$")
            );


            recording.setTracksTable(tracksOfRecording);

            // Here the proper Track objects are added
            for (Row row : tracksOfRecording) {
                List<ColumnValue> colValues = new ArrayList<>();
                ColumnValue colValuePair;

                for (int colIndex = 0; colIndex < tracksOfRecording.columnCount(); colIndex++) {
                    String columnName = tracksOfRecording.column(colIndex).name();
                    Object value = row.getObject(colIndex);
                    colValues.add(new ColumnValue(columnName, (String) value));
                }

                Track track  = new Track(colValues);
                recording.addTrack(track);
            }


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
        if (table.containsColumn(COL_EXT_RECORDING_NAME) && table.column(COL_EXT_RECORDING_NAME) instanceof StringColumn) {
            StringColumn col = table.stringColumn(COL_EXT_RECORDING_NAME);
            return table.where(col.isEqualTo(recordingName));
        }
        return table.emptyCopy();
    }

    /*
     * Validates the structure of an experiment directory. Certain files and directories need to be present
     */
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
        String maxAllowableVariability = (String) getUniqueColumnValueOrExit(table, "Max Allowable Variability");
        String minRequiredDensityRatio = (String) getUniqueColumnValueOrExit(table, "Min Required Density Ratio");
        String minRequiredRSquared = (String) getUniqueColumnValueOrExit(table, "Min Required R Squared");

        experiment.setCaseName(caseName);
        experiment.setMaxFrameGap(maxFrameGap);
        experiment.setGapClosingMaxDistance(gapClosingMaxDistance);
        experiment.setLinkingMaxDistance(linkingMaxDistance);
        experiment.setMedianFiltering(medianFiltering);
        experiment.setMinNumberOfSpotsInTrack(minNumberOfSpotsInTrack);
        experiment.setMinTracksForTau(minTracksForTau);
        experiment.setNeighbourMode(neighbourMode);
        experiment.setMaxAllowableVariability(maxAllowableVariability);
        experiment.setMinRequiredDensityRatio(minRequiredDensityRatio);
        experiment.setMinRequiredRSquared(minRequiredRSquared);

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

    // This class is used for creating a new Experiment object.
    // If all works well, the Expriment object will be returned, otherwise errors will be reported.

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

}
