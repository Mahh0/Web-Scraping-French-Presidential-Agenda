package scraping;

import java.sql.Connection;
import java.io.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import init.MySqlConnection;

public class DataEnrichment {
	Connection con = new MySqlConnection().getConnection();
	private static Logger logger = LogManager.getLogger(DataEnrichment.class);
	
	public void typeEnrich(String entitled, String eventtype, int currentEventID) throws SQLException {
		
		String s = Normalizer.normalize(entitled, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").toLowerCase();
	    /*
	     * FR - L'enrichissement est fait � partir de l'intitul�. On retire les accents et les majuscules pour qu'il soit plus simple � analyser.
	     * EN - The enrichment is made from the title. We remove accents and capital letters to make it easier to analyze.
	     */
	    Pattern p;
	    Matcher m;
	    
	    final String updategenerated = "UPDATE evenement SET `generated` = ?, type = ? WHERE id = ?";
        PreparedStatement preparedStatementug = con.prepareStatement(updategenerated);
        preparedStatementug.setInt(3, currentEventID);
        preparedStatementug.setInt(1, 1);
        /*
         * Pr�paration de l'insertion
         */

		if (eventtype.isEmpty()) {
			
			p = Pattern.compile("^(Entretien)");
			m = p.matcher(entitled);
				if(m.find()) {
					logger.info("Enrichissement ! Patterne Entretien trouv�");
					preparedStatementug.setString(2, "Entretien");
					preparedStatementug.executeUpdate();
				} else {
				p = Pattern.compile("^(Conseil des (m|M)inistres)");
				m = p.matcher(entitled);
				} if (m.find()) {
					logger.info("Conseil des ministres trouv�");
					preparedStatementug.setString(2, "Conseil des ministres");
					preparedStatementug.executeUpdate();
				} else {
				p = Pattern.compile("^((Conseil de (d|D)�fense|Conseil restreint de (d|D)�fense))");
				m = p.matcher(entitled);
				} if(m.find()) {
					logger.info("Conseil de d�fense trouv� !");
					preparedStatementug.setString(2, "Conseil de d�fense");
					preparedStatementug.executeUpdate();
				} else {
				p = Pattern.compile("^(Conseil europ�en)");
				m = p.matcher(entitled);
				} if (m.find()) {
					logger.info("Conseil europ�en trouv� !");
					preparedStatementug.setString(2, "Conseil europ�en");
					preparedStatementug.executeUpdate();
				} else { 
				p = Pattern.compile("^(D�placement )");
				m= p.matcher(entitled);
				}
				if (m.find()) {
					logger.info("D�placement trouv� !");
					preparedStatementug.setString(2, "D�placement");
					preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(D�jeuner)");
					m = p.matcher(entitled);
				}
				if (m.find()) {
					logger.info("D�jeuner trouv� !");
					preparedStatementug.setString(2, "D�jeuner");
					preparedStatementug.executeUpdate();
				} else {	
					p = Pattern.compile("^(R�union)");
					m = p.matcher(entitled);
				} if (m.find()) {
							logger.info("R�union trouv�e");
							p = Pattern.compile("^(R�union minist�rielle )");
							m = p.matcher(entitled);
							if (m.find()) {
								logger.debug("R�union minist�rielle trouv�e");
								preparedStatementug.setString(2, "R�union minist�rielle");
							} else {
								preparedStatementug.setString(2, "R�union");
							}
									preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(D(�|i)ner)");
					m = p.matcher(entitled);
				} if (m.find()) {
					logger.info("Diner trouv�");
					preparedStatementug.setString(2, "D�ner");
					preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(Discours)");
					m = p.matcher(entitled);
				if (m.find()) {
					logger.info("Discours trouv�");
					preparedStatementug.setString(2, "Discours");
					preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(R�ception )");
					m = p.matcher(entitled);
				} if (m.find()) {
					logger.info("R�ception trouv�");
					preparedStatementug.setString(2, "R�ception");
					preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(C�r�monie( |s ))");
					m = p.matcher(entitled);
				} if (m.find()) {
					logger.info("C�r�monie trouv�e");
					preparedStatementug.setString(2, "C�r�monie");
					preparedStatementug.executeUpdate();
				} else {	
						p = Pattern.compile("^(Visite )");
						m = p.matcher(entitled);
				} if (m.find()) {
							logger.debug("Visite trouv�e");
							preparedStatementug.setString(2, "Visite");
							preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(Remise (de son|du) rapport )");
					m = p.matcher(entitled);
				} if (m.find()) {
							logger.info("Remise rapport trouv�e");
							preparedStatementug.setString(2, "Remise de rapport");
							preparedStatementug.executeUpdate();
				} else {	
						p = Pattern.compile("^(Rencontre avec)");
						m = p.matcher(entitled);
				} if (m.find()) {
							logger.info("Rencontre trouv�e");
							preparedStatementug.setString(2, "Rencontre");
							preparedStatementug.executeUpdate();
				} else {
						p = Pattern.compile("^(Sommet)");
						m = p.matcher(entitled);
				} if (m.find()) {
							logger.info("Sommet trouv�e");
							preparedStatementug.setString(2, "Sommet");
							preparedStatementug.executeUpdate();
				} else {
						p = Pattern.compile("^(Inauguration)");
						m = p.matcher(entitled);
				} if (m.find()) {
							logger.info("Inauguration trouv�e");
							preparedStatementug.setString(2, "Inauguration");
							preparedStatementug.executeUpdate();
						}
				}}		
		/*
		 * EN - If generated is true, we update the new eventtype and set generated to 1.
		 * FR - Si "generated" est vrai, on fait un update dans la BDD pour d�finir le nouveau type d'�v�nement et on met generated � true.
		 */
		
}
	
	public void personnalitesEnrich (String entitled, int currentEventID) {
		Pattern p;
        Matcher m;
	      p = Pattern.compile("(?:M\\.|Mme)\\s(.+),") ;
	      m = p.matcher(entitled);
	      
	      if(m.find()) { 
	            String[] splitter = m.group().split(" ");
	            
	            if (splitter.length == 3) {
		        String nom = splitter[2].replaceAll(".$", "");
		        logger.info("Personnalies found ! sexe : " + splitter[0] + " Pr�nom : " + splitter[1] + " Nom : "  + splitter[2]);
		        try {
	            PreparedStatement perso = con.prepareStatement("INSERT INTO personne(idTable, sexe, prenom, nom) VALUES (?,?,?,?)");           
	            perso.setInt(1, currentEventID);      
	            perso.setString(2, splitter[0]);           
	            perso.setString(3, splitter[1]);
	            perso.setString(4, nom);
	            perso.executeUpdate();
		        } catch (SQLException e) {
		        	logger.error("Error while personnality insert !");
		        }
	         
	            }
	            
	        }
		
	}

}
