package simulation.events.windturbine;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

//-----------------------------------------------------------------------------
/**
* The class <code>AbstractWindTurbineEvent</code> defines all events
* used in the <code>WindTurbineModel</code>
*
* <p><strong>Description</strong></p>
* 
* <p><strong>Invariant</strong></p>
* 
* <pre>
* invariant		true
* </pre>
* 
* <p>Created on : 2020-01-27</p>
* 
* @author	<p>Bah Thierno, Zheng Pascal</p>
*/
public class AbstractWindTurbineEvent extends ES_Event {

	private static final long serialVersionUID = 1L;


	public AbstractWindTurbineEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
}