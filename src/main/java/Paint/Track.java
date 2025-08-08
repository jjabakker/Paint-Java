package Paint;

public class Track {
    private String trackId;
    private String trackLabel;
    private int nrSpots;
    private int nrGaps;
    private double longestGap;
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

    public Track() {}

    public Track(String trackId, String trackLabel, int nrSpots, int nrGaps, double longestGap, double trackDuration,
                 double trackXLocation, double trackYLocation, double trackDisplacement, double trackMaxSpeed,
                 double trackMedianSpeed, double trackMeanSpeed, double trackMaxSpeedCalc, double trackMedianSpeedCalc,
                 double trackMeanSpeedCalc, double diffusionCoefficient, double diffusionCoefficientExt,
                 double totalDistance, double confinementRatio) {
        this.trackId = trackId;
        this.trackLabel = trackLabel;
        this.nrSpots = nrSpots;
        this.nrGaps = nrGaps;
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

    // Getters and setters

    public String getTrackId() { return trackId; }
    public void setTrackId(String trackId) { this.trackId = trackId; }

    public String getTrackLabel() { return trackLabel; }
    public void setTrackLabel(String trackLabel) { this.trackLabel = trackLabel; }

    public int getNrSpots() { return nrSpots; }
    public void setNrSpots(int nrSpots) { this.nrSpots = nrSpots; }

    public int getNrGaps() { return nrGaps; }
    public void setNrGaps(int nrGaps) { this.nrGaps = nrGaps; }

    public double getLongestGap() { return longestGap; }
    public void setLongestGap(double longestGap) { this.longestGap = longestGap; }

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
                escape(trackId),
                escape(trackLabel),
                String.valueOf(nrSpots),
                String.valueOf(nrGaps),
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
    public static Track fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length != 19) {
            throw new IllegalArgumentException("CSV line does not have 19 fields: " + csvLine);
        }
        return new Track(
                unescape(parts[0]),
                unescape(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]),
                Double.parseDouble(parts[4]),
                Double.parseDouble(parts[5]),
                Double.parseDouble(parts[6]),
                Double.parseDouble(parts[7]),
                Double.parseDouble(parts[8]),
                Double.parseDouble(parts[9]),
                Double.parseDouble(parts[10]),
                Double.parseDouble(parts[11]),
                Double.parseDouble(parts[12]),
                Double.parseDouble(parts[13]),
                Double.parseDouble(parts[14]),
                Double.parseDouble(parts[15]),
                Double.parseDouble(parts[16]),
                Double.parseDouble(parts[17]),
                Double.parseDouble(parts[18])
        );
    }

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
