package Paint;

import java.util.ArrayList;
import java.util.List;

public class Square {
    private List<Track> tracks = new ArrayList<>();
    private double startX;
    private double startY;
    private double endX;
    private double endY;
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
    private boolean squareManuallyExcluded;
    private boolean imageExcluded;

    public Square() {}

    public Square(double startX, double startY, double endX, double endY,
                  double variability, double density, double densityRatio, double tau, double rSquared,
                  double medianDiffusionCoefficient, double meanDiffusionCoefficient,
                  double medianDiffusionCoefficientExt, double meanDiffusionCoefficientExt,
                  double medianLongTrackDuration, double medianShortTrackDuration,
                  double medianDisplacement, double maxDisplacement, double totalDisplacement,
                  double medianMaxSpeed, double maxMaxSpeed, double medianMeanSpeed, double maxMeanSpeed,
                  double maxTrackDuration, double totalTrackDuration, double medianTrackDuration,
                  boolean squareManuallyExcluded, boolean imageExcluded) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
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

    // Getters and setters for all fields

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public double getStartX() {
        return startX;
    }

    public void setStartX(double startX) {
        this.startX = startX;
    }

    public double getStartY() {
        return startY;
    }

    public void setStartY(double startY) {
        this.startY = startY;
    }

    public double getEndX() {
        return endX;
    }

    public void setEndX(double endX) {
        this.endX = endX;
    }

    public double getEndY() {
        return endY;
    }

    public void setEndY(double endY) {
        this.endY = endY;
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

    public void setMedianLongTrackDuration(double medianLongTrackDuration) {
        this.medianLongTrackDuration = medianLongTrackDuration;
    }

    public double getMedianShortTrackDuration() {
        return medianShortTrackDuration;
    }

    public void setMedianShortTrackDuration(double medianShortTrackDuration) {
        this.medianShortTrackDuration = medianShortTrackDuration;
    }

    public double getMedianDisplacement() {
        return medianDisplacement;
    }

    public void setMedianDisplacement(double medianDisplacement) {
        this.medianDisplacement = medianDisplacement;
    }

    public double getMaxDisplacement() {
        return maxDisplacement;
    }

    public void setMaxDisplacement(double maxDisplacement) {
        this.maxDisplacement = maxDisplacement;
    }

    public double getTotalDisplacement() {
        return totalDisplacement;
    }

    public void setTotalDisplacement(double totalDisplacement) {
        this.totalDisplacement = totalDisplacement;
    }

    public double getMedianMaxSpeed() {
        return medianMaxSpeed;
    }

    public void setMedianMaxSpeed(double medianMaxSpeed) {
        this.medianMaxSpeed = medianMaxSpeed;
    }

    public double getMaxMaxSpeed() {
        return maxMaxSpeed;
    }

    public void setMaxMaxSpeed(double maxMaxSpeed) {
        this.maxMaxSpeed = maxMaxSpeed;
    }

    public double getMedianMeanSpeed() {
        return medianMeanSpeed;
    }

    public void setMedianMeanSpeed(double medianMeanSpeed) {
        this.medianMeanSpeed = medianMeanSpeed;
    }

    public double getMaxMeanSpeed() {
        return maxMeanSpeed;
    }

    public void setMaxMeanSpeed(double maxMeanSpeed) {
        this.maxMeanSpeed = maxMeanSpeed;
    }

    public double getMaxTrackDuration() {
        return maxTrackDuration;
    }

    public void setMaxTrackDuration(double maxTrackDuration) {
        this.maxTrackDuration = maxTrackDuration;
    }

    public double getTotalTrackDuration() {
        return totalTrackDuration;
    }

    public void setTotalTrackDuration(double totalTrackDuration) {
        this.totalTrackDuration = totalTrackDuration;
    }

    public double getMedianTrackDuration() {
        return medianTrackDuration;
    }

    public void setMedianTrackDuration(double medianTrackDuration) {
        this.medianTrackDuration = medianTrackDuration;
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

    // CSV serialization
    public String toCSV() {
        return String.join(",",
                String.valueOf(startX),
                String.valueOf(startY),
                String.valueOf(endX),
                String.valueOf(endY),
                String.valueOf(variability),
                String.valueOf(density),
                String.valueOf(densityRatio),
                String.valueOf(tau),
                String.valueOf(rSquared),
                String.valueOf(medianDiffusionCoefficient),
                String.valueOf(meanDiffusionCoefficient),
                String.valueOf(medianDiffusionCoefficientExt),
                String.valueOf(meanDiffusionCoefficientExt),
                String.valueOf(medianLongTrackDuration),
                String.valueOf(medianShortTrackDuration),
                String.valueOf(medianDisplacement),
                String.valueOf(maxDisplacement),
                String.valueOf(totalDisplacement),
                String.valueOf(medianMaxSpeed),
                String.valueOf(maxMaxSpeed),
                String.valueOf(medianMeanSpeed),
                String.valueOf(maxMeanSpeed),
                String.valueOf(maxTrackDuration),
                String.valueOf(totalTrackDuration),
                String.valueOf(medianTrackDuration),
                String.valueOf(squareManuallyExcluded),
                String.valueOf(imageExcluded)
        );
    }

    // CSV deserialization
//    public static Square fromCSV(String csvLine) {
//        String[] parts = csvLine.split(",", -1);
//        if (parts.length != 27) {
//            throw new IllegalArgumentException("CSV line does not have 27 fields: " + csvLine);
//        }
//
//        return new Square(
//                Double.parseDouble(parts[0]),
//                Double.parseDouble(parts[1]),
//                Double.parseDouble(parts[2]),
//                Double.parseDouble(parts[3]),
//                Double.parseDouble(parts[4]),
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
//                Double.parseDouble(parts[18]),
//                Double.parseDouble(parts[19]),
//                Double.parseDouble(parts[20]),
//                Double.parseDouble(parts[21]),
//                Double.parseDouble(parts[22]),
//                Double.parseDouble(parts[23]),
//                Boolean.parseBoolean(parts[24]),
//                Boolean.parseBoolean(parts[25])
//        );
//    }
}
