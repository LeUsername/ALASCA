package simulation.events.enginegenerator;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.enginegenerator.EngineGeneratorModel;

//----------------------------------------------------------------------------
/**
 * The class <code>RefillEvent</code> define an event sent by the engine
 * generator user to refill the engine generator fuel
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
public class RefillEvent extends AbstractEngineGeneratorEvent {

	private static final long serialVersionUID = 1L;

	/**
	 * Create an RefillEvent sent by the engine generator user
	 * 
	 *
	 * @param timeOfOccurrence time of occurrence of the event.
	 */
	public RefillEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		return false;
	}

	@Override
	public String eventAsString() {
		return "RefillEvent(" + this.eventContentAsString() + ")";
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof EngineGeneratorModel;

		EngineGeneratorModel m = (EngineGeneratorModel) model;
		m.refill();
	}

}
