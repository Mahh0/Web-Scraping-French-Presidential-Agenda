package init;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClearTablesAtProgramStartup {
	/*
	 * FR - Cette classe instantie une connexion SQL, demande à l'utilisateur s'il veut clear les tables SQL, si l'utilisateur répond oui, on supprime ce que contient les deux tables et reset l'auto-increment.
	 * EN - This class asks to the user if he want's to clear the tables, if the answer is 'y' the 2 tables are cleared and auto-increment is reset. 
	 */
	private static Logger logger = LogManager.getLogger(ClearTablesAtProgramStartup.class);
	Connection con = new MySqlConnection().getConnection();	
	public ClearTablesAtProgramStartup() throws SQLException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Do you wan't to clear MySql tables ? (y/n)");
		String str = sc.nextLine();

		if (str.contains("y")) {
				try {
					PreparedStatement dl0 = con.prepareStatement("delete from dossier");
					PreparedStatement dl1 = con.prepareStatement("delete from ressources");
					PreparedStatement dl2 = con.prepareStatement("delete from personne");
					PreparedStatement dl3 = con.prepareStatement("delete from evenement");
					PreparedStatement dl4 = con.prepareStatement("ALTER TABLE evenement AUTO_INCREMENT = 1");
					dl1.executeUpdate();
					dl0.executeUpdate();
					dl2.executeUpdate();
					dl3.executeUpdate();
					dl4.executeUpdate();
					con.close();
					logger.info("Database cleaned !");
					
				} catch (Exception e) {
					logger.error("Error while cleaning tables ! " + e);
				}
				}
	}
}




