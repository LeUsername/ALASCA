package simulation.events.hairdryer;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventI;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation.models.sechecheveux.HairDryerModel;

public class IncreasePowerEvent extends AbstractHairDryerEvent {
	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public IncreasePowerEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
		// TODO Auto-generated constructor stub
	}

	public IncreasePowerEvent(Time timeOfOccurrence) {
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
		return "HairDryer::IncreasePower";
	}

	@Override
	public boolean hasPriorityOver(EventI e) {
		if (e instanceof SwitchOnEvent || e instanceof SwitchModeEvent || e instanceof SwitchOffEvent
				|| e instanceof DecreasePowerEvent) {
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
		m.increasePower();
	}

}
