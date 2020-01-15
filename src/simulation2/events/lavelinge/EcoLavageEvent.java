package simulation2.events.lavelinge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation2.models.lavelinge.LaveLingeModel;

public class EcoLavageEvent extends AbstractLaveLingeEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EcoLavageEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if(e instanceof StartAtEvent) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String		eventAsString()
	{
		return "EcoLavage(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof LaveLingeModel;

		LaveLingeModel m = (LaveLingeModel) model;
		m.ecoLavage();
	}

}
