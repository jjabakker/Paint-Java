package paint.objects;

import paint.utilities.ColumnValue;

import java.util.ArrayList;
import java.util.List;

import static paint.constants.PaintConstants.*;

public class Square {

    // Attributes

    private String uniqueKey;
    private String recordingName;
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

    private List<Track> tracks = new ArrayList<>();


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

                    case "Square Number":
                        this.squareNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Row Number":
                        this.rowNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Column Number":
                        this.colNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Label Number":
                        this.labelNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Cell ID":
                        this.cellId = (int) Double.parseDouble(curValue);
                        break;
                    case "Number of Tracks":
                        this.numberOfTracks = (int) Double.parseDouble(curValue);
                        break;

                    // String values

                    case "Recording Name":
                        this.recordingName = curValue;
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
                    case "Mean Long Track Duration":
                        this.meanLongTrackDuration = Double.parseDouble(curValue);
                        break;
                    case "Median Short Track Duration":
                        this.medianShortTrackDuration = Double.parseDouble(curValue);
                        break;
                    case "Mean Short Track Duration":
                        this.meanShortTrackDuration = Double.parseDouble(curValue);
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
                        System.out.println("Warning: Unknown column in Square: " + cv.getColumnName());
                        break;

                }
            }
        } catch (Exception e) {
            System.err.printf("Error parsing column: %s. Conflicting value is %s.", curColumn, curValue);
            System.exit(-1);
        }
    }


    // --- Getters and Setters ---

    public String getUniqueKey() { return uniqueKey; }
    public void setUniqueKey(String uniqueKey) { this.uniqueKey = uniqueKey;}

    public String getRecordingName() { return recordingName; }
    public void setRecordingName(String recordingName) { this.recordingName = recordingName;}

    public int getLabelNumber() { return labelNumber; }
    public void setLabelNumber(int labelNumber) { this.labelNumber = labelNumber; }

    public int getSquareNumber() { return squareNumber; }
    public void setSquareNumber(int squareNumber) { this.squareNumber = squareNumber; }

    public int getRowNumber() { return rowNumber; }
    public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }

    public int getColNumber() { return colNumber; }
    public void setColNumber(int colNumber) { this.colNumber = colNumber; }

    public int getCellId() { return cellId; }
    public void setCellId(int cellId) { this.cellId = cellId; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    public boolean isSquareManuallyExcluded() { return squareManuallyExcluded; }
    public void setSquareManuallyExcluded(boolean squareManuallyExcluded) { this.squareManuallyExcluded = squareManuallyExcluded; }

    public boolean isImageExcluded() { return imageExcluded; }
    public void setImageExcluded(boolean imageExcluded) { this.imageExcluded = imageExcluded; }

    public double getX0() { return x0; }
    public void setX0(double x0) { this.x0 = x0; }

    public double getY0() { return y0; }
    public void setY0(double y0) { this.y0 = y0; }
  
    public double getX1() { return x1; }
    public void setX1(double x1) { this.x1 = x1; }

    public double getY1() { return y1; }
    public void setY1(double y1) { this.y1 = y1; }

    public int getNumberOfTracks() { return numberOfTracks; }
    public void setNumberOfTracks(int numberTracks) { this.numberOfTracks = numberTracks; }

    public double getVariability() { return variability; }
    public void setVariability(double variability) { this.variability = variability; }

    public double getDensity() { return density; }
    public void setDensity(double density) { this.density = density; }

    public double getDensityRatio() { return densityRatio; }
    public void setDensityRatio(double densityRatio) { this.densityRatio = densityRatio; }

    public double getTau() { return tau; }
    public void setTau(double tau) { this.tau = tau; }

    public double getRSquared() { return rSquared; }
    public void setRSquared(double rSquared) { this.rSquared = rSquared; }

    public double getMedianDiffusionCoefficient() { return medianDiffusionCoefficient; }   
    public void setMedianDiffusionCoefficient(double medianDiffusionCoefficient) { this.medianDiffusionCoefficient = medianDiffusionCoefficient; }

    public double getMeanDiffusionCoefficient() { return meanDiffusionCoefficient; }
    public void setMeanDiffusionCoefficient(double meanDiffusionCoefficient) { this.meanDiffusionCoefficient = meanDiffusionCoefficient; }

    public double getMedianDiffusionCoefficientExt() { return medianDiffusionCoefficientExt; }
    public void setMedianDiffusionCoefficientExt(double medianDiffusionCoefficientExt) { this.medianDiffusionCoefficientExt = medianDiffusionCoefficientExt; }

    public double getMeanDiffusionCoefficientExt() { return meanDiffusionCoefficientExt; }  
    public void setMeanDiffusionCoefficientExt(double meanDiffusionCoefficientExt) { this.meanDiffusionCoefficientExt = meanDiffusionCoefficientExt; }

    public double getMedianLongTrackDuration() { return medianLongTrackDuration; }
    public void setMedianLongTrackDuration(double medianLongTrackDuration) { this.medianLongTrackDuration = medianLongTrackDuration; }

    public double getMeanLongTrackDuration() { return meanLongTrackDuration; }
    public void setMeanLongTrackDuration(double meanLongTrackDuration) { this.meanLongTrackDuration = meanLongTrackDuration; }

    public double getMeanShortTrackDuration() { return meanShortTrackDuration; }
    public void setMeanShortTrackDuration(double meanShortTrackDuration) { this.meanShortTrackDuration = meanShortTrackDuration;  }

    public double getMedianShortTrackDuration() { return medianShortTrackDuration; }
    public void setMedianShortTrackDuration(double medianShortTrackDuration) { this.medianShortTrackDuration = medianShortTrackDuration;  }

    public double getTotalTrackDuration() { return totalTrackDuration; }
    public void setTotalTrackDuration(double totalTrackDuration) { this.totalTrackDuration = totalTrackDuration;  }

    public double getMedianTrackDuration() { return medianTrackDuration; }
    public void setMedianTrackDuration(double medianTrackDuration) { this.medianTrackDuration = medianTrackDuration;  }

    public double getMaxTrackDuration() { return maxTrackDuration; }
    public void setMaxTrackDuration(double maxTrackDuration) { this.maxTrackDuration = maxTrackDuration;  }

    public double getMedianMeanSpeed() { return medianMeanSpeed; }
    public void setMedianMeanSpeed(double medianMeanSpeed) { this.medianMeanSpeed = medianMeanSpeed;  }

    public double getMaxMeanSpeed() { return maxMeanSpeed; }
    public void setMaxMeanSpeed(double maxMeanSpeed) { this.maxMeanSpeed = maxMeanSpeed;  }

    public double getMedianMaxSpeed() { return medianMaxSpeed; }
    public void setMedianMaxSpeed(double medianMaxSpeed) { this.medianMaxSpeed = medianMaxSpeed;  }

    public double getMaxMaxSpeed() { return maxMaxSpeed; }
    public void setMaxMaxSpeed(double maxMaxSpeed) { this.maxMaxSpeed = maxMaxSpeed;  }

    public double getMedianDisplacement() { return medianDisplacement; }
    public void setMedianDisplacement(double medianDisplacement) { this.medianDisplacement = medianDisplacement;  }

    public double getMaxDisplacement() { return maxDisplacement; }
    public void setMaxDisplacement(double maxDisplacement) { this.maxDisplacement = maxDisplacement;  }

    public double getTotalDisplacement() { return totalDisplacement; }
    public void setTotalDisplacement(double totalDisplacement) { this.totalDisplacement = totalDisplacement;  }

    public List<Track> getTracks() { return tracks; }
    public void setTracks(List<Track> tracks) { this.tracks = tracks;  }

    public void addTrack(Track track) {
        this.tracks.add(track);
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();

        String recordingName = "Test";   //ToDo

        sb.append("\n\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("Square: ").append(recordingName).append("\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("\n");
        sb.append(String.format("Square data%n"));
        sb.append(String.format("\tSquare Number                    : %d%n", squareNumber));
        sb.append(String.format("\tRecording Name                   : %s%n", recordingName));
        sb.append(String.format("\tRow Number                       : %d%n", rowNumber));
        sb.append(String.format("\tColumn Number                    : %d%n", colNumber));
        sb.append(String.format("\tX0                               : %.2f%n", x0));
        sb.append(String.format("\tY0                               : %.2f%n", y0));
        sb.append(String.format("\tX1                               : %.2f%n", x1));
        sb.append(String.format("\tY1                               : %.2f%n", y1));
        sb.append(String.format("\tNumber of Tracks                 : %d%n", numberOfTracks));
        sb.append(String.format("\tVariability                      : %.2f%n", variability));
        sb.append(String.format("\tDensity                          : %.2f%n", density));
        sb.append(String.format("\tDensity Ratio                    : %.2f%n", densityRatio));
        sb.append(String.format("\tTau                              : %.2f%n", tau));
        sb.append(String.format("\tR Squared                        : %.2f%n", rSquared));
        sb.append(String.format("\tMedian Diffusion Coefficient     : %.2f%n", medianDiffusionCoefficient));
        sb.append(String.format("\tMean Diffusion Coefficient       : %.2f%n", meanDiffusionCoefficient));
        sb.append(String.format("\tMedian Diffusion Coefficient Ext : %.2f%n", medianDiffusionCoefficientExt));
        sb.append(String.format("\tMean Diffusion Coefficient Ext   : %.2f%n", meanDiffusionCoefficient));
        sb.append(String.format("\tMedian Long Track Duration       : %.2f%n", medianLongTrackDuration));
        sb.append(String.format("\tMean Long Track Duration         : %.2f%n", meanLongTrackDuration));
        sb.append(String.format("\tMedian Short Track Duration      : %.2f%n", medianShortTrackDuration));
        sb.append(String.format("\tMean Short Track Duration        : %.2f%n", meanShortTrackDuration));
        sb.append(String.format("\tMedian Displacement              : %.2f%n", medianDisplacement));
        sb.append(String.format("\tMax Displacement                 : %.2f%n", maxDisplacement));
        sb.append(String.format("\tTotal Displacement               : %.2f%n", totalDisplacement));
        sb.append(String.format("\tMedian Max Speed                 : %.2f%n", medianMaxSpeed));
        sb.append(String.format("\tMax Max Speed                    : %.2f%n", maxMaxSpeed));
        sb.append(String.format("\tMedian Mean Speed                : %.2f%n", medianMeanSpeed));
        sb.append(String.format("\tMax Mean Speed                   : %.2f%n", maxMeanSpeed));
        sb.append(String.format("\tMedian Max Speed                 : %.2f%n", medianMaxSpeed));
        sb.append(String.format("\tMax Track Duration               : %.2f%n", maxTrackDuration));
        sb.append(String.format("\tTotal Track Duration             : %.2f%n", totalTrackDuration));
        sb.append(String.format("\tMedian Track Duration            : %.2f%n", medianTrackDuration));
        return sb.toString();
    }


    private static double calcSquareAreaOriginal(int nrSquaresInRow)
    {
        double micrometer_per_pixel = 0.1602804;
        int pixel_per_image = 512;
        double micrometer_per_image_axis = micrometer_per_pixel * pixel_per_image;
        double micrometer_per_square_axis = micrometer_per_image_axis / nrSquaresInRow;
        return micrometer_per_square_axis * micrometer_per_square_axis;
    }

    /**
     * Calculates the area of a square by dividing the area of the image by the number of squares in the recording.
     * The area of a recording is currently hard coded and specified by IMAGE_WIDTH and IMAGE_HEIGHT.
     * @param  nrSquaresInRecording  The number of squares in the recording
     * @return The area of the square.
     */

    public static double calcSquareArea(int nrSquaresInRecording)
    {
        return IMAGE_WIDTH * IMAGE_HEIGHT / nrSquaresInRecording;
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