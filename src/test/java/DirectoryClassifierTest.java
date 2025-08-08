import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import Paint.DirectoryClassifier;

public class DirectoryClassifierTest {

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("testDir");
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(tempDir)) {
            deleteRecursively(tempDir);
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                for (Path entry : stream.collect(Collectors.toList())) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    private void createFiles(Path base, String... files) throws IOException {
        for (String f : files) {
            Files.createFile(base.resolve(f));
        }
    }

    private void createDirs(Path base, String... dirs) throws IOException {
        for (String d : dirs) {
            Files.createDirectories(base.resolve(d));
        }
    }

    @Test
    void testMatureExperiment() throws IOException {
        createFiles(tempDir, "Experiment Info.csv", "All Recordings.csv", "All Squares.csv");
        createDirs(tempDir, "Brightfield Images", "TrackMate Images");

        DirectoryClassifier.ClassificationResult result =
                DirectoryClassifier.classifyDirectoryWork(tempDir);

        assertEquals(DirectoryClassifier.DirectoryType.EXPERIMENT, result.type);
        assertEquals(DirectoryClassifier.Maturity.MATURE, result.maturity);
        assertNull(result.feedback);
    }

    @Test
    void testImmatureExperiment() throws IOException {
        createFiles(tempDir, "Experiment Info.csv", "All Recordings.csv");
        createDirs(tempDir, "Brightfield Images", "TrackMate Images");

        DirectoryClassifier.ClassificationResult result =
                DirectoryClassifier.classifyDirectoryWork(tempDir);

        assertEquals(DirectoryClassifier.DirectoryType.EXPERIMENT, result.type);
        assertEquals(DirectoryClassifier.Maturity.IMMATURE, result.maturity);
        assertNull(result.feedback);
    }

    @Test
    void testMatureProject() throws IOException {
        // Project root files
        createFiles(tempDir, "All Recordings.csv", "All Tracks.csv", "All Squares.csv");

        // Create Experiment subdir
        Path expDir = tempDir.resolve("Experiment1");
        Files.createDirectories(expDir);
        createFiles(expDir, "Experiment Info.csv", "All Recordings.csv", "All Squares.csv");
        createDirs(expDir, "Brightfield Images", "TrackMate Images");

        DirectoryClassifier.ClassificationResult result =
                DirectoryClassifier.classifyDirectoryWork(tempDir);

        assertEquals(DirectoryClassifier.DirectoryType.PROJECT, result.type);
        assertEquals(DirectoryClassifier.Maturity.MATURE, result.maturity);
        assertNull(result.feedback);
    }

    @Test
    void testImmatureProject() throws IOException {
        // Missing some project files
        createFiles(tempDir, "All Recordings.csv");

        // Create Experiment subdir
        Path expDir = tempDir.resolve("Experiment1");
        Files.createDirectories(expDir);
        createFiles(expDir, "Experiment Info.csv", "All Recordings.csv");
        createDirs(expDir, "Brightfield Images", "TrackMate Images");

        DirectoryClassifier.ClassificationResult result =
                DirectoryClassifier.classifyDirectoryWork(tempDir);

        assertEquals(DirectoryClassifier.DirectoryType.PROJECT, result.type);
        assertEquals(DirectoryClassifier.Maturity.IMMATURE, result.maturity);
        assertNull(result.feedback);
    }

    @Test
    void testUnknownDirectory() throws IOException {
        createFiles(tempDir, "random.txt");

        DirectoryClassifier.ClassificationResult result =
                DirectoryClassifier.classifyDirectoryWork(tempDir);

        assertEquals(DirectoryClassifier.DirectoryType.UNKNOWN, result.type);
        assertEquals(DirectoryClassifier.Maturity.IMMATURE, result.maturity);
        assertNotNull(result.feedback);
        assertTrue(result.feedback.contains("Not an Experiment"));
        assertTrue(result.feedback.contains("Not a Project"));
    }
}