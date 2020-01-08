package simulation.equipements.groupeelectrogene.models.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.equipements.groupeelectrogene.models.GroupeElectrogeneModel;

public class ReplenishEvent extends GroupeElectrogeneAbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public ReplenishEvent(Time timeOfOccurrence) {
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
		return "ReplenishEvent(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof GroupeElectrogeneModel;

		GroupeElectrogeneModel m = (GroupeElectrogeneModel) model;
		m.refill();
	}
	
}
