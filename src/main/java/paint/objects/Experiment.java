package paint.objects;

import java.util.*;

public class Experiment {

    private String experimentName;

    private ArrayList<Recording> recordings;


    // Constructors

    public Experiment(String experimentName) {
        this.experimentName = experimentName;
        this.recordings = new ArrayList<>();
    }

    public Experiment() {
        this.recordings = new ArrayList<>();
    }

    // Getters and setters


    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }
    public String getExperimentName() {
        return experimentName;
    }

    public void addRecording(Recording recording) {
        this.recordings.add(recording);
    }

    public List<Recording> getRecordings() {
        return recordings;
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
        sb.append(String.format("%nExperiment %s has %d recordings%n",  experimentName,  recordings.size()));
        for (Recording recording : recordings) {
            sb.append(String.format("\t%s%n", recording.getRecordingName()));
        }
        return sb.toString();
    }
}
