package PaintLogger;

import java.io.IOException;
import java.util.logging.*;

public class AppLogger {
    private static Logger LOGGER;

    public static void init(String logFileName, boolean append) {
        LOGGER = Logger.getLogger("PaintLogger");
        LOGGER.setUseParentHandlers(false); // prevents double logging

        if (LOGGER.getHandlers().length == 0) {
            try {
                Formatter formatter = new SimpleFormatter() {
                    @Override
                    public synchronized String format(LogRecord record) {
                        return String.format("[%1$tF %1$tT] [%2$-7s] %3$s %n",
                                new java.util.Date(record.getMillis()), // Convert millis to Date
                                record.getLevel().getName(),
                                record.getMessage()
                        );
                    }
                };

                // 📁 File output
                FileHandler fileHandler = new FileHandler(logFileName, append);
                fileHandler.setFormatter(formatter);
                LOGGER.addHandler(fileHandler);

                // 🖥️ Console output
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setFormatter(formatter);
                LOGGER.addHandler(consoleHandler);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to initialize logger", e);
            }
        }
    }

    public static Logger getLogger() {
        if (LOGGER == null) {
            throw new IllegalStateException("Logger not initialized. Call AppLogger.init() first.");
        }
        return LOGGER;
    }
}