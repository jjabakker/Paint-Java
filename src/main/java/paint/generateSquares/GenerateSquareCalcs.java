package paint.generateSquares;

import paint.loaders.ProjectDataLoader;
import paint.objects.Context;
import paint.objects.Experiment;

import java.nio.file.Path;

import static paint.loaders.ProjectDataLoader.loadExperiment;
import static paint.loaders.ProjectDataLoader.loadExperiment1;

public class GenerateSquareCalcs {

    static boolean calculateSquares(Path projectPath, String experimentName, Context context) {
        try {
            Experiment experiment = loadExperiment1(projectPath, experimentName, context);
            if (experiment != null) {
                System.out.println("Experiment loaded: " + experimentName);
                System.out.println(experiment);
            }
            else {
                System.err.println("Failed to load experiment: " + experimentName);
            }
        }
        catch (Exception e) {
            System.err.println("Error: Failed to load experiment: " + experimentName);
        }
       return false;
    }
}
