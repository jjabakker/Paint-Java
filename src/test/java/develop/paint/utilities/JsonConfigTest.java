package develop.paint.utilities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import paint.utilities.JsonConfig;

public class JsonConfigTest {

    @TempDir
    Path tempDir;

    @Test
    public void loadsDefaultsWhenFileDoesNotExist() throws Exception {
        File nonExistent = tempDir.resolve("no_config.json").toFile();
        Path path = nonExistent.toPath();
        assertFalse(nonExistent.exists());

        JsonConfig cfg = new JsonConfig(path);

        // Existing default keys should return their default values
        assertEquals(3, cfg.getInt("TrackMate", "MAX_FRAME_GAP", -1));
        assertEquals(0.6, cfg.getDouble("TrackMate", "LINKING_MAX_DISTANCE", -1.0), 1e-9);
        assertFalse(cfg.getBoolean("TrackMate", "DO_MEDIAN_FILTERING", true));

        // Nonexistent key should return provided fallback default
        assertEquals("Free", cfg.getString("Generate Squares", "NEIGHBOUR_MODE", "Free"));

        // Existing default key name is "Neighbour Mode" (capital N)
        assertEquals("Free", cfg.getString("Generate Squares", "Neighbour Mode", "X"));
    }

    @Test
    public void setAndGetValuesAndKeys() throws Exception {
        Path path = tempDir.resolve("in_mem.json");
        JsonConfig cfg = new JsonConfig(path);

        cfg.setInt("S1", "K1", 42);
        cfg.setDouble("S1", "K2", 3.14);
        cfg.setBoolean("S1", "K3", true);
        cfg.setString("S2", "Name", "Alice");

        assertEquals(42, cfg.getInt("S1", "K1", -1));
        assertEquals(3.14, cfg.getDouble("S1", "K2", -1.0), 1e-9);
        assertTrue(cfg.getBoolean("S1", "K3", false));
        assertEquals("Alice", cfg.getString("S2", "Name", "Bob"));

        Set<String> s1Keys = cfg.keys("S1");
        assertTrue(s1Keys.contains("K1"));
        assertTrue(s1Keys.contains("K2"));
        assertTrue(s1Keys.contains("K3"));

        Set<String> s2Keys = cfg.keys("S2");
        assertTrue(s2Keys.contains("Name"));
    }

    @Test
    public void saveAndReloadFromDisk() throws Exception {
        Path path = tempDir.resolve("config.json");

        JsonConfig cfg = new JsonConfig(path);
        cfg.setInt("TrackMate", "MAX_FRAME_GAP", 7);
        cfg.setDouble("TrackMate", "LINKING_MAX_DISTANCE", 1.25);
        cfg.setBoolean("TrackMate", "DO_MEDIAN_FILTERING", true);
        cfg.setString("Generate Squares", "Neighbour Mode", "Nearest");
        cfg.save();

        JsonConfig cfg2 = new JsonConfig(path);
        assertEquals(7, cfg2.getInt("TrackMate", "MAX_FRAME_GAP", -1));
        assertEquals(1.25, cfg2.getDouble("TrackMate", "LINKING_MAX_DISTANCE", -1.0), 1e-9);
        assertTrue(cfg2.getBoolean("TrackMate", "DO_MEDIAN_FILTERING", false));
        assertEquals("Nearest", cfg2.getString("Generate Squares", "Neighbour Mode", "Free"));
    }

    @Test
    public void removeSingleKey() throws Exception {
        Path path = tempDir.resolve("remove_single.json");
        JsonConfig cfg = new JsonConfig(path);

        cfg.setInt("Section", "A", 10);
        cfg.setInt("Section", "B", 20);
        assertEquals(10, cfg.getInt("Section", "A", -1));
        assertEquals(20, cfg.getInt("Section", "B", -1));

        cfg.remove("Section", "A");
        assertEquals(-1, cfg.getInt("Section", "A", -1));
        assertEquals(20, cfg.getInt("Section", "B", -1));
    }

    @Test
    public void removeWithPrefixRemovesAllMatchingKeys() throws Exception {
        Path path = tempDir.resolve("remove_prefix.json");
        JsonConfig cfg = new JsonConfig(path);

        cfg.setBoolean("Generate Squares", "Checkbox-1", true);
        cfg.setBoolean("Generate Squares", "Checkbox-2", false);
        cfg.setInt("Generate Squares", "Other", 5);

        cfg.removeWithPrefix("Generate Squares", "Checkbox-");

        // Removed
        assertFalse(cfg.getBoolean("Generate Squares", "Checkbox-1", false));
        assertFalse(cfg.getBoolean("Generate Squares", "Checkbox-2", false));

        // Not removed
        assertEquals(5, cfg.getInt("Generate Squares", "Other", -1));
    }
}
