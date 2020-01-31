package simulation.events.fridge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.fridge.FridgeModel;

//----------------------------------------------------------------------------
/**
 * The class <code>ResumeEvent</code> define an event sent by the fridge sensor
 * to resume the engine door
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
public class ResumeEvent extends AbstractFridgeEvent {

	private static final long serialVersionUID = 1L;

	/**
	 * create a new ResumeEvent.
	 * 
	 *
	 * @param timeOfOccurrence time of occurrence of the event.
	 */
	public ResumeEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SuspendEvent) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String eventAsString() {
		return "ResumeEvent(" + this.eventContentAsString() + ")";
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FridgeModel;

		FridgeModel m = (FridgeModel) model;
		m.resume();
	}
}
//----------------------------------------------------------------------------
