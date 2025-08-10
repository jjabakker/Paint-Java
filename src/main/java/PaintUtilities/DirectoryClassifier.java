package PaintUtilities;

import Paint.Constants.PaintConstants;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DirectoryClassifier {

    private static final Logger logger = Logger.getLogger(DirectoryClassifier.class.getName());

    public enum DirectoryType {
        EXPERIMENT, PROJECT, UNKNOWN
    }

    public enum Maturity {
        MATURE, IMMATURE
    }

    public static class ClassificationResult {
        public final DirectoryType type;
        public final Maturity maturity;
        public final String feedback;

        public ClassificationResult(DirectoryType type, Maturity maturity, String feedback) {
            this.type = type;
            this.maturity = maturity;
            this.feedback = feedback;
        }
    }

    // --- File/Dir Constants ---
    private static final Set<String> EXPERIMENT_FILES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("Experiment Info.csv", "All Recordings.csv")));

    private static final Set<String> REQUIRED_DIRS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("Brightfield Images", "TrackMate Images")));

    private static final String OPTIONAL_FILE = "All Squares.csv";

    private static final Set<String> OPTIONAL_FILES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("All Squares.csv", "All Tracks.csv", "Paint.json")));

    private static final Set<String> PROJECT_FILES = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("All Recordings.csv", "All Tracks.csv", "All Squares.csv")));

    private static final HashSet<String> REQUIRED_TOP_LEVEL_FILES =
            new HashSet<>(Arrays.asList(
                    PaintConstants.EXPERIMENT_INFO_CSV,
                    PaintConstants.RECORDINGS_CSV
            ));

    private static final HashSet<String> EXPECTED_EXPERIMENT_FILES =
            new HashSet<>(Arrays.asList(
                    PaintConstants.SQUARES_CSV,
                    PaintConstants.TRACKS_CSV,
                    PaintConstants.PAINT_JSON
            ));

    private static final HashSet<String> EXPECTED_EXPERIMENT_DIRS =
            new HashSet<>(Arrays.asList(
                    PaintConstants.DIR_BRIGHTFIELD_IMAGES,
                    PaintConstants.DIR_TRACKMATE_IMAGES
            ));

    public static ClassificationResult classifyDirectoryWork(Path directory) throws IOException {
        List<Path> contents = Files.list(directory)
                .filter(p -> !p.getFileName().toString().equals(".DS_Store"))
                .collect(Collectors.toList());

        Path outputDir = directory.resolve("Output");
        List<String> feedback = new ArrayList<>();

        // --- EXPERIMENT CHECK ---
        if (isExperiment(directory)) {
            if (!Files.exists(outputDir) || Files.isDirectory(outputDir)) {
                Maturity maturity = Files.isRegularFile(directory.resolve(OPTIONAL_FILE))
                        ? Maturity.MATURE : Maturity.IMMATURE;
                return new ClassificationResult(DirectoryType.EXPERIMENT, maturity, null);
            } else {
                feedback.add("Experiment directory contains unexpected files or directories.");
            }
        } else {
            feedback.addAll(experimentMissingItems(directory, contents));
        }

        // --- PROJECT CHECK ---
        List<Path> experimentDirs = findExperimentSubDirs(contents);
        boolean hasProjectFiles = hasAllFiles(directory, PROJECT_FILES);

        if (!experimentDirs.isEmpty()) {
            if (!Files.exists(outputDir) || Files.isDirectory(outputDir)) {
                Maturity maturity = hasProjectFiles ? Maturity.MATURE : Maturity.IMMATURE;
                return new ClassificationResult(DirectoryType.PROJECT, maturity, null);
            } else {
                logUnexpectedProjectItems(contents, experimentDirs, outputDir);
            }
        } else {
            feedback.add("Not a Project: No valid experiment directories found for a project.");
        }

        if (!hasProjectFiles) {
            feedback.add("Not a Project: Missing required project files.");
        }

        String feedbackMessage = String.join("; ", feedback);
        return new ClassificationResult(DirectoryType.UNKNOWN, Maturity.IMMATURE, feedbackMessage);
    }

    public static String[] classifyDirectory(Path directory) throws IOException {
        ClassificationResult result = classifyDirectoryWork(directory);
        if (result.type == DirectoryType.UNKNOWN && result.feedback != null) {
            for (String line : result.feedback.split(";")) {
                logger.severe(line.trim());
            }
            return new String[]{"Unknown", ""};
        }
        return new String[]{result.type.name(), result.maturity.name()};
    }

    // -------------------- Helper Methods --------------------

    private static boolean isExperiment(Path directory) {
        return hasAllFiles(directory, EXPERIMENT_FILES) &&
                hasAllDirs(directory, REQUIRED_DIRS);
    }

    private static List<String> experimentMissingItems(Path directory, List<Path> contents) throws IOException {
        List<String> feedback = new ArrayList<>();

        if (!hasAllFiles(directory, EXPERIMENT_FILES)) {
            Set<String> presentFiles = listFileNames(contents, true);
            Set<String> missingFiles = new HashSet<>(EXPERIMENT_FILES);
            missingFiles.removeAll(presentFiles);
            feedback.add("Not an Experiment: Missing required experiment files: " + missingFiles);
        }

        if (!hasAllDirs(directory, REQUIRED_DIRS)) {
            Set<String> presentDirs = listFileNames(contents, false);
            Set<String> missingDirs = new HashSet<>(REQUIRED_DIRS);
            missingDirs.removeAll(presentDirs);
            feedback.add("Not an Experiment: Missing required directories for an experiment: " + missingDirs);
        }

        return feedback;
    }

    private static boolean hasAllFiles(Path directory, Set<String> required) {
        return required.stream()
                .allMatch(f -> Files.isRegularFile(directory.resolve(f)));
    }

    private static boolean hasAllDirs(Path directory, Set<String> required) {
        return required.stream()
                .allMatch(d -> Files.isDirectory(directory.resolve(d)));
    }

    private static Set<String> listFileNames(List<Path> contents, boolean files) {
        return contents.stream()
                .filter(p -> files ? Files.isRegularFile(p) : Files.isDirectory(p))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toSet());
    }

    private static List<Path> findExperimentSubDirs(List<Path> contents) {
        List<Path> experimentDirs = new ArrayList<>();
        for (Path subDir : contents) {
            if (Files.isDirectory(subDir)) {
                try {
                    ClassificationResult subResult = classifyDirectoryWork(subDir);
                    if (subResult.type == DirectoryType.EXPERIMENT) {
                        experimentDirs.add(subDir);
                    }
                } catch (IOException e) {
                    logger.warning("Could not check subdirectory: " + subDir + " - " + e.getMessage());
                }
            }
        }
        return experimentDirs;
    }

    private static void logUnexpectedProjectItems(List<Path> contents, List<Path> experimentDirs, Path outputDir) {
        List<Path> additionalDirs = contents.stream()
                .filter(Files::isDirectory)
                .filter(p -> !p.equals(outputDir))
                .filter(p -> !experimentDirs.contains(p))
                .collect(Collectors.toList());

        List<Path> additionalFiles = contents.stream()
                .filter(Files::isRegularFile)
                .filter(p -> !PROJECT_FILES.contains(p.getFileName().toString()))
                .collect(Collectors.toList());

        List<String> fileNames = additionalFiles.stream()
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        List<String> dirNames = additionalDirs.stream()
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());

        logger.info(String.format(
                "Not a Project: unexpected files %s or directories %s",
                fileNames, dirNames));
    }
}
