package paint.utilities;

import tech.tablesaw.api.Table;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.NumberColumn; // non-generic in older Tablesaw versions
import tech.tablesaw.columns.Column;

public final class TablesawUtils {

    private TablesawUtils() { /* no instances */ }

    /**
     * Ensures the given column exists in the table and is a DoubleColumn.
     * If it's numeric of another type, or a StringColumn, it will be converted.
     * Missing/invalid values are written as Double.NaN.
     *
     * Compatible with Java 8 and older Tablesaw versions where NumberColumn is non-generic
     * and Selection#and(...) may be mutating.
     */
    public static DoubleColumn ensureDoubleColumn(Table t, String colName) {
        if (t == null) throw new IllegalArgumentException("Table is null");
        if (colName == null) throw new IllegalArgumentException("Column name is null");

        if (!t.columnNames().contains(colName)) {
            throw new IllegalArgumentException("Column not found: " + colName);
        }

        Column col = t.column(colName);

        // Already a DoubleColumn
        if (col instanceof DoubleColumn) {
            return (DoubleColumn) col;
        }

        // Other numeric column type (IntegerColumn, LongColumn, etc.)
        if (col instanceof NumberColumn) {
            NumberColumn nc = (NumberColumn) col;
            DoubleColumn dc = DoubleColumn.create(colName);
            for (int i = 0; i < nc.size(); i++) {
                double v = nc.isMissing(i) ? Double.NaN : nc.getDouble(i);
                dc.append(v);
            }
            t.replaceColumn(colName, dc);
            return dc;
        }

        // String column — try to parse doubles
        if (col instanceof StringColumn) {
            StringColumn sc = (StringColumn) col;
            DoubleColumn dc = DoubleColumn.create(colName);
            for (int i = 0; i < sc.size(); i++) {
                String s = sc.get(i);
                Double v = parseDoubleSafe(s);
                dc.append(v == null ? Double.NaN : v.doubleValue());
            }
            t.replaceColumn(colName, dc);
            return dc;
        }

        throw new IllegalArgumentException("Column " + colName +
                " is not numeric or string: " + col.type());
    }

    private static Double parseDoubleSafe(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        s = s.replace(',', '.'); // Handle decimal commas like "12,34"
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
