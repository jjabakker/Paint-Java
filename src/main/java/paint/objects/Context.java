package paint.objects;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Context {

    private int numberOfSquaresInRecording;
    private int numberOfSquaresInRow;
    private int numberOfSquaresInColumn;
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


    // Constructors
    public Context() { }

    public Context(int numberOfSquaresInRecording) {
        setNumberOfSquaresInRecording(numberOfSquaresInRecording);
    }

    public Context(Context other) {
        this.numberOfSquaresInRecording = other.numberOfSquaresInRecording;
        this.numberOfSquaresInRow = other.numberOfSquaresInRow;
        this.numberOfSquaresInColumn = other.numberOfSquaresInColumn;
        this.minRequiredRSquared = other.minRequiredRSquared;
        this.maxAllowableVariability = other.maxAllowableVariability;
        this.minRequiredDensityRatio = other.minRequiredDensityRatio;
        this.minTracksForTau = other.minTracksForTau;
        this.maxFrameGap = other.maxFrameGap;
        this.gapClosingMaxDistance = other.gapClosingMaxDistance;
        this.linkingMaxDistance = other.linkingMaxDistance;
        this.medianFiltering = other.medianFiltering;
        this.minNumberOfSpotsInTrack = other.minNumberOfSpotsInTrack;
        this.neighbourMode = other.neighbourMode;
    }


    // Getters and setters

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
          this.numberOfSquaresInRow = (int) Math.sqrt(numberOfSquaresInRecording);
          this.numberOfSquaresInColumn = (int) Math.sqrt(numberOfSquaresInRecording);
      } else {
          System.out.println("Number of squares in recording must be a perfect square.");
          System.exit(1);
      }
    }

    public int getNumberOfSquaresInRecording() {
        return this.numberOfSquaresInRecording;
    }

    public int getNumberOfSquaresInRow() {
        return this.numberOfSquaresInRow;
    }

    public int getNumberOfSquaresInColumn() {
        return this.numberOfSquaresInColumn;
    }

    public void setMaxFrameGap(int maxFrameGap) {
        this.maxFrameGap = maxFrameGap;
    }

    public void setGapClosingMaxDistance(double gapClosingMaxDistance) {
        this.gapClosingMaxDistance = gapClosingMaxDistance;
    }

    public void setLinkingMaxDistance(double linkingMaxDistance) {
        this.linkingMaxDistance = linkingMaxDistance;
    }

    public void setMedianFiltering(boolean medianFiltering) {
        this.medianFiltering = medianFiltering;
    }

    public void setMinNumberOfSpotsInTrack(int minNumberOfSpotsInTrack) {
        this.minNumberOfSpotsInTrack = minNumberOfSpotsInTrack;
    }

    public void setMinTracksForTau(int minTracksForTau) {
        this.minTracksForTau = minTracksForTau;
    }

    public void setNeighbourMode(String neighbourMode) {
        this.neighbourMode = neighbourMode;
    }

    public void setMaxAllowableVariability(double maxAllowableVariability) {
        this.maxAllowableVariability = maxAllowableVariability;
    }

    public void setMinRequiredDensityRatio(double minRequiredDensityRatio) {
        this.minRequiredDensityRatio = minRequiredDensityRatio;
    }

    public void setMinRequiredRSquared(double minRequiredRSquared) {
        this.minRequiredRSquared = minRequiredRSquared;
    }

    private static Boolean checkBooleanValue(String string) {
        Set<String> yesValues = new HashSet<>(Arrays.asList("y", "ye", "yes", "ok", "true", "t"));
        return yesValues.contains(string.trim().toLowerCase());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("\tNr of Squares in Recording   : %d%n", numberOfSquaresInRecording));
        sb.append(String.format("\tNr of Squares in Row         : %d%n", numberOfSquaresInRow));
        sb.append(String.format("\tNr of Squares in Column      : %d%n", numberOfSquaresInColumn));
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