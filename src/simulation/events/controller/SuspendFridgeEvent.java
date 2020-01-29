package simulation.events.controller;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.events.fridge.CloseEvent;
import simulation.events.fridge.OpenEvent;
import simulation.models.fridge.FridgeModel;

public class SuspendFridgeEvent extends AbstractControllerEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SuspendFridgeEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	public SuspendFridgeEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "Controller::SuspendFridgeEvent";
	}
	
	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof OpenEvent || e instanceof CloseEvent) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FridgeModel;
		FridgeModel m = (FridgeModel) model;
		m.suspend();
	}


}
