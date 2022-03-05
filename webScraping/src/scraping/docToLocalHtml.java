package scraping;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class docToLocalHtml {   
    /**
     * This class is used to download HTML from website.
     * The method downloadPage takes as parameter a HTML code (as a String) and a link.
     * Then, it creates a local document.
     * @param outerHtml
     * @param link
     * @throws Exception
     */ 

    public static void downloadPage(String outerHtml, String link) throws Exception {
        Pattern pMonth = Pattern.compile("(janvier|fevrier|mars|avril|mai|juin|juillet|aout|septembre|octobre|novembre|decembre)");
        Matcher mMonth = pMonth.matcher(link);
        Pattern pYear = Pattern.compile("20[0-9][0-9]");
        Matcher mYear = pYear.matcher(link);
        /**
         * Finding in the link the month and the year to name the file.
         */

        if (mMonth.find() && mYear.find()) {
            Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("webScraping/src/main/resources/htmlAgenda/html-" + mMonth.group(0) + "-" + mYear.group(0) + ".html"), "UTF-8"));
                try {
                    out.write(outerHtml);
                } finally {
                    out.close();
                }
        }
        /**
         * Writing the file through a BufferedWriter, an OutputStreamWriter and a FileOutputStream.
         */
    }
    }

