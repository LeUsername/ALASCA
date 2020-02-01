package simulation.events.enginegenerator;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.enginegenerator.EngineGeneratorModel;

//----------------------------------------------------------------------------
/**
* The class <code>StartEngineEvent</code> define an 
* event sent by the engine generator user 
* to start the engine generator fuel 
** 
* <p>
* Created on : 2020-01-27
* </p>
* 
* @author
*        <p>
*        Bah Thierno, Zheng Pascal
*        </p>
*/
public class StartEngineEvent extends AbstractEngineGeneratorEvent{

	
	private static final long serialVersionUID = 1L;

	/**
	 * Create an StartEvent sent by the engine generator user
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public StartEngineEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		if(e instanceof RefillEvent) {
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
		assert model instanceof EngineGeneratorModel;

		EngineGeneratorModel m = (EngineGeneratorModel) model;
		m.start();
	}
}
