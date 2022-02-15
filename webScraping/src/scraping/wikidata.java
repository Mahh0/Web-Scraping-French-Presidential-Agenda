package scraping;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DateFormatSymbols;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
import org.jsoup.select.Elements;
import java.util.*;
import org.apache.commons.lang3.*;

public class wikidata {
	static String goodtitle;
	String id;

    public static String wikidata(String nom, String prenom) throws Exception {
		// First request

		String url = "https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&srlimit=1&srsearch=" + prenom + "+" + nom;
		// Create a client
		HttpClient client = HttpClient.newHttpClient();
		// Create a request and build it using the url
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
		// Set the request using the client, sent it async, tell the server that we wan't to receive the response body as a string
		String respfirstrequest = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenApply(wikidata::parse1).join();


		// Second request (using respfirstrequest)
		String url2 = "https://en.wikipedia.org/w/api.php?action=query&prop=pageprops&format=json&titles=" + respfirstrequest;
		HttpRequest request2 = HttpRequest.newBuilder().uri(URI.create(url2)).build();
		String respsecondrequest = client.sendAsync(request2, HttpResponse.BodyHandlers.ofString()).thenApply(HttpResponse::body).thenApply(wikidata::parse2).join();

		return respsecondrequest;
		}

		public static String parse1(String responseBody) {

			JSONObject list = new JSONObject(responseBody);
			JSONObject query = list.getJSONObject("query");
			JSONArray search = query.getJSONArray("search");
			JSONObject jsonObject = search.getJSONObject(0);

			String title = jsonObject.getString("title");
			String goodtitle = title.replaceAll(" ", "%20");

			return goodtitle;
		}

		public static String parse2(String responseBody) {

			JSONObject list = new JSONObject (responseBody);
			JSONObject query = list.getJSONObject("query");
			JSONObject pages = query.getJSONObject("pages");
			System.out.println(pages);

			Iterator<?> keys = pages.keys();
			while( keys.hasNext() ) {
				String key = (String)keys.next();
				if ( pages.get(key) instanceof JSONObject ) {
					JSONObject page = pages.getJSONObject(key);
					JSONObject pageprops = page.getJSONObject("pageprops");
					String id = pageprops.getString("wikibase_item");
					System.out.println(id);
					return id;
				}
			}
			return null;
		}
	}



