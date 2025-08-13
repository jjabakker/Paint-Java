package paint.utilities;

import java.io.IOException;
import java.nio.file.*;

public class FileCleanForTrackMate {

    public static void deleteAssociatedFiles(Path experimentInfoFile) {
        Path parentDir = experimentInfoFile.getParent();

        if (parentDir == null) {
            System.err.println("❌ experimentInfoFile has no parent directory.");
            return;
        }

        Path allTracks = parentDir.resolve("All Tracks.csv");
        Path allRecordings = parentDir.resolve("All Recordings.csv");

        //deleteIfExists(allTracks);
        //deleteIfExists(allRecordings);
    }

    private static void deleteIfExists(Path path) {
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("🗑️ Deleted: " + path);
            } else {
                System.out.println("ℹ️ File not found (no deletion needed): " + path);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to delete " + path + ": " + e.getMessage());
        }
    }
}
