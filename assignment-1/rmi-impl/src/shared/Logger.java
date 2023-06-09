/**
 * The program implements an application that
 * input from users and issues cheques.
 *
 * @author  Mayur Jodhani
 * @version 1.0
 * @since   2023-01-24
 */
package shared;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class Logger {
    public static java.util.logging.Logger getLogger(String fileName, boolean debugLogs)  {
        java.util.logging.Logger LOGGER = null;
        FileHandler fileHandler = null;
        try {
            LOGGER =  java.util.logging.Logger.getLogger(Logger.class.getName());
            fileHandler = new FileHandler(System.getProperty("user.dir")+"/src/logs/"+ (debugLogs ? "client/":"server/") + fileName+".txt",true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.setUseParentHandlers(debugLogs);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return LOGGER;
    }
    public static String getFullMessage(String line1, String line2){
        return line1 + " => " + line2;
    }
}
