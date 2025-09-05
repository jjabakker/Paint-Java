package paint.generateSquares;

import paint.calculations.CalculateTauResult;
import paint.io.RecordingTableIO;
import paint.io.SquareTableIO;
import paint.io.TrackTableIO;
import paint.objects.*;
import paint.utilities.AppLogger;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static paint.calculations.CalculateTau.calculateTau;
import static paint.loaders.ProjectDataLoader.*;

public class GenerateSquareCalcs {

    public static void main(String[] args) {
        Path projectPath = Paths.get("/Users/hans/Paint Test Project");
        String experimentName = "221012";
        Table table = null;

        AppLogger.init("GenerateSquares.log");
        Project project = loadBareProject(projectPath);
        Context context = project.getContext();

        calculateSquaresForExperiment(project, experimentName, context);

        RecordingTableIO recordingsTableIO = new RecordingTableIO();
        table = recordingsTableIO.toTable(project.getExperiment(experimentName).getRecordings());
        try {
            recordingsTableIO.writeCsv(table, "/Users/hans/Downloads/Recordings-221022.csv");
        }
        catch ( Exception e) {
        }

        SquareTableIO squaresTableIO = new SquareTableIO();
        Table allSquaresTable = squaresTableIO.emptyTable();

        List<Recording> recordings = project.getExperiment(experimentName).getRecordings();
        for (Recording recording : recordings) {
            table = squaresTableIO.toTable(recording.getSquares());
            squaresTableIO.appendInPlace(allSquaresTable, table);

        }
        try {
            squaresTableIO.writeCsv(allSquaresTable, "/Users/hans/Downloads/Squares-221022.csv");
        } catch (Exception e) {
            AppLogger.error(e.getMessage());
        }
    }

    static boolean calculateSquaresForExperiment(Project project, String experimentName, Context context) {

        Experiment experiment = loadExperimentForSquaresCalc(project.getProjectPath(), experimentName);
        if (experiment != null) {
            AppLogger.infof("Experiment loaded: %s", experimentName);

            for (Recording recording : experiment.getRecordings()) {
                AppLogger.infof("Recording loaded: %s", recording.getRecordingName());
                AppLogger.info(recording.toString());

                // Create the squares with basic geometric information
                List<Square> squares = generateSquaresForRecording(context, recording);
                recording.setSquares(squares);

                // Assign the recording tracks to the squares
                assignTracksToSquares(recording, context);

                // Calculate recording attributes
                calculateRecordingAttributes(recording, context);

                // Calculate squares attributes
                calculateSquareAttributes(recording, context);
                int i = 1;
            }
            project.addExperiment(experiment);
        }
        else {
            AppLogger.errorf("Failed to load experiment: %s", experimentName);
        }
       return false;
    }


    public static List<Square> generateSquaresForRecording(Context context, Recording recording) {

        double imageWidth = context.getImageWidth();
        double imageHeight = context.getImageHeight();
        int n = context.getNumberOfSquaresInRow();

        List<Square> squares = new ArrayList<>();
        double squareWidth  = imageWidth / n;
        double squareHeight = imageHeight / n;

        int squareNumber = 0;
        for (int rowNumber = 0; rowNumber < n; rowNumber++) {
            for (int columnNumber = 0; columnNumber < n; columnNumber++) {
                double X0 = columnNumber * squareWidth;
                double Y0 = rowNumber * squareHeight;
                double X1 = (columnNumber + 1) * squareWidth;
                double Y1 = (rowNumber + 1) * squareHeight;

                squares.add(new Square(
                        recording.getRecordingName() + '-' + squareNumber,
                        recording.getRecordingName(),
                        squareNumber,
                        rowNumber,
                        columnNumber,
                        X0,
                        Y0,
                        X1,
                        Y1));

                squareNumber += 1;
            }
        }

        return squares;
    }


    public static void assignTracksToSquares(Recording recording, Context context) {

        Table tracksOfRecording = recording.getTracksTable();
        TrackTableIO trackTableIO = new TrackTableIO();

        int lastRowCol = context.getNumberOfSquaresInRow() - 1;

        for (Square square : recording.getSquares()) {
            Table squareTracksTable = filterTracksInSquare(tracksOfRecording, square, lastRowCol);
            List<Track> tracks = trackTableIO.toEntities(squareTracksTable);
            square.setTracks(tracks);
            square.setTracksTable(squareTracksTable);
            square.setNumberOfTracks(tracks.size());
        }
    }

