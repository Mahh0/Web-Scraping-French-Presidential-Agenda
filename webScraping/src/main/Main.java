package main;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.CombiningEvaluator.And;

import init.ClearTablesAtProgramStartup;
import init.MainLinks;
import init.MySqlConnection;
import init.SSLHelper;
import scraping.DataEnrichment;
import scraping.EventsTimeManagements;
import scraping.resources;

import org.jsoup.select.Elements;
import java.util.*;

public class Main {
	private static Logger logger = LogManager.getLogger(Main.class);
	public static void main(String[] args) throws Exception {
		logger.info("Starting the program");
		Connection con = new MySqlConnection().getConnection();
		/*
		 * FR - Instantiation d'un connection con qui utilise getConnection dans la classe MySqlConnection. Cette instantiation est utilis�e dans les classes dans lesquelles on souhaite utiliser la BDD
		 * EN - Instantation of a Connection con that uses getConnection from MySqlConnection. It is used in the classes where we need interactions with the database
		 */
		
		new ClearTablesAtProgramStartup();
		/*
		 * FR - Instantiation d'une classe qui permet de nettoyer les tables Sql � chaque d�but de programme (avec choix). 
		 * EN - Class to clear tables at the beginning of the program (with choice).
		 */
		
		MainLinks links = new MainLinks();
		/*
		 * FR - Instantiation de la classe MainLinks qui s'occupe de la g�n�ration des liens � analyser. Elle retourne une ArrayList de liens.
		 * EN - Instantiation of the MainLinks class which takes care of the generation of the links to analyze. It returns links ArrayList.
		 */
		
		for(Object link : links.AnalyseLiens()) {
			logger.info("----------------------------------------------------------- Analyzing " + link);
			final Document doc = SSLHelper.getConnection(link.toString()).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").timeout(50000).get();
			/*
			 * EN - For Each link in the ArrayList we create a document doc, and we run the rest of the program.
			 * FR - Pour chaque lien dans l'ArrayList, on cr�e un document doc et on fait la suite du programme.
			 */
			
			String date = doc.select(".h2--agenda").first().text();
			String[] splitter = date.split(" ");
			SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM");
			SimpleDateFormat outputFormat = new SimpleDateFormat("MM");
			Calendar cal = Calendar.getInstance();
			cal.setTime(inputFormat.parse(splitter[0]));
			String monthFormat = outputFormat.format(cal.getTime());
			String year = splitter[1];
			/* 
			 * FR - Les pr�c�dentes lignes nous permettent de r�cup�rer la date sur la page (format Mai 2017) par exemple, et d'en extraire l'ann�e (format 2017 => year) et le mois au format nombre (05 => monthFormat).
			 * EN - The previous lines permit us to get a date (format May 2017 for example), to split it in two to get May+2017, and to convert May to 05 (monthFormat). We also have the year
			 */

			Elements containers = doc.select("section.container > div.container--thicker");
			/*
			 * FR - S�lection de tous les conteneurs dans la page ( 1conteneur = 1 jour).
			 * EN - Select of all containers in the web page (1 container = 1 day)
			 */
			
			for (Element container : containers) {
				/*
				 * EN - For Each Element container (day) in Elements containers (days)
				 * FR - Pour chaque Element container (jour) dans Elements containers (joueurs)
				 */
				
				Elements rows = container.select("ul.list-table > li");
				Elements dat = container.select("div.sticker > h3.sticker__content");
				String day = dat.select("span").text().replaceAll("[^\\d.]", "");
				/*
				 * The previous lines defines some values for each container (=day) :
				 * 	- rows : it represents every lines, containing hour + kind of event + title
				 *  - day : it is only the day (numeric) like 06, selected in the h3 sticker content, in the container
				 *  
				 *  Les deux lignes pr�c�dentes nous permettent, pour chaque conteneur d'avoir : 
				 *  - rows : liste d'�l�ments (de lignes) qui contiennent l'heure, le type d'�v�nement et l'intitul�.
				 *  - day : S�lection du jour en num�rique (06).
				 */

				for (Element row : rows) {
					/*
					 * EN - For each Element row (in Elements rows) -a row
					 * FR - Pour chaque row (Elements rows)
					 */
					String eventhour = row.select("div.list-table__details > p.list-table__hour").text(); // On s�lectionne l'heure
					String eventtype = row.select("div.list-table__details > p.list-table__type").text(); // On s�lectionne le type
					String entitled = row.select("div.list-table__content > p.m-b-n").first().text(); // On s�lectionne l'intitul�
					Elements externallinkslist = row.select("a[href]"); // On s�lectionne les liens
					/*
					 * EN - In each row (containing hour + event kind + title), we can select theses things (eventhour, eventtype and entitled). Under each title, we can have 0, 1 or several links to analyse. externallinkslist contains these links
					 * FR - Chaque ligne contient une heure (qui peut �tre au format HH:MM ou Apr�s-Midi par exemple) => eventhour, un type => eventtype, un intitul� => entitled. En dessous de chaque intitul�, on a 0, 1 ou plusieurs liens qu'on va analyser. On les s�lectionne dans externallistlinks
					 */
					
					String finaleventhour = eventhour.replaceAll("[^\\d.]", ":");
					if(eventhour.equalsIgnoreCase("Matin")) {
						finaleventhour="08:01";
					} else if(eventhour.equalsIgnoreCase("Apr�s-midi")) {
						finaleventhour="14:01";
					} else if(eventhour.equalsIgnoreCase("Toute la journ�e")) {
						finaleventhour="08:02";
					}
					/*
					 * EN - put the eventhour in good format, and if it's "Apr�s-Midi, Matin or Toute la journ�e" it put a special hour.
					 * FR - Dans le cas ou la s�lection de eventhour soit �gale � une chaine de caract�re type Apr�s-Midi, Matin, ou toute la journ�e, on la remplace par une heure (01 ou 02 pour la reconnaitre).
					 */
					
					String eventstartdate = year + "-" + monthFormat + "-" + day + " " + finaleventhour + ":00";
					/*
					 * EN - This string is the datetime (duree) contained in the event table
					 * FR - Composition de dated d'un �v�nement � partir des informations r�colt�es pr�c�demment.
					 */
					
					final String insertionevent = "INSERT INTO evenement (dated,type,intitule) values (?,?,?)";
	                PreparedStatement preparedStatementevent = con.prepareStatement(insertionevent);
	                    preparedStatementevent.setString(1, eventstartdate);
	                    preparedStatementevent.setString(2, eventtype);
	                    preparedStatementevent.setString(3, entitled);
	                    preparedStatementevent.executeUpdate();
	                /*
	                 * EN - Insertion of rows in evenement table, using PreparedStatement. We now have start date, type and entitled, we just need the duration.
	                 * FR - Insertion de lignes dans evenement en utilisant PreparedStatement, avec les donn�es r�colt�es pr�c�demment. Il manque maintenant seulement la dur�e.
	                 */
	                  
	                final String getIdRequest = "SELECT id from evenement where dated = '" + eventstartdate + "'";
	                PreparedStatement getId =  con.prepareStatement(getIdRequest);
	                ResultSet a = getId.executeQuery();	                
	                int currentEventID = 0;
	                if (a.next()) {
	                	currentEventID = a.getInt(1);
	                }
	                /*
	                 * EN - Recovery of the ID of the event, for the duration of the previous event.
	                 * FR - Lignes qui permettent de r�cup�rer l'ID de l'�v�nement pour apr�s calculer les dur�es.
	                 */
	                
	               DataEnrichment te = new DataEnrichment();
	               te.typeEnrich(entitled, eventtype, currentEventID);
	               /*
	                * EN - Call of typeEnrich method in TypeEnrichment class (enrichissement de donn�es).
	                * FR - Instantiation et appel de la m�thode typeEnrich de TypeEnrichment. Cela permet de faire de l'enrichissement de donn�es au niveau des types d'�v�nement.
	                */
	               
	               te.personnalitesEnrich(entitled, currentEventID);
	               /*
	                * EN - Call of personnalitesEnrich method in TypeEnrichment class to add persons to personnes table
	                * FR - Appel de la m�thode personnalitesEnrich qui s'occupe d'ajouter des personnes � des �v�nements li�s.
	                */
	               
	                
	                int previousEventID = currentEventID-1;
	                final String lastDate = "SELECT dated FROM evenement WHERE id= ? ";
	                PreparedStatement derniereDate = con.prepareStatement(lastDate);
	                derniereDate.setInt(1, previousEventID);
	                ResultSet to = derniereDate.executeQuery();
	                String LastEventDate = "0000-00-00 00:00:00";
	                if(to.next()) {
	                	LastEventDate = to.getString(1);
	                }
	                /*
	                 * FR - S�lection de la dated de l'�v�nement pr�c�dent � l'aide de l'idActuel-1
	                 * EN - Selection of the previous event dated thanks to currentEventID-1
	                 */
	                
	                final String updateeven = "UPDATE evenement SET duree = ? WHERE id= ? ";
	        		EventsTimeManagements etms = new EventsTimeManagements(LastEventDate, eventstartdate);
	        		String timeformat = etms.CalculDurees();
	                PreparedStatement pSduree = con.prepareStatement(updateeven); // Ce statement et ce qui suit permet d'ins�rer la dur�e calcul�e pr�c�demment dans la table
	                pSduree.setString(1, timeformat);
	                pSduree.setInt(2, previousEventID);
	                pSduree.executeUpdate();
	                /*
	                 * FR - Ajout de la dur�e de l'�v�nement pr�c�dent � l'aide d'un preparedStatement et d'une instantiation de la classe EventTimeManagement.
	                 * EN - addition of event duration thanks to preparedStatement and instantiation of EventTimeManagement class.
	                 */
	            
	                logger.info("======================================= Datetime evenement : " + eventstartdate + "\n" + "type �v�nement : " + eventtype + "\nIntitul� : " + entitled + "\nDate �v�nement pr�c�dent : " + LastEventDate + "\nDur�e event pr�c�dent : " + timeformat + "\nId evenement: " + currentEventID + "\n");
	                resources gestionRessources = new resources();
	                gestionRessources.insertionresources(externallinkslist, currentEventID);
	                /*
	                 * EN - Everything related to resources table (external links) was made in gestionRessources class
	                 * FR - Tous les liens externes sont analys�s dans gestionRessources.insertionRessources. Il prend les listes des liens externes et l'id de l'event actuel en param�tre.
	                 */
	                logger.info("======================================= End of event");
				}
			}
		} 
		}
	}



