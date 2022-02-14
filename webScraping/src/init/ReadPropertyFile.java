package init;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadPropertyFile {
    /*
     * ReadyPropertyFile method reads properties from the file and put it in string variables.
     * Get methods are created thanks to the readProps method.
     */
    private static Logger logger = LogManager.getLogger(ReadPropertyFile.class);
    // Creating a logger to log errors/info/debug

    private String user, userpw, host, port, database;
    // variables used to store the property file values.

    private static String askForCleanup, useLocalHtml, askForHtmlCleanup;

    public void readProps() {
        try {
            this.user = user;
            this.userpw = userpw;
            this.host = host;
            this.port = port;
            this.database = database;
            this.askForCleanup = askForCleanup;
            this.useLocalHtml = useLocalHtml;
            this.askForHtmlCleanup = askForHtmlCleanup;
            Properties prop = new Properties(); // Object of Properties class
            FileInputStream ip = new FileInputStream("webScraping/src/config.properties"); // Say to the program where is the properties file
            prop.load(ip);
            // Loading the property file prop into InputStream ip.

            user = prop.getProperty("user");
            userpw = prop.getProperty("userpw");
            host = prop.getProperty("host");
            port = prop.getProperty("port");
            database = prop.getProperty("database");
            askForCleanup = prop.getProperty("askForCleanup");
            useLocalHtml = prop.getProperty("useLocalHtml");
            askForHtmlCleanup = prop.getProperty("askForHtmlCleanup");
            // Assigning the values
        } catch (IOException e) {
            logger.error("Error while reading the properties file !");
        }
    }

    public String getUser() {
        return user;
    }

    public String getUserpw() {
        return userpw;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public static String getAskforcleanup() {
        return askForCleanup;
    }

    public static String getUseLocalHtml(){
        return useLocalHtml;
    }

    public static String getAskForHtmlCleanup(){
        return askForHtmlCleanup;
    }
}