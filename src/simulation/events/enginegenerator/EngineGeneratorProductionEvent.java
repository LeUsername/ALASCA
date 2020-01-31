package simulation.events.enginegenerator;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.controller.ControllerModel;

//----------------------------------------------------------------------------
/**
 * The class <code>EngineGeneratorProductionEvent</code> define an event sent by
 * the engine generator to transmit the energy production of the engine
 * generator
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
public class EngineGeneratorProductionEvent extends AbstractEngineGeneratorEvent {

	/**
	 * The class <code>Reading</code> implements the energy production value as an
	 * event content.
	 *
	 * <p>
	 * <strong>Description</strong>
	 * </p>
	 * 
	 * <p>
	 * <strong>Invariant</strong>
	 * </p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
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
	public static class Reading implements EventInformationI {
		private static final long serialVersionUID = 1L;
		public final double value;

		public Reading(double value) {
			super();
			this.value = value;
		}
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Create an EngineGeneratorProductionEvent sent by the engine generator
	 * 
	 *
	 * @param timeOfOccurrence time of occurrence of the event.
	 * @param content          content of the event.
	 */
	public EngineGeneratorProductionEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}

	/**
	 * Create an EngineGeneratorProductionEvent sent by the engine generator
	 * 
	 *
	 * @param timeOfOccurrence time of occurrence of the event.
	 */
	public EngineGeneratorProductionEvent(Time timeOfOccurrence, double content) {
		super(timeOfOccurrence, new Reading(content));
	}

	@Override
	public String eventAsString() {
		return "Engine generator(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return "time = " + this.getTimeOfOccurrence() + ", " + "production = "
				+ ((Reading) this.getEventInformation()).value + " W";
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof ControllerModel;

		ControllerModel m = (ControllerModel) model;
		m.setProductionEngineGenerator(((Reading) this.getEventInformation()).value);
	}
}
