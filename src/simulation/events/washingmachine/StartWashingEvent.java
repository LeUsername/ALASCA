package simulation.events.washingmachine;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.events.controller.StopWashingMachineEvent;
import simulation.models.washingmachine.WashingMachineModel;

//-----------------------------------------------------------------------------
/**
* The class <code>StartWashingEvent</code> defines the event which starts
* the washing machine
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
public class StartWashingEvent extends AbstractWashingMachineEvent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The class <code>StartingTimeDelay</code> implements the 
	 * time at which the washing machine starts
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
	public static class		StartingTimeDelay
	implements EventInformationI
	{
		private static final long serialVersionUID = 1L;
		public final double	value ;

		public			StartingTimeDelay(double value)
		{
			super();
			this.value = value;
		}
	}

	public StartWashingEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
		
	}
	
	public StartWashingEvent(Time timeOfOccurrence, double delay) {
		super(timeOfOccurrence, new StartingTimeDelay(delay));
	}
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if(e instanceof StopWashingMachineEvent ||e instanceof StopWashingMachineEvent) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String eventAsString() {
		return "StartAtEvent(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return	"time = " + this.getTimeOfOccurrence() + ", " +
				"starting time delay = " + ((StartingTimeDelay)this.getEventInformation()).value
												+ " min";
	}
	
	@Override
	public void	executeOn(AtomicModel model)
	{
		assert model instanceof WashingMachineModel;

		WashingMachineModel m = (WashingMachineModel) model;
		m.startAt(((StartingTimeDelay)this.getEventInformation()).value);
	}

}
