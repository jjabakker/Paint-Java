package Paint.Loaders;

import Paint.Objects.TracksTable;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.nio.file.Path;
import java.nio.file.Paths;

import static PaintUtilities.ExceptionUtils.friendlyMessage;

public class LoadTracksTable {

    public static void main(String[] args) {
        TracksTable tracks = LoadTracksTable(Paths.get("/Users/hans/Downloads/Paint Data - v38/Regular Probes/Paint Regular Probes - 20 Squares/221012/All Tracks.csv"), "");
        System.out.println("Tracks count: " + tracks.rowCount());
    }

    public static TracksTable LoadTracksTable(Path csvPath) {
        Table table;

        try {
            table = Table.read().csv(csvPath.toFile());
        } catch (Exception e) {
            System.err.println("Failed to read tracks file: " + friendlyMessage(e));
            System.exit(-1);
            return null; // Unreachable, but keeps compiler happy for Java 8
        }

        // Wrap the Tablesaw table; no casting
        return TracksTable.of(table);
    }

    public static TracksTable LoadTracksTable(Path csvPath, String recordingName) {
        Table table;

        try {
            table = Table.read().csv(csvPath.toFile());
        } catch (Exception e) {
            System.err.println("Failed to read tracks file: " + friendlyMessage(e));
            System.exit(-1);
            return null; // Unreachable, but keeps compiler happy for Java 8
        }

        // Optional filtering by recordingName
        if (recordingName != null && !recordingName.isEmpty() && table.containsColumn("Ext Recording Name")) {
            if (table.column("Ext Recording Name") instanceof StringColumn) {
                StringColumn recordingCol = table.stringColumn("Ext Recording Name");
                table = table.where(recordingCol.isEqualTo(recordingName));
            }
        }

        // Wrap the Tablesaw table; no casting
        return TracksTable.of(table);
    }
}