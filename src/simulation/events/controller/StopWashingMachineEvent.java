package simulation.events.controller;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.washingmachine.WashingMachineModel;

public class StopWashingMachineEvent extends AbstractControllerEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StopWashingMachineEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
		
	}
	
	public StopWashingMachineEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
		
	}
	
	@Override
	public String eventAsString() {
		return "Controller::StopWashingMachineEvent";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if(e instanceof StartWashingMachineEvent) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof WashingMachineModel;

		WashingMachineModel m = (WashingMachineModel) model;
		m.stop();
	}
}
