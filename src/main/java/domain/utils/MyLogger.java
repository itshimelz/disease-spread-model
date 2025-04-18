package domain.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger {
    // ... existing code ...
    public static boolean isEnabled() {
        return enabled;
    }
    private static boolean enabled = false;

    public static void setEnabled(boolean flag) {
        enabled = flag;
    }

    private static final Logger logger = Logger.getLogger(MyLogger.class.getName());

    public static Logger getLogger() {
        return logger;
    }

    public static void logInfo(String message) {
        if (enabled) logger.log(Level.INFO, message);
    }

    public static void logWarning(String message) {
        if (enabled) logger.log(Level.WARNING, message);
    }

    public static void logSevere(String message) {
        if (enabled) logger.log(Level.SEVERE, message);
    }

    public static void logFine(String message){
        if (enabled) logger.log(Level.FINE, message);
    }

    public static void logFinest(String message){
        if (enabled) logger.log(Level.FINEST, message);
    }
}
