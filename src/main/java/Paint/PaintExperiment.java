package Paint;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.ColumnType;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.*;

import PaintUtilities.ColumnValue;


public class PaintExperiment {

    private String experimentName;
    private ArrayList<PaintRecording> paintRecordings;

    public static void main(String[] args) {
        PaintExperiment p = new PaintExperiment(Paths.get("/Users/hans/Downloads/221012/Experiment Info.csv"));
    }


    public PaintExperiment(Path experimentPath) {

        PaintRecording recording;
        this.paintRecordings = new ArrayList<>();

        // Read first line to get columns count
        String headerLine = null;
        try {
            headerLine = Files.lines(experimentPath).findFirst().orElseThrow(() -> new RuntimeException("Empty file"));
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

        // Cycle through the records and create name - value pairs
        // Create a PaintRecording with that data and store it in the Experiment
        List<ColumnValue> data = new ArrayList<>();
        for (Row row : table) {
            for (int i = 0; i < nrColumns; i++) {
                data.add(new ColumnValue(columns[i], row.getObject(i).toString()));
            }
            PaintRecording paintRecording = new PaintRecording(data);
            this.paintRecordings.add(paintRecording);
        }
    }

    public PaintExperiment() {
        this.paintRecordings = new ArrayList<>();
    }


    public void setExperimentName(String experimentName) {
        this.experimentName = experimentName;
    }

    public List<PaintRecording> getRecordings() {
        return paintRecordings;
    }

//    public void setRecordings(List<PaintRecording> recordings) {
//        this.paintRecordings = recordings;
//    }

    public void addRecording(PaintRecording paintRecording) {
        this.paintRecordings.add(paintRecording);
    }

}
