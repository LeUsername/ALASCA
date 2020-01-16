package simulation.events.hairdryer;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.hairdryer.HairDryerModel;
import wattwatt.tools.hairdryer.HairDryerMode;

public class SwitchModeEvent extends AbstractHairDryerEvent {
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
		return "HairDryer::SwitchMode";
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
		assert model instanceof HairDryerModel;
		
		HairDryerModel m = (HairDryerModel) model;
		if (m.getMode() == HairDryerMode.COLD_AIR) {
			m.setMode(HairDryerMode.HOT_AIR);
		} else {
			m.setMode(HairDryerMode.COLD_AIR);
		}
	}
}
