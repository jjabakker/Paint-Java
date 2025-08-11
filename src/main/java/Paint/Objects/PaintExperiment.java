package Paint.Objects;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import PaintUtilities.ColumnValue;

public class PaintExperiment {

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

    private ArrayList<PaintRecording> paintRecordings;

    public static void main(String[] args) {
        PaintExperiment p = new PaintExperiment(Paths.get("/Users/hans/Downloads/221012/Experiment Info.csv"));
    }

    public PaintExperiment(Path experimentPath) {

        this.paintRecordings = new ArrayList<>();

        // Read first line to get columns count
        String headerLine;
        try {
            // Ensure stream is closed promptly
            try (java.util.stream.Stream<String> lines = Files.lines(experimentPath)) {
                headerLine = lines.findFirst().orElseThrow(() -> new RuntimeException("Empty file"));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String[] columns = headerLine.split(",");
        int nrColumns = columns.length;

        // Create ColumnType array with all STRING, so that everything is read in as String
        // Then read the data into a table
        ColumnType[] colTypes = new ColumnType[columns.length];
        Arrays.fill(colTypes, ColumnType.STRING);
        CsvReadOptions options = CsvReadOptions.builder(experimentPath.toFile())
                .columnTypes(colTypes)
                .build();
        Table table = Table.read().csv(options);

        // Create a fresh list per row to avoid accumulating across rows
        for (Row row : table) {
            List<ColumnValue> data = new ArrayList<>();
            for (int i = 0; i < nrColumns; i++) {
                Object cell = row.getObject(i);
                data.add(new ColumnValue(columns[i], cell == null ? "" : cell.toString()));
            }
            PaintRecording paintRecording = new PaintRecording(data);
            this.paintRecordings.add(paintRecording);
        }
    }

    public PaintExperiment() {
        this.paintRecordings = new ArrayList<>();
    }

    public String getExperimentName() {
        return experimentName;
    }

    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public List<PaintRecording> getRecordings() {
        return paintRecordings;
    }

    public void addRecording(PaintRecording paintRecording) {
        this.paintRecordings.add(paintRecording);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append("Experiment: ").append(experimentName).append("\n");
        sb.append("----------------------------------------------------------------------\n");

        sb.append(String.format("%nExperiment data%n"));
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

        sb.append(String.format("%nExperiment %s has %d recordings%n",  experimentName,  paintRecordings.size()));
        for (PaintRecording paintRecording : paintRecordings) {
            sb.append(String.format("\t%s%n", paintRecording.getRecordingName()));
        }

        return sb.toString();
    }
}
