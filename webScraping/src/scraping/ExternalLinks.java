package scraping;

import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import main.Main;

public class ExternalLinks {
	Document doc;
	private static Logger logger = LogManager.getLogger(ExternalLinks.class);
	/*
	 * FR - La classe ExternalLinks prend en param�tre un document (code HTML) et l'analyse pour la table ressources.
	 * EN - The ExternalLinks class takes a document (HTML code) as a parameter and analyzes it for ressources table.
	 */

	public ExternalLinks(Document doc) {
		this.doc = doc;
	}
	
	public ArrayList<String> getPdf() {
		/*
		 * FR - cette m�thode regarde si le document doc contient des PDF. Elle retourne une ArrayList contenant les liens des pdf.
		 * EN - this method checks if the doc document contains PDFs. It returns an ArrayList containing pdf links
		 */
		Elements pdf = doc.select("section.container.js-document");
		ArrayList<String> pdfs = new ArrayList<String>();
		if (pdf != null) {
			Elements pdfe = pdf.select("a[href]");
			for (Element t1 : pdfe) {
				pdfs.add(t1.absUrl("href"));
			}
		}
		return pdfs;		
	}
	
	
	public ArrayList<String> getVidDaily() {
		/*
		 * FR - cette m�thode regarde si le document doc contient des vid�os daylimotion. Elle retourne une ArrayList contenant les liens des vid�os.
		 * EN - this method checks if the doc document contains dailymotion videos. It returns an ArrayList containing videos links
		 */
		Elements viddaily = doc.select(".dailymotion_player");
		ArrayList<String> videos = new ArrayList<String>();
		
			        if(viddaily != null) {
			        	for (Element vidd : viddaily) {
			        	videos.add("https://www.dailymotion.com/video/" + vidd.attr("videoid"));
			        	}
			        }
			        return videos;
	}
	
	
	public ArrayList<String> getVidYtb() {
		/*
		 * FR - cette m�thode regarde si le document doc contient des vid�os youtube. Elle retourne une ArrayList contenant les liens des vid�os.
		 * EN - this method checks if the doc document contains youtube videos. It returns an ArrayList containing videos links
		 */
		Elements vidytb = doc.select(".youtube_player");
		ArrayList<String> videos = new ArrayList<String>();

			      if (vidytb != null) {
			    	  for (Element vidd : vidytb) {
			    		  videos.add("https://www.youtube.com/watch?v=" + vidd.attr("videoid"));  
			    	  }
			        }
			      return videos;       
	}
	
	
	public String getText() {
		/*
		 * FR - cette m�thode regarde si le corps de la page contient du texte. Elle le retourne s'il en contient, sous forme String.
		 * EN - this method looks to see if the body of the page contains text. It returns it if it contains any, in String form.
		 */
	Elements text = doc.select(".reset-last-space");
	for (Element textu : text) {
		return textu.text();
	}
	return "";
	}
	
	
	public ArrayList<String> getImgs() {
		/*
		 * FR - cette m�thode regarde si le document doc contient des images. Elle retourne une ArrayList les contenant.
		 * EN - this method checks if the doc document contains images. It returns an ArrayList containing them.
		 */
		ArrayList<String> images = new ArrayList<String>();
		Elements diapos = doc.select("div.carousel-container");
		for (Element diapo : diapos) {
			Elements imgs = diapo.select(".owl-carousel > div.item > div.img-fill");
			for (Element img : imgs) {
			Element imgUtest = img.select("img").first();
			String imgUtest2 = imgUtest.attr("src");
			images.add(imgUtest2);
			}
		}
		return images;		
	}
	
	public ArrayList<String> getTwitter() {
		/*
		 * Cette m�thode se charge de r�cup�rer les liens twitter.
		 */
		ArrayList<String> liens = new ArrayList<String>();
		Elements twitters = doc.select(".twitter-tweet");
		
		for (Element twitter : twitters) {
		Element tests2 = twitter.select("a[href]").last();
		
				if (tests2 != null) {
					String tests3 = tests2.absUrl("href");
					liens.add(tests3);
				}
		}
		return liens;
	}
	
	public ArrayList<String> getInsta(){
	/* 
	 * Cette m�thode se charge de r�cup�rer les liens instas. 
	 */
		ArrayList<String> liens = new ArrayList<String>();
		Elements instas = doc.select(".instagram-media");
		
		for (Element insta : instas) {
		Element tests2 = insta.select("a[href]").last();
		
				if (tests2 != null) {
					String tests3 = tests2.absUrl("href");
					liens.add(tests3);
				}
		}
	
				return liens; 
	}
	
	public ArrayList<String> getDossier(){
		/*
		 * Cette m�thode se charge de v�rifier si des pages font parties d'un dossier
		 */
		ArrayList<String> dossiers = new ArrayList<String>(); // ArrayList containing 0: entitled, 1:link, 2: entitled2, 3: link2, ...
		
		Element conteneurdossier = doc.select(".accessibility-banner").first(); // Le conteneur de classe accessibility-banner est celui r�serv� aux dossiers.
		  if (conteneurdossier != null){ // Inutile de faire la suite si notre �l�ment est vide : pas de dossier, sinon on continue
		  		Element casesingle = conteneurdossier.select(".folder-link.folder-link--link").first(); // S�lection du bloc contenant 1 seul dossier
		  			if (casesingle !=null) { // Si s�lection r�ussie, ...
		 				System.out.println("y'a un lien !");
						Element url = casesingle.select(".folder-link-content-title").first();
		 				dossiers.add(casesingle.select(".folder-link-content-title > span").text());
		 				dossiers.add(url.absUrl("href"));
		  			} else { // Sinon, c'est un conteneur contant plusieurs dossiers ...
						Element casedouble = conteneurdossier.select(".folder-link.folder-link--multiple").first();
						if (casedouble != null ) {
						Element ul = casedouble.select(".list-group").first();
						Elements lis = ul.select("li > a");
						for (Element li : lis){
							dossiers.add("https://www.elysee.fr" + li.attr("href"));
						}
						dossiers.add("debug");
						}
		}
	}
		  return dossiers;
	}
	
	}


