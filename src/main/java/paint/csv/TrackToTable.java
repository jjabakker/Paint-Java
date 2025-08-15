package paint.csv;

import paint.objects.Track;
import tech.tablesaw.api.*;
import java.util.ArrayList;
import java.util.List;

import static paint.constants.PaintConstants.TRACK_COLS;

public class TrackToTable {

    public static Table emptyTrackTable() {
        return Table.create("tracks",
                StringColumn.create(TRACK_COLS[0]),  // 0  Unique Key
                StringColumn.create(TRACK_COLS[1]),  // 1  Ext Recording Name
                IntColumn.create(TRACK_COLS[2]),     // 2  Track Id
                StringColumn.create(TRACK_COLS[3]),  // 3  Track Label
                IntColumn.create(TRACK_COLS[4]),     // 4  Number Spots
                IntColumn.create(TRACK_COLS[5]),     // 5  Number Gaps
                IntColumn.create(TRACK_COLS[6]),     // 6  Longest Gap
                DoubleColumn.create(TRACK_COLS[7]),  // 7  Track Duration
                DoubleColumn.create(TRACK_COLS[8]),  // 8  Track X Location
                DoubleColumn.create(TRACK_COLS[9]),  // 9  Track Y Location
                DoubleColumn.create(TRACK_COLS[10]), // 10 Track Displacement
                DoubleColumn.create(TRACK_COLS[11]), // 11 Track Max Speed
                DoubleColumn.create(TRACK_COLS[12]), // 12 Track Median Speed
                DoubleColumn.create(TRACK_COLS[13]), // 13 Track Mean Speed
                DoubleColumn.create(TRACK_COLS[14]), // 14 Track Max Speed Calc
                DoubleColumn.create(TRACK_COLS[15]), // 15 Track Median Speed Calc
                DoubleColumn.create(TRACK_COLS[16]), // 16 Track Mean Speed Calc
                DoubleColumn.create(TRACK_COLS[17]), // 17 Diffusion Coefficient
                DoubleColumn.create(TRACK_COLS[18]), // 18 Diffusion Coefficient Ext
                DoubleColumn.create(TRACK_COLS[19]), // 19 Total Distance
                DoubleColumn.create(TRACK_COLS[20]), // 20 Confinement Ratio
                IntColumn.create(TRACK_COLS[21]),    // 21 Square Number
                IntColumn.create(TRACK_COLS[22])     // 22 Label Number
        );
    }

    public static Table toTable(List<Track> tracks) {
        StringColumn  c0  = StringColumn.create (TRACK_COLS[0]);   // 0  Unique Key
        StringColumn  c1  = StringColumn.create (TRACK_COLS[1]);   // 1  Ext Recording Name
        IntColumn     c2  = IntColumn.create    (TRACK_COLS[2]);   // 2  Track Id
        StringColumn  c3  = StringColumn.create (TRACK_COLS[3]);   // 3  Track Label
        IntColumn     c4  = IntColumn.create    (TRACK_COLS[4]);   // 4  Number Spots
        IntColumn     c5  = IntColumn.create    (TRACK_COLS[5]);   // 5  Number Gaps
        IntColumn     c6  = IntColumn.create    (TRACK_COLS[6]);   // 6  Longest Gap
        DoubleColumn  c7  = DoubleColumn.create (TRACK_COLS[7]);   // 7  Track Duration
        DoubleColumn  c8  = DoubleColumn.create (TRACK_COLS[8]);   // 8  Track X Location
        DoubleColumn  c9  = DoubleColumn.create (TRACK_COLS[9]);   // 9  Track Y Location
        DoubleColumn  c10 = DoubleColumn.create (TRACK_COLS[10]);  // 10 Track Displacement
        DoubleColumn  c11 = DoubleColumn.create (TRACK_COLS[11]);  // 11 Track Max Speed
        DoubleColumn  c12 = DoubleColumn.create (TRACK_COLS[12]);  // 12 Track Median Speed
        DoubleColumn  c13 = DoubleColumn.create (TRACK_COLS[13]);  // 13 Track Mean Speed
        DoubleColumn  c14 = DoubleColumn.create (TRACK_COLS[14]);  // 14 Track Max Speed Calc
        DoubleColumn  c15 = DoubleColumn.create (TRACK_COLS[15]);  // 15 Track Median Speed Calc
        DoubleColumn  c16 = DoubleColumn.create (TRACK_COLS[16]);  // 16 Track Mean Speed Calc
        DoubleColumn  c17 = DoubleColumn.create (TRACK_COLS[17]);  // 17 Diffusion Coefficient
        DoubleColumn  c18 = DoubleColumn.create (TRACK_COLS[18]);  // 18 Diffusion Coefficient Ext
        DoubleColumn  c19 = DoubleColumn.create (TRACK_COLS[19]);  // 19 Total Distance
        DoubleColumn  c20 = DoubleColumn.create (TRACK_COLS[20]);  // 20 Confinement Ratio
        IntColumn     c21 = IntColumn.create    (TRACK_COLS[21]);  // 21 Square Number
        IntColumn     c22 = IntColumn.create    (TRACK_COLS[22]);  // 22 Label Number

        for (Track t : tracks) {
            append(c0,  t.getUniqueKey());                   // 0
            append(c1,  t.getRecordingName());               // 1
            append(c2,  t.getTrackId());                     // 2
            append(c3,  t.getTrackLabel());                  // 3
            append(c4,  t.getNumberSpots());                 // 4
            append(c5,  t.getNumberGaps());                  // 5
            append(c6,  t.getLongestGap());                  // 6
            append(c7,  t.getTrackDuration());               // 7
            append(c8,  t.getTrackXLocation());              // 8
            append(c9,  t.getTrackYLocation());              // 9
            append(c10, t.getTrackDisplacement());           // 10
            append(c11, t.getTrackMaxSpeed());               // 11
            append(c12, t.getTrackMedianSpeed());            // 12
            append(c13, t.getTrackMeanSpeed());              // 13
            append(c14, t.getTrackMaxSpeedCalc());           // 14
            append(c15, t.getTrackMedianSpeedCalc());        // 15
            append(c16, t.getTrackMeanSpeedCalc());          // 16
            append(c17, t.getDiffusionCoefficient());        // 17
            append(c18, t.getDiffusionCoefficientExt());     // 18
            append(c19, t.getTotalDistance());               // 19
            append(c20, t.getConfinementRatio());            // 20
            append(c21, t.getSquareNumber());                // 21
            append(c22, t.getLabelNumber());                 // 22

        }

        return Table.create("tracks",
                c0, c1, c2, c3, c4, c5, c6,
                c7, c8, c9, c10, c11, c12, c13,
                c14, c15, c16, c17, c18, c19, c20, c21, c22
        );
    }

