package table;

import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.io.IOException;
import java.util.List;

public class PaintTable {

    protected Table table;

    public PaintTable() {
        this.table = Table.create();
    }

    public void readTable(String csvPath) throws IOException {
        this.table = Table.read().csv(csvPath);
    }

    public void writeTable(String csvPath) throws IOException {
        this.table.write().csv(csvPath);
    }

    public int rowCount() {
        return table.rowCount();
    }

    public List<Column<?>> columns() {
        return table.columns();
    }

    public void addColumns(Column<?>... cols) {
        table.addColumns(cols);
    }

    public void removeColumns(String... columnNames) {
        table.removeColumns(columnNames);
    }

    public void removeColumn(int index) {
        String columnName = table.column(index).name();
        table.removeColumns(columnName);
    }

    public Column<?> column(String columnName) {
        return table.column(columnName);
    }

    public String structure() {
        return table.structure().toString();
    }

    public String first(int n) {
        return table.first(n).toString();
    }

    public void setTable(Table t) {

        this.table = t;
    }

    public void insertColumn(int columnIndex, tech.tablesaw.columns.Column<?> column) {
        table.insertColumn(columnIndex, column);
    }

    public Table getTable() {
        return this.table;
    }
}