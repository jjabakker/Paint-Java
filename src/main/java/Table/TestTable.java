package Table;

import PaintUtilities.AppLogger;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.Table;


import java.io.IOException;
import java.util.logging.Logger;

public class TestTable {

    private static Logger log;

    public static void main(String[] args) throws IOException {

        // Start fresh every run:
        // Initialize the logger before anything else uses it
        AppLogger.init("TrackMate Batch.log", false);
        log = AppLogger.getLogger();

        // Or: append if exists
        // AppLogger.init("my-log.log", true);

        log.info("This is an info message");

        // Read recordings
        Recordings recordings = Recordings.load("/Users/hans/Downloads/All Recordings.csv");

        // Check Integrity
        if (recordings.checkIntegrity()) {
            log.info("Integrity OK");
            // System.out.println("\nIntegrity OK\n");
        }
        else {
            // System.out.println("\nIntegrity not OK\n");
            log.info("Integrity not OK");
        }

        // Inspection
        System.out.println("\n--- Structure ---\n");
        System.out.println(recordings.structure());

        System.out.println("\n--- First 5 rows ---\n");
        System.out.println(recordings.first(5));

        // Filter on Process == "Yes"
        StringColumn processCol = recordings.stringColumn("Process");
        Recordings filteredRecordings = recordings.where(processCol.isEqualTo("Yes"));

        System.out.println("\n--- Filtered rows (Process == y) ---\n");
        System.out.println(filteredRecordings);

        // Add column by name at end
        DoubleColumn newDoubleCol = DoubleColumn.create("Score 1", filteredRecordings.rowCount());
        for (int i = 0; i < filteredRecordings.rowCount(); i++) {
            newDoubleCol.set(i, Math.random());
        }
        filteredRecordings.addColumns(newDoubleCol);  // Add columns at the end of the table

        // Add column byn name at index
        IntColumn newIntCol = IntColumn.create("Score 2", filteredRecordings.rowCount());
        for (int i = 0; i < filteredRecordings.rowCount(); i++) {
            newIntCol.set(i, (int) (Math.random()*10));
        }
        filteredRecordings.insertColumn(2, newIntCol);

        filteredRecordings.removeColumns("Process");
        filteredRecordings.removeColumn(0);

        // Write CSV
        filteredRecordings.writeTable("filtered_output.csv");

        // Read recordings
        Tracks tracks = Tracks.load("/Users/hans/Downloads/All Tracks.csv");

        Tracks squareTracks = tracks.tracksInSquare(10.0, 20.0, 15.0, 25.0);

        DoubleColumn xCol = (DoubleColumn) squareTracks.column("Track X Location");
        DoubleColumn yCol = (DoubleColumn) squareTracks.column("Track Y Location");
        DoubleColumn trackDuration = (DoubleColumn) squareTracks.column("Track Duration");

        for (int i = 0; i < squareTracks.rowCount(); i++) {
            double x = xCol.getDouble(i);
            double y = yCol.getDouble(i);
            double duration = trackDuration.getDouble(i);
            System.out.printf("x: %.3f, y: %.3f, y: %.3f%n", x, y, trackDuration.getDouble(i));
        }

        Table histogram = tracks.frequencyDistribution();

        System.out.println("Filtered: " + tracks.rowCount());
        System.out.println("Original: " + squareTracks.rowCount());


        System.out.println("\n--- Successfully saved to filtered_output.csv ---\n");

    }
}
