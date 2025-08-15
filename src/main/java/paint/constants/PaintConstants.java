package paint.constants;

public final class PaintConstants {

    private PaintConstants() {
        // Prevent instantiation
    }
    // Filenames
    public static final String RECORDINGS_CSV = "All Recordings.csv";
    public static final String TRACKS_CSV = "All Tracks.csv";
    public static final String SQUARES_CSV = "All Squares.csv";
    public static final String EXPERIMENT_INFO_CSV = "Experiment Info.csv";
    public static final String PAINT_JSON = "Paint Configuration.json";
    public static final String PROJECT_INFO_CSV = "Paint Project Info.csv";

    // Directories
    public static final String DIR_TRACKMATE_IMAGES = "TrackMate Images";
    public static final String DIR_BRIGHTFIELD_IMAGES = "Brightfield Images";

    // Column names
    public static final String COL_EXT_RECORDING_NAME = "Ext Recording Name";
    public static final String COL_RECORDING_NAME = "Recording Name";

    // Squares
    public static final double IMAGE_WIDTH = 82.0864;
    public static final double IMAGE_HEIGHT = 82.0864;

    public static final String[] TRACK_COLS = {
            "Unique Key",                  // 0
            "Ext Recording Name",          // 1
            "Track Id",                    // 2
            "Track Label",                 // 3
            "Number Spots",                // 4
            "Number Gaps",                 // 5
            "Longest Gap",                 // 6
            "Track Duration",              // 7
            "Track X Location",            // 8
            "Track Y Location",            // 9
            "Track Displacement",          // 10
            "Track Max Speed",             // 11
            "Track Median Speed",          // 12
            "Track Mean Speed",            // 13
            "Track Max Speed Calc",        // 14
            "Track Median Speed Calc",     // 15
            "Track Mean Speed Calc",       // 16
            "Diffusion Coefficient",       // 17
            "Diffusion Coefficient Ext",   // 18
            "Total Distance",              // 19
            "Confinement Ratio",           // 20
            "Square Nr",                   // 21
            "Label Nr"                     // 22
    };

    public static final String[] SQUARE_COLS = {
            "Unique Key",                       // 0
            "Recording Name",                   // 1
            "Square Number",                    // 2
            "Row Number",                       // 3
            "Col Number",                       // 4
            "Label Number",                     // 5
            "Cell ID",                          // 6
            "Selected",                         // 7
            "Square Manually Excluded",         // 8
            "Image Excluded",                   // 9
            "X0",                               // 10
            "Y0",                               // 11
            "X1",                               // 12
            "Y1",                               // 13
            "Number Tracks",                    // 14
            "Variability",                      // 15
            "Density",                          // 16
            "Density Ratio",                    // 17
            "Tau",                              // 18
            "RSquared",                         // 19
            "Median Diffusion Coefficient",     // 20
            "Mean Diffusion Coefficient",       // 21
            "Median Diffusion Coefficient Ext", // 22
            "Mean Diffusion Coefficient Ext",   // 23
            "Median Long Track Duration",       // 24
            "Mean Long Track Duration",         // 25
            "Median Short Track Duration",      // 26
            "Mean Short Track Duration",        // 27
            "Median Displacement",              // 28
            "Max Displacement",                 // 29
            "Total Displacement",               // 30
            "Median Max Speed",                 // 31
            "Max Max Speed",                    // 32
            "Median Mean Speed",                // 33
            "Max Mean Speed",                   // 34
            "Max Track Duration",               // 35
            "Total Track Duration",             // 36
            "Median Track Duration"             // 37
    };


    public static final String[] RECORDING_COLS = {
            "Recording Name",                // 1
            "Condition Nr",                  // 2
            "Replicate Nr",                  // 3
            "Probe Name",                    // 4
            "Probe Type",                    // 5
            "Cell Type",                     // 6
            "Adjuvant",                      // 7
            "Concentration",                 // 8
            "Process Flag",                  // 9
            "Threshold",                     // 10
            "Number Spots",                  // 11
            "Number Tracks",                 // 12
            "Run Time",                      // 13
            "Recording Size",                // 14
            "Time Stamp",                    // 15
            "Number Of Spots In All Tracks", // 16
            "Exclude",                       // 17
            "Tau",                           // 18
            "RSquared",                      // 19
            "Density"                        // 20
    };

    public static final String[] EXPERIMENT_COLS = {
            "experimentName"
    };

}