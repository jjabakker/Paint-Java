package Paint.Objects;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PaintProject {
    private String projectName;
    private Path projectPath;
    private List<PaintExperiment> experiments;

    public PaintProject() {
        this.experiments = new ArrayList<>();
    }

    public PaintProject(Path projectPath) {
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

    public List<PaintExperiment> getExperiments() {
        return experiments;
    }

    public void setExperiments(List<PaintExperiment> experiments) {
        this.experiments = experiments;
    }

    public void addExperiment(PaintExperiment experiment) {
        this.experiments.add(experiment);
    }

    public boolean loadProject(Path projectPath) {
        return true;
    }
}
