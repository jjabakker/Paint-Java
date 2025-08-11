package Paint.Objects;

import java.time.format.DateTimeFormatter;
import java.util.*;

import PaintUtilities.ColumnValue;

public class Recording {

    // Not used anywhere for now, but needed for integrity check
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
            "Nr Tracks",
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
    private double runTime;
    private int recordingSize;
    private String timeStamp;
    private int numberOfSpotsInAllTracks;
    private boolean exclude;
    private double tau;
    private double rSquared;
    private double density;

    private List<Square> squares = new ArrayList<>();
    private List<Track> tracks = new ArrayList<>();

    private TracksTable tracksTable;
    private SquaresTable squaresTable;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;

    public Recording() {
        this.squares = new ArrayList<>();
        // this.tracks = new ArrayList<>();
    }


    public Recording(List<ColumnValue> columns) {

        String curColumn = "";
        String curValue = "";
        try {
            for (ColumnValue cv : columns) {
                curColumn = cv.getColumnName();
                curValue  = cv.getValue().toString();
                switch (curColumn) {

                    // String values

                    case "Recording Name":
                        this.recordingName = (String) cv.getValue();
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
                    case "Time Stamp":
                        this.timeStamp = cv.getValue().toString();
                        break;


                    // Integer values

                    case "Condition Nr":
                        this.conditionNr = Integer.parseInt(cv.getValue().toString());
                        break;
                    case "Replicate Nr":
                        this.replicateNr = Integer.parseInt(cv.getValue().toString());
                        break;
                    case "Nr Spots":
                        this.numberSpots = Integer.parseInt(cv.getValue().toString());
                        break;
                    case "Nr Tracks":
                        this.numberTracks = Integer.parseInt(cv.getValue().toString());
                        break;

                    case "Nr Spots in All Tracks":
                        this.numberOfSpotsInAllTracks = Integer.parseInt(cv.getValue().toString());
                        break;

                    // Double values

                    case "Concentration":
                        this.concentration = Double.parseDouble(cv.getValue().toString());;
                        break;
                    case "Threshold":
                        this.threshold = Double.parseDouble(cv.getValue().toString());
                        break;
                    case "Run Time":
                        this.runTime = Double.parseDouble(cv.getValue().toString());
                        break;
                   case "Tau":
                        this.tau = Double.parseDouble(cv.getValue().toString());
                        break;
                    case "Density":
                        this.density = Double.parseDouble(cv.getValue().toString());
                        break;
                    case "R Squared":
                        this.rSquared = Double.parseDouble(cv.getValue().toString());
                        break;

                    // Boolean values

                    case "Process":
                        this.processFlag = checkBooleanValue(cv.getValue().toString());
                        break;
                    case "Exclude":
                        this.exclude = checkBooleanValue(cv.getValue().toString());
                        break;

                    // These are values that are not recording, byt experiment attributes

                    case "Experiment Name":
                        // this.probeName = (String) cv.getValue();
                        // break;
                    case "Gap Closing Max Distance":
                        // this.gapClosingMaxDistance = Double.parseDouble(cv.getValue().toString());
                        // break;
                    case "Linking Max Distance":
                        // this.linkingMaxDistance = Double.parseDouble(cv.getValue().toString());
                        // break;
                    case "Min Required R Squared":
                        // this.minRequiredRSquared = Double.parseDouble(cv.getValue().toString());
                        // break;
                    case "Max Allowable Variability":
                        // this.maxAllowableVariability = Double.parseDouble(cv.getValue().toString());
                        // break;
                    case "Min Required Density Ratio":
                        // this.minRequiredDensityRatio = Double.parseDouble(cv.getValue().toString());
                        // break;
                    case "Median Filtering":
                        // this.medianFiltering = checkBooleanValue(cv.getValue().toString());
                        // break;
                    case "Neighbour Mode":
                        // this.neighbourMode = (String) cv.getValue();
                        // break;
                    case "Case":
                        // this.caseName = (String) cv.getValue();
                        // break;
                    case "Max Frame Gap":
                        // this.maxFrameGap = Integer.parseInt(cv.getValue().toString());
                        // break;

                    // These are values that are not recording, byt experiment attributes
                    // These fields should be int but occur as double in the files

                    case "Nr of Squares in Row":
                        // this.maxFrameGap = (int) Double.parseDouble(cv.getValue().toString());
                        // break;
                    case "Min Spots in Track":
                        // this.minNumberOfSpotsInTrack = (int) Double.parseDouble(cv.getValue().toString());
                        // break;
                    case "Min Tracks for Tau":
                        // this.minTracksForTau = (int) Double.parseDouble(cv.getValue().toString());
                        // break;
                        break;

                    // These are legacy values. They exist, but we don't use them. No reason to complain.
                    case "Experiment Date":
                    case "Ext Recording Name":
                    case "Recording Size":
                    case "Recording Sequence Nr":
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


    public Recording(String recordingName, int conditionNr, int replicateNr,
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
        this.squares = new ArrayList<>();
        this.tracks = new ArrayList<>();
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

    public List<Square> getSquares() {
        return squares;
    }

    public void setSquares(List<Square> squares) {
        this.squares = squares;
    }

    public void addSquare(Square square) {
        this.squares.add(square);
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<Track> tracks) { this.tracks = tracks; }

    public void addTrack(Track track) {
        this.tracks.add(track);
    }

    public void setTracksTable(TracksTable tracksTable) {
        this.tracksTable = tracksTable;
    }

    public void setSquaresTable(SquaresTable squaresTable) {
        this.squaresTable = squaresTable;
    }

    private static Boolean checkBooleanValue(String string) {
        Set<String> yesValues = new HashSet<>(Arrays.asList("y", "ye", "yes", "ok", "true", "t"));
        return yesValues.contains(string.trim().toLowerCase());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("Recording: ").append(recordingName).append("\n");
        sb.append("----------------------------------------------------------------------\n");

        //sb.append(String.format("%Recording data%n"));
        sb.append(String.format("\tRecording Name                : %s%n", recordingName));
        sb.append(String.format("\tCondition Nr                  : %d%n", conditionNr));
        sb.append(String.format("\tReplicate Nr                  : %d%n", replicateNr));
        sb.append(String.format("\tProbe Name                    : %s%n", probeName));
        sb.append(String.format("\tProbe Type                    : %s%n", probeType));
        sb.append(String.format("\tCell Type                     : %s%n", cellType));
        sb.append(String.format("\tAdjuvant                      : %s%n", adjuvant));
        sb.append(String.format("\tConcentration                 : %.2f%n", concentration));
        sb.append(String.format("\tThreshold                     : %.2f%n", threshold));
        // sb.append(String.format("\tProcess                       : %b%n", processFlag));
        sb.append(String.format("\tExclude                       : %b%n", exclude));
        sb.append(String.format("\tTime Stamp                    : %s%n", timeStamp));
        sb.append(String.format("\tNumber of Spots               : %d%n", numberSpots));
        sb.append(String.format("\tNumber of Tracks              : %d%n", numberTracks));
        sb.append(String.format("\tNumber of Spots in All Tracks : %d%n", numberOfSpotsInAllTracks));
        sb.append(String.format("\tRun Time                      : %.2f%n", runTime));
        sb.append(String.format("\tRecording Size                : %d%n", recordingSize));
        sb.append(String.format("\tTau                           : %.2f%n", tau));
        sb.append(String.format("\tR Squared                     : %.2f%n", rSquared));
        sb.append(String.format("\tDensity                       : %.2f%n", density));

        if (tracks != null) {
            sb.append(String.format("%nRecording %s has %d tracks", recordingName, tracks.size()));
        }
        if (squares != null) {
            sb.append(String.format("%nRecording %s has %d squares%n", recordingName, squares.size()));
        }

        if (tracksTable != null) {
            sb.append(String.format("%nRecording %s has %d tracks", recordingName, tracksTable.rowCount()));
        }
        if (squaresTable != null) {
            sb.append(String.format("%nRecording %s has %d squares%n", recordingName, squaresTable.rowCount()));
        }

        return sb.toString();
    }
}
