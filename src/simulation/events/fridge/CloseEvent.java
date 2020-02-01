package simulation.events.fridge;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.fridge.FridgeModel;

//----------------------------------------------------------------------------
/**
* The class <code>CloseEvent</code> define an event sent by the fridge user to close the fridge door 
*
* 
* <p>
* Created on : 2020-01-27
* </p>
* 
* @author
*        <p>
*        Bah Thierno, Zheng Pascal
*        </p>
*/
public class CloseEvent  extends AbstractFridgeEvent
{

	private static final long serialVersionUID = 1L;


	/**
	 * create a new CloseEvent.
	 * 
	 *
	 * @param timeOfOccurrence	time of occurrence of the event.
	 */
	public				CloseEvent(
		Time timeOfOccurrence
		)
	{
		super(timeOfOccurrence, null) ;
	}

	@Override
	public boolean		hasPriorityOver(EventI e)
	{
		return true ;
	}

	@Override
	public String		eventAsString()
	{
		return "CloseEvent(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof FridgeModel;

		FridgeModel m = (FridgeModel) model;
		m.closeDoor();
	}
}
// -----------------------------------------------------------------------------
