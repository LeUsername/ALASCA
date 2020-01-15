package simulation2.events.controleur;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation2.events.groupeelectrogene.ReplenishEvent;
import simulation2.models.groupeelectrogene.GroupeElectrogeneModel;

public class StartEngineGenerator extends AbstractControleurEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StartEngineGenerator(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	public StartEngineGenerator(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Controller::StartEngineGenerator";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof ReplenishEvent || e instanceof StopEngineGenerator) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeOn(AtomicModel model) {
		assert model instanceof GroupeElectrogeneModel;

		GroupeElectrogeneModel m = (GroupeElectrogeneModel) model;
		m.start();
	}
}
