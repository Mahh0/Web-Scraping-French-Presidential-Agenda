package scraping;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
			
				PreparedStatement psLien = con.prepareStatement("INSERT INTO ressources (idTable, url, categorie, contenu) values (?,?,?,?)");
				psLien.setInt(1, currentEventID);
				// Preparing inserts.
				
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
				
				if ((isEmptyTwitterArray == true)&&(isEmptyHashtableInsertionsArray==true)&&(isEmptyPDF == true)&&(isEmptyVidDaily == true)&&(isEmptyVidYtb == true)&&(tex == "")&&(isEmptyIMG == true)&&(isEmptyinstaArray == true)&&(isEmptyfolderArray == true)) {
					final String insertionlienVide = "INSERT INTO ressources (idTable, url) values (?,?)";
					PreparedStatement psLienVide = con.prepareStatement(insertionlienVide);
					psLienVide.setInt(1, currentEventID);
					psLienVide.setString(2, lienu.absUrl("href"));
					psLienVide.executeUpdate();
					psLienVide.close();
					/**
					 * If everything is empty, an empty link in ressources will be inserted.
					 */
				} else {
					/**
					 * Else, we will test for each content type (pdf, img, ytb, ...) 
					 * if it is not empty, and if it is, we will add it in the database.
					 */
				if (isEmptyPDF == false) {	
					PdfsTest.forEach((o) -> {
						try {
							psLien.setString(2, lienu.absUrl("href"));
							psLien.setString(3, "PDF");
							psLien.setString(4, o);
							psLien.executeUpdate();
						} catch (SQLException e) {
							logger.error("SQL Error while PDF insert !" + e);
						}
						});	
						/**
						 * Foreach entrie present in PdfsTest Array, we will create a row in the database.
						 */
				}
				
				if (isEmptyVidDaily == false) {

					vidDaily.forEach((o) -> {
						try {
							psLien.setString(2, lienu.absUrl("href"));
							psLien.setString(3, "VID");
							psLien.setString(4, o);
							psLien.executeUpdate();
						} catch (SQLException e) {
							logger.error("SQL Error while Dailymotion insert !" + e);
						}
						});
						/**
						 * Foreach video present in vidDaily Array, we will create a row in the database.
						 */
				}
				
				if (isEmptyVidYtb == false) {
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
						});
						/**
						 * Foreach entrie present in vidYtb Array, we will create a row in the database.
						 */
				}
				
				
				if (tex != "") {
					psLien.setString(2, lienu.absUrl("href"));
					psLien.setString(3, "TXT");
					if (tex.length()<=16777215)
						{
							psLien.setString(4, tex);
						}
					psLien.executeUpdate();
					/**
					 * If string contains text, we insert in the database a row with TEXT.
					 */
				}
				
				
				if (isEmptyIMG == false){
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
					/**
					 * Foreach entrie present in Imgs Array, we will create a row in the database.
					 */
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
					});
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
					
					});		
				}

				if (isEmptyHashtableInsertionsArray == false){
					PreparedStatement psLienInsertions = con.prepareStatement("INSERT INTO ressources (idTable, url, categorie, `sous-categorie`, contenu) values (?,?,?,?,?)");
					insertionsArray.forEach((n, n2) -> {
					try {
						psLienInsertions.setInt(1, currentEventID);
						psLienInsertions.setString(2, lienu.absUrl("href"));
						psLienInsertions.setString(3, "insertions");
						psLienInsertions.setString(4, n);
						psLienInsertions.setString(5, n2);
						psLienInsertions.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
					});	
					psLienInsertions.close();
				}
				
				if (isEmptyfolderArray == false){
					folderArray.forEach((n) -> {
						try {
							psLien.setString(2, lienu.absUrl("href"));
							psLien.setString(3, "dossier");
							psLien.setString(4, n);
							psLien.executeUpdate();
						} catch (SQLException e) {
							logger.error("SQL Error while instagram insert !" + e);
						}
						});	
				}
			}
			psLien.close();
		}
		}
	}



