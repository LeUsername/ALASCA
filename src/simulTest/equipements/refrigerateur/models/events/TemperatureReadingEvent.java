package simulTest.equipements.refrigerateur.models.events;

import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;

public class TemperatureReadingEvent extends		AbstractRefrigerateurEvent
{
	// -------------------------------------------------------------------------
	// Inner classes
	// -------------------------------------------------------------------------

	public static class	Reading
	implements	EventInformationI
	{
		private static final long serialVersionUID = 1L;
		public final double	value ;

		public			Reading(double value)
		{
			super();
			this.value = value;
		}
	}

	// -------------------------------------------------------------------------
	// Constants and variables
	// -------------------------------------------------------------------------

	private static final long serialVersionUID = 1L ;

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	public				TemperatureReadingEvent(
		Time timeOfOccurrence,
		double TemperatureReading
		)
	{
		super(timeOfOccurrence, new Reading(TemperatureReading)) ;
	}

	// -------------------------------------------------------------------------
	// Methods
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#eventAsString()
	 */
	@Override
	public String		eventAsString()
	{
		return "TemperatureReadingEvent(" + this.eventContentAsString() + ")" ;
	}

	/**
	 * @see fr.sorbonne_u.devs_simulation.models.events.EventI#eventContentAsString()
	 */
	@Override
	public String		eventContentAsString()
	{
		return	"time = " + this.getTimeOfOccurrence() + ", " +
				"TemperatureReading = " + ((Reading)this.getEventInformation()).value
											+ " °C" ;
	}
}
