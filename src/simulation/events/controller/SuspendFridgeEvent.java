package simulation.events.controller;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.events.fridge.CloseEvent;
import simulation.events.fridge.OpenEvent;
import simulation.models.fridge.FridgeModel;

//----------------------------------------------------------------------------
/**
* The class <code>SuspendFridgeEvent</code> define an event sent by the controller to order to the fridge to suspend his behaviour
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
public class SuspendFridgeEvent extends AbstractControllerEvent {


	private static final long serialVersionUID = 1L;

	/**
	 * Create an SuspendFridgeEvent sent by the controller 
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content of the event.
	 */
	public SuspendFridgeEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	/**
	 * Create an SuspendFridgeEvent sent by the controller 
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
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
