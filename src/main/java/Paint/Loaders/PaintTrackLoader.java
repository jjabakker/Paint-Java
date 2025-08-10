package Paint.Loaders;

import Paint.Objects.PaintTrack;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PaintTrackLoader {

    public static void main(String[] args) {

        List<PaintTrack> tracks = loadTracks(Paths.get("/Users/hans/Downloads/221012/All Tracks.csv"), null);
        System.out.println("All tracks count: " + tracks.size());

        tracks = loadAllTracks(Paths.get("/Users/hans/Downloads/221012/All Tracks.csv"));
        System.out.println("All tracks count: " + tracks.size());

        List<PaintTrack> filteredTracks = loadTracks(Paths.get("/Users/hans/Downloads/221012/All Tracks.csv"), "221012-Exp-1-A1-2-threshold-8");
        System.out.println("Filtered tracks count: " + filteredTracks.size());
    }

    public static List<PaintTrack> loadAllTracks(Path csvPath)  {
        // Load all the tracks from CSV
        return loadTracks(csvPath, null);
    }

    public static List<PaintTrack> loadTracks(Path csvPath, String recordingName)  {
        // Load tracks from CSV, optionally filtered by recordingName.
        // If the recordingName is null or empty, all tracks are loaded.

        Table table = null;

        try {
            table = Table.read().csv(csvPath.toFile());
        } catch (Exception e) {
            String errorMsg = e.toString(); // e.g. "java.io.FileNotFoundException: /path/to/file (No such file or directory)"
            int colonIndex = errorMsg.lastIndexOf(":");
            String messageAfterColon = (colonIndex != -1) ? errorMsg.substring(colonIndex + 1).trim() : errorMsg;
            System.err.println("Failed to read tracks file: " + messageAfterColon);
            System.exit(-1);
        }

        if (recordingName != null && !recordingName.isEmpty() && table.containsColumn("Ext Recording Name")) {
            if (table.column("Ext Recording Name") instanceof StringColumn) {
                StringColumn recordingCol = table.stringColumn("Ext Recording Name");
                table = table.where(recordingCol.isEqualTo(recordingName));
            }
        }
        return createTracks(table);
    }

    // New: public, side-effect-free conversion from a preloaded/filtered Table
    public static List<PaintTrack> fromTable(Table table) {
        return createTracks(table);
    }

//    public static List<PaintTrack> loadTracksSet(String csvPath, String recordingName) {
//
//    }

    private static List<PaintTrack> createTracks(Table table) {

        List<PaintTrack> tracks = new ArrayList<>();

        try {
            for (Row row : table) {
                PaintTrack track = new PaintTrack(
                        String.valueOf(row.getInt("Track Id")),
                        row.getString("Track Label"),
                        row.getInt("Nr Spots"),
                        row.getInt("Nr Gaps"),
                        row.getInt("Longest Gap"),
                        row.getDouble("Track Duration"),
                        row.getDouble("Track X Location"),
                        row.getDouble("Track Y Location"),
                        row.getDouble("Track Displacement"),
                        row.getDouble("Track Max Speed"),
                        row.getDouble("Track Median Speed"),
                        row.getDouble("Track Mean Speed"),
                        row.getDouble("Track Max Speed Calc"),
                        row.getDouble("Track Median Speed Calc"),
                        row.getDouble("Track Mean Speed Calc"),
                        row.getDouble("Diffusion Coefficient"),
                        row.getDouble("Diffusion Coefficient Ext"),
                        row.getDouble("Total Distance"),
                        row.getDouble("Confinement Ratio")
                );
                tracks.add(track);
            }
        }
        catch (Exception e) {
            System.err.println("Failed to load tracks - columns contain data in wrong format: " + e.getMessage());
            System.exit(-1);
        }
        return tracks;
    }
}