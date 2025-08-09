package Paint.Loaders;

import Paint.Objects.PaintSquare;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PaintSquareLoader {

    public static void main(String[] args) {

        List<PaintSquare> squares = loadSquares(Paths.get("/Users/hans/Downloads/221012/All Squares.csv"), null);
        System.out.println("All tracks count: " + squares.size());

        squares = loadAllSquares(Paths.get("/Users/hans/Downloads/221012/All Squares.csv"));
        System.out.println("All squares count: " + squares.size());

        List<PaintSquare> filteredSquares = loadSquares(Paths.get("/Users/hans/Downloads/221012/All Squares.csv"), "221012-Exp-1-A1-2-threshold-8");
        System.out.println("Filtered squares count: " + filteredSquares.size());
    }



    public static List<PaintSquare> loadAllSquares(Path csvPath) {

        // Load all the squares from CSV

        return loadSquares(csvPath, null);
    }




    public static List<PaintSquare> loadSquares(Path csvPath, String recordingName) {

        // Load squares from CSV, optionally filtered by recordingName.
        // If the recordingName is null or empty, all tracks are loaded.

        Table table = null;

        try {
            table = Table.read().csv(csvPath.toFile());
        } catch (Exception e) {
            String errorMsg = e.toString(); // e.g. "java.io.FileNotFoundException: /path/to/file (No such file or directory)"
            int colonIndex = errorMsg.lastIndexOf(":");
            String messageAfterColon = (colonIndex != -1) ? errorMsg.substring(colonIndex + 1).trim() : errorMsg;
            System.err.println("Failed to read squares file: " + messageAfterColon);
            System.exit(-1);
        }

        if (recordingName != null && !recordingName.isEmpty() && table.containsColumn("Ext Recording Name")) {
            if (table.column("Ext Recording Name") instanceof StringColumn) {
                StringColumn recordingCol = table.stringColumn("Ext Recording Name");
                table = table.where(recordingCol.isEqualTo(recordingName));
            }
        }
        return createSquares(table);
    }

    private static List<PaintSquare> createSquares(Table table) {
        List<PaintSquare> squares = new ArrayList<>();

        try {
            for (Row row : table) {
                PaintSquare square = new PaintSquare(
                        row.getString("Unique Key"),
                        row.getInt("Recording Sequence Nr"),
                        row.getInt("Condition Nr"),
                        row.getInt("Replicate Nr"),
                        row.getInt("Square Nr"),
                        row.getInt("Row Nr"),
                        row.getInt("Col Nr"),
                        row.getInt("label Nr"),
                        row.getInt("Cell Id"),
                        row.getInt("Nr Spots"),
                        row.getInt("Nr Tracks"),
                        row.getDouble("X0"),
                        row.getDouble("Y0"),
                        row.getDouble("X1"),
                        row.getDouble("Y1"),
                        row.getBoolean("Selected"),
                        row.getDouble("Variability"),
                        row.getDouble("Density"),
                        row.getDouble("Density Ratio"),
                        Double.valueOf (row.getInt("Tau")),
                        row.getDouble("R Squared"),
                        row.getDouble("Median Diffusion Coefficient"),
                        row.getDouble("Mean Diffusion Coefficient"),
                        row.getDouble("Median Diffusion Coefficient Ext"),
                        row.getDouble("Mean Diffusion Coefficient Ext"),
                        row.getDouble("Median Long Track Duration"),
                        row.getDouble("Median Short Track Duration"),
                        row.getDouble("Median Displacement"),
                        row.getDouble("Max Displacement"),
                        row.getDouble("Total Displacement"),
                        row.getDouble("Median Max Speed"),
                        row.getDouble("Max Max Speed"),
                        row.getDouble("Median Mean Speed"),
                        row.getDouble("Max Mean Speed"),
                        row.getDouble("Max Track Duration"),
                        row.getDouble("Total Track Duration"),
                        row.getDouble("Median Track Duration"),
                        row.getBoolean("Square Manually Excluded"),
                        row.getBoolean("Image Excluded")
                );
                squares.add(square);
            }
        } catch (Exception e) {
            System.err.println("Failed to load squares - columns contain data in wrong format: " + e.getMessage());
            System.exit(-1);
        }
        return squares;
    }
}