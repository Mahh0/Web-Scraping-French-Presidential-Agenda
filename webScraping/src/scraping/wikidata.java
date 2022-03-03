package scraping;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class wikidata {
	/**
	 * This class contains a method (wikidata) which takes as parameters name and surname.
	 * It returns a String which is Q id on wikidata.
	 */

	private static Logger logger = LogManager.getLogger(wikidata.class);
	static String id;

    public static String wikidata(String nom, String prenom) throws UnsupportedEncodingException {
		// First request
		String baseurl = "https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&srlimit=1&srsearch=";
		String query = prenom + "+" + nom;
		query = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
		String request = baseurl + query;
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest httprequest = HttpRequest.newBuilder().uri(URI.create(request)).build();
		String respfirstrequest = client.sendAsync(httprequest, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenApply(wikidata::parse1).join();
		/**
		 * First request : definifing a baseurl, a query (parameters), encoding these parameters and then creating a string (request) ready for request.
		 * Then, it is building a HTTP client, and a HttpRequest thanks to URL. 
		 * Then, it sends the request, using client and sending it async, and the response data is taken as a string and sent to parse1 method. The response from the method is respfirstrequest
		 */

		if (respfirstrequest != null && !respfirstrequest.isEmpty()){
			String url2 = "https://en.wikipedia.org/w/api.php?action=query&prop=pageprops&format=json&titles=" + respfirstrequest;
			HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create(url2)).build();
			String respsecondrequest = client.sendAsync(request2, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenApply(wikidata::parse2).join();
			return respsecondrequest;
		} else {
			return null;
		}
		/**
		 * If we got a name from the first json analysis, we build the second request using the name that we got from first json, and then we call parse2 method
		 * (json analysis) and we return the result (else, if no result from first json analysis, we return null (nothing))
		 */
			
		}

		public static String parse1(String responseBody) {
			try {
			JSONObject list = new JSONObject(responseBody);
			JSONObject query = list.getJSONObject("query");
			JSONArray search = query.getJSONArray("search");
			JSONObject jsonObject = search.getJSONObject(0);
			String title = jsonObject.getString("title");
			title = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
			return title;
			} catch (Exception ex) {
				logger.error(ex);
				return null;
			}
			/**
			 * Analyzing the JSON from the first request, getting the name and returning it.
			 * If we got an error during the process, we log the error and we return null, it is probably because there is nobody with this name
			 */
		}

		public static String parse2(String responseBody) {
			try {
				JSONObject list = new JSONObject (responseBody);
				JSONObject query = list.getJSONObject("query");
				JSONObject pages = query.getJSONObject("pages");
				Iterator<?> keys = pages.keys();
				while( keys.hasNext() ) {
					String key = (String)keys.next();
					if ( pages.get(key) instanceof JSONObject ) {
						JSONObject page = pages.getJSONObject(key);
						JSONObject pageprops = page.getJSONObject("pageprops");
						id = pageprops.getString("wikibase_item");
					}
				}
				return id;
			} catch (Exception ex) {
				logger.error("Wikidata ID not found");
				return null;
			}
			/**
			 * Analyzing the JSON response from the query with the name, and getting the WIKIDATA ID.
			 * If we can't find the wikidata id, the method returns null.
			 */
		}
		
	}



