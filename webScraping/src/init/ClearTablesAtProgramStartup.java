package init;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClearTablesAtProgramStartup {
	/**
	 * When the config.properties parameters is set to yes, this class will delete contents from all the tables and reset their auto-incement.
	 */
	private static Logger logger = LogManager.getLogger(ClearTablesAtProgramStartup.class);
	Connection con = new MySqlConnection().getConnection();
	/**
	 * Defining a logger and a mysql connection.
	 */

	public ClearTablesAtProgramStartup() {
				try {
					PreparedStatement dl1 = con.prepareStatement("DELETE FROM ressources USING ressources INNER JOIN evenement ON evenement.id=ressources.idTable;");
					PreparedStatement dl2 = con.prepareStatement("DELETE FROM presence USING presence INNER JOIN personne ON personne.id=presence.idpersonne;");
					PreparedStatement dl3 = con.prepareStatement("DELETE FROM presence");
					PreparedStatement dl4 = con.prepareStatement("DELETE FROM evenement");
					PreparedStatement auto_inc1 = con.prepareStatement("ALTER TABLE ressources AUTO_INCREMENT = 1");
					PreparedStatement auto_inc2 = con.prepareStatement("ALTER TABLE presence AUTO_INCREMENT = 1");
					PreparedStatement auto_inc3 = con.prepareStatement("ALTER TABLE personne AUTO_INCREMENT = 1");
					PreparedStatement auto_inc4 = con.prepareStatement("ALTER TABLE evenement AUTO_INCREMENT = 1");
					dl1.executeUpdate(); dl2.executeUpdate(); dl3.executeUpdate(); dl4.executeUpdate();
					dl1.close(); dl2.close(); dl3.close(); dl4.close(); 
					auto_inc1.executeUpdate(); auto_inc2.executeUpdate(); auto_inc3.executeUpdate(); auto_inc4.executeUpdate();
					auto_inc1.close(); auto_inc2.close(); auto_inc3.close(); auto_inc4.close();
					logger.info("Database cleaned !");
					con.close();
				} catch (SQLException e) {
					logger.error("Error while cleaning tables; SQL Error ! " + e);
				} catch (Exception e) {
					logger.error("Unexpected error" + e);
				}
				/**
				 * Instantiating a MySQL conn through getConnection; 
				 * making the delete and auto_increment resets preparedStatements, executing and closing connection.
				 */
	}
}




