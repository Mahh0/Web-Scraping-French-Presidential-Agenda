package init;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClearTablesAtProgramStartup {
	/**
	 * When the config.properties parameters is set to yes, this class will delete
	 * contents from all the tables and reset their auto-incement.
	 */
	private static Logger logger = LogManager.getLogger(ClearTablesAtProgramStartup.class);
	Connection con = new MySqlConnection().getConnection();

	/**
	 * Defining a logger and a mysql connection.
	 * 
	 * @throws SQLException
	 */

	public ClearTablesAtProgramStartup() throws SQLException {

		try {
			PreparedStatement disableFK = con.prepareStatement("SET FOREIGN_KEY_CHECKS=0;");
			disableFK.executeUpdate();
			disableFK.close();
		} catch (SQLException e) {
			logger.error("Error while setting fk_checks to 0 ; SQL Error ! " + e);
		} catch (Exception e) {
			logger.error("Unexpected error" + e);
		}
		// Disabling foreign keys check to delete from tables without any problem

		ArrayList<String> tables = new ArrayList<String>();
		List<String> listeTables = Arrays.asList("ressource_detail", "ressources", "personne", "presence", "evenement");
		tables.addAll(listeTables);
		tables.forEach((o) -> {
			try {
				PreparedStatement delete = con.prepareStatement("DELETE FROM " + o);
				delete.executeUpdate();
				PreparedStatement resetai = con.prepareStatement("ALTER TABLE " + o + " AUTO_INCREMENT = 1");
				resetai.executeUpdate();
				delete.close();
				resetai.close();
			} catch (SQLException e) {
				logger.error("Error while cleaning tables; SQL Error ! " + e);
			} catch (Exception e) {
				logger.error("Unexpected error" + e);
			}
		});
		logger.info("Database cleaned !");
		// For each table, delete the rows and reset the auto increment to 1.

		try {
			PreparedStatement enableFK = con.prepareStatement("SET FOREIGN_KEY_CHECKS=1;");
			enableFK.executeUpdate();
			enableFK.close();
		} catch (SQLException e) {
			logger.error("Error while re-enabling foreign keys checks !");
			logger.error(e);
		} catch (Exception e) {
			logger.error("Unexpected error" + e);
		}
		// Re-enabling the foreign keys checks.

		con.close();
	}
}
