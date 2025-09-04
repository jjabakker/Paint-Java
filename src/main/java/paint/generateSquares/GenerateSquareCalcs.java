package paint.generateSquares;

import paint.loaders.ProjectDataLoader;
import paint.objects.Context;

import java.nio.file.Path;

import static paint.loaders.ProjectDataLoader.loadExperiment;

public class GenerateSquareCalcs {

    static boolean calculateSquares(Path projectPath, String experimentName, Context context, boolean matureProject) throws Exception {

       ProjectDataLoader.Result = loadExperiment(projectPath, experimentName, context, matureProject);
       return false;
    }
}
