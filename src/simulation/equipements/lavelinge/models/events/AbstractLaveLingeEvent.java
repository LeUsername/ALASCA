package simulation.equipements.lavelinge.models.events;

import fr.sorbonne_u.devs_simulation.es.events.ES_Event;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class AbstractLaveLingeEvent extends ES_Event {
	// -------------------------------------------------------------------------
		// Constants and variables
		// -------------------------------------------------------------------------

		private static final long serialVersionUID = 1L;

		// -------------------------------------------------------------------------
		// Constructors
		// -------------------------------------------------------------------------

		public AbstractLaveLingeEvent(Time timeOfOccurrence, EventInformationI content) {
			super(timeOfOccurrence, content);
		}

}
