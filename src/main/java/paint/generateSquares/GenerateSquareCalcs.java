package paint.generateSquares;

import paint.calculations.CalculateTauResult;
import paint.io.TrackTableIO;
import paint.objects.*;
import paint.utilities.AppLogger;
import tech.tablesaw.api.Table;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static paint.calculations.CalculateTau.calculateTau;
import static paint.loaders.ProjectDataLoader.*;

public class GenerateSquareCalcs {

    public static void main(String[] args) {
        Path projectPath = Paths.get("/Users/hans/Paint Test Project");
        String experimentName = "221012";

        AppLogger.init("GenerateSquares.log");
        Project project = loadBareProject(projectPath);
        Context context = project.getContext();

        calculateSquaresForExperiment(project, experimentName, context);
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
            CalculateTauResult results = calculateTau(square.getTracks(), minTracksForTau,  minRequiredRSquared);
            if (results.getStatus() == CalculateTauResult.Status.TAU_SUCCESS) {
                square.setTau(results.getTau());
                square.setRSquared(results.getRSquared());
            }
            else {
                square.setTau(Double.NaN);
                square.setRSquared(Double.NaN);
            }
        }
    }
}
