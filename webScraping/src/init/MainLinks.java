 	package init;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainLinks {
	/*
	 * EN - This class creates the links for the analysis of the presidential agenda. It creates only the links needed, thanks to mySql connection.
	 * FR - Cette classe cr�e les liens � analyser. Elle cr�e seulement lesl iens n�c�ssaires.
	 */
	private static Logger logger = LogManager.getLogger(MainLinks.class);

	public ArrayList<String> AnalyseLiens() throws ParseException, SQLException {
		Connection con = new MySqlConnection().getConnection();
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM");
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM");

		ArrayList<String> months = new ArrayList<String>();
		List<String> listeMois = Arrays.asList("janvier", "f�vrier", "mars", "avril", "mai", "juin", "juillet", "ao�t", "septembre", "octobre", "novembre", "d�cembre");
		months.addAll(listeMois);
			
		Hashtable<String, Integer> numbers = new Hashtable<String, Integer>();
		ArrayList<String> liens = new ArrayList<String>();
		/*
		 * FR - l'ArrayList months contient les mois, la Hashtable numbers va contenir les ann�es, et l'ArrayList liens, les liens qui seront retourn�s.
		 * EN - The ArrayList months contains the months, the Hashtable numbers will contain the years, and the ArrayList links, the links that will be returned.
		 */
		

		int e = 0;
		int nb = 0;
		int i3;

		for (int i = 2017; i <= year; i++) {
			String test = String.valueOf(i);
			numbers.put(test, i);
			/*
			 * Traitements concernant l'ann�e pour l'avoir en STRING et en INT.
			 * On a ici une premi�re boucle pour chaque ann�e de 2017 jusqu'� ann�e actuelle
			 */

			for (String currentmonth : months) {
				if (e != 11) { e++; } else { e = 0; }
				if (currentmonth == "d�cembre" && numbers.get(test) == i) { i3 = i + 1; } else { i3 = i; }

				String nextmonth = months.get(e);

				String nextmonthnormalized = Normalizer.normalize(nextmonth, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");
				String currentmonthnormalized = Normalizer.normalize(currentmonth, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");

				Calendar cal = Calendar.getInstance();
				cal.setTime(inputFormat.parse(nextmonth));
				String NextMonthFormat = outputFormat.format(cal.getTime());
				cal.setTime(inputFormat.parse(currentmonth));
				String CurrenttMonthFormat = outputFormat.format(cal.getTime());

				
				final String countRequest = "SELECT COUNT(*) from evenement WHERE dated LIKE '" + i3 + "-" + NextMonthFormat + "%'";
				PreparedStatement countPs = con.prepareStatement(countRequest);
				ResultSet res = countPs.executeQuery();
				while (res.next()) {
					nb = res.getInt("COUNT(*)");
				}
				/*
				 * FR - Comptage du nombre d'entr�es du prochain moins dans la base de donn�es
				 * EN - Count the number of entries for the next month in the database
				 */

				
				if (nb == 0) {
					if (numbers.get(test) == 2017 && (currentmonth == "janvier" || currentmonth == "f�vrier" || currentmonth == "mars" || currentmonth == "avril")) {
					} else {
					logger.info("Adding " + currentmonthnormalized + "-" + numbers.get(test) + " to list (" + nextmonthnormalized + i3 + " = 0)" );
					liens.add("http://www.elysee.fr/agenda-" + currentmonthnormalized + "-" + numbers.get(test)); // we add the link to the table
					
					String deleteReq = "DELETE re FROM evenement ev JOIN ressources re ON re.idTable=ev.id WHERE ev.dated > '"
							+ i + "-" + CurrenttMonthFormat
							+ "-01 00:00:00' AND ev.dated < '" + i + "-"
							+ NextMonthFormat + "-01 00:00:00';";
					String deleteReq2 = "DELETE ev FROM evenement ev JOIN ressources re ON re.idTable=ev.id WHERE ev.dated > '"
							+ i + "-" + CurrenttMonthFormat
							+ "-01 00:00:00' AND ev.dated < '" + i + "-"
							+ NextMonthFormat + "-01 00:00:00';";

					PreparedStatement psE = con.prepareStatement(deleteReq);
					PreparedStatement psE2 = con.prepareStatement(deleteReq2);
					psE.executeUpdate();
					psE2.executeUpdate();
					}
					
					
					}
				/*
				 * FR - Si le prochain mois contient aucune entr�e (nb ==0) on fait l'action B sinon si il contient des entr�es, on fait rien sur ce mois et la suite
				 * Action B : Ajout du lien et suppression de toutes les entr�es � partir du mois actuel
				 * 		+ si le mois g�n�r� c'est janvier/f�vrier/mars/avril 2017 on ne l'ajoute pas
				 */		
				} 
			}
			
		/*
		 * FR - Pour chaque ann�es (jusqu'� l'ann�e actuelle) et pour chaque mois, on g�n�re un lien d'un mois SEULEMENT si le mois suivant ne contient pas d'entr�es. S'il en contient toutes les entr�es des prochains mois sont supprim�es.
		 * EN - For each year (up to the current year) and for each month, a one-month link is generated ONLY if the following month does not contain entries. If it contains any entries for the next few months are deleted.
		 */

		return liens;
		/*
		 * Links ArrayList returned
		 */
	}
	}
