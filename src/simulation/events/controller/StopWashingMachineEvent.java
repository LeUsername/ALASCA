package simulation.events.controller;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.washingmachine.WashingMachineModel;

//----------------------------------------------------------------------------
/**
* The class <code>StopWashingMachineEvent</code> define an event sent by the controller to order to the washing machine
*  to stop
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
public class StopWashingMachineEvent extends AbstractControllerEvent {

	private static final long serialVersionUID = 1L;

	/**
	 * Create an StopWashingMachineEvent sent by the controller 
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content of the event.
	 */
	public StopWashingMachineEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
		
	}
	
	/**
	 * Create an StopWashingMachineEvent sent by the controller 
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
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
