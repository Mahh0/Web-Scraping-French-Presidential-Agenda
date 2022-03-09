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

public class MainLinks {
	/**
	 * This class creates links used for the analysis of the presidential agenda. It creates only the links needed.
	 * @throws ParseException
	 * @throws SQLException
	 */
	Connection con = new MySqlConnection().getConnection();

	public ArrayList<String> AnalyseLiens() throws ParseException, SQLException {
		/**
		 * Method used for links generation
		 */
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM");
		SimpleDateFormat outputFormat = new SimpleDateFormat("MM");
		/**
		 * Defining, the MySQL Connection
		 * getting the current Year and the current Month
		 * Adding formaters to transform months (eg: format Septembre to format 09)
		 */

		ArrayList<String> months = new ArrayList<String>();
		List<String> listeMois = Arrays.asList("janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre");
		months.addAll(listeMois);
		Hashtable<String, Integer> years = new Hashtable<String, Integer>();
		ArrayList<String> liens = new ArrayList<String>();
		/**
		 * Creating a list for the months to increment easily.
		 * Creating an Hashtable "numbers" which will asociate Strings and Int for years.
		 */
		

		int e = 0;
		int nb = 0;
		int i3;
		String test = "";
		/**
		 * Some variables which will be used for the calculations
		 */

		for (int i = 2017; i <= year; i++) { // For 2017 to 2022 (current year)
			test = String.valueOf(i);
			years.put(test, i);
			/**
			 * In this first for, we will say that we will do the following from 2017 to 2022.
			 * (Before, )
			 */

			for (String currentmonth : months) {
				/**
				 * For each month (we now have a loop like "mai 2017, avril 2017, juin 2017, ... , janvier 2018, février 2018, ... , janvier 2019, ...")
				 */
				if (e != 11) { e++; } else { e = 0; } // 
				if (currentmonth == "décembre" && years.get(test) == i) { i3 = i + 1; } else { i3 = i; }

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
				countPs.close();
				/*
				 * FR - Comptage du nombre d'entrées du prochain moins dans la base de données
				 * EN - Count the number of entries for the next month in the database
				 */

				
				if (nb == 0) {
					if (years.get(test) == 2017 && (currentmonth == "janvier" || currentmonth == "février" || currentmonth == "mars" || currentmonth == "avril")) {
					} else {
					liens.add("https://www.elysee.fr/agenda-" + currentmonthnormalized + "-" + years.get(test)); // we add the link to the table
					
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
					psE.close();
					psE2.close();
					}
					
					
					}
				/*
				 * FR - Si le prochain mois contient aucune entrée (nb ==0) on fait l'action B sinon si il contient des entrées, on fait rien sur ce mois et la suite
				 * Action B : Ajout du lien et suppression de toutes les entrées à partir du mois actuel
				 * 		+ si le mois généré c'est janvier/février/mars/avril 2017 on ne l'ajoute pas
				 */		
				} 
			}
			
		/*
		 * FR - Pour chaque années (jusqu'à l'année actuelle) et pour chaque mois, on génère un lien d'un mois SEULEMENT si le mois suivant ne contient pas d'entrées. S'il en contient toutes les entrées des prochains mois sont supprimées.
		 * EN - For each year (up to the current year) and for each month, a one-month link is generated ONLY if the following month does not contain entries. If it contains any entries for the next few months are deleted.
		 */

		return liens;
		/*
		 * Links ArrayList returned
		 */
	}
	}
