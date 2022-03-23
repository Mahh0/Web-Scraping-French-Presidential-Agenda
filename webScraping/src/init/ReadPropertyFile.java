package init;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadPropertyFile {
    /**
     * ReadyPropertyFile method reads properties from the file.
     * Then the properties are returned through get methods.
     */

    private static Logger logger = LogManager.getLogger(ReadPropertyFile.class);
    private static String user;
    private static String userpw;
    private static String host;
    private static String port;
    private static String database;
    private static String databaseCleanup, useLocalHtml, localfilesCleanup;

    /**
     * Creating a logger and defining the variables that will be used for the
     * properties.
     */

    public void readProps() {
        try {
            Properties prop = new Properties(); // Object of Properties class
            FileInputStream ip = new FileInputStream("webScraping/src/main/resources/config.properties");
            // Create a Properties object, and say with FileInputStream where is located the properties file.

            prop.load(ip); // Loading the file through prop (instance of Properties)

            user = prop.getProperty("user");
            userpw = prop.getProperty("userpw");
            host = prop.getProperty("host");
            port = prop.getProperty("port");
            database = prop.getProperty("database");
            databaseCleanup = prop.getProperty("databaseCleanup");
            useLocalHtml = prop.getProperty("useLocalHtml");
            localfilesCleanup = prop.getProperty("localfilesCleanup");
            /**
             * Assigning properties from the file to variables.
             */

        } catch (IOException e) {
            logger.error("Error while reading the properties file !");
            logger.error(e);
        }
    }

    public static String getUser() {
        return user;
    }

    public static String getUserpw() {
        return userpw;
    }

    public static String getHost() {
        return host;
    }

    public static String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public static String getDatabasecleanup() {
        return databaseCleanup;
    }

    public static String getUseLocalHtml() {
        return useLocalHtml;
    }

    public static String getLocalfilesCleanup() {
        return localfilesCleanup;
    }

    /**
     * Differents get methods to get the properties from other classes.
     */
}