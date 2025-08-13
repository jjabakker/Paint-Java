package table;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.selection.Selection;
import java.io.IOException;
import tech.tablesaw.api.Table;
import java.util.Map;
import java.util.TreeMap;

public class Tracks extends PaintTable {

    public Tracks() {
        super();
    }

    public static Tracks load(String path) throws IOException {
        Tracks instance = new Tracks();
        instance.readTable(path);
        return instance;
    }

    public Tracks tracksInSquare(double x1, double x2, double y1, double y2) {
        DoubleColumn xCol = (DoubleColumn) table.column("Track X Location");
        DoubleColumn yCol = (DoubleColumn) table.column("Track Y Location");

        Selection xInRange = xCol.isGreaterThanOrEqualTo(x1).and(xCol.isLessThan(x2));
        Selection yInRange = yCol.isGreaterThanOrEqualTo(y1).and(yCol.isLessThan(y2));
        Selection combined = xInRange.and(yInRange);

        Tracks result = new Tracks();
        result.setTable(table.where(combined));
        return result;
    }

    public Table frequencyDistribution() {
        // DoubleColumn durations = tracks.doubleColumn("Track Duration");
        DoubleColumn durations = (DoubleColumn) this.column("Track Duration");

        Map<Double, Integer> frequencyMap = new TreeMap<>();

        // Count frequencies
        for (double val : durations) {
            frequencyMap.put(val, frequencyMap.getOrDefault(val, 0) + 1);
        }

        // Create columns for the frequency distribution table
        DoubleColumn durationCol = DoubleColumn.create("Track Duration");
        IntColumn frequencyCol = IntColumn.create("Frequency");

        // Populate columns with sorted data by duration (ascending)
        for (Map.Entry<Double, Integer> entry : frequencyMap.entrySet()) {
            durationCol.append(entry.getKey());
            frequencyCol.append(entry.getValue());
        }

        return Table.create("Frequency Distribution", durationCol, frequencyCol);
    }
}
