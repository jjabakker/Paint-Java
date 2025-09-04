package paint.objects;

import java.util.*;

import paint.utilities.ColumnValue;
import tech.tablesaw.api.Table;


public class Recording {

    // The first set exists in both Experiment Info and All Recordings Experiment Info
    private String recordingName;
    private int conditionNumber;
    private int replicateNumber;
    private String probeName;
    private String probeType;
    private String cellType;
    private String adjuvant;
    private double concentration;
    private boolean doProcess;
    private double threshold;

    // The second set exists only in All Recordings
    private int numberOfSpots;
    private int numberOfTracks;
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
    private Table tracksTable;

    //
    // Constructors
    //

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
                    case "Probe Name":
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
                        this.timeStamp = curValue;
                        break;


                    // Integer values

                    case "Condition Number":
                        this.conditionNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Replicate Number":
                        this.replicateNumber = (int) Double.parseDouble(curValue);
                        break;
                    case "Number of Spots":
                        this.numberOfSpots = (int) Double.parseDouble(curValue);
                        break;
                    case "Number of Tracks":
                        this.numberOfTracks = (int) Double.parseDouble(curValue);
                        break;
                    case "Number of Spots in All Tracks":
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

                    case "Process Flag":
                        this.doProcess = checkBooleanValue(curValue);
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


    public Recording(String recordingName, int conditionNumber, int replicateNumber,
                     String probeName, String probeType, String cellType, String adjuvant,
                     double concentration, boolean doProcess, double threshold) {
        this.recordingName = recordingName;
        this.conditionNumber = conditionNumber;
        this.replicateNumber = replicateNumber;
        this.probeName = probeName;
        this.probeType = probeType;
        this.cellType = cellType;
        this.adjuvant = adjuvant;
        this.concentration = concentration;
        this.doProcess = doProcess;
        this.threshold = threshold;
        this.squares = new ArrayList<>();
        this.tracks = new ArrayList<>();
    }

    //
    // Getters and setters
    //

    public String getRecordingName() { return recordingName; }
    public void setRecordingName(String recordingName) { this.recordingName = recordingName; }

    public int getConditionNumber() { return conditionNumber; }
    public void setConditionNumber(int conditionNumber) { this.conditionNumber = conditionNumber; }

    public int getReplicateNumber() { return replicateNumber; }
    public void setReplicateNumber(int replicateNumber) { this.replicateNumber = replicateNumber; }

    public String getProbeName() { return probeName; }
    public void setProbeName(String probeName) { this.probeName = probeName; }

    public String getProbeType() { return probeType; }
    public void setProbeType(String probeType) { this.probeType = probeType; }

    public String getCellType() { return cellType; }
    public void setCellType(String cellType) { this.cellType = cellType; }

    public String getAdjuvant() { return adjuvant; }
    public void setAdjuvant(String adjuvant) { this.adjuvant = adjuvant; }

    public double getConcentration() { return concentration; }
    public void setConcentration(double concentration) { this.concentration = concentration; }

    public boolean isDoProcess() { return doProcess; }
    public void setDoProcess(boolean doProcess) { this.doProcess = doProcess; }

    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }

    public int getNumberOfSpots() { return numberOfSpots; }
    public void setNumberOfSpots(int numberOfSpots) { this.numberOfSpots = numberOfSpots; }

    public int getNumberOfTracks() { return numberOfTracks; }
    public void setNumberOfTracks(int numberOfTracks) { this.numberOfTracks = numberOfTracks; }

    public double getRunTime() { return runTime; }
    public void setRunTime(double runTime) { this.runTime = runTime; }

    public int getRecordingSize() { return recordingSize; }
    public void setRecordingSize(int recordingSize) { this.recordingSize = recordingSize; }

    public String getTimeStamp() { return timeStamp; }
    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }

    public int getNumberOfSpotsInAllTracks() { return numberOfSpotsInAllTracks; }
    public void setNumberOfSpotsInAllTracks(int numberOfSpotsInAllTracks) { this.numberOfSpotsInAllTracks = numberOfSpotsInAllTracks; }

    public boolean isExclude() { return exclude; }
    public void setExclude(boolean exclude) { this.exclude = exclude; }

    public double getTau() { return tau; }
    public void setTau(double tau) { this.tau = tau; }

    public double getRSquared() { return rSquared; }
    public void setRSquared(double rSquared) { this.rSquared = rSquared; }

    public double getDensity() { return density; }
    public void setDensity(double density) { this.density = density; }

    public List<Square> getSquares() { return squares; }

    public List<Track> getTracks() { return tracks; }

    public void setTracks(List<Track> tracks) { this.tracks = tracks; }

    public void setTracksTable(Table tracksTable) { this.tracksTable = tracksTable;}

    public void addSquares(List <Square> squares) { this.squares.addAll(squares); }
    public void addSquare(Square square) { this.squares.add(square); }

    public void addTrack(Track track) {
        this.tracks.add(track);
    }

    private static Boolean checkBooleanValue(String string) {
        Set<String> yesValues = new HashSet<>(Arrays.asList("y", "ye", "yes", "ok", "true", "t"));
        return yesValues.contains(string.trim().toLowerCase());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("\tRecording Name                : %s%n",   recordingName));
        sb.append(String.format("\tCondition Nr                  : %d%n", conditionNumber));
        sb.append(String.format("\tReplicate Nr                  : %d%n", replicateNumber));
        sb.append(String.format("\tProbe Name                    : %s%n",   probeName));
        sb.append(String.format("\tProbe Type                    : %s%n",   probeType));
        sb.append(String.format("\tCell Type                     : %s%n",   cellType));
        sb.append(String.format("\tAdjuvant                      : %s%n",   adjuvant));
        sb.append(String.format("\tConcentration                 : %.2f%n", concentration));
        sb.append(String.format("\tThreshold                     : %.2f%n", threshold));
        sb.append(String.format("\tExclude                       : %b%n",   exclude));
        sb.append(String.format("\tTime Stamp                    : %s%n",   timeStamp));
        sb.append(String.format("\tNumber of Spots               : %d%n", numberOfSpots));
        sb.append(String.format("\tNumber of Tracks              : %d%n", numberOfTracks));
        sb.append(String.format("\tNumber of Spots in All Tracks : %d%n",   numberOfSpotsInAllTracks));
        sb.append(String.format("\tRun Time                      : %.2f%n", runTime));
        sb.append(String.format("\tRecording Size                : %d%n",   recordingSize));
        sb.append(String.format("\tTau                           : %.2f%n", tau));
        sb.append(String.format("\tR Squared                     : %.2f%n", rSquared));
        sb.append(String.format("\tDensity                       : %.2f%n", density));

        if (tracks != null) {
            sb.append(String.format("\tNumber of tracks              : %d%n", tracks.size()));
        }
        if (squares != null) {
            sb.append(String.format("\tNumber of square              : %d%n", squares.size()));
        }

        int numberOfSquaresWithTracks = 0;
        for (Square square : squares) {
            if (square.getTracks() != null && square.getTracks().size() > 0) {
                numberOfSquaresWithTracks += 1;
            };
        }
        sb.append(String.format("\tNumber of squares with tracks : %d%n", numberOfSquaresWithTracks));

        return sb.toString();
    }
}
