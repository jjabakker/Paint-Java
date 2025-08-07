package Paint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;

public class JsonConfigReader {

    private JsonObject root;

    public JsonConfigReader(String jsonFilePath) {
        try (FileReader reader = new FileReader(jsonFilePath)) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
            root = null;
        }
    }

    // Retrieve value as String; returns defaultValue if missing or error
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

    // Retrieve value as int; returns defaultValue if missing or error
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

    // Retrieve value as double; returns defaultValue if missing or error
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

    // You can add more getter methods as needed (boolean, long, etc.)
}
