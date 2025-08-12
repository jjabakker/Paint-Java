package Paint.Loaders;

import Paint.Objects.Square;
import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

import java.util.ArrayList;
import java.util.List;

public class SquaresLoader {

    public static void main(String[] args) {/* implementation omitted for shortness */}

    //public static List<PaintSquare> loadAllSquares(Path csvPath) {/* implementation omitted for shortness */}

    //public static List<PaintSquare> loadSquares(Path csvPath, String recordingName) {/* implementation omitted for shortness */}

    // New: public, side-effect-free conversion from a preloaded/filtered Table
//    public static List<Square> fromTable(Table table) {
//        return createSquares(table);
//    }

//    private static List<Square> createSquares(Table table) {
//        List<Square> squares = new ArrayList<>();
//
//        try {
//            for (Row row : table) {
//                Square square = new Square(
//                        row.getString("Unique Key"),
//                        row.getInt("Recording Sequence Nr"),
//                        row.getInt("Condition Nr"),
//                        row.getInt("Replicate Nr"),
//                        row.getInt("Square Nr"),
//                        row.getInt("Row Nr"),
//                        row.getInt("Col Nr"),
//                        row.getInt("label Nr"),
//                        row.getInt("Cell Id"),
//                        row.getInt("Nr Spots"),
//                        row.getInt("Nr Tracks"),
//                        row.getDouble("X0"),
//                        row.getDouble("Y0"),
//                        row.getDouble("X1"),
//                        row.getDouble("Y1"),
//                        row.getBoolean("Selected"),
//                        row.getDouble("Variability"),
//                        row.getDouble("Density"),
//                        row.getDouble("Density Ratio"),
//                        Double.valueOf(row.getInt("Tau")),
//                        row.getDouble("R Squared"),
//                        row.getDouble("Median Diffusion Coefficient"),
//                        row.getDouble("Mean Diffusion Coefficient"),
//                        row.getDouble("Median Diffusion Coefficient Ext"),
//                        row.getDouble("Mean Diffusion Coefficient Ext"),
//                        row.getDouble("Median Long Track Duration"),
//                        row.getDouble("Median Short Track Duration"),
//                        row.getDouble("Median Displacement"),
//                        row.getDouble("Max Displacement"),
//                        row.getDouble("Total Displacement"),
//                        row.getDouble("Median Max Speed"),
//                        row.getDouble("Max Max Speed"),
//                        row.getDouble("Median Mean Speed"),
//                        row.getDouble("Max Mean Speed"),
//                        row.getDouble("Max Track Duration"),
//                        row.getDouble("Total Track Duration"),
//                        row.getDouble("Median Track Duration"),
//                        row.getBoolean("Square Manually Excluded"),
//                        row.getBoolean("Image Excluded")
//                );
//                squares.add(square);
//            }
//        } catch (Exception e) {
//            System.err.println("Failed to load squares - columns contain data in wrong format: " + e.getMessage());
//            System.exit(-1);
//        }
//        return squares;
//    }
}