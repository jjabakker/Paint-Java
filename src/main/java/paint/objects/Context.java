package paint.objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Context {

    private int numberOfSquaresInRecording;
    private double minRequiredRSquared;
    private double maxAllowableVariability;
    private double minRequiredDensityRatio;
    private int minTracksForTau;
    private int maxFrameGap;
    private double gapClosingMaxDistance;
    private double linkingMaxDistance;
    private boolean medianFiltering;
    private int minNumberOfSpotsInTrack;
    private String neighbourMode;

    public void setNumberOfSquaresInRecording(String numberOfSquaresInRecording) {
        int nr = (int) Double.parseDouble(numberOfSquaresInRecording);
        setNumberOfSquaresInRecording(nr);
    }

    public void setNumberOfSquaresInRecordingSpecifiedByRow(String numberOfSquaresInRecording) {
        int nr = (int) Double.parseDouble(numberOfSquaresInRecording);
        this.numberOfSquaresInRecording = nr * nr;
    }

    public void setNumberOfSquaresInRecording(int numberOfSquaresInRecording) {
      if (isPerfectSquare(numberOfSquaresInRecording)) {
          this.numberOfSquaresInRecording = numberOfSquaresInRecording;
      } else {
          System.out.println("Number of squares in recording must be a perfect square.");
          System.exit(1);
      }
    }

    public int getNumberOfSquaresInRecording() {
        return this.numberOfSquaresInRecording;
    }

    public void setMaxFrameGap(String maxFrameGap) {
        this.maxFrameGap = Integer.parseInt(maxFrameGap);
    }

    public void setGapClosingMaxDistance(String gapClosingMaxDistance) {
        this.gapClosingMaxDistance = Double.parseDouble(gapClosingMaxDistance);
    }

    public void setLinkingMaxDistance(String linkingMaxDistance) {
        this.linkingMaxDistance = Double.parseDouble(linkingMaxDistance);
    }

    public void setMedianFiltering(String medianFiltering) {
        this.medianFiltering = checkBooleanValue(medianFiltering);
    }

    public void setMinNumberOfSpotsInTrack(String minNumberOfSpotsInTrack) {
        this.minNumberOfSpotsInTrack = Integer.parseInt(minNumberOfSpotsInTrack);
    }

    public void setMinTracksForTau(String minTracksForTau) {
        this.minTracksForTau = (int) Double.parseDouble(minTracksForTau);
    }

    public void setNeighbourMode(String neighbourMode) {
        this.neighbourMode = neighbourMode;
    }

    public void setMaxAllowableVariability(String maxAllowableVariability) {
        this.maxAllowableVariability = Double.parseDouble(maxAllowableVariability);
    }

    public void setMinRequiredDensityRatio(String minRequiredDensityRatio) {
        this.minRequiredDensityRatio = Double.parseDouble(minRequiredDensityRatio);
    }

    public void setMinRequiredRSquared(String minRequiredRSquared) {
        this.minRequiredRSquared = Double.parseDouble(minRequiredRSquared);
    }

    private static Boolean checkBooleanValue(String string) {
        Set<String> yesValues = new HashSet<>(Arrays.asList("y", "ye", "yes", "ok", "true", "t"));
        return yesValues.contains(string.trim().toLowerCase());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("Context\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("\n");
        sb.append(String.format("\tNr of Squares in Recording   : %d%n", numberOfSquaresInRecording));
        sb.append(String.format("\tMax Allowable Variability    : %.2f%n", maxAllowableVariability));
        sb.append(String.format("\tMin Required Density Ratio   : %.2f%n", minRequiredDensityRatio));
        sb.append(String.format("\tMin Required R Squared       : %.2f%n", minRequiredRSquared));
        sb.append(String.format("\tMin Tracks to Calculate Tau  : %d%n", minTracksForTau));
        sb.append(String.format("\tMax Frame Gap                : %d%n", maxFrameGap));
        sb.append(String.format("\tGap Closing Max Distance     : %.2f%n", gapClosingMaxDistance));
        sb.append(String.format("\tLinking Max Distance         : %.2f%n", linkingMaxDistance));
        sb.append(String.format("\tMedian Filtering             : %b%n", medianFiltering));
        sb.append(String.format("\tMin Number of Spots in Track : %d%n", minNumberOfSpotsInTrack));
        sb.append(String.format("\tNeighbour Mode               : %s%n", neighbourMode));

        return sb.toString();
    }

    public static boolean isPerfectSquare(int n) {
        if (n < 0)
            return false; // no sqrt for negatives in real numbers
        double sqrt = Math.sqrt(n);
        return sqrt == Math.floor(sqrt);
    }
}