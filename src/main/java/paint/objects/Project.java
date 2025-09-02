package paint.objects;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Project {

    // Attributes

    private String projectName;
    private Path projectPath;
    private Context context;
    private List<Experiment> experiments;

    // Constructors
    public Project() {
        this.experiments = new ArrayList<>();
    }

    public Project(String projectName, Path projectPath, Context context, List<Experiment> experiments) {
        this.projectName = projectName;
        this.projectPath = projectPath;
        this.context = new Context(context);
        this.experiments = new ArrayList<>(experiments);
    }

    public Project(Path projectPath) {
        this.projectName = projectPath.getFileName().toString();
        this.projectPath = projectPath;
        this.experiments = new ArrayList<>();
    }

    // Getters and setters

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<Experiment> getExperiments() {
        return experiments;
    }

    public void setContext(Context context) {  // ToDo Should maybe make a deep copy
        this.context = context;
    }

    public Context getContext() {  // ToDo Should maybe make a deep copy
        return context;
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

    public String toString() {
        StringBuilder sb = new StringBuilder();


        sb.append("\n\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("Project: ").append(projectName).append("\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("\n");
        if (context == null) {
            sb.append("No context set yet.");
        }
        else {
            sb.append(context.toString());
        }
        sb.append("\n");
        sb.append(String.format("%nExperiment %s has %d experiment%n",  projectName,  experiments.size()));
        for (Experiment experiment : experiments) {
            sb.append(String.format("\t%s%n", experiment.getExperimentName()));
        }

        for (Experiment experiment : experiments) {
            sb.append("\n");
            sb.append(experiment);
            List <Recording> recordings = experiment.getRecordings();
            for (Recording rec : recordings) {
                sb.append("\n");
                sb.append(rec);
            }
        }
        return sb.toString();
    }

}
