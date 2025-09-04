package paint.io;

import paint.objects.Recording;

import tech.tablesaw.api.ColumnType;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.api.*;

import java.util.ArrayList;
import java.util.List;

import static paint.constants.PaintConstants.RECORDING_COLS;

public class RecordingTableIO extends BaseTableIO<Recording> {

    public RecordingTableIO() {
        super(new RecordingAdapter());
    }

    private static class RecordingAdapter implements EntityTableAdapter<Recording> {

        public String tableName() {
            return "recordings";
        }

        public String[] columns() {
            return RECORDING_COLS;
        }

        public ColumnType[] columnTypes() {
            return new ColumnType[] {
                    ColumnType.STRING,   //  0 recordingName
                    ColumnType.INTEGER,  //  1 conditionNumber
                    ColumnType.INTEGER,  //  2 replicateNumber
                    ColumnType.STRING,   //  3 probeName
                    ColumnType.STRING,   //  4 probeType
                    ColumnType.STRING,   //  5 cellType
                    ColumnType.STRING,   //  6 adjuvant
                    ColumnType.DOUBLE,   //  7 concentration
                    ColumnType.BOOLEAN,  //  8 processFlag
                    ColumnType.DOUBLE,   //  9 thresshold

                    ColumnType.INTEGER,  // 10 numberOfSpots
                    ColumnType.INTEGER,  // 11 numberOfTracks
                    ColumnType.DOUBLE,   // 12 runTime
                    ColumnType.LONG,     // 23 recordingSize
                    ColumnType.STRING,   // 14 timeStamp
                    ColumnType.INTEGER,  // 15 numberOfSpotsInAllTracks
                    ColumnType.BOOLEAN,  // 16 exclude
                    ColumnType.DOUBLE,   // 17 tau
                    ColumnType.DOUBLE,   // 18 rSquared
                    ColumnType.DOUBLE    // 19 density
            };
        }

        public List<Column<?>> newEmptyColumns() {
            List<Column<?>> c = new ArrayList<Column<?>>(RECORDING_COLS.length);
            c.add(StringColumn.create (RECORDING_COLS[0]));
            c.add(IntColumn.create    (RECORDING_COLS[1]));
            c.add(IntColumn.create    (RECORDING_COLS[2]));
            c.add(StringColumn.create (RECORDING_COLS[3]));
            c.add(StringColumn.create (RECORDING_COLS[4]));
            c.add(StringColumn.create (RECORDING_COLS[5]));
            c.add(StringColumn.create (RECORDING_COLS[6]));
            c.add(DoubleColumn.create (RECORDING_COLS[7]));
            c.add(BooleanColumn.create(RECORDING_COLS[8]));
            c.add(DoubleColumn.create (RECORDING_COLS[9]));
            c.add(IntColumn.create    (RECORDING_COLS[10]));
            c.add(IntColumn.create    (RECORDING_COLS[11]));
            c.add(DoubleColumn.create (RECORDING_COLS[12]));
            c.add(LongColumn.create   (RECORDING_COLS[13]));
            c.add(StringColumn.create (RECORDING_COLS[14]));
            c.add(IntColumn.create    (RECORDING_COLS[15]));
            c.add(BooleanColumn.create(RECORDING_COLS[16]));
            c.add(DoubleColumn.create (RECORDING_COLS[17]));
            c.add(DoubleColumn.create (RECORDING_COLS[18]));
            c.add(DoubleColumn.create (RECORDING_COLS[19]));
            return c;
        }

        @SuppressWarnings("unchecked")
        public void appendEntity(Recording r, List<Column<?>> c) {
            ((StringColumn ) c.get(0)).append(r.getRecordingName());
            ((IntColumn    ) c.get(1)).append(r.getConditionNumber());
            ((IntColumn    ) c.get(2)).append(r.getReplicateNumber());
            ((StringColumn ) c.get(3)).append(r.getProbeName());
            ((StringColumn ) c.get(4)).append(r.getProbeType());
            ((StringColumn ) c.get(5)).append(r.getCellType());
            ((StringColumn ) c.get(6)).append(r.getAdjuvant());
            ((DoubleColumn ) c.get(7)).append(r.getConcentration());
            ((BooleanColumn) c.get(8)).append(r.isDoProcess());
            ((DoubleColumn ) c.get(9)).append(r.getThreshold());
            ((IntColumn    ) c.get(10)).append(r.getNumberOfSpots());
            ((IntColumn    ) c.get(11)).append(r.getNumberOfTracks());
            ((DoubleColumn ) c.get(12)).append(r.getRunTime());
            ((LongColumn   ) c.get(13)).append(r.getRecordingSize());
            ((StringColumn ) c.get(14)).append(r.getTimeStamp());
            ((IntColumn    ) c.get(15)).append(r.getNumberOfSpotsInAllTracks());
            ((BooleanColumn) c.get(16)).append(r.isExclude());
            ((DoubleColumn ) c.get(17)).append(r.getTau());
            ((DoubleColumn ) c.get(18)).append(r.getRSquared());
            ((DoubleColumn ) c.get(19)).append(r.getDensity());
        }

        public Recording readEntity(Table t, int r) {
            Recording rec = new Recording();
            rec.setRecordingName(t.stringColumn(RECORDING_COLS[0]).get(r));
            rec.setConditionNumber(t.intColumn(RECORDING_COLS[1]).getInt(r));
            rec.setReplicateNumber(t.intColumn(RECORDING_COLS[2]).getInt(r));
            rec.setProbeName(t.stringColumn(RECORDING_COLS[3]).get(r));
            rec.setProbeType(t.stringColumn(RECORDING_COLS[4]).get(r));
            rec.setCellType(t.stringColumn(RECORDING_COLS[5]).get(r));
            rec.setAdjuvant(t.stringColumn(RECORDING_COLS[6]).get(r));
            rec.setConcentration(t.doubleColumn(RECORDING_COLS[7]).getDouble(r));
            rec.setDoProcess(t.booleanColumn(RECORDING_COLS[8]).get(r));
            rec.setThreshold(t.doubleColumn(RECORDING_COLS[9]).getDouble(r));

            rec.setNumberOfSpots(t.intColumn(RECORDING_COLS[10]).getInt(r));
            rec.setNumberOfTracks(t.intColumn(RECORDING_COLS[11]).getInt(r));
            rec.setRunTime(t.doubleColumn(RECORDING_COLS[12]).getDouble(r));
            rec.setRecordingSize(t.longColumn(RECORDING_COLS[13]).getLong(r));
            rec.setTimeStamp(t.stringColumn(RECORDING_COLS[14]).get(r));
            rec.setNumberOfSpotsInAllTracks(t.intColumn(RECORDING_COLS[15]).getInt(r));
            rec.setExclude(t.booleanColumn(RECORDING_COLS[16]).get(r));
            rec.setTau(t.doubleColumn(RECORDING_COLS[17]).getDouble(r));
            rec.setRSquared(t.doubleColumn(RECORDING_COLS[18]).getDouble(r));
            rec.setDensity(t.doubleColumn(RECORDING_COLS[19]).getDouble(r));
            return rec;
        }
    }
}