package simulation.events.electricmeter;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

//----------------------------------------------------------------------------
/**
* The class <code>AbstractElectricMeterEvent</code> define all Event sent by the electric meter
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
public class AbstractElectricMeterEvent extends ES_Event {


	private static final long serialVersionUID = 1L;


	/**
	 * 
	 * 
	 * Create an event sent by the electric meter 
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content of the event.
	 */
	public AbstractElectricMeterEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}

}
