package simulation2.events.lavelinge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation2.models.lavelinge.LaveLingeModel;

public class StartAtEvent extends AbstractLaveLingeEvent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	public StartAtEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
		
	}
	
	public StartAtEvent(Time timeOfOccurrence, double delay) {
		super(timeOfOccurrence, new StartingTimeDelay(delay));
	}
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		return true;
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
		assert model instanceof LaveLingeModel;

		LaveLingeModel m = (LaveLingeModel) model;
		m.startAt(((StartingTimeDelay)this.getEventInformation()).value);
	}

}
