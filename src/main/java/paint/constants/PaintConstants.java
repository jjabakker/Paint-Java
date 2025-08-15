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


}