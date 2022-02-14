package scraping;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

public class docToLocalHtml {    
    public static void downloadPage(String outerHtml, String link) throws Exception {

        Pattern pMonth = Pattern.compile("(janvier|fevrier|mars|avril|mai|juin|juillet|aout|septembre|octobre|novembre|decembre)");
        Matcher mMonth = pMonth.matcher(link);

        Pattern pYear = Pattern.compile("20[0-9][0-9]");
        Matcher mYear = pYear.matcher(link);

  
        // webScraping/src/main/resources/htmlAgenda/html-" + mMonth.group(0) + "-" + mYear.group(0));
        if (mMonth.find() && mYear.find()) {
            
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("webScraping/src/main/resources/htmlAgenda/html-" + mMonth.group(0) + "-" + mYear.group(0)), "UTF-8"));
                try {
                    out.write(outerHtml);
                } finally {
                    out.close();
                }
        }
    }
    }

