package simulation.events.electricmeter;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.controller.ControllerModel;

//----------------------------------------------------------------------------
/**
* The class <code>ConsumptionEvent</code> define an event sent by the electric meter to transmit
*  the overall energy consumption of all devices 
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
public class ConsumptionEvent extends AbstractElectricMeterEvent {

	/**
	 * The class <code>Reading</code> implements the energy consumption value as an
	 * event content.
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
	
	private static final long serialVersionUID = 1L;

	/**
	 * Create an ConsumptionEvent sent by the electric meter 
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 * @param content			content of the event.
	 */
	public ConsumptionEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	/**
	 * Create an ConsumptionEvent sent by the electric meter 
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public ConsumptionEvent(Time timeOfOccurrence, double content) {
		super(timeOfOccurrence, new Reading(content));
	}

	@Override
	public String eventAsString() {
		return "Electric meter(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return	"time = " + this.getTimeOfOccurrence() + ", " +
				"quantity = " + ((Reading)this.getEventInformation()).value
												+ " mAh";
	}
	
	@Override
	public void	executeOn(AtomicModel model)
	{
		assert	model instanceof ControllerModel ;

		ControllerModel m = (ControllerModel)model ;
		m.setConsumption(((Reading)this.getEventInformation()).value);
		
		// The following code is used to test the MIL of electric meter with his stub
//		assert	model instanceof ElectricMeterModel ;
//		ElectricMeterModel m = (ElectricMeterModel)model ;
//		m.setConsumption(((Reading)this.getEventInformation()).value);
	}
}