    /** Convert Tablesaw Table -> List<Track> (expects exact schema/order). */
    public static List<Track> toTracks(Table table) {
        // Validate header matches TRACK_COLS
        for (int i = 0; i < TRACK_COLS.length; i++) {
            if (!table.column(i).name().equals(TRACK_COLS[i])) {
                throw new IllegalArgumentException("Unexpected column at index " + i +
                        ": found '" + table.column(i).name() + "', expected '" + TRACK_COLS[i] + "'");
            }
        }

        List<Track> out = new ArrayList<>(table.rowCount());
        StringColumn  c0  = table.stringColumn(TRACK_COLS[0]);
        StringColumn  c1  = table.stringColumn(TRACK_COLS[1]);
        IntColumn     c2  = table.intColumn   (TRACK_COLS[2]);
        StringColumn  c3  = table.stringColumn(TRACK_COLS[3]);
        IntColumn     c4  = table.intColumn   (TRACK_COLS[4]);
        IntColumn     c5  = table.intColumn   (TRACK_COLS[5]);
        IntColumn     c6  = table.intColumn   (TRACK_COLS[6]);
        DoubleColumn  c7  = table.doubleColumn(TRACK_COLS[7]);
        DoubleColumn  c8  = table.doubleColumn(TRACK_COLS[8]);
        DoubleColumn  c9  = table.doubleColumn(TRACK_COLS[9]);
        DoubleColumn  c10 = table.doubleColumn(TRACK_COLS[10]);
        DoubleColumn  c11 = table.doubleColumn(TRACK_COLS[11]);
        DoubleColumn  c12 = table.doubleColumn(TRACK_COLS[12]);
        DoubleColumn  c13 = table.doubleColumn(TRACK_COLS[13]);
        DoubleColumn  c14 = table.doubleColumn(TRACK_COLS[14]);
        DoubleColumn  c15 = table.doubleColumn(TRACK_COLS[15]);
        DoubleColumn  c16 = table.doubleColumn(TRACK_COLS[16]);
        DoubleColumn  c17 = table.doubleColumn(TRACK_COLS[17]);
        DoubleColumn  c18 = table.doubleColumn(TRACK_COLS[18]);
        DoubleColumn  c19 = table.doubleColumn(TRACK_COLS[19]);
        DoubleColumn  c20 = table.doubleColumn(TRACK_COLS[20]);
        IntColumn     c21 = table.intColumn   (TRACK_COLS[21]);
        IntColumn     c22 = table.intColumn   (TRACK_COLS[22]);

        for (int r = 0; r < table.rowCount(); r++) {
            Track t = new Track();
            t.setUniqueKey(c0.get(r));
            t.setRecordingName(c1.get(r));        // Ext Recording Name
            t.setTrackId(c2.getInt(r));
            t.setTrackLabel(c3.get(r));
            t.setNumberSpots(c4.getInt(r));
            t.setNumberGaps(c5.getInt(r));
            t.setLongestGap(c6.getInt(r));
            t.setTrackDuration(c7.getDouble(r));
            t.setTrackXLocation(c8.getDouble(r));
            t.setTrackYLocation(c9.getDouble(r));
            t.setTrackDisplacement(c10.getDouble(r));
            t.setTrackMaxSpeed(c11.getDouble(r));
            t.setTrackMedianSpeed(c12.getDouble(r));
            t.setTrackMeanSpeed(c13.getDouble(r));
            t.setTrackMaxSpeedCalc(c14.getDouble(r));
            t.setTrackMedianSpeedCalc(c15.getDouble(r));
            t.setTrackMeanSpeedCalc(c16.getDouble(r));
            t.setDiffusionCoefficient(c17.getDouble(r));
            t.setDiffusionCoefficientExt(c18.getDouble(r));
            t.setTotalDistance(c19.getDouble(r));
            t.setConfinementRatio(c20.getDouble(r));
            t.setSquareNumber(c21.getInt(r));
            t.setLabelNumber(c22.getInt(r));
            out.add(t);
        }
        return out;
    }

    // --- Append helpers that gracefully handle nulls ---
    private static void append(StringColumn col, String v) {
        if (v == null) col.appendMissing(); else col.append(v);
    }
    private static void append(IntColumn col, Integer v) {
        if (v == null) col.appendMissing(); else col.append(v);
    }
    private static void append(DoubleColumn col, Double v) {
        if (v == null) col.appendMissing(); else col.append(v);
    }
}
