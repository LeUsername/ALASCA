package simulation.events.enginegenerator;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

//----------------------------------------------------------------------------
/**
* The class <code>AbstractEngineGeneratorEvent</code> define all Event sent in the engine generator coupled model
*
* 
* <p>
* Created on : 2020-01-27
* </p>
* 
* @author
*         <p>
*         Bah Thierno, Zheng Pascal
*         </p>
*/
public class AbstractEngineGeneratorEvent extends ES_Event {


	private static final long serialVersionUID = 1L;


	/**
	 * 
	 * 
	 * Create an event sent in the engine generator coupled model
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content of the event.
	 */
	public AbstractEngineGeneratorEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}

}
