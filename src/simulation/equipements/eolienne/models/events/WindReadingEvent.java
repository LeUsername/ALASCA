package simulation.equipements.eolienne.models.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.equipements.eolienne.models.EolienneModel;

public class WindReadingEvent extends AbstractEolienneEvent {

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

	public WindReadingEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
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
		return "Eolienne(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return	"time = " + this.getTimeOfOccurrence() + ", " +
				"wind = " + ((Reading)this.getEventInformation()).value
												+ " km/s";
	}
	
	@Override
	public void	executeOn(AtomicModel model)
	{
		assert	model instanceof EolienneModel ;

		EolienneModel m = (EolienneModel)model ;
		m.setProduction(((Reading)this.getEventInformation()).value);
	}
}
