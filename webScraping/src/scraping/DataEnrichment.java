package scraping;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import init.MySqlConnection;

public class DataEnrichment {
	/**
	 * The DataEnrichment class contains 2 enrichment methods :
	 * - typeEnrich : add types when they are not presents
	 * - personnalitesEnrich : try to guess personnalities from the entitleds.
	 */
	Connection con = new MySqlConnection().getConnection();
	private static Logger logger = LogManager.getLogger(DataEnrichment.class);
	// Initializing a logger and a MySQL Database con to interract with.


	public void typeEnrich(String entitled, String eventtype, int currentEventID) {
		/**
		 * typeEnrich : method which try to guess a type when it is not present from the
		 * entitled.
		 * It takes as parameters :
		 * - entitled (where we have to guess)
		 * - currentEventID (for insertions, when a type is find)
		 */
		try {

			Pattern p;
			Matcher m;
			PreparedStatement preparedStatementug = con
					.prepareStatement("UPDATE evenement SET `generated` = (?), type = (?) WHERE id = (?)");
			preparedStatementug.setInt(3, currentEventID);
			preparedStatementug.setInt(1, 1);
			/**
			 * Preparing the insertion : defining a pattern, which will be the regular
			 * expression and a matcher, which will be the entitled.
			 * Also starting the preparedStatement.
			 */

			p = Pattern.compile("^(Entretien)");
			m = p.matcher(entitled);
			if (m.find()) {
				preparedStatementug.setString(2, "Entretien");
				preparedStatementug.executeUpdate();
				preparedStatementug.close();
			} else {
				p = Pattern.compile("^(Conseil des (m|M)inistres)");
				m = p.matcher(entitled);
			}
			if (m.find()) {
				preparedStatementug.setString(2, "Conseil des ministres");
				preparedStatementug.executeUpdate();
				preparedStatementug.close();
			} else {
				p = Pattern.compile("^((Conseil de (d|D)éfense|Conseil restreint de (d|D)éfense))");
				m = p.matcher(entitled);
			}
			if (m.find()) {
				preparedStatementug.setString(2, "Conseil de défense");
				preparedStatementug.executeUpdate();
				preparedStatementug.close();
			} else {
				p = Pattern.compile("^(Conseil européen)");
				m = p.matcher(entitled);
			}
			if (m.find()) {
				preparedStatementug.setString(2, "Conseil européen");
				preparedStatementug.executeUpdate();
				preparedStatementug.close();
			} else {
				p = Pattern.compile("^(Déplacement )");
				m = p.matcher(entitled);
			}
			if (m.find()) {
				preparedStatementug.setString(2, "Déplacement");
				preparedStatementug.executeUpdate();
				preparedStatementug.close();
			} else {
				p = Pattern.compile("^(Déjeuner)");
				m = p.matcher(entitled);
			}
			if (m.find()) {
				preparedStatementug.setString(2, "Déjeuner");
				preparedStatementug.executeUpdate();
				preparedStatementug.close();
			} else {
				p = Pattern.compile("^(Réunion)");
				m = p.matcher(entitled);
			}
			if (m.find()) {
				p = Pattern.compile("^(Réunion ministérielle )");
				m = p.matcher(entitled);
				if (m.find()) {
					preparedStatementug.setString(2, "Réunion ministérielle");
				} else {
					preparedStatementug.setString(2, "Réunion");
				}
				preparedStatementug.executeUpdate();
				preparedStatementug.close();
			} else {
				p = Pattern.compile("^(D(î|i)ner)");
				m = p.matcher(entitled);
			}
			if (m.find()) {
				preparedStatementug.setString(2, "Dîner");
				preparedStatementug.executeUpdate();
				preparedStatementug.close();
			} else {
				p = Pattern.compile("^(Discours)");
				m = p.matcher(entitled);
				if (m.find()) {
					preparedStatementug.setString(2, "Discours");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
					p = Pattern.compile("^(Réception )");
					m = p.matcher(entitled);
				}
				if (m.find()) {
					preparedStatementug.setString(2, "Réception");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
					p = Pattern.compile("^(Cérémonie( |s ))");
					m = p.matcher(entitled);
				}
				if (m.find()) {
					preparedStatementug.setString(2, "Cérémonie");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
					p = Pattern.compile("^(Visite )");
					m = p.matcher(entitled);
				}
				if (m.find()) {
					preparedStatementug.setString(2, "Visite");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
					p = Pattern.compile("^(Remise (de son|du) rapport )");
					m = p.matcher(entitled);
				}
				if (m.find()) {
					preparedStatementug.setString(2, "Remise de rapport");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
					p = Pattern.compile("^(Rencontre avec)");
					m = p.matcher(entitled);
				}
				if (m.find()) {
					preparedStatementug.setString(2, "Rencontre");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
					p = Pattern.compile("^(Sommet)");
					m = p.matcher(entitled);
				}
				if (m.find()) {
					preparedStatementug.setString(2, "Sommet");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
					p = Pattern.compile("^(Inauguration)");
					m = p.matcher(entitled);
				}
				if (m.find()) {
					preparedStatementug.setString(2, "Inauguration");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				}
			}

		} catch (SQLException sqex) {
			logger.error("SQL Error");
			logger.error(sqex);
		} catch (Exception ex) {
			logger.error("Undefined ERROR");
			logger.error(ex);
		}
		/**
		 * This methods is looking with regex if patterns are found in the entitleds. If
		 * yes, then we are updating the mysql datas with the types.
		 * We also set generated to 1 to say that it's a generated type.
		 */

	}

