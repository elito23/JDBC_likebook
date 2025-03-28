package config;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.*;

public class AppLogger {

    private static final String LOG_FILE = "server.log";
    private static FileHandler fileHandler;

    static {
        try {
            fileHandler = new FileHandler(LOG_FILE,0,1, true);
            fileHandler.setEncoding("UTF-8");
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

        } catch (IOException e) {
            System.err.println("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static Logger getLogger(Class<?> clazz) {
        Logger logger = Logger.getLogger(clazz.getName());
        logger.setUseParentHandlers(false);
        boolean hasFileHandler = false;
        for (Handler handler : logger.getHandlers()) {
            if (handler instanceof FileHandler) {
                hasFileHandler = true;
                break;
            }
        }
        if (fileHandler != null && !hasFileHandler) {
            logger.addHandler(fileHandler);
        }
        logger.setLevel(Level.ALL);
        return logger;
    }

}
