package init;

import java.sql.*;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MySqlConnection {
	private static String username, userpasswd, host, port, database;
		
	/*
	 * mySqlConnection permits to connect to the database, you can call it with Connection con = new mySqlConnection().getConnection();
	 */
	private static Logger logger = LogManager.getLogger(MySqlConnection.class);
	
	public Connection getConnection()
	 {
		 String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
	     Connection con = null;
		 

	     try 
	     {
	         con = DriverManager.getConnection(url, username, userpasswd);
			 
	     } 
	     catch (Exception e) 
	     {
	         // TODO Auto-generated catch block
	    	 logger.error("Database connexion error !", e);
 
	     } 
		

		 return con;
	    
	    
	    /*
	     * FR - Connexion à la base de données
	     * EN - Connection to the database
	     */
		
	    }

		
		
			
	
			
		
		

		public static void setParameters() {
			/*
			 * Static method which will set the parameters for the rest of the program, using ReadProperty class and methods.
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