	public void personnalitesEnrich(String entitled, int currentEventID) {
		try {
			Pattern p;
			Matcher m;
			p = Pattern.compile("(M\\.|Mme) (([A-Z])[a-z\\W]+)+ ([A-Z\\W]+)+[,.]");
			m = p.matcher(entitled);
			// Defining a pattern (expression to find in something) and a matcher (the "something"
			// which is the entitled)

			while (m.find()) { // If regex found pattern
				String person = m.group();

				// We delete characters that we don't need
				person = person.replaceFirst("Mme ", "");
				person = person.replaceFirst("M. ", "");
				person = person.replace(",", "");
				person = person.replace(".", "");

				// We try to find Q ID
				String idpersonne = wikidata.wikidata(person);

				if (!idpersonne.isEmpty()) {

					PreparedStatement checkForQ = con.prepareStatement("SELECT id FROM personne WHERE wikidataid = (?) ");
					checkForQ.setString(1, idpersonne);
					ResultSet Qresponseset = checkForQ.executeQuery();
					int id = 0;
					if (Qresponseset.next()){
						id = Qresponseset.getInt(1);
					}
					// Checking if we already have insert this person in personne table

					if (id == 0) { // If we have not inserted a row in personne table yet, we will nsert in personne and then in presence
						PreparedStatement insertpersonne = con.prepareStatement("INSERT INTO personne(wikidataid) VALUES (?)");
						insertpersonne.setString(1, idpersonne);
						insertpersonne.executeUpdate();
						insertpersonne.close();
						// Inserting of the person in personne

						PreparedStatement selectidpersonne = con.prepareStatement("SELECT id FROM personne WHERE wikidataid = (?)");
						selectidpersonne.setString(1, idpersonne);
						ResultSet idResp = selectidpersonne.executeQuery();
						if (idResp.next()){
							id = idResp.getInt(1);
						}
						idResp.close();
						selectidpersonne.close();
						// Selecting person ID in personne table

					}
						
					PreparedStatement insertpresence = con.prepareStatement("INSERT INTO presence(idevenement, idpersonne) VALUES (?,?)");
					insertpresence.setInt(1, currentEventID);
					insertpresence.setInt(2, id);
					insertpresence.executeUpdate();
					insertpresence.close();
					Qresponseset.close();
					checkForQ.close();
					// Inserting in presence table the person

				
				} else {
					// Split the name : if the splitter lengths is 2, the name is probably correct, so we will insert it
					String[] splitter = person.split(" ");

					if (splitter.length==2) { // If the name is in good format (name + surname)
						String prenom = splitter[0];
						String nom = splitter[1];
						
						// Test if the person is already inserted in personne table
						PreparedStatement checkForPerson = con.prepareStatement("SELECT id FROM personne WHERE prenom = ? AND nom = ? ");
						checkForPerson.setString(1, prenom);
						checkForPerson.setString(2, nom);
						ResultSet personresponseset = checkForPerson.executeQuery();
						int id2 = 0;
						if (personresponseset.next()){
							id2 = personresponseset.getInt(1);
						}

						if (id2 == 0) { // If nobody is present in the person table
							PreparedStatement insertpersonne2 = con.prepareStatement("INSERT INTO personne(prenom, nom) VALUES (?, ?)");
							insertpersonne2.setString(1, prenom);
							insertpersonne2.setString(2, nom);
							insertpersonne2.executeUpdate();
							insertpersonne2.close();
						// Inserting of the person in personne

						PreparedStatement selectidpersonne2 = con.prepareStatement("SELECT id FROM personne WHERE prenom = ? AND nom = ?");
						selectidpersonne2.setString(1, prenom);
						selectidpersonne2.setString(2, nom);
						ResultSet idResp2 = selectidpersonne2.executeQuery();
						if (idResp2.next()){
							id2 = idResp2.getInt(1);
						}
						idResp2.close();
						selectidpersonne2.close();
						// Selecting person ID in personne table for presence insert
						} 
						
						// presence insert
						PreparedStatement insertpresence2 = con.prepareStatement("INSERT INTO presence(idevenement, idpersonne) VALUES (?,?)");
						insertpresence2.setInt(1, currentEventID);
						insertpresence2.setInt(2, id2);
						insertpresence2.executeUpdate();
						insertpresence2.close();


					}
				}
			}
		} catch (SQLException sql) {
			logger.error("SQL Exception");
			sql.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			logger.error("Encoding exception");
			uee.printStackTrace();
		}
	}
}
