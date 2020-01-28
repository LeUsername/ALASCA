package simulation.events.controller;

import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class ResumeFridgeEvent extends AbstractControllerEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ResumeFridgeEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	public ResumeFridgeEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "Controller::ResumeFridgeEvent";
	}

}
