package Paint.Objects;

import PaintUtilities.ColumnValue;
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
                if (curValue.equals("")) {
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
            System.err.println(String.format("Error parsing column: %s. Conflicting value is %s.", curColumn, curValue));
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

    // CSV serialization (all fields as one comma-separated line)
    public String toCSV() {
        return String.join(",",
//                escape(trackId),
                escape(trackLabel),
                String.valueOf(numberSpots),
                String.valueOf(numberGaps),
                String.valueOf(longestGap),
                String.valueOf(trackDuration),
                String.valueOf(trackXLocation),
                String.valueOf(trackYLocation),
                String.valueOf(trackDisplacement),
                String.valueOf(trackMaxSpeed),
                String.valueOf(trackMedianSpeed),
                String.valueOf(trackMeanSpeed),
                String.valueOf(trackMaxSpeedCalc),
                String.valueOf(trackMedianSpeedCalc),
                String.valueOf(trackMeanSpeedCalc),
                String.valueOf(diffusionCoefficient),
                String.valueOf(diffusionCoefficientExt),
                String.valueOf(totalDistance),
                String.valueOf(confinementRatio)
        );
    }

    // CSV deserialization
//    public static Track fromCSV(String csvLine) {
//        String[] parts = csvLine.split(",", -1);
//        if (parts.length != 19) {
//            throw new IllegalArgumentException("CSV line does not have 19 fields: " + csvLine);
//        }
//        return new Track(
//                unescape(parts[0]),
//                unescape(parts[1]),
//                Integer.parseInt(parts[2]),
//                Integer.parseInt(parts[3]),
//                (int) Double.parseDouble(parts[4]),
//                Double.parseDouble(parts[5]),
//                Double.parseDouble(parts[6]),
//                Double.parseDouble(parts[7]),
//                Double.parseDouble(parts[8]),
//                Double.parseDouble(parts[9]),
//                Double.parseDouble(parts[10]),
//                Double.parseDouble(parts[11]),
//                Double.parseDouble(parts[12]),
//                Double.parseDouble(parts[13]),
//                Double.parseDouble(parts[14]),
//                Double.parseDouble(parts[15]),
//                Double.parseDouble(parts[16]),
//                Double.parseDouble(parts[17]),
//                Double.parseDouble(parts[18])
//        );
//    }

    // Simple CSV escaping for commas and quotes
    private static String escape(String input) {
        if (input == null) return "";
        if (input.contains(",") || input.contains("\"")) {
            input = input.replace("\"", "\"\"");
            return "\"" + input + "\"";
        }
        return input;
    }

    private static String unescape(String input) {
        if (input == null) return "";
        input = input.trim();
        if (input.startsWith("\"") && input.endsWith("\"")) {
            input = input.substring(1, input.length() - 1).replace("\"\"", "\"");
        }
        return input;
    }
}
