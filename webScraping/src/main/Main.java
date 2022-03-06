package main;
import java.io.File;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
		logger.info("Starting the program. Firewall must be set to OFF if you wan't to use a remote SQL Server !");
		MySqlConnection.setParameters();
		Connection con = new MySqlConnection().getConnection();
		/**
		 * Call of the setParameters method from MySqlConnection which set database parameters and others, using config.properties file.
		 */

		/**
		 * Instantation of a Connection con that uses getConnection from MySqlConnection
		 * It is used in the classes where we need interactions with the database
		 * It uses ReadPropertyFile methods values.
		 */

		 switch (ReadPropertyFile.getDatabasecleanup()) {
			 case "true":
				logger.info("Parameter \"true\" found in properties file for databaseCleanup, cleaning it !");
			 	new ClearTablesAtProgramStartup();
			 	break;
			case "false":
				logger.info("Parameter \"false\" found in properties file for databaseCleanup, nothing done !");
				break;
			default:
				logger.info("Incorrect parameter for Databasecleanup, please correct it !");
				System.exit(1);
		 }
		 /**
		  * This asks through the parameter in the config file to the user if he want's to clear the tables. If yes, it is cleaned, if no, no, if not yes or no, exit program (error)
		  */
	
		  switch (ReadPropertyFile.getLocalfilesCleanup()) {
			case "true":
				logger.info("Files will not be kept, localfilesParameters set to false !");
				File dir = new File("webScraping/src/main/resources/htmlAgenda");
				for(File file: dir.listFiles()) 
				if (!file.isDirectory()) 
				file.delete();
			 	break;
			case "false":
				logger.info("Files will be kept, localfilesCleanup parameter set to true");
				break;
			default:
				logger.error("Incorrect parameter found ! Please correct it");
				System.exit(1);
		  }
		  /**
		   * Reading "LocalfilesCleanup into config file. If it is set to true, files will not be kept, so deleted, if false they are keep, else error parameter incorrect"
		   */

		LocalDate currentdate = LocalDate.now();
        String cyear = currentdate.format(DateTimeFormatter.ofPattern("yyyy"));
        String cmonth = currentdate.format(DateTimeFormatter.ofPattern("MMMM", Locale.FRENCH));
        String cmonthWaccent = StringUtils.stripAccents(cmonth);
		/**
		 * Getting current month and year (this will be useful, particulary to not download the current 
		 * month page, because it is in progress so if there is content being added we will not get it)
		 */

		MainLinks links = new MainLinks();
		/**
		 * Instantiating the MainLinks class which generate links to analyse. This returns an ArrayList containing the generated links.
		 */

		for(Object link : links.AnalyseLiens()) {
			/**
			 * For each link (defined as an object) in the ArrayList returned by "links"
			 */
			logger.info(" -------------> Analyzing " + link);
			String splitter[] = ((String) link).split("-");
			String month = splitter[1];
			String year = splitter[2];
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
			/**
			 * Getting the month and the year (we can also get it with MainLinks but it's easier like that), thanks to splitters
			 * Defining 2 dateformatters to convert month (eg : Mai(may) -> 05)
			 * Adding accents to months because dateformatters needs them
			 * monthFormat : month converted (format 05) (letters->numbers)
			 */

			Document doc = Jsoup.parse("", "UTF-8"); // Initialize a document (the html agenda which will be analyzed)
			switch (ReadPropertyFile.getUseLocalHtml()) {
				case "yes": //We wan't to use local html files if possible
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
									logger.error(e);
								}
							}
						}
					break;
				case "no": // We do not wan't to use local html files
						doc = SSLHelper.getConnection(link.toString()).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").timeout(50000).get();
					break;
				default:
						logger.error("Error in parameters files, yes or no in uselocalhtml parameter !");
						System.exit(1);
			}				
			/*
			 * Defining the document to analyse
			 * 
			 * If in prop. files we wan't to use Local HTML for agenda (yes)
			 * 		=> we read the file, if it exists, we put it in a doc and continue the program
			 * 		=> else (if it doesn't exists), we make a GET request to the website, we add it to local files and we put it into a doc
			 * If we don't wan't, we connect 
			 */
			

			Elements containers = doc.select("section.container > div.container--thicker");
			/**
			 * Selecting every containers on the HTML file (1 container = 1day)
			 */

			for (Element container : containers) {
				/**
				 * For each container (day) in containers (Elements containers is a list of containers)
				 */
				 
				Elements rows = container.select("ul.list-table > li");
				Elements dat = container.select("div.sticker > h3.sticker__content");
				String day = dat.select("span").text().replaceAll("[^\\d.]", "");
				/**
				 * The previous lines defines some values for each container (=day) :
				 * 	- rows : it represents every lines, containing hour + kind of event + title
				 * 	- day : it is only the day (numeric) like 06, selected in the h3 sticker content, in the container
				 * 	- dat : date (eg : Jeudi 17 Septembre)
				 */

				for (Element row : rows) {
					/**
					 * Now we are analyzing rows, containing for each row an event, with hour, type, entitled and 0, 1 or several links.
					 */
					String eventhour = row.select("div.list-table__details > p.list-table__hour").text(); // We select the hour of the event (ex : 09:30, Morning, Afternoon)
					String eventtype = row.select("div.list-table__details > p.list-table__type").text(); // We select the type of the event (ex : Meeting)
					String entitled = row.select("div.list-table__content > p.m-b-n").first().text(); // We select the entitled
					Elements externallinkslist = row.select("a[href]"); // We select the links (ressources) for this event => there can be several so stocked in Elements
					/**
					 * Selecting the eventhour, the eventtype, the entitled, and we are creating a list of elements (externallistlinks) to analyse them.
					 */
				
					int  specialHour = 0;
					switch (eventhour){
						case "Matin":
								eventhour="08:00";
								specialHour = 1;
							break;
						case "Après-midi":
								eventhour="14:00";
								specialHour = 2;
							break;
						case "Toute la journée":
								eventhour="08:00";
								specialHour = 3;
							break;
					}
					/**
					 * Sometimes, the hour can be "Matin", "Après-Midi" or "Toute la journée)", so we define an hour
					 * and also another value to remember, for the duration to make special calculations.
					 */
					
					String eventstartdate = year + "-" + monthFormat + "-" + day + " " + eventhour + ":00";
					/**
					 * At this step, we have enought for the event start date : year, month, day and hour.
					 */
					
					final String insertionevent = "INSERT INTO evenement (dated,type,intitule) values (?,?,?)";
	                PreparedStatement preparedStatementevent = con.prepareStatement(insertionevent);
	                    preparedStatementevent.setString(1, eventstartdate);
	                    preparedStatementevent.setString(2, eventtype);
	                    preparedStatementevent.setString(3, entitled);
	                    preparedStatementevent.executeUpdate();
						preparedStatementevent.close();
					/**
					 * Insert of an even with the start date, the type and the entitled. We just need to add the duration now (and type enrichment.)
					 */


	                PreparedStatement getId =  con.prepareStatement("SELECT id from evenement where dated = (?)");
					getId.setString(1, eventstartdate);
	                ResultSet a = getId.executeQuery();	                
	                int currentEventID = 0;
	                	if (a.next()) {
	                		currentEventID = a.getInt(1);
	               		 }
					getId.close();
					a.close();
					int previousEventID = currentEventID-1;
					/**
					 * Recovery of the event of the current, and the previous event ID inserted in database.
					 * Theses will be needed for events duration (operations between 2 events)
					 */
	                

	               DataEnrichment te = new DataEnrichment();

				   if (eventtype.isEmpty()) {
	               		te.typeEnrich(entitled, eventtype, currentEventID);
				   } 
				   /**
					* Calling "typeEnrich" method from DataEnrichment, to add a type, only if the eventtype is empty. It will try to guess from the entitled.
				    */
	               
					te.personnalitesEnrich(entitled, currentEventID);
				   /**
					* Calling personnalitesEnrich method, which will try to guess thanks to the entitled if the event has peoples involved
				    */
	                
	                PreparedStatement LastEventDate_ps = con.prepareStatement("SELECT dated, type FROM evenement WHERE id= (?)");
	                LastEventDate_ps.setInt(1, previousEventID);
	                ResultSet to = LastEventDate_ps.executeQuery();
	                String LastEventDate = "0000-00-00 00:00:00";
					String LastEventType = "";
	                if(to.next()) {
	                	LastEventDate = to.getString(1);
						LastEventType = to.getString(2);
	                }
					LastEventDate_ps.close();
					to.close();
					/**
					 * Selecting start date and type from the preivous event for durations calculations
					 */

    
	        		EventsTimeManagements etms = new EventsTimeManagements(LastEventDate, eventstartdate, LastEventType);
	        		String timeformat = etms.CalculDurees();
	                PreparedStatement pSduree = con.prepareStatement("UPDATE evenement SET duree = (?) WHERE id= (?) "); // Ce statement et ce qui suit permet d'insérer la durée calculée précédemment dans la table
	                pSduree.setString(1, timeformat);
	                pSduree.setInt(2, previousEventID);
	                pSduree.executeUpdate();
					pSduree.close();
					/**
					 * Calling EventsTimeManagement method, in a String (timeformat) to calculate length of the preivous event.
					 */
	                

					if (! externallinkslist.isEmpty()) {
	                resources_bdd gestionRessources = new resources_bdd();
	                gestionRessources.insertionresources(externallinkslist, currentEventID);
					}
					/**
					 * Now that we did everything related to the "agenda", we can now analyse external links. Everything is done in this method.
					 */
				}
			}
		} 
		}
	}



