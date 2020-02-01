package simulation.events.windturbine;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.windturbine.WindTurbineModel;

//-----------------------------------------------------------------------------
/**
* The class <code>WindReadingEvent</code> defines the event which indicates
* the wind speed
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
public class WindReadingEvent extends AbstractWindTurbineEvent {

	/**
	 * The class <code>Reading</code> implements the wind speed
	 * value as an event content.
	 *
	 * <p><strong>Description</strong></p>
	 * 
	 * <p><strong>Invariant</strong></p>
	 * 
	 * <pre>
	 * invariant		true
	 * </pre>
	 * 
	 * <p>Created on : 2020-01-27</p>
	 * 
	 * @author	<p>Bah Thierno, Zheng Pascal</p>
	 */
	public static class		Reading
	implements EventInformationI
	{
		private static final long serialVersionUID = 1L;
		public final double	value ;

		public			Reading(double value)
		{
			super();
			this.value = value;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create an WindReadingEvent sent by wind turbine sensor
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content of the event.
	 */
	public WindReadingEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	/**
	 * Create an WindReadingEvent sent by wind turbine sensor
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			double value of the content of the event.
	 */
	public WindReadingEvent(Time timeOfOccurrence, double content) {
		super(timeOfOccurrence, new Reading(content));
	}
	
	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchOnEvent) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String eventAsString() {
		return "WindTurbine(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return	"time = " + this.getTimeOfOccurrence() + ", " +
				"wind = " + ((Reading)this.getEventInformation()).value
												+ " km/s";
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void	executeOn(AtomicModel model)
	{
		assert	model instanceof WindTurbineModel ;

		WindTurbineModel m = (WindTurbineModel)model ;
		m.setProduction(((Reading)this.getEventInformation()).value);
	}
}
