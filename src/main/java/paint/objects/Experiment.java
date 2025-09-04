package paint.objects;

import java.util.*;

public class Experiment {

    private String experimentName;
    private ArrayList<Recording> recordings;

    //
    // Constructors
    //

    public Experiment(String experimentName) {
        this.experimentName = experimentName;
        this.recordings = new ArrayList<>();
    }

    public Experiment() {
        this.recordings = new ArrayList<>();
    }

    public Experiment(String experimentName, ArrayList<Recording> recordings) { // ToDo Should maybe make a deep copy
        this.experimentName = experimentName;
        this.recordings = recordings;
    }

    //
    // Getters and setters
    //

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void addRecording(Recording recording) { // ToDo Should maybe make a deep copy
        this.recordings.add(recording);
    }

    public List<Recording> getRecordings() { // ToDo Should maybe make a deep copy
        return recordings;
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
