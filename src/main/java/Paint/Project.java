package Paint;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Project {
    private String projectName;
    private Path projectPath;
    private List<Experiment> experiments;

    public Project() {
        this.experiments = new ArrayList<>();
    }

    public Project(Path projectPath) {
        this.projectName = projectPath.getFileName().toString();
        this.projectPath = projectPath;
        this.experiments = new ArrayList<>();
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments;
    }

    public void addExperiment(Experiment experiment) {
        this.experiments.add(experiment);
    }

    // CSV (only projectName, since experiments is complex)
    public String toCSV() {
        return projectName;
    }

    public static Project fromCSV(String csvLine) {
        // Simple: one field
        return new Project(Path.of(csvLine.trim()));
    }
}
