package init;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MySqlConnection {
	/**
	 * This class have 2 methods :
	 * 	- The first one, getConnection is the method that we instantiate to connect to the database
	 * 	- The second, setParameters, get the parameters of the config.properties through ReadProperties file 
	 * and then getConnection can read it to have the good parameters for the database.
	 */

	private static String username, userpasswd, host, port, database;
	private static Logger logger = LogManager.getLogger(MySqlConnection.class);
	/**
	 * Database parameters and logger
	 */

	public Connection getConnection() {
		/**
		 * connection to the database method.
		 */
		 String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
	     Connection con = null;
		 /**
		  * Defining an url to connect to the db (saying that we are using mysql, jdbc and the parameters that we got from setParameters)
		  */
		 
	     try {
	         con = DriverManager.getConnection(url, username, userpasswd);
	     } catch (Exception e) {
	    	 logger.error("Database connexion error !", e);
	     }
		return con;
		 /**
		  * We try to connect to the database thanks to DriverManager, with the url from above, and username and password from setParameters method.
		  */	
	    }

		public static void setParameters() {
			/**
			 * Method setParameters returns the parameters that he got from ReadPropertyFile class. It is static, so it will be called only one time.
			 */
			ReadPropertyFile rp = new ReadPropertyFile();
			rp.readProps();
			username = rp.getUser();
			userpasswd = rp.getUserpw();
			port = rp.getPort();
			host = rp.getHost();
			database = rp.getDatabase();
			logger.info("The following parameters have been retrieved from the base : " + username + " " + userpasswd + " " + port + " " + host + " " + database);
		}
	}
