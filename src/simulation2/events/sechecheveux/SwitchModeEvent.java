package simulation2.events.sechecheveux;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation2.models.sechecheveux.SecheCheveuxModel;
import wattwatt.tools.sechecheveux.SecheCheveuxMode;

public class SwitchModeEvent extends AbstractSecheCheveuxEvent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public SwitchModeEvent(Time timeOfOccurrence) {
		super(timeOfOccurrence, null);
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.Event#eventAsString()
	 */
	@Override
	public String eventAsString() {
		return "SecheCheveux::SwitchMode";
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.es.events.ES_Event#hasPriorityOver(fr.sorbonne_u.devs_simulation.models.events.EventI)
	 */
	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchOnEvent) {
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
		if (m.getMode() == SecheCheveuxMode.COLD_AIR) {
			m.setMode(SecheCheveuxMode.HOT_AIR);
		} else {
			m.setMode(SecheCheveuxMode.COLD_AIR);
		}
	}
}
