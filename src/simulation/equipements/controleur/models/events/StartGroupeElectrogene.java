package simulation.equipements.controleur.models.events;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.equipements.groupeelectrogene.models.GroupeElectrogeneModel;
import simulation.equipements.groupeelectrogene.models.events.ReplenishEvent;

public class StartGroupeElectrogene extends AbstractControleurEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StartGroupeElectrogene(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, null);
	}

	@Override
	public String eventAsString() {
		return "Controller::StartEngineGenerator";
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
		m.start();
	}
}
