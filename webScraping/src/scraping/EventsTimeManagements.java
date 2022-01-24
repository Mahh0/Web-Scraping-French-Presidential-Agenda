package scraping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EventsTimeManagements {
	/*
	 * FR - Cette classe g�re la dur�e des �v�nements. Elle prend en param�tre la date d'un �v�nement et la date de l'�v�nement pr�c�dent.
	 * EN - This class manages the duration of events. It takes as a parameter the date of an event and the date of the previous event.
	 */
	private static Logger logger = LogManager.getLogger(EventsTimeManagements.class);
	String toa;
	String dated;

	public EventsTimeManagements(String toa, String dated) {
		this.toa = toa;
		this.dated = dated;
	}
	
	public String CalculDurees() throws ParseException {
		Date dateEventPrecedent = null;
		Date dateEventActuel = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateEventPrecedent  = dateFormat.parse(toa);
		dateEventActuel = dateFormat.parse(dated);
		/*
		 * FR - Mise en forme des dates pour les calculs
		 * EN - Formatting the dates for calculations
		 */
		
				String jourEventPrecedent = toa.substring(0,10);
		        String jourEventActuel = dated.substring(0,10);
		        Date heureEvtPrecedent = null;
		        Date heureEvtActuel = null;
		        String stringHeureEventPrecedent = toa.substring(11,19);
		        String stringHeureEventActuel = dated.substring(11,19);
		        SimpleDateFormat HeureEvent = new SimpleDateFormat("HH:mm:ss");
		        heureEvtPrecedent = HeureEvent.parse(stringHeureEventPrecedent);
		        heureEvtActuel = HeureEvent.parse(stringHeureEventActuel);
		        /*
		         * FR - Pour faire les calculs, on r�cup�re seulement les heures en enlevant les jours
		         * Sortie : heureEvtPrecedent (Date) et heureEvtActuel (Date)
		         * jourEventPrecedent et jourEventActuel : strings pour comparer les jours
		         * 
		         * EN - To do the calculations, we only recover the hours by removing the days
		         */
		     
		
		        Date huit = HeureEvent.parse("08:00:00");
		        Date douze = HeureEvent.parse("12:00:00");
		        Date treize = HeureEvent.parse("13:00:00");
		        Date dixhuit = HeureEvent.parse("18:00:00");
		        Date vingt = HeureEvent.parse("20:00:00");
		        Date minuit = dateFormat.parse("1970-01-02 00:00:00");
		        long resultat = 0;
		        
        if (jourEventPrecedent.equalsIgnoreCase(jourEventActuel)) {
            resultat = dateEventActuel.getTime() - dateEventPrecedent.getTime();
        }
        /*
         * FR - Si les deux �v�nements compar�s sont sur le m�me jour, on fait une simple soustraction des dates compl�tes
         * EN - If the two compared events are on the same day, we do a simple subtraction of the complete dates
         */
        
        else if (jourEventPrecedent != jourEventActuel) // sinon si les evenements compar�s ne sont pas le m�me jour
        	{
            if (heureEvtPrecedent.getTime() >= huit.getTime() && heureEvtPrecedent.getTime() < douze.getTime()) {
        		resultat = douze.getTime() -  heureEvtPrecedent.getTime();
            } 
            else if (heureEvtPrecedent.getTime() >= douze.getTime() && heureEvtPrecedent.getTime() < treize.getTime()) {
            	resultat = treize.getTime() - heureEvtPrecedent.getTime(); // ...
            }
         
            else if (heureEvtPrecedent.getTime() >= treize.getTime() && heureEvtPrecedent.getTime() < dixhuit.getTime()) {
        		resultat = dixhuit.getTime() -  heureEvtPrecedent.getTime();
            }
            
            else if (heureEvtPrecedent.getTime() >= dixhuit.getTime() && heureEvtPrecedent.getTime() < vingt.getTime()){
        		resultat = vingt.getTime() - heureEvtPrecedent.getTime();
            }
            else if (heureEvtPrecedent.getTime() >= vingt.getTime() && heureEvtPrecedent.getTime() < minuit.getTime()){
        		resultat = minuit.getTime() - heureEvtPrecedent.getTime();
            }
        }
        /*
         * FR - Cette s�rie de tests s'applique dans le cas ou les deux �v�nements ne sont pas le m�me jour, on fait des calculs � l'aide de cr�neaux :
         * EN - This series of tests applies in the case where the two events are not the same day, we make calculations using time slots :
         * 
         * 8h-12h 12h-13h 13h-18h 18h-20h 20h-00h
         */
        
        
        if (resultat < 0) { 
        	resultat = 1000;
        	logger.error("Error for event duration ! Look the event with " + toa);
        }
        /*
         * A supprimer (resoudre problemes), si la duree (resultat) est inferieure � 0, on met 1000 par defaut).
         */
        
        

        TimeUnit time = TimeUnit.MINUTES; 
        long diffrence = time.convert(resultat, TimeUnit.MILLISECONDS) * 60000;
        String timeformat = String.format("%02d:%02d:%02d", 
        	    TimeUnit.MILLISECONDS.toHours(diffrence),
        	    TimeUnit.MILLISECONDS.toMinutes(diffrence) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diffrence)),
        	    TimeUnit.MILLISECONDS.toSeconds(diffrence) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diffrence)));
        logger.info("The previous event duration will be : " + timeformat + " minutes");
        return timeformat;
        /*
         * FR - Conversion de resultat qui est en Millisecondes, en un format HH:mm:ss.
         * EN - Converting result which is in Milliseconds, to HH:mm:ss format
         */
		
	}
}
