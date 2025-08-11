package Paint.Objects;

import Table.Squares;

import java.util.ArrayList;
import java.util.List;

import static Paint.Constants.PaintConstants.*;

public class Square {


    // Attributes

    private String uniqueKey;

    private int squareNumber;
    private int rowNumber;
    private int colNumber;
    private int labelNumber;
    private int cellId;

    private boolean selected;
    private boolean squareManuallyExcluded;
    private boolean imageExcluded;

    private double x0;
    private double y0;
    private double x1;
    private double y1;

    private int numberTracks;
    private double variability;
    private double density;
    private double densityRatio;
    private double tau;
    private double rSquared;

    private double medianDiffusionCoefficient;
    private double meanDiffusionCoefficient;

    private double medianDiffusionCoefficientExt;
    private double meanDiffusionCoefficientExt;

    private double medianLongTrackDuration;
    private double medianShortTrackDuration;

    private double medianDisplacement;
    private double maxDisplacement;

    private double totalDisplacement;
    private double medianMaxSpeed;

    private double maxMaxSpeed;
    private double medianMeanSpeed;

    private double maxMeanSpeed;
    private double maxTrackDuration;

    private double totalTrackDuration;
    private double medianTrackDuration;

    private List<Squares> tracks;


    // Constructors

    public Square() {

    }

    public Square(int squareNumber, int numberOfSquaresInRecording) {

        int numberSquaresInRow = (int) Math.sqrt(numberOfSquaresInRecording);

        double width = IMAGE_WIDTH / numberSquaresInRow;
        double height = IMAGE_HEIGHT / numberSquaresInRow;

        colNumber = squareNumber % numberSquaresInRow;
        rowNumber = squareNumber / numberSquaresInRow;

        x0 = colNumber * width;
        x1 = (colNumber + 1) * width;
        y0 = rowNumber * height;
        y1 = (rowNumber + 1) * width;
        this.squareNumber = squareNumber;

    }

    public Square(String uniqueKey,
                  int recordingSequenceNr,
                  int conditionNumber,
                  int replicateNumber,
                  int squareNumber,
                  int rowNumber,
                  int colNumber,
                  int labelNumber,
                  int cellId,
                  int numberSpots,
                  int numberTracks,
                  double X0,
                  double Y0,
                  double X1,
                  double Y1,
                  boolean selected,
                  double variability,
                  double density,
                  double densityRatio,
                  double tau,
                  double rSquared,
                  double medianDiffusionCoefficient,
                  double meanDiffusionCoefficient,
                  double medianDiffusionCoefficientExt,
                  double meanDiffusionCoefficientExt,
                  double medianLongTrackDuration,
                  double medianShortTrackDuration,
                  double medianDisplacement,
                  double maxDisplacement,
                  double totalDisplacement,
                  double medianMaxSpeed,
                  double maxMaxSpeed,
                  double medianMeanSpeed,
                  double maxMeanSpeed,
                  double maxTrackDuration,
                  double totalTrackDuration,
                  double medianTrackDuration,
                  boolean squareManuallyExcluded,
                  boolean imageExcluded) {
        this.uniqueKey = uniqueKey;
//        this.recordingSequenceNr = recordingSequenceNr;
//        this.conditionNumber = conditionNumber;
//        thisis.replicateNumber = replicateNumber;
        this.squareNumber = squareNumber;
        this.rowNumber = rowNumber;
        this.colNumber = colNumber;
        this.labelNumber = labelNumber;
        this.cellId = cellId;
        // this.numberSpots = numberSpots;
        this.numberTracks = numberTracks;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
        this.selected = selected;
        this.variability = variability;
        this.density = density;
        this.densityRatio = densityRatio;
        this.tau = tau;
        this.rSquared = rSquared;
        this.medianDiffusionCoefficient = medianDiffusionCoefficient;
        this.meanDiffusionCoefficient = meanDiffusionCoefficient;
        this.medianDiffusionCoefficientExt = medianDiffusionCoefficientExt;
        this.meanDiffusionCoefficientExt = meanDiffusionCoefficientExt;
        this.medianLongTrackDuration = medianLongTrackDuration;
        this.medianShortTrackDuration = medianShortTrackDuration;
        this.medianDisplacement = medianDisplacement;
        this.maxDisplacement = maxDisplacement;
        this.totalDisplacement = totalDisplacement;
        this.medianMaxSpeed = medianMaxSpeed;
        this.maxMaxSpeed = maxMaxSpeed;
        this.medianMeanSpeed = medianMeanSpeed;
        this.maxMeanSpeed = maxMeanSpeed;
        this.maxTrackDuration = maxTrackDuration;
        this.totalTrackDuration = totalTrackDuration;
        this.medianTrackDuration = medianTrackDuration;
        this.squareManuallyExcluded = squareManuallyExcluded;
        this.imageExcluded = imageExcluded;
    }

    private static double calcSquareAreaOriginal(int nrSquaresInRow)
    {
        double micrometer_per_pixel = 0.1602804;
        int pixel_per_image = 512;
        double micrometer_per_image_axis = micrometer_per_pixel * pixel_per_image;
        double micrometer_per_square_axis = micrometer_per_image_axis / nrSquaresInRow;
        double area = micrometer_per_square_axis * micrometer_per_square_axis;
        return area;
    }

    /**
     * Calculates the area of a square by dividing the area of the image by the number of squares in the recording.
     * The area of a recording is currently hard coded and specified by IMAGE_WIDTH and IMAGE_HEIGHT.
     * @param  nrSquaresInRecording  The number of squares in the recording
     * @return The area of the square.
     */

    public static double calcSquareArea(int nrSquaresInRecording)
    {
        double area = IMAGE_WIDTH * IMAGE_HEIGHT / nrSquaresInRecording;
        return area;
    }

    public static void main(String[] args) {
        List<Square> squares = new ArrayList<>();
        for (int i = 0; i < 21; i++) {
            Square square = new Square(i, 100);
            squares.add(square);
        }
        System.out.println(squares);

        double areaOriginal  = calcSquareAreaOriginal(20);
        double areaNew = calcSquareArea(400);
        double difference = areaNew - areaOriginal;
        double percentualDifference = (areaNew - areaOriginal)/areaOriginal * 100;
        System.out.println("Area original: " + areaOriginal);
        System.out.println("Area new: " + areaNew);
        System.out.println(String.format("Difference: %.6f", difference));
        System.out.println(String.format("Percentual difference: %.4f%%", percentualDifference));
    }


    public boolean assignTracksToSquare(int squareNumber) {
        return false;
    }



    // getters and setters here ...
}