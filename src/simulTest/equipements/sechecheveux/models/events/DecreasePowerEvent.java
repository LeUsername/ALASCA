package simulTest.equipements.sechecheveux.models.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulTest.equipements.sechecheveux.models.SecheCheveuxModel;

public class DecreasePowerEvent extends AbstractSecheCheveuxEvent{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DecreasePowerEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	public DecreasePowerEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}
	
	@Override
	public String eventAsString() {
		return "SecheCheveux::DecreasePower";
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchOnEvent || e instanceof SwitchModeEvent || e instanceof SwitchOffEvent) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#executeOn(fr.sorbonne_u.devs_simulation.models.AtomicModel)
	 */
	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof SecheCheveuxModel;
		
		SecheCheveuxModel m = (SecheCheveuxModel) model;
		m.decreasePower();
	}

}
