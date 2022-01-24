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
	    /*
	     * FR - L'enrichissement est fait à partir de l'intitulé. On retire les accents et les majuscules pour qu'il soit plus simple à analyser.
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
					preparedStatementug.setString(2, "Entretien");
					preparedStatementug.executeUpdate();
				} else {
				p = Pattern.compile("^(Conseil des (m|M)inistres)");
				m = p.matcher(entitled);
				} if (m.find()) {
					preparedStatementug.setString(2, "Conseil des ministres");
					preparedStatementug.executeUpdate();
				} else {
				p = Pattern.compile("^((Conseil de (d|D)éfense|Conseil restreint de (d|D)éfense))");
				m = p.matcher(entitled);
				} if(m.find()) {
					preparedStatementug.setString(2, "Conseil de défense");
					preparedStatementug.executeUpdate();
				} else {
				p = Pattern.compile("^(Conseil européen)");
				m = p.matcher(entitled);
				} if (m.find()) {
					preparedStatementug.setString(2, "Conseil européen");
					preparedStatementug.executeUpdate();
				} else { 
				p = Pattern.compile("^(Déplacement )");
				m= p.matcher(entitled);
				}
				if (m.find()) {
					preparedStatementug.setString(2, "Déplacement");
					preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(Déjeuner)");
					m = p.matcher(entitled);
				}
				if (m.find()) {
					preparedStatementug.setString(2, "Déjeuner");
					preparedStatementug.executeUpdate();
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
				} else {
					p = Pattern.compile("^(D(î|i)ner)");
					m = p.matcher(entitled);
				} if (m.find()) {
					preparedStatementug.setString(2, "Dîner");
					preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(Discours)");
					m = p.matcher(entitled);
				if (m.find()) {
					preparedStatementug.setString(2, "Discours");
					preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(Réception )");
					m = p.matcher(entitled);
				} if (m.find()) {
					preparedStatementug.setString(2, "Réception");
					preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(Cérémonie( |s ))");
					m = p.matcher(entitled);
				} if (m.find()) {
					preparedStatementug.setString(2, "Cérémonie");
					preparedStatementug.executeUpdate();
				} else {	
						p = Pattern.compile("^(Visite )");
						m = p.matcher(entitled);
				} if (m.find()) {
							preparedStatementug.setString(2, "Visite");
							preparedStatementug.executeUpdate();
				} else {
					p = Pattern.compile("^(Remise (de son|du) rapport )");
					m = p.matcher(entitled);
				} if (m.find()) {
							preparedStatementug.setString(2, "Remise de rapport");
							preparedStatementug.executeUpdate();
				} else {	
						p = Pattern.compile("^(Rencontre avec)");
						m = p.matcher(entitled);
				} if (m.find()) {
							preparedStatementug.setString(2, "Rencontre");
							preparedStatementug.executeUpdate();
				} else {
						p = Pattern.compile("^(Sommet)");
						m = p.matcher(entitled);
				} if (m.find()) {
							preparedStatementug.setString(2, "Sommet");
							preparedStatementug.executeUpdate();
				} else {
						p = Pattern.compile("^(Inauguration)");
						m = p.matcher(entitled);
				} if (m.find()) {
							preparedStatementug.setString(2, "Inauguration");
							preparedStatementug.executeUpdate();
						}
				}}		
		/*
		 * EN - If generated is true, we update the new eventtype and set generated to 1.
		 * FR - Si "generated" est vrai, on fait un update dans la BDD pour définir le nouveau type d'évènement et on met generated à true.
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
