package simulation.events.controller;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.events.enginegenerator.RefillEvent;
import simulation.models.enginegenerator.EngineGeneratorModel;

//----------------------------------------------------------------------------
/**
* The class <code>StopEngineGeneratorEvent</code> define an event sent by the controller to order to the engine generator
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
public class StopEngineGeneratorEvent extends AbstractControllerEvent {

	
	private static final long serialVersionUID = 1L;

	/**
	 * Create an StopEngineGeneratorEvent sent by the controller 
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content of the event.
	 */
	public StopEngineGeneratorEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	/**
	 * Create an StopEngineGeneratorEvent sent by the controller 
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public StopEngineGeneratorEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Controller::StopEngineGenerator";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof RefillEvent) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof EngineGeneratorModel;

		EngineGeneratorModel m = (EngineGeneratorModel) model;
		m.stop();
	}
}
