package init;

import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MySqlConnection {
	/*
	 * mySqlConnection permits to connect to the database, you can call it with Connection con = new mySqlConnection().getConnection();
	 */
	private static Logger logger = LogManager.getLogger(MySqlConnection.class);
	public Connection getConnection()
	 {
	     String url = "jdbc:mysql://localhost:3306/webscraping";
	     String username = "root";
	     String password = "root";
	     Connection con = null;
	     try 
	     {
	         con = DriverManager.getConnection(url, username, password);
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
	}
