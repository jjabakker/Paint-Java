package exclude;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static paint.constants.PaintConstants.TRACK_COLS;
public class TrackCSVReader {

    // Expected header order


    public static List<Track> readTracksFromCSV(String filePath) throws IOException {
        List<Track> tracks = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(Path.of(filePath), StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("CSV file is empty");
            }

            String[] headerCols = parseCSVLine(headerLine);
            if (!Arrays.equals(headerCols, TRACK_COLS.toArray(new String[0]))) {
                throw new IOException("CSV header does not match expected format.\n" +
                        "Expected: " + TRACK_COLS + "\n" +
                        "Found:    " + Arrays.asList(headerCols));
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] cols = parseCSVLine(line);

                if (cols.length != TRACK_COLS.size()) {
                    throw new IOException("Invalid CSV: expected " + TRACK_COLS.size() +
                            " columns but got " + cols.length + " in line: " + line);
                }

                Track t = new Track();
                t.setUniqueKey(cols[0]);
                t.setTrackId(parseInt(cols[1]));
                t.setTrackLabel(cols[2]);
                t.setNumberSpots(parseInt(cols[3]));
                t.setNumberGaps(parseInt(cols[4]));
                t.setLongestGap(parseInt(cols[5]));
                t.setTrackDuration(parseDouble(cols[6]));
                t.setTrackXLocation(parseDouble(cols[7]));
                t.setTrackYLocation(parseDouble(cols[8]));
                t.setTrackDisplacement(parseDouble(cols[9]));
                t.setTrackMaxSpeed(parseDouble(cols[10]));
                t.setTrackMedianSpeed(parseDouble(cols[11]));
                t.setTrackMeanSpeed(parseDouble(cols[12]));
                t.setTrackMaxSpeedCalc(parseDouble(cols[13]));
                t.setTrackMedianSpeedCalc(parseDouble(cols[14]));
                t.setTrackMeanSpeedCalc(parseDouble(cols[15]));
                t.setDiffusionCoefficient(parseDouble(cols[16]));
                t.setDiffusionCoefficientExt(parseDouble(cols[17]));
                t.setTotalDistance(parseDouble(cols[18]));
                t.setConfinementRatio(parseDouble(cols[19]));

                tracks.add(t);
            }
        }
        return tracks;
    }

    private static int parseInt(String s) {
        if (s == null || s.isEmpty()) return 0;
        return Integer.parseInt(s);
    }

    private static double parseDouble(String s) {
        if (s == null || s.isEmpty()) return 0.0;
        return Double.parseDouble(s);
    }

    private static String[] parseCSVLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    tokens.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
        }
        tokens.add(sb.toString());
        return tokens.toArray(new String[0]);
    }
}