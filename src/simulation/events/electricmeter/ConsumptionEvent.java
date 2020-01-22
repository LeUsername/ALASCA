package simulation.events.electricmeter;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class ConsumptionEvent extends AbstractElectricMeterEvent {

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

	public ConsumptionEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
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
//		assert	model instanceof ElectricMeterModel ;
//
//		ElectricMeterModel m = (ElectricMeterModel)model ;
//		m.setConsumption(((Reading)this.getEventInformation()).value);
	}
}
