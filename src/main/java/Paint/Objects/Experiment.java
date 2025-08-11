package Paint.Objects;

import static Paint.Constants.PaintConstants.*;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import PaintUtilities.ColumnValue;

public class Experiment {

    private String experimentName;
    private double minRequiredRSquared;
    private double maxAllowableVariability;
    private double minRequiredDensityRatio;
    private int minTracksForTau;
    private int maxFrameGap;
    private int gapClosingMaxDistance;
    private int linkingMaxDistance;
    private boolean medianFiltering;
    private int minNumberOfSpotsInTrack;
    private String neighbourMode;
    private String caseName;

    private ArrayList<Recording> recordings;
    public TracksTable tracksTable;
    private SquaresTable  squaresTable;

    public static void main(String[] args) {
        //Experiment exp = new Experiment("221012", Paths.get("/Users/hans/Downloads/221012/Experiment Info.csv"));
        //System.out.println(exp);
    }

    public Experiment(String experimentName) {
        this.experimentName = experimentName;
        this.recordings = new ArrayList<>();
    }

//    public Experiment(String experimentName, Path projectPath) {
//
//        Path experimentPath = projectPath.resolve(experimentName).resolve(RECORDINGS_CSV);
//        this.experimentName = experimentName;
//        this.recordings = new ArrayList<>();
//
//        // Read first line to get columns count
//        String headerLine;
//        try {
//            // Ensure stream is closed promptly
//            try (java.util.stream.Stream<String> lines = Files.lines(experimentPath)) {
//                headerLine = lines.findFirst().orElseThrow(() -> new RuntimeException("Empty file"));
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        String[] columns = headerLine.split(",");
//        int nrColumns = columns.length;
//
//        // Create ColumnType array with all STRING, so that everything is read in as String
//        // Then read the data into a table
//        ColumnType[] colTypes = new ColumnType[columns.length];
//        Arrays.fill(colTypes, ColumnType.STRING);
//        CsvReadOptions options = CsvReadOptions.builder(experimentPath.toFile())
//                .columnTypes(colTypes)
//                .build();
//        Table table = Table.read().csv(options);
//
//        // Create a fresh list per row to avoid accumulating across rows
//        for (Row row : table) {
//            List<ColumnValue> data = new ArrayList<>();
//            for (int i = 0; i < nrColumns; i++) {
//                Object cell = row.getObject(i);
//                data.add(new ColumnValue(columns[i], cell == null ? "" : cell.toString()));
//            }
//            Recording recording = new Recording(data);
//            this.recordings.add(recording);
//        }
//    }

    public Experiment() {
        this.recordings = new ArrayList<>();
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public void setTracksTable(TracksTable tracksTable) {
        this.tracksTable = tracksTable;
    }

    public void setSquaresTable(SquaresTable squaresTable) {
        this.squaresTable = squaresTable;
    }


    public List<Recording> getRecordings() {
        return recordings;
    }

    public void addRecording(Recording recording) {
        this.recordings.add(recording);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("Experiment: ").append(experimentName).append("\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("\n");
        sb.append(String.format("Experiment data%n"));
        sb.append(String.format("\tMax Allowable Variability    : %.2f%n", maxAllowableVariability));
        sb.append(String.format("\tMin Required Density Ratio   : %.2f%n", minRequiredDensityRatio));
        sb.append(String.format("\tMin Required R Squared       : %.2f%n", minRequiredRSquared));
        sb.append(String.format("\tMin Tracks to Calculate Tau  : %d%n", minTracksForTau));
        sb.append(String.format("\tMax Frame Gap                : %d%n", maxFrameGap));
        sb.append(String.format("\tGap Closing Max Distance     : %d%n", gapClosingMaxDistance));
        sb.append(String.format("\tLinking Max Distance         : %d%n", linkingMaxDistance));
        sb.append(String.format("\tMedian Filtering             : %b%n", medianFiltering));
        sb.append(String.format("\tMin Number of Spots in Track : %d%n", minNumberOfSpotsInTrack));
        sb.append(String.format("\tNeighbour Mode               : %s%n", neighbourMode));
        sb.append(String.format("\tCase Name                    : %s%n", caseName));

        sb.append("\n");
        if (tracksTable != null) {
            sb.append(String.format("Experiments has %d squares loaded%n", squaresTable.rowCount()));
        }
        else {
            sb.append(String.format("Experiments has no squares loaded%n"));
        }

        if (tracksTable != null) {
            sb.append(String.format("Experiments has %d tracks loaded%n", tracksTable.rowCount()));
        }
        else {
            sb.append(String.format("Experiments has no tracks loaded%n"));
        }

        sb.append(String.format("%nExperiment %s has %d recordings%n",  experimentName,  recordings.size()));
        for (Recording recording : recordings) {
            sb.append(String.format("\t%s%n", recording.getRecordingName()));
        }

        return sb.toString();
    }
}
