package paint.objects;

import paint.utilities.ColumnValue;
import java.util.List;

public class Track {
    private String uniqueKey;                 // 0
    private String recordingName;             // 1
    private int trackId;                      // 2
    private String trackLabel;                // 3
    private int numberSpots;                  // 4
    private int numberGaps;                   // 5
    private int longestGap;                   // 6
    private double trackDuration;             // 7
    private double trackXLocation;            // 8
    private double trackYLocation;            // 9
    private double trackDisplacement;         // 10
    private double trackMaxSpeed;             // 11
    private double trackMedianSpeed;          // 12
    private double trackMeanSpeed;            // 13
    private double trackMaxSpeedCalc;         // 14
    private double trackMedianSpeedCalc;      // 15
    private double trackMeanSpeedCalc;        // 16
    private double diffusionCoefficient;      // 17
    private double diffusionCoefficientExt;   // 18
    private double totalDistance;             // 19
    private double confinementRatio;          // 20`
    private int squareNumber;                 // 21
    private int labelNumber;                  // 22

    public Track() { }

    public Track(String uniqueKey, String recordingName, int trackId, String trackLabel, int nrSpots, int nrGaps, int longestGap, double trackDuration,
                 double trackXLocation, double trackYLocation, double trackDisplacement, double trackMaxSpeed,
                 double trackMedianSpeed, double trackMeanSpeed, double trackMaxSpeedCalc, double trackMedianSpeedCalc,
                 double trackMeanSpeedCalc, double diffusionCoefficient, double diffusionCoefficientExt,
                 double totalDistance, double confinementRatio, int squareNumber, int labelNumber) {
        this.uniqueKey = uniqueKey;
        this.recordingName = recordingName;
        this.trackId = trackId;
        this.trackLabel = trackLabel;
        this.numberSpots = nrSpots;
        this.numberGaps = nrGaps;
        this.longestGap = longestGap;
        this.trackDuration = trackDuration;
        this.trackXLocation = trackXLocation;
        this.trackYLocation = trackYLocation;
        this.trackDisplacement = trackDisplacement;
        this.trackMaxSpeed = trackMaxSpeed;
        this.trackMedianSpeed = trackMedianSpeed;
        this.trackMeanSpeed = trackMeanSpeed;
        this.trackMaxSpeedCalc = trackMaxSpeedCalc;
        this.trackMedianSpeedCalc = trackMedianSpeedCalc;
        this.trackMeanSpeedCalc = trackMeanSpeedCalc;
        this.diffusionCoefficient = diffusionCoefficient;
        this.diffusionCoefficientExt = diffusionCoefficientExt;
        this.totalDistance = totalDistance;
        this.confinementRatio = confinementRatio;
        this.squareNumber = squareNumber;
        this.labelNumber = labelNumber;
    }


