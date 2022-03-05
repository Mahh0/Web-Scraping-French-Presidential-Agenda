package scraping;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import init.MySqlConnection;

public class DataEnrichment {
	/**
	 * The DataEnrichment class contains 2 enrichment methods :
	 * 		- typeEnrich : add types when they are not presents
	 * 		- personnalitesEnrich : try to guess personnalities from the entitleds.
	 */

	Connection con = MySqlConnection.getConnection();
	private static Logger logger = LogManager.getLogger(DataEnrichment.class);
	/**
	 * Initializing a logger and the static mysql connection.
	 * @param eventtype
	 */
	
	public void typeEnrich(String entitled, String eventtype, int currentEventID) {
		/**
		 * typeEnrich : method which try to guess a type when it is not present from the entitled.
		 * It takes as parameters : 
		 * 		- entitled (where we have to guess)
		 * 		- currentEventID (for insertions, when a type is find)
		 */
		try {

	    Pattern p;
	    Matcher m;
        PreparedStatement preparedStatementug = con.prepareStatement("UPDATE evenement SET `generated` = (?), type = (?) WHERE id = (?)");
        preparedStatementug.setInt(3, currentEventID);
        preparedStatementug.setInt(1, 1);
		/**
		 * Preparing the insertion : defining a pattern, which will be the regular expression and a matcher, which will be the entitled.
		 * Also starting the preparedStatement.
		 */
			
			p = Pattern.compile("^(Entretien)");
			m = p.matcher(entitled);
				if(m.find()) {
					preparedStatementug.setString(2, "Entretien");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
				p = Pattern.compile("^(Conseil des (m|M)inistres)");
				m = p.matcher(entitled);
				} if (m.find()) {
					preparedStatementug.setString(2, "Conseil des ministres");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
				p = Pattern.compile("^((Conseil de (d|D)éfense|Conseil restreint de (d|D)éfense))");
				m = p.matcher(entitled);
				} if(m.find()) {
					preparedStatementug.setString(2, "Conseil de défense");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
				p = Pattern.compile("^(Conseil européen)");
				m = p.matcher(entitled);
				} if (m.find()) {
					preparedStatementug.setString(2, "Conseil européen");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else { 
				p = Pattern.compile("^(Déplacement )");
				m= p.matcher(entitled);
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
				} if (m.find()) {
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
				} if (m.find()) {
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
				} if (m.find()) {
					preparedStatementug.setString(2, "Réception");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {
					p = Pattern.compile("^(Cérémonie( |s ))");
					m = p.matcher(entitled);
				} if (m.find()) {
					preparedStatementug.setString(2, "Cérémonie");
					preparedStatementug.executeUpdate();
					preparedStatementug.close();
				} else {	
						p = Pattern.compile("^(Visite )");
						m = p.matcher(entitled);
				} if (m.find()) {
							preparedStatementug.setString(2, "Visite");
							preparedStatementug.executeUpdate();
							preparedStatementug.close();
				} else {
					p = Pattern.compile("^(Remise (de son|du) rapport )");
					m = p.matcher(entitled);
				} if (m.find()) {
							preparedStatementug.setString(2, "Remise de rapport");
							preparedStatementug.executeUpdate();
							preparedStatementug.close();
				} else {	
						p = Pattern.compile("^(Rencontre avec)");
						m = p.matcher(entitled);
				} if (m.find()) {
							preparedStatementug.setString(2, "Rencontre");
							preparedStatementug.executeUpdate();
							preparedStatementug.close();
				} else {
						p = Pattern.compile("^(Sommet)");
						m = p.matcher(entitled);
				} if (m.find()) {
							preparedStatementug.setString(2, "Sommet");
							preparedStatementug.executeUpdate();
							preparedStatementug.close();
				} else {
						p = Pattern.compile("^(Inauguration)");
						m = p.matcher(entitled);
				} if (m.find()) {
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
		 * This methods is looking with regex if patterns are found in the entitleds. If yes, then we are updating the mysql datas with the types.
		 * We also set generated to 1 to say that it's a generated type.
		 */
		
}
	
/*
	public void personnalitesEnrich (String entitled, int currentEventID) {
		Pattern p;
        Matcher m;
	      p = Pattern.compile("(?:M\\.|Mme)\\s(.+),") ;
	      m = p.matcher(entitled);
	      

		  /**
		   * Algo à faire :
		   * Si le patterne est trouvé, on enlève le début (M. ou Mme et la fin (la virgule))
		   * On injecte dans la méthode wikidata le nom et le prénom pour trouver le Q. 
		   * On récupère ce Q
		   * On teste si le Q existe déjà
		   * Si le Q existe déjà dans la table personne, on récupère son ID et on fait une insertion dans la table presence
		   * Si le Q n'existe pas, on récupère son ID et on fait une insertion dans la table personne et dans la table présence (récupération du nom et prénom sur wikidata et sparql) 
		   *  et on l'injecte dans la base de données
		   

	      if(m.find()) { // If regex found
			String person = m.toString();
			person = person.replace("M", "");
			person = person.replace("Mme.")

	            
	            
	            if (splitter.length == 3) {
		        String nom = splitter[2].replaceAll(".$", "");
		        try {
	            PreparedStatement perso = con.prepareStatement("INSERT INTO personne(idTable, sexe, prenom, nom) VALUES (?,?,?,?)");           
	            perso.setInt(1, currentEventID);      
	            perso.setString(2, splitter[0]);           
	            perso.setString(3, splitter[1]);
	            perso.setString(4, nom);
	            perso.executeUpdate();
				perso.close();
				

				try {
					String idpersonne = wikidata.wikidata(nom, splitter[1]);
					if(!idpersonne.isEmpty()){
						logger.debug("I found an ID ! it is " + idpersonne + " for the person(name/surname) " + nom + " " + splitter[1]);
						PreparedStatement wikidata = con.prepareStatement("UPDATE personne SET idwikidata = ? WHERE nom = ? AND prenom = ?");
						wikidata.setString(1, idpersonne);
						wikidata.setString(2, nom);
						wikidata.setString(3, splitter[1]); 
						wikidata.executeUpdate();
						wikidata.close();
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					logger.error("Error while adding a wikidata ID ! Maybe the person was not found in wikidata !");
				}

		        } catch (SQLException e) {
		        	logger.error("Error while personnality insert !");
		        }
	         
	            }
	            
	        }
		
	}
*/
}
