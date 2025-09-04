package paint.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.*;

public class AppLoggerNew {

    private static Logger logger;

    public static void init(String logFileName) {
        setupLogger(logFileName);
    }

    private static void setupLogger(String baseName) {
        logger = Logger.getLogger("Paint");
        logger.setUseParentHandlers(false);

        // Console handler
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

        try {
            String logFile = nextLogFileName(baseName);
            FileHandler fileHandler = new FileHandler(logFile, false); // always fresh file
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            consoleHandler.publish(new LogRecord(Level.WARNING,
                    "File logging disabled: " + e.getMessage()));
        }

        logger.setLevel(Level.ALL);
    }

    private static String nextLogFileName(String baseName) {
        int counter = 0;
        while (true) {
            String name = baseName + "-" + counter + ".log";
            if (!Files.exists(Paths.get(name))) {
                return name; // first unused filename
            }
            counter++;
        }
    }
    // Convenience methods
    public static void info(String msg) {
        logger.info(msg);
    }

    public static void warning(String msg) {
        logger.warning(msg);
    }

    public static void error(String msg) {
        logger.severe(msg);
    }

    public static void error(String msg, Throwable t) {
        logger.severe(msg + " | Exception: " + t.getMessage());
    }

    public static void debug(String msg) {
        logger.fine(msg);
    }
}