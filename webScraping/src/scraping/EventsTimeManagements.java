package scraping;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventsTimeManagements {
	/**
	 * This class cares of the events duration (enrichment), for the PREVIOUS event.
	 * There are several methods : 
	 * 		- EventsTimeManagement, it takes all the parameters needed (toa, dated, specialHour, lastEventType)
	 * 		- CalculDurees, the main method, which calculate the lengths of the events
	 * 		- CalculType, the last one, it is used by CalculDurees to set max event time limits (because without this one, even can be like 10 hours, for a talk for example)
	 */

	private static Logger logger = LogManager.getLogger(EventsTimeManagements.class);
	String toa;
	String dated;
	int specialHour;
	String lastEventType;
	/**
	 * @param toa
	 * toa is the previous event hour
	 * @param dated
	 * dated is the current event hour
	 * @param lastEventType
	 * type of the last event, for the last method.
	 */

	public EventsTimeManagements(String toa, String dated, String lastEventType) {
		/**
		 * Takes the parameters
		 */
		this.toa = toa;
		this.dated = dated;
		this.lastEventType = lastEventType;
	}
	
	public String CalculDurees() throws ParseException {
		/**
		 * Do the calculations
		 */
		
		Date dateEventPrecedent = null;
		Date dateEventActuel = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateEventPrecedent  = dateFormat.parse(toa); // toa = previous = the one we wan't to calculate
		dateEventActuel = dateFormat.parse(dated); // dated = actuel = the one which can be used for the calcul
		/**
		 * Formatting the dates for calculations
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
				/**
				 * To make certain calculations, we only keep the hours (by deleting the days)
				 * heureEvtActuel and heureEvtPrecedent contains theses values.
				 * jourEventPrecedent and jourEventActuel contains the values with the days
				 */
		
		        Date huit = HeureEvent.parse("08:00:00");
		        Date douze = HeureEvent.parse("12:00:00");
		        Date treize = HeureEvent.parse("13:00:00");
		        Date dixhuit = HeureEvent.parse("18:00:00");
		        Date vingt = HeureEvent.parse("20:00:00");
		        Date minuit = dateFormat.parse("1970-01-02 00:00:00");
		        long resultat = 0;
				/**
				 * Parsing time slots for the calculations.
				 */

		        
        if (jourEventPrecedent.equalsIgnoreCase(jourEventActuel)) {
            resultat = dateEventActuel.getTime() - dateEventPrecedent.getTime();
			resultat = this.calculType(resultat);
			/**
			 * If the two compared events are on the same day, we just make a simple subscration.
			 * Then, we send the result to calculType which returns a new result.
			 * calculType check that the events durations are not too long
			 */
        }
        else if (jourEventPrecedent != jourEventActuel) {
			/**
			 * Else if the two compared events are not the same day, we do calculations with time slots
			 * So then we check in which time slot is the event, when found we do (end hour of the time slot - event hour)
			 * And then we pass it into CalculType.
			 */
            if (heureEvtPrecedent.getTime() >= huit.getTime() && heureEvtPrecedent.getTime() < douze.getTime()) {
        		resultat = douze.getTime() -  heureEvtPrecedent.getTime();
				resultat = this.calculType(resultat);
            } 
            else if (heureEvtPrecedent.getTime() >= douze.getTime() && heureEvtPrecedent.getTime() < treize.getTime()) {
            	resultat = treize.getTime() - heureEvtPrecedent.getTime();
				resultat = this.calculType(resultat);
            }
         
            else if (heureEvtPrecedent.getTime() >= treize.getTime() && heureEvtPrecedent.getTime() < dixhuit.getTime()) {
        		resultat = dixhuit.getTime() -  heureEvtPrecedent.getTime();
				resultat = this.calculType(resultat);
            }
            
            else if (heureEvtPrecedent.getTime() >= dixhuit.getTime() && heureEvtPrecedent.getTime() < vingt.getTime()){
        		resultat = vingt.getTime() - heureEvtPrecedent.getTime();
				resultat = this.calculType(resultat);
            }
            else if (heureEvtPrecedent.getTime() >= vingt.getTime() && heureEvtPrecedent.getTime() < minuit.getTime()){
        		resultat = minuit.getTime() - heureEvtPrecedent.getTime();
				resultat = this.calculType(resultat);
            }
        }

        TimeUnit time = TimeUnit.MINUTES; 
        long diffrence = time.convert(resultat, TimeUnit.MILLISECONDS) * 60000;
        String timeformat = String.format("%02d:%02d:%02d", 
        	    TimeUnit.MILLISECONDS.toHours(diffrence),
        	    TimeUnit.MILLISECONDS.toMinutes(diffrence) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(diffrence)),
        	    TimeUnit.MILLISECONDS.toSeconds(diffrence) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(diffrence)));
        return timeformat;
		/**
		 * Converting the resul (resultat) which is in milliseconds to a normal format (HH:mm:ss)
		 */		
	}

	public long calculType (long duree) {
		/**
		 * This method is used in the method above. It checks, for each event, 
		 * with the event type if the duration is not too long, and if too long, it is updated with a normal one.
		 */
			switch (lastEventType) {
				case "Conseil de défense":
					if (duree > 7200000) { duree = 5400000; }
					break;
				case "Conseil des ministres":
					if (duree > 7200000) { duree = 5400000; }
					break;
				case "Entretien":
					if (duree > 5400000) { duree = 1800000; }
					break;
				case "Cérémonie":
					if (duree > 10800000) { duree = 7200000; }
					break;
				case "Cérémonie officielle":
					if (duree > 10800000) { duree = 7200000; }
					break;
				case "Réception":
					if (duree > 7200000) { duree = 3600000; }
					break;
				case "Déjeuner":
					if (duree > 7200000) { duree = 5400000; }
					break;
				case "Déplacement":
					if (duree > 28800000) { duree = 28800000; }
					break;
				case "Discours":
					if (duree > 5400000) { duree = 3600000; }
					break;
				case "Rencontre":
					if (duree > 7200000) { duree = 7200000; }
					break;
				case "Voyage officiel":
					if (duree > 36000000) { duree = 28800000; }
					break;
				case "Remise de rapport":
					if (duree > 5400000) { duree = 5400000; }
					break;
				default:
				if (duree > 28800000) { duree = 28800000; }
			}
		return duree;
	}
}
