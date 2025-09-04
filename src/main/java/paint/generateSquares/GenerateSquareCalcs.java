package paint.generateSquares;

import paint.objects.Context;
import paint.objects.Experiment;

import java.nio.file.Path;

import static paint.loaders.ProjectDataLoader.loadExperiment;

public class GenerateSquareCalcs {

    static boolean calculateSquares(Path projectPath, String experimentName, Context context) {
        try {
            Experiment experiment = loadExperiment(projectPath, experimentName, context, false);
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
