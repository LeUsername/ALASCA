package simulation.events.hairdryer;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.electricmeter.ElectricMeterModel;

public class HairDryerConsumptionEvent extends AbstractHairDryerEvent {

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
	
	public HairDryerConsumptionEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	public HairDryerConsumptionEvent(Time timeOfOccurrence, double content) {
		super(timeOfOccurrence, new Reading(content));
	}
	
	@Override
	public String eventAsString() {
		return "HairDryer(" + this.eventContentAsString() + ")";
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
		assert	model instanceof ElectricMeterModel ;

		ElectricMeterModel m = (ElectricMeterModel)model ;
		m.setHairDryerConsumption(((Reading)this.getEventInformation()).value);
	}


}
