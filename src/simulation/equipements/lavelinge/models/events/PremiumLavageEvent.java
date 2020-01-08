package simulation.equipements.lavelinge.models.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.equipements.lavelinge.models.LaveLingeModel;

public class PremiumLavageEvent extends AbstractLaveLingeEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PremiumLavageEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		return false;
	}
	
	@Override
	public String		eventAsString()
	{
		return "PremiumLavage(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof LaveLingeModel;

		LaveLingeModel m = (LaveLingeModel) model;
		m.premiumLavage();
	}

}
