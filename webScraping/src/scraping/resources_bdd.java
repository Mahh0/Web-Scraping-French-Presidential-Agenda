package scraping;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import init.MySqlConnection;
import init.SSLHelper;

public class resources_bdd {
	int idRessource=0;
	/**
	 * This class is used for external links, it contains 1 class : insertionsressources.
	 */

	Connection con = new MySqlConnection().getConnection();
	Logger logger = LogManager.getLogger(resources_bdd.class);
	/**
	 * Defining some parameters, mysql database connection and logger.
	 * @throws SQLException
	 * @throws IOException
	 */

	public void insertionresources(Elements externallinkslist, int currentEventID) throws SQLException, IOException {
		/**
		 * This method cares of calling ressources_scraping methods to scrap the datas from the website, and then insert theses datas.
		 * 		- externallinklist : list of links.
		 * 		- currentEventID : to insert
		 */
		
		
			for (Element lienu : externallinkslist) {
				try {	
				final Document docU = SSLHelper.getConnection(lienu.absUrl("href")).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0").timeout(1000000).get();
				resources_scraping ext = new resources_scraping(docU);
				ArrayList<String> PdfsTest = ext.getPdf();
				ArrayList<String> vidYtb = ext.getVidYtb();
				ArrayList<String> vidDaily = ext.getVidDaily();
				String tex = ext.getText();
				ArrayList<String> ImgsTest = ext.getImgs();
				ArrayList<String> TwitterArray = ext.getTwitter();
				ArrayList<String> instaArray = ext.getInsta();
				ArrayList<String> folderArray = ext.getDossier();
				Hashtable<String, String> insertionsArray = ext.getInsertions();
				/**
				 * For each link in the links list, we will make a GET request to the link, and then we will 
				 * instantiate ressources_scraping method with the document and then call each method.
				 */
				
				boolean isEmptyIMG = ImgsTest.isEmpty();
				boolean isEmptyPDF = PdfsTest.isEmpty();
				boolean isEmptyVidYtb = vidYtb.isEmpty();
				boolean isEmptyVidDaily = vidDaily.isEmpty();
				boolean isEmptyTwitterArray = TwitterArray.isEmpty();
				boolean isEmptyinstaArray = instaArray.isEmpty();
				boolean isEmptyfolderArray = folderArray.isEmpty();
				boolean isEmptyHashtableInsertionsArray = insertionsArray.isEmpty();
				/**
				 * Boolean values to know if the returns of the above methods are empty or not.
				 */

				 PreparedStatement testIfLinksExists = con.prepareStatement("SELECT id FROM ressources WHERE url = (?)");
				 testIfLinksExists.setString(1, lienu.absUrl("href"));
				 ResultSet rtest = testIfLinksExists.executeQuery();
				 while (rtest.next()) {
					 idRessource = rtest.getInt("id");
				 }


				 /**
				  * On teste si le lien a déjà été inséré dans la table ressource
				  */
				
				 if (rtest.next() == false) { // Si le lien n'a pas encore été inséré
					PreparedStatement insertRessource = con.prepareStatement("INSERT INTO ressources(idTable, url) values (?, ?)");
					insertRessource.setInt(1, currentEventID);
					insertRessource.setString(2, lienu.absUrl("href"));
					insertRessource.executeUpdate();
					insertRessource.close();

					PreparedStatement recupidInsertRessource = con.prepareStatement("SELECT id FROM ressources WHERE idTable = (?) and url = (?)");
					recupidInsertRessource.setInt(1, currentEventID);
					recupidInsertRessource.setString(2, lienu.absUrl("href"));
					ResultSet recupId = recupidInsertRessource.executeQuery();
					while (recupId.next()) {
						idRessource = recupId.getInt("id");
					  }
					recupId.close();
					recupidInsertRessource.close();

				// On insert une ligne dans ressource avec le lien puis on récupère l'id de cette ligne.
				  
				  }
				
				  // A l'aide de l'ID récupérer précédement, on peut maintenant tester si la ressource a des données, si elle en a, il y a insertion
				if (!isEmptyPDF) {	
					PdfsTest.forEach((o) -> {
						try {
							PreparedStatement insertpdf = con.prepareStatement("INSERT INTO ressource_detail(idressources, categorie, contenu) VALUES(?, ?, ?)");
							insertpdf.setInt(1, idRessource);
							insertpdf.setString(2, "PDF");
							insertpdf.setString(3, o);
							insertpdf.executeUpdate();
							insertpdf.close();
						} catch (SQLException e) {
							logger.error("SQL Error while PDF insert !" + e);
						}
						});	
						/**
						 * Foreach entrie present in PdfsTest Array, we will create a row in the database.
						 */
				}
				
				if (!isEmptyVidDaily) {

					vidDaily.forEach((o) -> {
						try {
							PreparedStatement insertviddaily = con.prepareStatement("INSERT INTO ressource_detail(idressources, categorie, `sous-categorie`, contenu) VALUES(?, ?, ?, ?)");
							insertviddaily.setInt(1, idRessource);
							insertviddaily.setString(2, "VIDEO");
							insertviddaily.setString(3, "Dailymotion");
							insertviddaily.setString(4, o);
							insertviddaily.executeUpdate();
							insertviddaily.close();
						} catch (SQLException e) {
							logger.error("SQL Error while Dailymotion insert !" + e);
						}
						});
						/**
						 * Foreach video present in vidDaily Array, we will create a row in the database.
						 */
				}
				
				if (!isEmptyVidYtb) {
					vidYtb.forEach((o) -> {
						try {
							PreparedStatement insertvidytb = con.prepareStatement("INSERT INTO ressource_detail(idressources, categorie, `sous-categorie`, contenu) VALUES(?, ?, ?, ?)");
							insertvidytb.setInt(1, idRessource);
							insertvidytb.setString(2, "VIDEO");
							insertvidytb.setString(3, "Youtube");
							insertvidytb.setString(4, o);
							insertvidytb.executeUpdate();
							insertvidytb.close();
						} catch (SQLException e) {
							logger.error("SQL Error while Youtube insert !" + e);
						}
						});
						/**
						 * Foreach entrie present in vidYtb Array, we will create a row in the database.
						 */
				}
				
				
				if (tex != "") {
					PreparedStatement inserttxt = con.prepareStatement("INSERT INTO ressource_detail(idressources, categorie, contenu) VALUES(?, ?, ?)");
					inserttxt.setInt(1, idRessource);
					inserttxt.setString(2, "TEXTE");
					inserttxt.setString(3, tex);
					inserttxt.executeUpdate();
					inserttxt.close();
					// if (tex.length()<=16777215)
			
					/**
					 * If string contains text, we insert in the database a row with TEXT.
					 */
				}
				
				
				if (!isEmptyIMG){
					ImgsTest.forEach((n) -> {
					try {
						PreparedStatement insertimg = con.prepareStatement("INSERT INTO ressource_detail(idressources, categorie, contenu) VALUES(?, ?, ?)");
						insertimg.setInt(1, idRessource);
						insertimg.setString(2, "IMG");
						insertimg.setString(3, n);
						insertimg.executeUpdate();
						insertimg.close();
					} catch (SQLException e) {
						logger.error("SQL Error while Image insert !" + e);
					}
					});
					/**
					 * Foreach entrie present in Imgs Array, we will create a row in the database.
					 */
				}
				
				if (!isEmptyTwitterArray){
					TwitterArray.forEach((n) -> {
					try {
						PreparedStatement inserttwitter = con.prepareStatement("INSERT INTO ressource_detail(idressources, categorie, `sous-categorie`, contenu) VALUES(?, ?, ?, ?)");
						inserttwitter.setInt(1, idRessource);
						inserttwitter.setString(2, "MEDIA");
						inserttwitter.setString(3, "Twitter");
						inserttwitter.setString(4, n);
						inserttwitter.executeUpdate();
						inserttwitter.close();
					} catch (SQLException e) {
						logger.error("SQL Error while twitter insert !" + e);
					}
					});
				}
				
				if (!isEmptyinstaArray){
					instaArray.forEach((n) -> {
					try {
						PreparedStatement insertinsta = con.prepareStatement("INSERT INTO ressource_detail(idressources, categorie, `sous-categorie`, contenu) VALUES(?, ?, ?, ?)");
						insertinsta.setInt(1, idRessource);
						insertinsta.setString(2, "MEDIA");
						insertinsta.setString(3, "Instagram");
						insertinsta.setString(4, n);
						insertinsta.executeUpdate();
						insertinsta.close();
					} catch (SQLException e) {
						logger.error("SQL Error while instagram insert !" + e);
					}
					
					});		
				}

				if (!isEmptyHashtableInsertionsArray){
					insertionsArray.forEach((n, n2) -> {
					try {
						PreparedStatement insertlienindirect = con.prepareStatement("INSERT INTO ressource_detail(idressources, categorie, contenu) VALUES(?, ?, ?)");
						insertlienindirect.setInt(1, idRessource);
						insertlienindirect.setString(2, "LIEN_INDIRECTE");
						insertlienindirect.setString(3, n + " - " + n2);
						insertlienindirect.executeUpdate();
						insertlienindirect.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					});	
				}
				

				if (!isEmptyfolderArray){
					folderArray.forEach((n) -> {
						try {
							PreparedStatement insertdossier = con.prepareStatement("INSERT INTO ressource_detail(idressources, categorie, contenu) VALUES((?), (?), (?))");
							insertdossier.setInt(1, idRessource);
							insertdossier.setString(2, "DOSSIER");
							insertdossier.setString(3, n);
							insertdossier.executeUpdate();
							insertdossier.close();
						} catch (SQLException e) {
							logger.error("SQL Error while instagram insert !" + e);
						}
						});	
				}
			}  catch (IOException ioe) {
				logger.error("IOException while connecting to the page");
				logger.error(ioe);
			}
				}
			
			}
		}



