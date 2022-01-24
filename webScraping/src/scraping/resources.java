package scraping;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import init.MySqlConnection;
import init.SSLHelper;

public class resources {
	Elements externallinkslist;
	int currentEventID;

	public void insertionresources(Elements externallinkslist, int currentEventID) throws SQLException, IOException {
		/*
		 * FR - Cette classe s'occupe des insertions dans la table ressources.
		 * EN - This class takes care of the insertions in the resources table.
		 */
		Logger logger = LogManager.getLogger(resources.class);

		this.externallinkslist = externallinkslist;
		this.currentEventID = currentEventID;
		Connection con = new MySqlConnection().getConnection();
		
		if (! externallinkslist.isEmpty()) {
			logger.info("The event " + currentEventID + " has ressources ! Processing...");
			for (Element lienu : externallinkslist) {
				/*
				 * FR - On teste si notre liste contient des liens, si oui, on exécute pour chaque lien la suite de la classe
				 * EN - We test if our list contains links, if so, we run the rest of the class for each link
				 */						
				logger.info("=============== \nAnalyzing a ressource ! " + lienu.absUrl("href") + " Associated event ID : " + currentEventID);
				
				final Document docU = SSLHelper.getConnection(lienu.absUrl("href")).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").timeout(1000000).get();
				ExternalLinks ext = new ExternalLinks(docU);
				ArrayList<String> PdfsTest = ext.getPdf();
				ArrayList<String> vidYtb = ext.getVidYtb();
				ArrayList<String> vidDaily = ext.getVidDaily();
				String tex = ext.getText();
				ArrayList<String> ImgsTest = ext.getImgs();
				ArrayList<String> TwitterArray = ext.getTwitter();
				ArrayList<String> instaArray = ext.getInsta();
				ArrayList<String> folderArray = ext.getDossier();
				/*
				 * FR - Connection à la page, et analyse + appel des méthodes de la classe ExternalLinks, pour retourner ce que contient le code HTML.
				 * EN - Connection to the page, and analysis + call of the methods of the ExternalLinks class, to return what there is on the HTML
				 */
				
				
				final String insertionlien = "INSERT INTO ressources (idTable, url, categorie, contenu) values (?,?,?,?)";
				PreparedStatement psLien = con.prepareStatement(insertionlien);
				psLien.setInt(1, currentEventID);
				/*
				 * FR - Préparation des insertions
				 * EN - Preparation of insertions
				 */
				
				boolean isEmptyIMG = ImgsTest.isEmpty();
				boolean isEmptyPDF = PdfsTest.isEmpty();
				boolean isEmptyVidYtb = vidYtb.isEmpty();
				boolean isEmptyVidDaily = vidDaily.isEmpty();
				boolean isEmptyTwitterArray = TwitterArray.isEmpty();
				boolean isEmptyinstaArray = instaArray.isEmpty();
				boolean isEmptyfolderArray = folderArray.isEmpty();
				/*
				 * FR - Conditions pour tester si les ArrayList sont remplies
				 * EN - Conditions for testing if the ArrayLists are filled or not
				 */
				
				if ((isEmptyTwitterArray == true)&&(isEmptyPDF == true)&&(isEmptyVidDaily == true)&&(isEmptyVidYtb == true)&&(tex == "")&&(isEmptyIMG == true)&&(isEmptyinstaArray == true)&&(isEmptyfolderArray == true)) {
					final String insertionlienVide = "INSERT INTO ressources (idTable, url) values (?,?)";
					PreparedStatement psLienVide = con.prepareStatement(insertionlienVide);
					psLienVide.setInt(1, currentEventID);
					psLienVide.setString(2, lienu.absUrl("href"));
					psLienVide.executeUpdate();
					logger.info("The ressource " + lienu + " will be added without anything (nothing detected on the web page !)");
					/*
					 * FR - Si tout est vide, on insert dans la table ressources un lien sans rien.
					 * EN - If everything is empty, we insert a link with nothing in the resources table.
					 */	
				} else {
					logger.info("Things found on the web page ! Porcessing...");
				if (isEmptyPDF == false) {	
					/*
					 * FR - Si l'arrayList PDF n'est pas vide, pour chaque lien dans cette ArrayList, on ajoute une entrée dans la table ressources
					 * EN - If the PDF arrayList is not empty, for each link in this ArrayList, we add an entry in the resources table.
					 */
					PdfsTest.forEach((o) -> {
						try {
							psLien.setString(2, lienu.absUrl("href"));
							psLien.setString(3, "PDF");
							psLien.setString(4, o);
							psLien.executeUpdate();
						} catch (SQLException e) {
							logger.error("SQL Error while PDF insert !" + e);
						}
						logger.info("The following will be added : " + o);
						});	
				}
				
				if (isEmptyVidDaily == false) {
					/*
					 * FR - Si l'arrayList VidDaily n'est pas vide, pour chaque lien dans cette ArrayList, on ajoute une entrée dans la table ressources
					 * EN - If the VidDaily arrayList is not empty, for each link in this ArrayList, we add an entry in the resources table.
					 */
					vidDaily.forEach((o) -> {
						try {
							psLien.setString(2, lienu.absUrl("href"));
							psLien.setString(3, "VID");
							psLien.setString(4, o);
							psLien.executeUpdate();
						} catch (SQLException e) {
							logger.error("SQL Error while Dailymotion insert !" + e);
						}
						logger.info("The following will be added : " + o);
						});	
					
				}
				
				if (isEmptyVidYtb == false) {
					/*
					 * FR - Si l'arrayList VidYtb n'est pas vide, pour chaque lien dans cette ArrayList, on ajoute une entrée dans la table ressources
					 * EN - If the VidYtb arrayList is not empty, for each link in this ArrayList, we add an entry in the resources table.
					 */
					vidYtb.forEach((o) -> {
						try {
							psLien.setString(2, lienu.absUrl("href"));
							psLien.setString(3, "VID");
							psLien.setString(4, o);
							psLien.executeUpdate();
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							logger.error("SQL Error while Youtube insert !" + e);
						}
						logger.info("The following will be added : " + o);
						});	
				}
				
				
				if (tex != "") {
					/*
					 * FR - Si le String contient du texte, on insert une ligne avec du texte.
					 * EN - If string contains text, we insert in the database a row with TEXT.
					 */
					psLien.setString(2, lienu.absUrl("href"));
					psLien.setString(3, "TXT");
					if (tex.length()<=16777215)
						{
							psLien.setString(4, tex);
						}
					psLien.executeUpdate();
					logger.info("Text added !");
				}
				
				
				if (isEmptyIMG == false){
					/*
					 * FR - Si l'arrayList des liens des images n'est pas vide, pour chaque lien on insert une colonne.
					 * EN - If the arrayList of image links is not empty, a column is inserted for each link.
					 */
					int im = 0;
					ImgsTest.forEach((n) -> {
					try {
						psLien.setString(2, lienu.absUrl("href"));
						psLien.setString(3, "IMG");
						psLien.setString(4, n);
						psLien.executeUpdate();
					} catch (SQLException e) {
						logger.error("SQL Error while Image insert !" + e);
					}
					});
					logger.info("Image(s) added !");
				}
				
				if (isEmptyTwitterArray == false){
					TwitterArray.forEach((n) -> {
					try {
						psLien.setString(2, lienu.absUrl("href"));
						psLien.setString(3, "TWI");
						psLien.setString(4, n);
						psLien.executeUpdate();
					} catch (SQLException e) {
						logger.error("SQL Error while twitter insert !" + e);
					}
					logger.info("The following will be added : " + n);
					});		
				}
				}
				
				if (isEmptyinstaArray == false){
					instaArray.forEach((n) -> {
					try {
						psLien.setString(2, lienu.absUrl("href"));
						psLien.setString(3, "insta");
						psLien.setString(4, n);
						psLien.executeUpdate();
					} catch (SQLException e) {
						logger.error("SQL Error while instagram insert !" + e);
					}
					
					logger.info("The following will be added : " + n);
					});		
				}
				
				if (isEmptyfolderArray == false){
					int fSize = folderArray.size();	
					logger.debug("FOLDER ARRAY SIZE :" + fSize);
					
					if (fSize == 2) {
						final String insertiondossier = "INSERT INTO dossier(lien) values (?)";
						PreparedStatement insertdossier = con.prepareStatement(insertiondossier);
						insertdossier.setString(1, folderArray.get(1));
						insertdossier.executeUpdate();
					} else if (fSize == 3) {
						final String insertiondossier2 = "INSERT INTO dossier(lien, intitule) values (?, ?)";
						PreparedStatement insertdossier2 = con.prepareStatement(insertiondossier2);
						insertdossier2.setString(1, folderArray.get(1));
						insertdossier2.setString(2, folderArray.get(2));
						insertdossier2.executeUpdate();
					} else {
						logger.error("FOLDER ERROR");
					}
				}
				logger.info("=============== \nEnd of ressource analysis ! ");

			}
		}
	}
	}

