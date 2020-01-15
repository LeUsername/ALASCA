package simulation.events.washingmachine;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.washingmachine.WashingMachineModel;

public class EcoModeEvent extends AbstractWashingMachineEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EcoModeEvent(Time timeOfOccurrence) {
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
		return "EcoModeEvent(" + this.eventContentAsString() + ")" ;
	}
	
	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof WashingMachineModel;

		WashingMachineModel m = (WashingMachineModel) model;
		m.ecoLavage();
	}

}
