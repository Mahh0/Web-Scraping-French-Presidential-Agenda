package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.DateFormatSymbols;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.CombiningEvaluator.And;
import init.ClearTablesAtProgramStartup;
import init.MainLinks;
import init.MySqlConnection;
import init.ReadPropertyFile;
import init.SSLHelper;
import scraping.DataEnrichment;
import scraping.EventsTimeManagements;
import scraping.docToLocalHtml;
import scraping.resources_bdd;
import org.jsoup.select.Elements;
import java.util.*;
import org.apache.commons.lang3.*;


public class Main {
	private static Logger logger = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) throws Exception {
		logger.info("Starting the program");
		logger.debug("Firewall must be set to OFF !");
		
		MySqlConnection.setParameters();
		// Calling setParameters method from MySqlConnection which will set database parameters : user, pw, url, ... using config.properties file

		Connection con = new MySqlConnection().getConnection();
		/*
		 * Instantation of a Connection con that uses getConnection from MySqlConnection. It is used in the classes where we need interactions with the database
		 * It uses ReadPropertyFile methods values.
		 */


		 logger.debug("AskForCleanup => " + ReadPropertyFile.getAskforcleanup());
		 if (ReadPropertyFile.getAskforcleanup().equals("yes")) {
			logger.info("Asking for database clearing, press yes or no.");
			new ClearTablesAtProgramStartup();
		 }
		 // Ask to the users if he want's to clear the database (y/n)


		logger.debug("AskForHtmlCleanup => " + ReadPropertyFile.getAskForHtmlCleanup());
		File dir = new File("webScraping/src/main/resources/htmlAgenda");
		if (ReadPropertyFile.getAskForHtmlCleanup().equals("yes")) {
			System.out.println("Do you wan't to clear local HTML tables ? (y/n)");
		   	Scanner sc = new Scanner(System.in);
			String str = sc.nextLine();
			if (str.contains("yes")) {
				for(File file: dir.listFiles()) 
				if (!file.isDirectory()) 
				file.delete();
			}
		}
		// If in the config file AskForHtmlCleanup is set to yes, it will check if there are HTML files in htmlAgenda folder and it will ask to user if he want's to clear them


		LocalDate currentdate = LocalDate.now();
        String cyear = currentdate.format(DateTimeFormatter.ofPattern("yyyy"));
        String cmonth = currentdate.format(DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH));
        String cmonthWaccent = StringUtils.stripAccents(cmonth);
		// Get the current month and year to not download this page (because it is in progress so if there is content being added we will not get it)

		
		MainLinks links = new MainLinks();
		// Instantiation of the MainksLinks which generate links to analyse. It returns an ArrayList with these links.


		for(Object link : links.AnalyseLiens()) {
			// For each link in the generated ArrayList of links
			logger.info("----------------------- Analyzing " + link);
			String splitter[] = ((String) link).split("-");
			String month = splitter[1]; // Month extracted from link (ex: decembre, janvier)
			String year = splitter[2]; // Year extracted from link (ex : 2002)
			SimpleDateFormat inputFormat = new SimpleDateFormat("MMMM");
			SimpleDateFormat outputFormat = new SimpleDateFormat("MM");
			Calendar cal = Calendar.getInstance();
			switch(month) {
				case "aout":
					month = "août";
					break;
				case "decembre":
					month = "décembre";
					break;
				case "fevrier":
					month = "février";
					break;
			}
			cal.setTime(inputFormat.parse(month));
			String monthFormat = outputFormat.format(cal.getTime());
			// Convert month (ex : decembre, septembre) letters to number (09) (monthFormat output

			Document doc;	
			if (ReadPropertyFile.getUseLocalHtml().equals("yes")) {
				File input = new File("webScraping/src/main/resources/htmlAgenda/html-" + month + "-" + year + ".html");
				if (input.exists() == true) {
					doc = Jsoup.parse(input, "UTF-8");
				} else {
					doc = SSLHelper.getConnection(link.toString()).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").timeout(50000).get();
					
					if (link.toString()!= "http://www.elysee.fr/agenda-" + cmonthWaccent + "-" + cyear) { // while the link is not equal to current month
						try {
						docToLocalHtml.downloadPage(doc.outerHtml(), link.toString()); // download the page
						} catch (Exception e) {
							logger.error("Error while downloading the page" + month + year);
						}
					}

				}
			} else {
				doc = SSLHelper.getConnection(link.toString()).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").timeout(50000).get();
			}
			/*
			 * If in prop. files we wan't to use Local HTML for agenda (yes)
			 * 		=> we read the file, if it exists, we put it in a doc
			 * 		=> else (if it doesn't exists), we connect and we add it to local files
			 * If we don't wan't, we connect 
			 */
			

			Elements containers = doc.select("section.container > div.container--thicker");
			// Selecting every containers in the web page (1 container = 1 day)
			for (Element container : containers) {
				// For each day (container) in days (containers)
				 
				Elements rows = container.select("ul.list-table > li");
				Elements dat = container.select("div.sticker > h3.sticker__content");
				String day = dat.select("span").text().replaceAll("[^\\d.]", "");
				/*
				 * The previous lines defines some values for each container (=day) :
				 * 	- rows : it represents every lines, containing hour + kind of event + title
				 *  - day : it is only the day (numeric) like 06, selected in the h3 sticker content, in the container
				 */

				for (Element row : rows) {
					// For each row (event) in this day (ex : there is 1 event at 09:30AM, 1 at 10:30AM, 1 at 6:00PM)
					String eventhour = row.select("div.list-table__details > p.list-table__hour").text(); // We select the hour of the event (ex : 09:30, Morning, Afternoon)
					String eventtype = row.select("div.list-table__details > p.list-table__type").text(); // We select the type of the event (ex : Meeting)
					String entitled = row.select("div.list-table__content > p.m-b-n").first().text(); // We select the entitled
					Elements externallinkslist = row.select("a[href]"); // We select the links (ressources) for this event => there can be several so stocked in Elements
					
		
					String finaleventhour = eventhour.replaceAll("[^\\d.]", ":");
					int  specialHour = 0;
					if(eventhour.equalsIgnoreCase("Matin")) {
						finaleventhour="08:00";
						specialHour = 1;	
					} else if(eventhour.equalsIgnoreCase("Après-midi")) {
						finaleventhour="14:00";
						specialHour = 2;
					} else if(eventhour.equalsIgnoreCase("Toute la journée")) {
						finaleventhour="08:02";
						specialHour = 3;
					}
					// Put the hour with a good format for the database (in case where it is text, and also set a string to remember that this is not a common data)
					
					String eventstartdate = year + "-" + monthFormat + "-" + day + " " + finaleventhour + ":00";
					// At this step we have enought to put the datetime in the database, so we create the String.
					
					final String insertionevent = "INSERT INTO evenement (dated,type,intitule) values (?,?,?)";
	                PreparedStatement preparedStatementevent = con.prepareStatement(insertionevent);
	                    preparedStatementevent.setString(1, eventstartdate);
	                    preparedStatementevent.setString(2, eventtype);
	                    preparedStatementevent.setString(3, entitled);
	                    preparedStatementevent.executeUpdate();
						preparedStatementevent.close();
					// Inserting an event with a prepared statement. We have start date (dated), type (type) and entitled (intitule), we just need the duration.


	                final String getIdRequest = "SELECT id from evenement where dated = '" + eventstartdate + "'";
	                PreparedStatement getId =  con.prepareStatement(getIdRequest);
	                ResultSet a = getId.executeQuery();	                
	                int currentEventID = 0;
	                if (a.next()) {
	                	currentEventID = a.getInt(1);
	                }
					getId.close();
					a.close();
					int previousEventID = currentEventID-1;
					// Recovery of the ID of the event that we have just inserted, it will be needed for durations of events (operation between 2 events). We also have the previous one.
	                
	               DataEnrichment te = new DataEnrichment();
	               te.typeEnrich(entitled, eventtype, currentEventID);
	               // Call of the typeEnrich method, if the event that we have inserted has no type, we will try to guess one from the entitled.
	               te.personnalitesEnrich(entitled, currentEventID);
	               // Call of the personnalitesEnrich method, it will try to guess if the event has peoples involved.
	               
	                
	            	final String lastDate = "SELECT dated FROM evenement WHERE id= ? ";
	                PreparedStatement derniereDate = con.prepareStatement(lastDate);
	                derniereDate.setInt(1, previousEventID);
	                ResultSet to = derniereDate.executeQuery();
	                String LastEventDate = "0000-00-00 00:00:00";
	                if(to.next()) {
	                	LastEventDate = to.getString(1);
	                }
					derniereDate.close();
					to.close();
					// Selecting the start date of the previous event, for durations calculations, thanks the the previousEventID.

    
	                final String updateeven = "UPDATE evenement SET duree = ? WHERE id= ? ";
	        		EventsTimeManagements etms = new EventsTimeManagements(LastEventDate, eventstartdate, specialHour);
	        		String timeformat = etms.CalculDurees();
	                PreparedStatement pSduree = con.prepareStatement(updateeven); // Ce statement et ce qui suit permet d'insérer la durée calculée précédemment dans la table
	                pSduree.setString(1, timeformat);
	                pSduree.setInt(2, previousEventID);
	                pSduree.executeUpdate();
					pSduree.close();
					// Adding the event duration, thanks to emts, and a preparedStatement. It adds the event duration to the previous event (because it is made thanks to the currentevent)
	
	                
	                resources_bdd gestionRessources = new resources_bdd();
	                gestionRessources.insertionresources(externallinkslist, currentEventID);
	                // Everything related to  the resources will be made in gestionRessources
				}
			}
		} 
		}
	}



