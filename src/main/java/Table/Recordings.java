package Table;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.selection.Selection;

import java.io.IOException;

public class Recordings extends PaintTable {

    public static void main(String[] args) throws IOException {
    }

    public Recordings() {
        super();
    }

    public static Recordings load(String path) throws IOException {
        Recordings instance = new Recordings();
        instance.readTable(path);
        return instance;
    }

    public Recordings where(Selection selection) {
        Recordings filtered = new Recordings();
        filtered.setTable(this.table.where(selection));
        return filtered;
    }

    public StringColumn stringColumn(String columnName) {
        return table.stringColumn(columnName);
    }

    public Boolean checkIntegrity() {
        String[] columnsToCheck = {
                "Recording Sequence Nr",
                "Recording Name",
                "Experiment Date",
                "Experiment Name",
                "Condition Nr",
                "Replicate Nr",
                "Probe",
                "Probe Type",
                "Cell Type",
                "Adjuvant",
                "Concentration",
                "Threshold",
                "Process",
                "Ext Recording Name",
                "Nr Spots",
                "Recording Size",
                "Run Time",
                "Time Stamp" };

        for (String colName : columnsToCheck) {
            if (!table.columnNames().contains(colName)) {
                return false;
            }
        }
        return true;
    }
}
