package simulation2.events.controleur;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation2.events.groupeelectrogene.ReplenishEvent;
import simulation2.models.groupeelectrogene.GroupeElectrogeneModel;

public class StopEngineGenerator extends AbstractControleurEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StopEngineGenerator(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	public StopEngineGenerator(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Controller::StopEngineGenerator";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof ReplenishEvent) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof GroupeElectrogeneModel;

		GroupeElectrogeneModel m = (GroupeElectrogeneModel) model;
		m.stop();
	}
}
