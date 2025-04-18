package domain.utils;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyLogger {

    private static final Logger logger = Logger.getLogger(MyLogger.class.getName());

    public static Logger getLogger() {
        return logger;
    }

    public static void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    public static void logWarning(String message) {
        logger.log(Level.WARNING, message);
    }

    public static void logSevere(String message) {
        logger.log(Level.SEVERE, message);
    }

    public static void logFine(String message){
        logger.log(Level.FINE, message);
    }

    public static void logFinest(String message){
        logger.log(Level.FINEST, message);
    }
}
