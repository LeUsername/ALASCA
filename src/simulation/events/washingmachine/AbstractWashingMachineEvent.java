package simulation.events.washingmachine;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

//-----------------------------------------------------------------------------
/**
* The class <code>AbstractWashingMachineEvent</code> defines all events
* used in the <code>WashingMachineCoupledModel</code>
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
public class AbstractWashingMachineEvent extends ES_Event {

		private static final long serialVersionUID = 1L;

		
		public AbstractWashingMachineEvent(Time timeOfOccurrence, EventInformationI content) {
			super(timeOfOccurrence, content);
		}

}
