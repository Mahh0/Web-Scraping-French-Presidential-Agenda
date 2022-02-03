package scraping;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import init.MySqlConnection;
import init.SSLHelper;

public class resources_bdd {
	Elements externallinkslist;
	int currentEventID;

	public void insertionresources(Elements externallinkslist, int currentEventID) throws SQLException, IOException {
		/*
		 * FR - Cette classe s'occupe des insertions dans la table ressources.
		 * EN - This class takes care of the insertions in the resources table.
		 */
		Logger logger = LogManager.getLogger(resources_bdd.class);

		this.externallinkslist = externallinkslist;
		this.currentEventID = currentEventID;
		Connection con = new MySqlConnection().getConnection();
		
		if (! externallinkslist.isEmpty()) {
			for (Element lienu : externallinkslist) {
				/*
				 * FR - On teste si notre liste contient des liens, si oui, on exécute pour chaque lien la suite de la classe
				 * EN - We test if our list contains links, if so, we run the rest of the class for each link
				 */						
				
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
				/*
				 * FR - Connection à la page, et analyse + appel des méthodes de la classe ExternalLinks, pour retourner ce que contient le code HTML.
				 * EN - Connection to the page, and analysis + call of the methods of the ExternalLinks class, to return what there is on the HTML
				 */
				
				
				final String insertionlien = "INSERT INTO ressources (idTable, url, categorie, contenu) values (?,?,?,?)";
				PreparedStatement psLien = con.prepareStatement(insertionlien);
				psLien.setInt(1, currentEventID);
				/*
				 * FR - Pr�paration des insertions
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
					psLienVide.close();
					/*
					 * FR - Si tout est vide, on insert dans la table ressources un lien sans rien.
					 * EN - If everything is empty, we insert a link with nothing in the resources table.
					 */	
				} else {
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
				}
				
				if (isEmptyTwitterArray == false){
					/*
					 * FR - Si l'arrayList des liens twitter n'est pas vide, on procède à l'insertion pour chaque lien de l'ArrayList.
					 * EN - If twitter links ArrayList is not empty, we insert for each links of the ArrayList.
					 */
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
					/*
					 * FR - Si l'arrayList des liens insta n'est pas vide, on procède à l'insertion pour chaque lien de l'ArrayList.
					 * EN - If insta links ArrayList is not empty, we insert for each links of the ArrayList.
					 */
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
				
				if (isEmptyfolderArray == false){
					// Insertion des dossiers 

					int fSize = folderArray.size();	
					logger.debug("FOLDER ARRAY SIZE :" + fSize);
						for (int i = 1; i<=fSize;i++) {
							// Pour chaque lien de dossier dans l'ArrayList


							// Comptage du nombre de dossiers dans la table dossier avec ce même lien
						int compteur = 0;
						PreparedStatement countDossier = con.prepareStatement("SELECT COUNT(*) FROM dossier WHERE lien=(?)");
						countDossier.setString(1, folderArray.get(i-1));
						ResultSet st1 = countDossier.executeQuery();
						
						while (st1.next()) {
							compteur = st1.getInt(1);
						 }
						 

						 if (compteur == 0){ // Si dans la table, aucune ligne ne comporte ce dossier (jamais créé)
							int compteur2 = 0;
						// On fait l'insertion dans dossier
						PreparedStatement insertdossier = con.prepareStatement("INSERT INTO dossier(lien) values (?)");
						insertdossier.setString(1, folderArray.get(i-1));
						insertdossier.executeUpdate();

						// On récupère l'ID de la requête précédente
						final String recupId = "SELECT id from dossier WHERE lien=(?)";
						PreparedStatement recupIdR = con.prepareStatement(recupId);
						recupIdR.setString(1, folderArray.get(i-1));
						ResultSet stIdR = recupIdR.executeQuery();
						while (stIdR.next()) {
							compteur2 = stIdR.getInt(1);
						 }
						 
						 // On set dans ressource l'idDossier avec l'ID Précédent
						PreparedStatement updateRess2 = con.prepareStatement("UPDATE ressources SET idDossier = (?) WHERE url = (?)");
						updateRess2.setInt(1, compteur2);
						updateRess2.setString(2, lienu.absUrl("href"));
						updateRess2.executeUpdate();


						 } else if (compteur != 0) { // Un dossier avec ce lien a déjà été inséré
							int compteur2 = 0;
							logger.debug("le compteur != 0");
						// On récupère l'ID du dossier (de la requete count)
						final String selectIdFolder = "SELECT id from dossier WHERE lien=(?)";
						PreparedStatement recupIdRc = con.prepareStatement(selectIdFolder);
						recupIdRc.setString(1, folderArray.get(i-1));
						logger.debug("requete : " + recupIdRc);
						ResultSet stIdc = recupIdRc.executeQuery();
						logger.debug("resultatset : " + stIdc);
						while (stIdc.next()) {
							compteur2 = stIdc.getInt(1);
						 }
						 
						// On fait un update dans ressource avec cet ID
						PreparedStatement updateRess2 = con.prepareStatement("UPDATE ressources SET idDossier = (?) WHERE url = (?)");
						updateRess2.setInt(1, compteur2);
						updateRess2.setString(2, lienu.absUrl("href"));
						updateRess2.executeUpdate();

						 } else {
						logger.error("ERROR");
					}
				}

			}
			psLien.close();
		}
		}
	}
}
}


