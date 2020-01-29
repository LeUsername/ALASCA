package simulation.events.controller;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.washingmachine.WashingMachineModel;

public class StartWashingMachineEvent extends AbstractControllerEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StartWashingMachineEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
		
	}
	
	public StartWashingMachineEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
		
	}
	
	@Override
	public String eventAsString() {
		return "Controller::StartWashingMachineEvent";
	}
	
	@Override
	public boolean hasPriorityOver(EventI e) {
		return true;
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof WashingMachineModel;

		WashingMachineModel m = (WashingMachineModel) model;
		m.start();
	}
}
