package paint.csv;

import paint.objects.Track;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import tech.tablesaw.api.Table;
import java.util.HashSet;
import java.util.Set;

import static paint.constants.PaintConstants.TRACK_COLS;

public class TrackCSVWriter {

    public static void writeTracksTableToCSV(Table tracksTable, String filePath) throws IOException {
        // Validate that all expected columns are present
        Set<String> nameSet = new HashSet<String>(tracksTable.columnNames());
        for (String expected : TRACK_COLS) {
            if (!nameSet.contains(expected)) {
                throw new IllegalArgumentException("Missing expected column: " + expected);
            }
        }

        // Reorder to fixed column order
        Table normalized = tracksTable.copy().retainColumns(TRACK_COLS);

        // Write to CSV with a single header row
        normalized.write().csv(filePath);
    }

    public static void writeTracksToCSV(List<Track> tracks, String filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
            // Header
            writer.append(String.join(",", TRACK_COLS));

            // Rows (CSV-escaped)
            for (Track t : tracks) {
                writer.append(csv(t.getUniqueKey())).append(",")
                        .append(csv(t.getRecordingName()))          .append(",")
                        .append(csv(t.getTrackId()))                .append(",")
                        .append(csv(t.getTrackLabel()))             .append(",")
                        .append(csv(t.getNumberSpots()))            .append(",")
                        .append(csv(t.getNumberGaps()))             .append(",")
                        .append(csv(t.getLongestGap()))             .append(",")
                        .append(csv(t.getTrackDuration()))          .append(",")
                        .append(csv(t.getTrackXLocation()))         .append(",")
                        .append(csv(t.getTrackYLocation()))         .append(",")
                        .append(csv(t.getTrackDisplacement()))      .append(",")
                        .append(csv(t.getTrackMaxSpeed()))          .append(",")
                        .append(csv(t.getTrackMedianSpeed()))       .append(",")
                        .append(csv(t.getTrackMeanSpeed()))         .append(",")
                        .append(csv(t.getTrackMaxSpeedCalc()))      .append(",")
                        .append(csv(t.getTrackMedianSpeedCalc()))   .append(",")
                        .append(csv(t.getTrackMeanSpeedCalc()))     .append(",")
                        .append(csv(t.getDiffusionCoefficient()))   .append(",")
                        .append(csv(t.getDiffusionCoefficientExt())).append(",")
                        .append(csv(t.getTotalDistance()))          .append(",")
                        .append(csv(t.getConfinementRatio()))
                        .append("\n");
            }
        }
    }

    /** CSV-escape per RFC 4180: quote if the value contains comma, quote, or newline; double quotes inside. */
    private static String csv(Object value) {
        if (value == null) return "";
        String s = String.valueOf(value);
        boolean mustQuote = s.indexOf(',') >= 0 || s.indexOf('"') >= 0 || s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0;
        if (mustQuote) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
}