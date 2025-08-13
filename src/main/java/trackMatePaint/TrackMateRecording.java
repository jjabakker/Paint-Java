package trackMatePaint;

import paintUtilities.AppLogger;

import java.nio.file.Path;
import java.util.logging.Logger;

public class TrackMateRecording {

    private static final Logger log = AppLogger.getLogger();

    public TrackMateRecording(Path experimentPath, Path omeroExperimentPath, String recording) {
        log.info(String.format("Ready to start TrackMate on a recording: %s.", recording));
        go(experimentPath, omeroExperimentPath, recording);
    }

    public void go(Path experimentPath, Path omeroExperimentPath, String recording) {


    }
}
