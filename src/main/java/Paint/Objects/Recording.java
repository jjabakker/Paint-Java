package Paint.Objects;

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

    public Recording() {
        this.squares = new ArrayList<>();
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
                        this.recordingName = curValue;
                        break;
                    case "Probe":
                        this.probeName = curValue;
                        break;
                    case "Probe Type":
                        this.probeType = curValue;
                        break;
                    case "Cell Type":
                        this.cellType = curValue;
                        break;
                    case "Adjuvant":
                        this.adjuvant = curValue;
                        break;
                    case "Time Stamp":
                        this.timeStamp = toString();
                        break;


                    // Integer values

                    case "Condition Nr":
                        this.conditionNr = (int) Double.parseDouble(curValue);
                        break;
                    case "Replicate Nr":
                        this.replicateNr = (int) Double.parseDouble(curValue);
                        break;
                    case "Nr Spots":
                        this.numberSpots = (int) Double.parseDouble(curValue);
                        break;
                    case "Nr Tracks":
                        this.numberTracks = (int) Double.parseDouble(curValue);
                        break;
                    case "Nr Spots in All Tracks":
                        this.numberOfSpotsInAllTracks = (int) Double.parseDouble(curValue);
                        break;
                    case "Recording Size":
                        this.recordingSize = (int) Double.parseDouble(curValue);
                        break;

                    // Double values

                    case "Concentration":
                        this.concentration = Double.parseDouble(curValue);
                        break;
                    case "Threshold":
                        this.threshold = Double.parseDouble(curValue);
                        break;
                    case "Run Time":
                        this.runTime = Double.parseDouble(curValue);
                        break;
                   case "Tau":
                        this.tau = Double.parseDouble(curValue);
                        break;
                    case "Density":
                        this.density = Double.parseDouble(curValue);
                        break;
                    case "R Squared":
                        this.rSquared = Double.parseDouble(curValue);
                        break;

                    // Boolean values

                    case "Process":
                        this.processFlag = checkBooleanValue(curValue);
                        break;
                    case "Exclude":
                        this.exclude = checkBooleanValue(curValue);
                        break;

                    // These are values that are not recording, byt experiment attributes

                    case "Experiment Name":
                    case "Gap Closing Max Distance":
                    case "Linking Max Distance":
                    case "Min Required R Squared":
                    case "Max Allowable Variability":
                    case "Min Required Density Ratio":
                    case "Median Filtering":
                    case "Neighbour Mode":
                    case "Case":
                    case "Max Frame Gap":
                    case "Nr of Squares in Row":
                    case "Min Spots in Track":
                    case "Min Tracks for Tau":
                        break;

                    // These are legacy values. They exist, but we don't use them. No reason to complain.
                    case "Experiment Date":
                    case "Ext Recording Name":
                    case "Recording Sequence Nr":
                        break;

                    default:
                        System.out.println("Warning: Unknown column " + cv.getColumnName());
                        break;
                }
            }
        } catch (Exception e) {
            System.err.printf("Error parsing column: %s. Conflicting value is %s.%n", curColumn, curValue);
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

        return sb.toString();
    }
}
