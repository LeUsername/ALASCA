package simulation2.events.groupeelectrogene;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation2.models.groupeelectrogene.GroupeElectrogeneModel;

public class StartEvent extends AbstractGroupeElectrogeneEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StartEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if(e instanceof ReplenishEvent) {
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String		eventAsString()
	{
		return "StartEvent(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof GroupeElectrogeneModel;

		GroupeElectrogeneModel m = (GroupeElectrogeneModel) model;
		m.start();
	}
}
