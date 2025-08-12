package PaintUtilities;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

import java.nio.file.Path;
import java.util.*;


public class CSVHandling {

    public static Table readCSV(Path filePath) {
        Table table;

        try {
            Table probe = Table.read().csv(filePath.toFile());
            ColumnType[] allString = new ColumnType[probe.columnCount()];
            Arrays.fill(allString, ColumnType.STRING);

            CsvReadOptions options = CsvReadOptions.builder(filePath.toFile())
                    .columnTypes(allString)
                    .build();

            table = Table.read().csv(options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return table;
    }

}
