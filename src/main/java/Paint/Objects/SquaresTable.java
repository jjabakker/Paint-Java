package Paint.Objects;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.nio.file.Path;

import static PaintUtilities.ExceptionUtils.friendlyMessage;

/**
 * Lightweight wrapper around a Tablesaw Table representing tracks.
 * Uses composition instead of extending Tablesaw's Table to avoid casting issues.
 */

public final class SquaresTable {

    private Table table = null;


    public SquaresTable(Path csvPath) {
        try {
            table = Table.read().csv(csvPath.toFile());
        } catch (Exception e) {
            System.err.println("Failed to read tracks file: " + friendlyMessage(e));
            System.exit(-1);
        }
    }

//    TracksTable (Path csvPath, String recordingName) {
//        Table table;
//
//        try {
//            table = Table.read().csv(csvPath.toFile());
//        } catch (Exception e) {
//            System.err.println("Failed to read tracks file: " + friendlyMessage(e));
//            System.exit(-1);
//        }
//
//        // Optional filtering by recordingName
//        if (recordingName != null && !recordingName.isEmpty() && table.containsColumn("Ext Recording Name")) {
//            if (table.column("Ext Recording Name") instanceof StringColumn) {
//                StringColumn recordingCol = table.stringColumn("Ext Recording Name");
//                table = table.where(recordingCol.isEqualTo(recordingName));
//            }
//        }
//
//    }


    private SquaresTable(Table table) {
        if (table == null) {
            throw new IllegalArgumentException("table cannot be null");
        }
        this.table = table;
    }

    public static SquaresTable of(Table table) {
        return new SquaresTable(table);
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

    public SquaresTable where(tech.tablesaw.selection.Selection selection) {
        return new SquaresTable(table.where(selection));
    }

    public Table toTable() {
        return table;
    }
}
