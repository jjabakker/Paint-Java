package Paint;

import java.util.ArrayList;
import java.util.List;

public class Experiment {

    private String experimentName;
    private List<Recording> recordings;

    public Experiment() {
        this.recordings = new ArrayList<>();
    }

    public Experiment(String experimentName) {
        this.experimentName = experimentName;
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

    public void setRecordings(List<Recording> recordings) {
        this.recordings = recordings;
    }

    public void addRecording(Recording recording) {
        this.recordings.add(recording);
    }

    // CSV (only experimentName)
    public String toCSV() {
        return experimentName;
    }

    public static Experiment fromCSV(String csvLine) {
        return new Experiment(csvLine.trim());
    }
}
