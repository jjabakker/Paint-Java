package Paint.Objects;

import java.util.*;

public class Experiment {

    private String experimentName;
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
    private String caseName;

    private ArrayList<Recording> recordings;


    public Experiment(String experimentName) {
        this.experimentName = experimentName;
        this.recordings = new ArrayList<>();
    }

    public Experiment() {
        this.recordings = new ArrayList<>();
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public List<Recording> getRecordings() {
        return recordings;
    }

    public void addRecording(Recording recording) {
        this.recordings.add(recording);
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
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
        //this.minTracksForTau = Integer.parseInt(minTracksForTau);
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
        sb.append("Experiment: ").append(experimentName).append("\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("\n");
        sb.append(String.format("Experiment data%n"));
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
        sb.append(String.format("\tCase Name                    : %s%n", caseName));



        sb.append(String.format("%nExperiment %s has %d recordings%n",  experimentName,  recordings.size()));
        for (Recording recording : recordings) {
            sb.append(String.format("\t%s%n", recording.getRecordingName()));
        }

        return sb.toString();
    }
}
