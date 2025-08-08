package Paint;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import PaintUtilities.ColumnValue;
import com.sun.org.apache.bcel.internal.generic.BREAKPOINT;

public class PaintRecording {

    static final String[] columnsToCheckAllRecordings = {
            "Recording Sequence Nr",
            "Recording Name",
            "Experiment Date",
            "Experiment Name",
            "Condition Nr",
            "Replicate Nr",
            "Probe",
            "Probe Type",
            "Cell Type",
            "Adjuvant",
            "Concentration",
            "Threshold",
            "Process",
            "Ext Recording Name",
            "Nr Spots",
            "Recording Size",
            "Run Time",
            "Time Stamp" };

    // The first set exists in both Experiment Info and All Recordings Experiment Info
    private String recordingName;
    private int conditionNr;
    private int replicateNr;
    private String probeName;
    private String probeType;
    private String cellType;
    private String adjuvant;
    private double concentration;
    private boolean processFlag;
    private double threshold;

    // The second set exists only in All Recordings
    private int numberSpots;
    private int numberTracks;
    private int runTime;
    private int recordingSize;
    private LocalDate timeStamp;
    private double maxFrameGap;
    private double gapClosingMaxDistance;
    private double linkingMaxDistance;
    private boolean medianFiltering;
    private int SpotsinAllTracks;
    private int minSpotsInTrack;
    private String Case;

    private List<PaintSquare> paintSquares;
    private List<PaintTrack> paintTracks;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    public PaintRecording() {
        this.paintSquares = new ArrayList<>();
        // this.tracks = new ArrayList<>();
    }


    public PaintRecording(List<ColumnValue> columns) {

        String curColumn = "";
        String curValue = "";
        try {
            for (ColumnValue cv : columns) {
                curColumn = cv.getColumnName();
                curValue  = cv.getValue().toString();
                switch (curColumn) {
                    case "Recording Name":
                        this.recordingName = (String) cv.getValue();
                        break;
                    case "Recording Sequence Nr":
                        break;
                    case "Experiment Name":
                        this.probeName = (String) cv.getValue();
                        break;
                    case "Experiment Date":
                        break;
                    case "Condition Nr":
                        this.conditionNr = Integer.parseInt(cv.getValue().toString());
                        break;
                    case "Replicate Nr":
                        this.replicateNr = Integer.parseInt(cv.getValue().toString());
                        break;
                    case "Probe":
                        this.probeName = (String) cv.getValue();
                        break;
                    case "Probe Type":
                        this.probeType = (String) cv.getValue();
                        break;
                    case "Cell Type":
                        this.cellType = (String) cv.getValue();
                        break;
                    case "Adjuvant":
                        this.adjuvant = (String) cv.getValue();
                        break;
                    case "Concentration":
                        this.concentration = Double.parseDouble(cv.getValue().toString());;
                        break;
                    case "Threshold":
                        this.threshold = Double.parseDouble(cv.getValue().toString());
                        break;
                    case "Process":
                        this.processFlag = checkBooleanValue(cv.getValue().toString());
                        break;
                    case "Nr Spots":
                        this.numberSpots = Integer.parseInt(cv.getValue().toString());
                        break;
                    case "Number Tracks":
                        this.numberTracks = Integer.parseInt(cv.getValue().toString());
                        break;
                    case "Run Time":
                        this.runTime = Integer.parseInt(cv.getValue().toString());
                        break;
                    case "Time Stamp":
                        this.timeStamp = LocalDate.parse(cv.getValue().toString(), DATE_FORMAT);
                        break;
                    case "Max Frame Gap":
                        this.maxFrameGap = (double) cv.getValue();
                        break;
                    case "Gap Closing Max Distance":
                        this.gapClosingMaxDistance = Double.parseDouble(cv.getValue().toString());
                        break;
                    case "Linking Max Distance":
                        this.linkingMaxDistance = Double.parseDouble(cv.getValue().toString());
                        break;
                    case "Median Filtering":
                        this.medianFiltering = checkBooleanValue(cv.getValue().toString());
                        break;
                    default:
                        System.out.println("Warning: Unknown column " + cv.getColumnName());
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println(String.format("Error parsing column: %s. Conflicting value is %s.", curColumn, curValue));
            System.exit(-1);
        }
    }


    public PaintRecording(String recordingName, int conditionNr, int replicateNr,
                          String probeName, String probeType, String cellType, String adjuvant,
                          double concentration, boolean processFlag, double threshold) {
        this.recordingName = recordingName;
        this.conditionNr = conditionNr;
        this.replicateNr = replicateNr;
        this.probeName = probeName;
        this.probeType = probeType;
        this.cellType = cellType;
        this.adjuvant = adjuvant;
        this.concentration = concentration;
        this.processFlag = processFlag;
        this.threshold = threshold;
        this.paintSquares = new ArrayList<>();
        this.paintTracks = new ArrayList<>();
    }

    // Getters and setters
    public String getRecordingName() { return recordingName; }
    public void setRecordingName(String recordingName) { this.recordingName = recordingName; }

    public int getConditionNr() { return conditionNr; }
    public void setConditionNr(int conditionNr) { this.conditionNr = conditionNr; }

    public int getReplicateNr() { return replicateNr; }
    public void setReplicateNr(int replicateNr) { this.replicateNr = replicateNr; }

    public String getProbeName() { return probeName; }
    public void setProbeName(String probeName) { this.probeName = probeName; }

    public String getRecordingType() { return probeType; }
    public void setRecordingType(String recordingType) { this.probeType = recordingType; }

    public String getCellType() { return cellType; }
    public void setCellType(String cellType) { this.cellType = cellType; }

    public String getAdjuvant() { return adjuvant; }
    public void setAdjuvant(String adjuvant) { this.adjuvant = adjuvant; }

    public double getConcentration() { return concentration; }
    public void setConcentration(double concentration) { this.concentration = concentration; }

    public boolean isProcessFlag() { return processFlag; }
    public void setProcessFlag(boolean processFlag) { this.processFlag = processFlag; }

    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public List<PaintSquare> getSquares() {
        return paintSquares;
    }

    public void setSquares(List<PaintSquare> paintSquares) {
        this.paintSquares = paintSquares;
    }

    public void addSquare(PaintSquare paintSquare) {
        this.paintSquares.add(paintSquare);
    }

    public List<PaintTrack> getTracks() {
        return paintTracks;
    }

    public void setTracks(List<PaintTrack> paintTracks) { this.paintTracks = paintTracks; }

    public void addTrack(PaintTrack paintTrack) {
        this.paintTracks.add(paintTrack);
    }

    static Boolean checkBooleanValue(String string) {
        Set<String> yesValues = new HashSet<>(Arrays.asList("y", "ye", "yes", "ok", "true", "t"));
        return yesValues.contains(string.trim().toLowerCase());
    }
}