    public static void calculateRecordingAttributes(Recording recording, Context context) {

        double minRequiredRSquared = context.getMinRequiredRSquared();
        int minTracksForTau = context.getMinTracksForTau();
        CalculateTauResult results = calculateTau(recording.getTracks(), minTracksForTau,  minRequiredRSquared);
        if (results.getStatus() == CalculateTauResult.Status.TAU_SUCCESS) {
            recording.setTau(results.getTau());
            recording.setRSquared(results.getRSquared());
        }
        else {
            recording.setTau(Double.NaN);
            recording.setRSquared(Double.NaN);
        }
    }

    public static void calculateSquareAttributes(Recording recording, Context context) {

        double minRequiredRSquared = context.getMinRequiredRSquared();
        int minTracksForTau = context.getMinTracksForTau();

        for (Square square : recording.getSquares()) {

            List<Track> tracksInSquare = square.getTracks();
            Table tracksInSquareTable = square.getTracksTable();

            if (tracksInSquare == null || tracksInSquare.size() == 0) {
                continue;
            }

            // Calculate Tau
            CalculateTauResult results = calculateTau(tracksInSquare, minTracksForTau,  minRequiredRSquared);
            if (results.getStatus() == CalculateTauResult.Status.TAU_SUCCESS) {
                square.setTau(results.getTau());
                square.setRSquared(results.getRSquared());
            }
            else {
                square.setTau(Double.NaN);
                square.setRSquared(Double.NaN);
            }

            square.setMedianDiffusionCoefficient(tracksInSquareTable.doubleColumn("Diffusion Coefficient").median());
            square.setMedianDiffusionCoefficientExt(tracksInSquareTable.doubleColumn("Diffusion Coefficient Ext").median());

            square.setMedianLongTrackDuration(calculateMedianLongTrack(tracksInSquareTable, 0.1));
            square.setMedianShortTrackDuration(calculateMedianShortTrack(tracksInSquareTable, 0.1));

            square.setMedianDisplacement(tracksInSquareTable.doubleColumn("Track Displacement").mean());
            square.setMaxDisplacement(tracksInSquareTable.doubleColumn("Track Displacement").max());
            square.setTotalDisplacement(tracksInSquareTable.doubleColumn("Track Displacement").sum());

            square.setMedianMaxSpeed(tracksInSquareTable.doubleColumn("Track Max Speed").median());
            square.setMaxMaxSpeed(tracksInSquareTable.doubleColumn("Track Max Speed").max());

            square.setMedianMeanSpeed(tracksInSquareTable.doubleColumn("Track Mean Speed").median());
            square.setMaxMeanSpeed(tracksInSquareTable.doubleColumn("Track Mean Speed").max());

            square.setMaxTrackDuration(tracksInSquareTable.doubleColumn("Track Duration").max());
            square.setTotalTrackDuration(tracksInSquareTable.doubleColumn("Track Duration").sum());
            square.setMedianTrackDuration(tracksInSquareTable.doubleColumn("Track Duration").median());


            int i =1;
        }
        int i = 1;
    }

    public static double calculateMedianLongTrack(Table tracks, double fraction) {
        int nrOfTracks = tracks.rowCount();
        if (nrOfTracks == 0) {
            return 0.0;
        }

        Table sorted = tracks.sortAscendingOn("Track Duration");
        int nrTracksToAverage = Math.max((int) Math.round(fraction * nrOfTracks), 1);

        // Get the last nrTracksToAverage durations
        DoubleColumn durations = sorted.doubleColumn("Track Duration");
        List<Double> tail = durations.asList()
                .subList(nrOfTracks - nrTracksToAverage, nrOfTracks);

        return median(tail);
    }

    public static double calculateMedianShortTrack(Table tracks, double fraction) {
        int nrOfTracks = tracks.rowCount();
        if (nrOfTracks == 0) {
            return 0.0;
        }

        Table sorted = tracks.sortAscendingOn("Track Duration");
        int nrTracksToAverage = Math.max((int) Math.round(fraction * nrOfTracks), 1);

        // Get the first nrTracksToAverage durations
        DoubleColumn durations = sorted.doubleColumn("Track Duration");
        List<Double> head = durations.asList().subList(0, nrTracksToAverage);

        return median(head);
    }

    // Utility function to calculate the median of a list of doubles
    private static double median(List<Double> values) {
        values.sort(Comparator.naturalOrder());
        int size = values.size();
        if (size == 0) {
            return 0.0;
        }
        if (size % 2 == 1) {
            return values.get(size / 2);
        } else {
            return (values.get(size / 2 - 1) + values.get(size / 2)) / 2.0;
        }
    }

}
