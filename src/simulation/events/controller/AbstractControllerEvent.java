package simulation.events.controller;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class AbstractControllerEvent extends ES_Event {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AbstractControllerEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}

}