    public Track(List<ColumnValue> columns) {

        String curColumn = "";
        String curValue = "";
        try {
            for (ColumnValue cv : columns) {
                curColumn = cv.getColumnName();
                curValue = cv.getValue().toString();
                if (curValue.isEmpty() || curValue.equals("NaN")) {
                    continue;
                }
                switch (curColumn) {

                    // Integer values

                    case "Track Id":
                        this.trackId = (int) Double.parseDouble(curValue);
                        break;
                    case "Number of Gaps":
                        this.numberGaps = (int) Double.parseDouble(curValue);
                        break;
                    case "Number of Spots":
                        this.numberSpots = (int) Double.parseDouble(curValue);
                        break;
                    case "Longest Gap":
                        this.longestGap = (int) Double.parseDouble(curValue);
                        break;
                    case "Square Number":
                        this.squareNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Label Number":
                        this.labelNumber = (int) Double.parseDouble(curValue);
                        break;

                    // String values
                    case "Track Label":
                        this.trackLabel = curValue;
                        break;

                    // Double values
                    case "Track Duration":
                        this.trackDuration = Double.parseDouble(curValue);
                        break;
                    case "Track X Location":
                        this.trackXLocation = Double.parseDouble(curValue);
                        break;
                    case "Track Y Location":
                        this.trackYLocation = Double.parseDouble(curValue);
                        break;
                    case "Track Displacement":
                        this.trackDisplacement = Double.parseDouble(curValue);
                        break;
                    case "Track Max Speed":
                        this.trackMaxSpeed = Double.parseDouble(curValue);
                        break;
                    case "Track Median Speed":
                        this.trackMedianSpeed = Double.parseDouble(curValue);
                        break;
                    case "Track Mean Speed":
                        this.trackMeanSpeed = Double.parseDouble(curValue);
                        break;
                    case "Track Max Speed Calc":
                        this.trackMaxSpeedCalc = Double.parseDouble(curValue);
                        break;
                    case "Track Median Speed Calc":
                        this.trackMedianSpeedCalc = Double.parseDouble(curValue);
                        break;
                    case "Track Mean Speed Calc":
                        this.trackMeanSpeedCalc = Double.parseDouble(curValue);
                        break;
                    case "Diffusion Coefficient":
                        this.diffusionCoefficient = Double.parseDouble(curValue);
                        break;
                    case "Diffusion Coefficient Ext":
                        this.diffusionCoefficientExt = Double.parseDouble(curValue);
                        break;
                    case "Total Distance":
                        this.totalDistance = Double.parseDouble(curValue);
                        break;
                    case "Confinement Ratio":
                        this.confinementRatio = Double.parseDouble(curValue);
                        break;

                    // Unused:
                    case "Unique Key":
                    case "Ext Recording Name":
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


    // Getters and setters


    public String getUniqueKey() { return uniqueKey; }
    public void setUniqueKey(String uniqueKey) { this.uniqueKey = uniqueKey;}

    public String getRecordingName() { return recordingName; }
    public void setRecordingName(String recordingName) { this.recordingName = recordingName;}

    public int getTrackId() { return trackId; }
    public void setTrackId(int trackId) { this.trackId = trackId; }

    public String getTrackLabel() { return trackLabel; }
    public void setTrackLabel(String trackLabel) { this.trackLabel = trackLabel; }

    public int getNumberSpots() { return numberSpots; }
    public void setNumberSpots(int nrSpots) { this.numberSpots = nrSpots; }

    public int getNumberGaps() { return numberGaps; }
    public void setNumberGaps(int nrGaps) { this.numberGaps = nrGaps; }

    public int getLongestGap() { return longestGap; }
    public void setLongestGap(int longestGap) { this.longestGap = longestGap; }

    public double getTrackDuration() { return trackDuration; }
    public void setTrackDuration(double trackDuration) { this.trackDuration = trackDuration; }

    public double getTrackXLocation() { return trackXLocation; }
    public void setTrackXLocation(double trackXLocation) { this.trackXLocation = trackXLocation; }

    public double getTrackYLocation() { return trackYLocation; }
    public void setTrackYLocation(double trackYLocation) { this.trackYLocation = trackYLocation; }

    public double getTrackDisplacement() { return trackDisplacement; }
    public void setTrackDisplacement(double trackDisplacement) { this.trackDisplacement = trackDisplacement; }

    public double getTrackMaxSpeed() { return trackMaxSpeed; }
    public void setTrackMaxSpeed(double trackMaxSpeed) { this.trackMaxSpeed = trackMaxSpeed; }

    public double getTrackMedianSpeed() { return trackMedianSpeed; }
    public void setTrackMedianSpeed(double trackMedianSpeed) { this.trackMedianSpeed = trackMedianSpeed; }

    public double getTrackMeanSpeed() { return trackMeanSpeed; }
    public void setTrackMeanSpeed(double trackMeanSpeed) { this.trackMeanSpeed = trackMeanSpeed; }

    public double getTrackMaxSpeedCalc() { return trackMaxSpeedCalc; }
    public void setTrackMaxSpeedCalc(double trackMaxSpeedCalc) { this.trackMaxSpeedCalc = trackMaxSpeedCalc; }

    public double getTrackMedianSpeedCalc() { return trackMedianSpeedCalc; }
    public void setTrackMedianSpeedCalc(double trackMedianSpeedCalc) { this.trackMedianSpeedCalc = trackMedianSpeedCalc; }

    public double getTrackMeanSpeedCalc() { return trackMeanSpeedCalc; }
    public void setTrackMeanSpeedCalc(double trackMeanSpeedCalc) { this.trackMeanSpeedCalc = trackMeanSpeedCalc; }

    public double getDiffusionCoefficient() { return diffusionCoefficient; }
    public void setDiffusionCoefficient(double diffusionCoefficient) { this.diffusionCoefficient = diffusionCoefficient; }

    public double getDiffusionCoefficientExt() { return diffusionCoefficientExt; }
    public void setDiffusionCoefficientExt(double diffusionCoefficientExt) { this.diffusionCoefficientExt = diffusionCoefficientExt; }

    public double getTotalDistance() { return totalDistance; }
    public void setTotalDistance(double totalDistance) { this.totalDistance = totalDistance; }

    public double getConfinementRatio() { return confinementRatio; }
    public void setConfinementRatio(double confinementRatio) { this.confinementRatio = confinementRatio; }

    public int getSquareNumber() { return squareNumber; }
    public void setSquareNumber(int squareNumber) { this.squareNumber = squareNumber; }

    public int getLabelNumber() { return labelNumber; }
    public void setLabelNumber(int labelNumber) { this.labelNumber = labelNumber; }
}
