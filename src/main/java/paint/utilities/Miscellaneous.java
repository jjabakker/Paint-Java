package paint.utilities;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;



public class Miscellaneous {

    /** Always read CSV with ALL columns forced to STRING. */
    public static Table readTableAsStrings(Path csvPath) throws Exception {
        String headerLine;
        try (BufferedReader br = Files.newBufferedReader(csvPath)) {
            headerLine = br.readLine();
        }
        if (headerLine == null) {
            return Table.create(csvPath.getFileName().toString());
        }

        // simple split on comma; switch to a CSV parser if headers may contain commas in quotes
        int columnCount = headerLine.split(",", -1).length;

        ColumnType[] types = new ColumnType[columnCount];
        Arrays.fill(types, ColumnType.STRING);

        CsvReadOptions options = CsvReadOptions.builder(csvPath.toFile())
                .header(true)
                .columnTypes(types)
                .build();

        return Table.read().usingOptions(options);
    }

    public static String friendlyMessage(Throwable t) {
        if (t == null)
            return "";
        String m = t.toString();
        int colon = m.lastIndexOf(':');
        return (colon != -1) ? m.substring(colon + 1).trim() : m;
    }

    // Same idea, but from the root cause
    public static String rootCauseFriendlyMessage(Throwable t) {
        if (t == null) return "";
        Throwable cur = t;
        while (cur.getCause() != null) cur = cur.getCause();
        return friendlyMessage(cur);
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        StringBuilder sb = new StringBuilder();
        if (hours > 0) sb.append(hours).append(" hour").append(hours > 1 ? "s " : " ");
        if (minutes > 0) sb.append(minutes).append(" minute").append(minutes > 1 ? "s " : " ");
        if (secs > 0 || sb.length() == 0) sb.append(secs).append(" second").append(secs != 1 ? "s" : "");

        return sb.toString().trim();
    }

    public static void deleteAssociatedFiles(Path experimentInfoFile) {
        Path parentDir = experimentInfoFile.getParent();

        if (parentDir == null) {
            System.err.println("❌ experimentInfoFile has no parent directory.");
            return;
        }

        Path allTracks = parentDir.resolve("All Tracks.csv");
        Path allRecordings = parentDir.resolve("All Recordings.csv");

        //deleteIfExists(allTracks);
        //deleteIfExists(allRecordings);
    }

    private static void deleteIfExists(Path path) {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("🗑️ Deleted: " + path);
            } else {
                System.out.println("ℹ️ File not found (no deletion needed): " + path);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to delete " + path + ": " + e.getMessage());
        }
    }
}
