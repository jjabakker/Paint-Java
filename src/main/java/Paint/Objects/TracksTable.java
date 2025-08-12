package Paint.Objects;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static PaintUtilities.CSVHandling.readCSV;
import static PaintUtilities.ExceptionUtils.friendlyMessage;

/**
 * Lightweight wrapper around a Tablesaw Table representing tracks.
 * Uses composition instead of extending Tablesaw's Table to avoid casting issues.
 */

public final class TracksTable {

    private Table table = null;


    public TracksTable (Path csvPath) {
        this.table = readCSV(csvPath);
    }

    private TracksTable(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("table cannot be null");
        }
        this.table = table;
    }

    public static TracksTable of(Table table) {
        return new TracksTable(table);
    }

    // Expose only what you need. Add more delegations as required.

    public int rowCount() {
        return table.rowCount();
    }

    public boolean containsColumn(String name) {
        return table.containsColumn(name);
    }

    public StringColumn stringColumn(String name) {
        return table.stringColumn(name);
    }

    public TracksTable where(tech.tablesaw.selection.Selection selection) {
        return new TracksTable(table.where(selection));
    }

    public Table toTable() {
        return table;
    }
}
