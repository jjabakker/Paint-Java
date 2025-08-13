package paint.objects;

import paint.utilities.ColumnValue;
import java.util.List;

public class Track {
    private int trackId;
    private String trackLabel;
    private int numberSpots;
    private int numberGaps;
    private int longestGap;
    private double trackDuration;
    private double trackXLocation;
    private double trackYLocation;
    private double trackDisplacement;
    private double trackMaxSpeed;
    private double trackMedianSpeed;
    private double trackMeanSpeed;
    private double trackMaxSpeedCalc;
    private double trackMedianSpeedCalc;
    private double trackMeanSpeedCalc;
    private double diffusionCoefficient;
    private double diffusionCoefficientExt;
    private double totalDistance;
    private double confinementRatio;

    public Track(int trackId, String trackLabel, int nrSpots, int nrGaps, int longestGap, double trackDuration,
                 double trackXLocation, double trackYLocation, double trackDisplacement, double trackMaxSpeed,
                 double trackMedianSpeed, double trackMeanSpeed, double trackMaxSpeedCalc, double trackMedianSpeedCalc,
                 double trackMeanSpeedCalc, double diffusionCoefficient, double diffusionCoefficientExt,
                 double totalDistance, double confinementRatio) {
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
                    case "Nr Gaps":
                        this.numberGaps = (int) Double.parseDouble(curValue);
                        break;
                    case "Nr Spots":
                        this.numberSpots = (int) Double.parseDouble(curValue);
                        break;
                    case "Longest Gap":
                        this.longestGap = (int) Double.parseDouble(curValue);
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
                    case "Square Nr":
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

    public int getTrackId() { return trackId; }
    public void setTrackId(int trackId) { this.trackId = trackId; }

    public String getTrackLabel() { return trackLabel; }
    public void setTrackLabel(String trackLabel) { this.trackLabel = trackLabel; }

    public int getNrSpots() { return numberSpots; }
    public void setNrSpots(int nrSpots) { this.numberSpots = nrSpots; }

    public int getNrGaps() { return numberGaps; }
    public void setNrGaps(int nrGaps) { this.numberGaps = nrGaps; }

    public double getLongestGap() { return longestGap; }
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

}
