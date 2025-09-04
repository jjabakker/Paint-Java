import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

import paint.loaders.ProjectDataLoader;
import paint.objects.Project;
import paint.objects.Square;

public class ProjectDataLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadProject_withNoProjectInfoCsv_returnsEmptyProject() {
        // Given a minimal project folder (no files inside)
        Path projectRoot = tempDir.resolve("MyProject");
        assertTrue(projectRoot.toFile().mkdirs());

        // When
        List<String> experimentNames = new ArrayList<>();
        Project project = ProjectDataLoader.loadProject(projectRoot, experimentNames,false);

        // Then
        assertNotNull(project);
        assertEquals("MyProject", project.getProjectName());
        assertNotNull(project.getContext());
        assertNotNull(project.getExperiments());
        assertEquals(0, project.getExperiments().size());
    }

    @Test
    void filterTracksInSquare_exclusiveRightAndBottomBorders_whenNotLastRowOrCol() {
        // Table with track positions
        DoubleColumn x = DoubleColumn.create("Track X Location", new double[] { 5.0, 10.0, 0.0, 10.0, 9.999, 5.0 });
        DoubleColumn y = DoubleColumn.create("Track Y Location", new double[] { 5.0, 10.0, 0.0, 5.0, 10.0, 0.001 });
        Table tracks = Table.create("tracks", x, y);

        // Square from (0,0) to (10,10), not in last row/col
        Square sq = new Square();
        sq.setX0(0.0);
        sq.setY0(0.0);
        sq.setX1(10.0);
        sq.setY1(10.0);
        sq.setColNumber(0);
        sq.setRowNumber(0);

        int lastRowCol = 5; // square is not the last row/col

        Table inside = ProjectDataLoader.filterTracksInSquare(tracks, sq, lastRowCol);

        // Expected to include points strictly inside plus top/left borders:
        // (5,5), (0,0), (5,0.001)
        // Excludes right/bottom borders (10,*) and (*,10)
        assertEquals(3, inside.rowCount());
        assertTrue(inside.doubleColumn("Track X Location").contains(5.0));
        assertTrue(inside.doubleColumn("Track X Location").contains(0.0));
        assertTrue(inside.doubleColumn("Track X Location").contains(5.0));
        // Verify excluded boundary cases
        assertFalse(inside.doubleColumn("Track X Location").contains(10.0));
        assertFalse(inside.doubleColumn("Track Y Location").contains(10.0));
    }

    @Test
    void filterTracksInSquare_inclusiveRightAndBottomBorders_whenLastRowAndCol() {
        // Table with track positions including right/bottom borders
        DoubleColumn x = DoubleColumn.create("Track X Location", new double[] { 0.0, 10.0, 10.0, 5.0 });
        DoubleColumn y = DoubleColumn.create("Track Y Location", new double[] { 0.0, 5.0, 10.0, 10.0 });
        Table tracks = Table.create("tracks", x, y);

        // Square from (0,0) to (10,10), and it IS the last row/col
        Square sq = new Square();
        sq.setX0(0.0);
        sq.setY0(0.0);
        sq.setX1(10.0);
        sq.setY1(10.0);
        sq.setColNumber(7);
        sq.setRowNumber(7);

        int lastRowCol = 7; // indicates this is the last row and last column

        Table inside = ProjectDataLoader.filterTracksInSquare(tracks, sq, lastRowCol);

        // Should include right and bottom borders when at last row/col:
        // Expect all four rows to be included
        assertEquals(4, inside.rowCount());
        assertTrue(inside.doubleColumn("Track X Location").contains(10.0));
        assertTrue(inside.doubleColumn("Track Y Location").contains(10.0));
    }
}