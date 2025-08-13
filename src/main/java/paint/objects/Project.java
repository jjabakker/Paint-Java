package paint.objects;

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

    public Experiment getExperiment(String experimentName) {
        for (Experiment experiment : experiments) {
            if (experiment.getExperimentName().equals(experimentName)) {
                return experiment;
            }
        }
        return null; // not found
    }

    public void setExperiments(List<Experiment> experiments) {
        this.experiments = experiments;
    }

    public void addExperiment(Experiment experiment) {
        this.experiments.add(experiment);
    }


}
