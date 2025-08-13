package paint.utilities;

public class ColumnValue {
    private String columnName;
    private String value;

    public ColumnValue(String columnName, String value) {
        this.columnName = columnName;
        this.value = value;
    }

    public String getColumnName() { return columnName; }
    public Object getValue() { return value; }
}