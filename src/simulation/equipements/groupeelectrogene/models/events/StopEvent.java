package simulation.equipements.groupeelectrogene.models.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.equipements.groupeelectrogene.models.GroupeElectrogeneModel;

public class StopEvent extends GroupeElectrogeneAbstractEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StopEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		return true ;
	}
	
	@Override
	public String		eventAsString()
	{
		return "StopEvent(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof GroupeElectrogeneModel;

		GroupeElectrogeneModel m = (GroupeElectrogeneModel) model;
		m.stop();
	}

}
