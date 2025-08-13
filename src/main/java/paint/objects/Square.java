package paint.objects;

import paintUtilities.ColumnValue;
import table.Squares;

import java.util.ArrayList;
import java.util.List;

import static paint.constants.PaintConstants.*;

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
    private double meanLongTrackDuration;

    private double medianShortTrackDuration;
    private double meanShortTrackDuration;

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

    public Square(List<ColumnValue> columns) {

        String curColumn = "";
        String curValue = "";
        try {
            for (ColumnValue cv : columns) {
                curColumn = cv.getColumnName();
                curValue  = cv.getValue().toString();
                if (curValue.isEmpty()) {
                    continue;
                }
                switch (curColumn) {

                    // Integer values

                    case "Square Nr":
                        this.squareNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Row Nr":
                        this.rowNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Col Nr":
                        this.colNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Label Nr":
                        this.labelNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Cell Id":
                        this.cellId = (int) Double.parseDouble(curValue);
                        break;
                    case "Nr Tracks":
                        this.numberTracks = (int) Double.parseDouble(curValue);
                        break;

                    // Double values

                    case "X0":
                        this.x0 = Double.parseDouble(curValue);
                        break;
                    case "X1":
                        this.x1 = Double.parseDouble(curValue);
                        break;
                    case "Y0":
                        this.y0 = Double.parseDouble(curValue);
                        break;
                    case "Y1":
                        this.y1 = Double.parseDouble(curValue);
                        break;
                    case "Variability":
                        this.variability = Double.parseDouble(curValue);
                        break;
                    case "Density":
                        this.density = Double.parseDouble(curValue);
                        break;
                    case "Density Ratio":
                        this.densityRatio = Double.parseDouble(curValue);
                        break;
                    case "Tau":
                        this.tau = Double.parseDouble(curValue);
                        break;
                    case "R Squared":
                        this.rSquared = Double.parseDouble(curValue);
                        break;
                    case "Median Diffusion Coefficient":
                        this.medianDiffusionCoefficient = Double.parseDouble(curValue);
                        break;
                    case "Mean Diffusion Coefficient":
                        this.meanDiffusionCoefficient = Double.parseDouble(curValue);
                        break;
                    case "Median Diffusion Coefficient Ext":
                        this.medianDiffusionCoefficientExt = Double.parseDouble(curValue);
                        break;
                    case "Mean Diffusion Coefficient Ext":
                        this.meanDiffusionCoefficientExt = Double.parseDouble(curValue);
                        break;
                    case "Median Long Track Duration":
                        this.medianLongTrackDuration = Double.parseDouble(curValue);
                        break;
                    case "Median Short Track Duration":
                        this.medianShortTrackDuration = Double.parseDouble(curValue);
                        break;
                    case "Median Displacement":
                        this.medianDisplacement = Double.parseDouble(curValue);
                        break;
                    case "Max Displacement":
                        this.maxDisplacement = Double.parseDouble(curValue);
                        break;
                    case "Total Displacement":
                        this.totalDisplacement = Double.parseDouble(curValue);
                        break;
                    case "Median Max Speed":
                        this.medianMaxSpeed = Double.parseDouble(curValue);
                        break;
                    case "Max Max Speed":
                        this.maxMaxSpeed = Double.parseDouble(curValue);
                        break;
                    case "Median Mean Speed":
                        this.medianMeanSpeed = Double.parseDouble(curValue);
                        break;
                    case "Max Mean Speed":
                        this.maxMeanSpeed = Double.parseDouble(curValue);
                        break;
                    case "Max Track Duration":
                        this.maxTrackDuration = Double.parseDouble(curValue);
                        break;
                    case "Total Track Duration":
                        this.totalTrackDuration = Double.parseDouble(curValue);
                        break;
                    case "Median Track Duration":
                        this.medianTrackDuration = Double.parseDouble(curValue);
                        break;

                    case "Threshold":
                    case "Recording Sequence Nr":
                    case "Condition Nr":
                    case "Replicate Nr":
                    case "Cell Type":
                    case "Probe":
                    case "Probe Type":
                    case "Adjuvant":
                    case "Concentration":
                    case "Recording Data":
                    case "Nr Spots":
                    case "Experiment Date":
                    case "Experiment Name":
                    case "Ext Recording Name":
                    case "Unique Key":
                    case "Selected":
                    case "Square Manually Excluded":
                    case "Image Excluded":
                        break;

                    default:
                        System.out.println("Warning: Unknown column " + cv.getColumnName());
                        break;

                }
            }
        } catch (Exception e) {
            System.err.printf("Error parsing column: %s. Conflicting value is %s.", curColumn, curValue);
            System.exit(-1);
        }
    }


    // --- Getters and Setters ---

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }

    public int getSquareNumber() {
        return squareNumber;
    }

    public void setSquareNumber(int squareNumber) {
        this.squareNumber = squareNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getColNumber() {
        return colNumber;
    }

    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }

    public int getLabelNumber() {
        return labelNumber;
    }

    public void setLabelNumber(int labelNumber) {
        this.labelNumber = labelNumber;
    }

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {
        this.cellId = cellId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSquareManuallyExcluded() {
        return squareManuallyExcluded;
    }

    public void setSquareManuallyExcluded(boolean squareManuallyExcluded) {
        this.squareManuallyExcluded = squareManuallyExcluded;
    }

    public boolean isImageExcluded() {
        return imageExcluded;
    }

    public void setImageExcluded(boolean imageExcluded) {
        this.imageExcluded = imageExcluded;
    }

    public double getX0() {
        return x0;
    }

    public void setX0(double x0) {
        this.x0 = x0;
    }

    public double getY0() {
        return y0;
    }

    public void setY0(double y0) {
        this.y0 = y0;
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public int getNumberTracks() {
        return numberTracks;
    }

    public void setNumberTracks(int numberTracks) {
        this.numberTracks = numberTracks;
    }

    public double getVariability() {
        return variability;
    }

    public void setVariability(double variability) {
        this.variability = variability;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public double getDensityRatio() {
        return densityRatio;
    }

    public void setDensityRatio(double densityRatio) {
        this.densityRatio = densityRatio;
    }

    public double getTau() {
        return tau;
    }

    public void setTau(double tau) {
        this.tau = tau;
    }

    public double getRSquared() {
        return rSquared;
    }

    public void setRSquared(double rSquared) {
        this.rSquared = rSquared;
    }

    public double getMedianDiffusionCoefficient() {
        return medianDiffusionCoefficient;
    }

    public void setMedianDiffusionCoefficient(double medianDiffusionCoefficient) {
        this.medianDiffusionCoefficient = medianDiffusionCoefficient;
    }

    public double getMeanDiffusionCoefficient() {
        return meanDiffusionCoefficient;
    }

    public void setMeanDiffusionCoefficient(double meanDiffusionCoefficient) {
        this.meanDiffusionCoefficient = meanDiffusionCoefficient;
    }

    public double getMedianDiffusionCoefficientExt() {
        return medianDiffusionCoefficientExt;
    }

    public void setMedianDiffusionCoefficientExt(double medianDiffusionCoefficientExt) {
        this.medianDiffusionCoefficientExt = medianDiffusionCoefficientExt;
    }

    public double getMeanDiffusionCoefficientExt() {
        return meanDiffusionCoefficientExt;
    }

    public void setMeanDiffusionCoefficientExt(double meanDiffusionCoefficientExt) {
        this.meanDiffusionCoefficientExt = meanDiffusionCoefficientExt;
    }

    public double getMedianLongTrackDuration() {
        return medianLongTrackDuration;
    }

    public void setMedianLongTrackDiffusionCoefficient(double medianLongTrackDuration) {
        this.medianLongTrackDuration = medianLongTrackDuration;
    }

    public double getMeanLongTrackDuration() {
        return meanLongTrackDuration;
    }

    public void setMeanLongTrackDiffusionCoefficient(double meanLongTrackDuration) {
        this.meanLongTrackDuration = meanLongTrackDuration;
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
        System.out.printf("Difference: %.6f%n", difference);
        System.out.printf("Percentual difference: %.4f%%%n", percentualDifference);
    }
}