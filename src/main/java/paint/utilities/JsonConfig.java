package paint.utilities;

import com.google.gson.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class JsonConfig {

    private JsonObject root;
    private Path jsonFilePath;  // store path for saving

    public JsonConfig(Path jsonFilePath) {
        this.jsonFilePath = jsonFilePath;
        try (FileReader reader = new FileReader(jsonFilePath.toString())) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
            root = new JsonObject();  // initialize empty if fail
        }
    }

    public boolean reload() {
        try (FileReader reader = new FileReader(jsonFilePath.toFile())) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
            return true;
        } catch (IOException e) {
            System.err.println("Failed to reload config: " + e.getMessage());
            return false;
        }
    }

    public void remove(String section, String keyPath) {
        JsonObject sectionObj = root.getAsJsonObject(section);
        if (sectionObj == null) {
            return;
        }

        String[] keys = keyPath.split("\\.");
        JsonObject current = sectionObj;

        // Traverse down until the second-to-last key
        for (int i = 0; i < keys.length - 1; i++) {
            if (!current.has(keys[i]) || !current.get(keys[i]).isJsonObject()) {
                return; // nothing to remove
            }
            current = current.getAsJsonObject(keys[i]);
        }

        // Remove the final key
        current.remove(keys[keys.length - 1]);
    }


    public void removeAllCheckboxStates(String section) {
        JsonObject sectionObj = root.getAsJsonObject(section);
        if (sectionObj == null) {
            return;
        }

        // collect keys to avoid ConcurrentModificationException
        java.util.List<String> keysToRemove = new java.util.ArrayList<>();

        for (String key : sectionObj.keySet()) {
            if (key.startsWith("Checkbox States")) {
                keysToRemove.add(key);
            }
        }

        for (String key : keysToRemove) {
            sectionObj.remove(key);
        }
    }

    // GETTERS

    public String getString(String section, String key, String defaultValue) {
        try {
            JsonObject sectionObj = root.getAsJsonObject(section);
            if (sectionObj != null) {
                JsonElement element = sectionObj.get(key);
                if (element != null && !element.isJsonNull()) {
                    return element.getAsString();
                }
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    public int getInt(String section, String key, int defaultValue) {
        try {
            JsonObject sectionObj = root.getAsJsonObject(section);
            if (sectionObj != null) {
                JsonElement element = sectionObj.get(key);
                if (element != null && !element.isJsonNull()) {
                    return element.getAsInt();
                }
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    public double getDouble(String section, String key, double defaultValue) {
        try {
            JsonObject sectionObj = root.getAsJsonObject(section);
            if (sectionObj != null) {
                JsonElement element = sectionObj.get(key);
                if (element != null && !element.isJsonNull()) {
                    return element.getAsDouble();
                }
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    public boolean getBoolean(String section, String key, boolean defaultValue) {
        try {
            JsonObject sectionObj = root.getAsJsonObject(section);
            if (sectionObj != null) {
                JsonElement element = sectionObj.get(key);
                if (element != null && !element.isJsonNull()) {
                    return element.getAsBoolean();
                }
            }
        } catch (Exception ignored) {}
        return defaultValue;
    }

    // SETTERS

    public void setString(String section, String key, String value) {
        JsonObject sectionObj = root.has(section) && root.get(section).isJsonObject()
                ? root.getAsJsonObject(section)
                : new JsonObject();
        sectionObj.addProperty(key, value);
        root.add(section, sectionObj);
    }

    public void setInt(String section, String key, int value) {
        JsonObject sectionObj = root.has(section) && root.get(section).isJsonObject()
                ? root.getAsJsonObject(section)
                : new JsonObject();
        sectionObj.addProperty(key, value);
        root.add(section, sectionObj);
    }

    public void setDouble(String section, String key, double value) {
        JsonObject sectionObj = root.has(section) && root.get(section).isJsonObject()
                ? root.getAsJsonObject(section)
                : new JsonObject();
        sectionObj.addProperty(key, value);
        root.add(section, sectionObj);
    }

    public void setBoolean(String section, String key, boolean value) {
        JsonObject sectionObj = root.has(section) && root.get(section).isJsonObject()
                ? root.getAsJsonObject(section)
                : new JsonObject();

        sectionObj.addProperty(key, value);
        root.add(section, sectionObj);
    }

    // SAVE METHOD

    public boolean save() {
        try (FileWriter writer = new FileWriter(jsonFilePath.toFile())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(root, writer);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
            return false;
        }
    }


}