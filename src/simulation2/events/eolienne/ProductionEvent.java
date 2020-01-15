package simulation2.events.eolienne;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation2.models.compteur.CompteurModel;

public class ProductionEvent extends AbstractEolienneEvent {

	public static class		Reading
	implements EventInformationI
	{
		private static final long serialVersionUID = 1L;
		public final double	value ;

		public			Reading(double value)
		{
			super();
			this.value = value;
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProductionEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	public ProductionEvent(Time timeOfOccurrence, double content) {
		super(timeOfOccurrence, new Reading(content));
	}

	@Override
	public String eventAsString() {
		return "Eolienne(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return	"temps = " + this.getTimeOfOccurrence() + ", " +
				"production = " + ((Reading)this.getEventInformation()).value
												+ " W";
	}
	
	@Override
	public void	executeOn(AtomicModel model)
	{
		assert	model instanceof CompteurModel ;

		CompteurModel m = (CompteurModel)model ;
		m.setProduction(((Reading)this.getEventInformation()).value);
	}
}
