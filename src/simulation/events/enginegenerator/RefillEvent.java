package simulation.events.enginegenerator;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.enginegenerator.EngineGeneratorModel;

public class RefillEvent extends AbstractEngineGeneratorEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	public RefillEvent(Time timeOfOccurrence) {
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
		return "RefillEvent(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof EngineGeneratorModel;

		EngineGeneratorModel m = (EngineGeneratorModel) model;
		m.refill();
	}
	
}
