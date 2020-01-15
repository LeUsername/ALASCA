package simulation2.events.groupeelectrogene;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation2.models.groupeelectrogene.GroupeElectrogeneModel;

public class StopEvent extends AbstractGroupeElectrogeneEvent {

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
