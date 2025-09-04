package paint.utilities;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonConfig {

    private final Path path;
    private final Gson gson;
    private final Map<String, Map<String, Object>> data;

    public JsonConfig(Path path) {
        this.path = path;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.data = new HashMap<>();

        if (Files.exists(path)) {
            loadFromFile();
        } else {
            loadDefaults();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadFromFile() {
        try (Reader reader = Files.newBufferedReader(path)) {
            Map<String, Map<String, Object>> loaded =
                    gson.fromJson(reader, new TypeToken<Map<String, Map<String, Object>>>() {}.getType());
            if (loaded != null) {
                data.putAll(loaded);
            }
        } catch (IOException e) {
            System.err.println("Failed to read config file: " + e.getMessage());
            loadDefaults();
        }
    }

    private void loadDefaults() {
        // === Generate Squares ===
        Map<String, Object> generateSquares = new HashMap<>();
        generateSquares.put("Plot to File", false);
        generateSquares.put("Min Tracks to Calculate Tau", 20);
        generateSquares.put("Max Track Duration", 1000000);
        generateSquares.put("Min Required R Squared", 0.1);
        generateSquares.put("Min Track Duration", 0);
        generateSquares.put("Fraction of Squares to Determine Background", 0.1);
        generateSquares.put("Nr of Squares in Row", 30);
        generateSquares.put("Exclude zero DC tracks from Tau Calculation", false);
        generateSquares.put("Max Allowable Variability", 10.0);
        generateSquares.put("Min Required Density Ratio", 2.0);
        generateSquares.put("Plot Max", 5);
        generateSquares.put("Neighbour Mode", "Free");
        generateSquares.put("Last Used Directory", "");
        data.put("Generate Squares", generateSquares);

        // === Paint ===
        Map<String, Object> paint = new HashMap<>();
        paint.put("Version", "1.0");
        paint.put("Image File Extension", ".nd2");
        paint.put("Fiji Path", "/Applications/Fiji.app");
        data.put("Paint", paint);

        // === Recording Viewer ===
        Map<String, Object> recordingViewer = new HashMap<>();
        recordingViewer.put("Save Mode", "Ask");
        data.put("Recording Viewer", recordingViewer);

        // === TrackMate ===
        Map<String, Object> trackMate = new HashMap<>();
        trackMate.put("MAX_FRAME_GAP", 3);
        trackMate.put("ALTERNATIVE_LINKING_COST_FACTOR", 1.05);
        trackMate.put("DO_SUBPIXEL_LOCALIZATION", false);
        trackMate.put("MIN_NR_SPOTS_IN_TRACK", 3);
        trackMate.put("LINKING_MAX_DISTANCE", 0.6);
        trackMate.put("MAX_NR_SPOTS_IN_IMAGE", 2000000);
        trackMate.put("GAP_CLOSING_MAX_DISTANCE", 1.2);
        trackMate.put("TARGET_CHANNEL", 1);
        trackMate.put("SPLITTING_MAX_DISTANCE", 15.0);
        trackMate.put("TRACK_COLOURING", "TRACK_DURATION");
        trackMate.put("RADIUS", 0.5);
        trackMate.put("ALLOW_GAP_CLOSING", true);
        trackMate.put("DO_MEDIAN_FILTERING", false);
        trackMate.put("ALLOW_TRACK_SPLITTING", false);
        trackMate.put("ALLOW_TRACK_MERGING", false);
        trackMate.put("MERGING_MAX_DISTANCE", 15.0);
        data.put("TrackMate", trackMate);

        // === User Directories ===
        Map<String, Object> userDirs = new HashMap<>();
        userDirs.put("Project Directory", "");
        userDirs.put("Experiment Directory", "");
        userDirs.put("Level", "Project");
        userDirs.put("Images Directory", "");
        data.put("User Directories", userDirs);
    }

    public void save() throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            gson.toJson(data, writer);
        }
    }

    // === Getters with defaults ===
    public int getInt(String section, String key, int defaultValue) {
        Object val = get(section, key);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return defaultValue;
    }

    public double getDouble(String section, String key, double defaultValue) {
        Object val = get(section, key);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return defaultValue;
    }

    public String getString(String section, String key, String defaultValue) {
        Object val = get(section, key);
        if (val instanceof String) {
            return (String) val;
        }
        return defaultValue;
    }

    public boolean getBoolean(String section, String key, boolean defaultValue) {
        Object val = get(section, key);
        if (val instanceof Boolean) {
            return (Boolean) val;
        }
        return defaultValue;
    }

    private Object get(String section, String key) {
        Map<String, Object> current = data.get(section);
        return current != null ? current.get(key) : null;
    }

    // === Setters ===
    public void setInt(String section, String key, int value) {
        set(section, key, value);
    }

    public void setDouble(String section, String key, double value) {
        set(section, key, value);
    }

    public void setString(String section, String key, String value) {
        set(section, key, value);
    }

    public void setBoolean(String section, String key, boolean value) {
        set(section, key, value);
    }

    private void set(String section, String key, Object value) {
        Map<String, Object> current = data.computeIfAbsent(section, k -> new HashMap<>());
        current.put(key, value);
    }

    public void remove(String section, String key) {
        Map<String, Object> current = data.get(section);
        if (current != null) {
            current.remove(key);
        }
    }

    /**
     * Remove all entries in a section whose keys start with the given prefix.
     * This is useful to clear a whole logical group (e.g., "Checkbox States.")
     * without knowing the exact keys ahead of time.
     */
    public void removeWithPrefix(String section, String prefix) {
        Map<String, Object> current = data.get(section);
        if (current == null || current.isEmpty()) {
            return;
        }
        // collect first to avoid ConcurrentModificationException
        java.util.List<String> toRemove = new java.util.ArrayList<>();
        for (String key : current.keySet()) {
            if (key.startsWith(prefix)) {
                toRemove.add(key);
            }
        }
        for (String k : toRemove) {
            current.remove(k);
        }
    }


    // Optional: list keys in a section
    public Set<String> keys(String section) {
        Map<String, Object> current = data.get(section);
        if (current != null) {
            return current.keySet();
        }
        return Collections.emptySet();
    }
}