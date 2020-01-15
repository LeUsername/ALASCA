package simulation2.events.compteur;

import fr.sorbonne_u.devs_simulation.models.AtomicModel;
import fr.sorbonne_u.devs_simulation.models.events.EventInformationI;
import fr.sorbonne_u.devs_simulation.models.time.Time;
import simulation2.models.compteur.CompteurModel;

public class ConsommationEvent extends AbstractCompteurEvent {

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

	public ConsommationEvent(Time timeOfOccurrence, EventInformationI content) {
		super(timeOfOccurrence, content);
	}
	
	public ConsommationEvent(Time timeOfOccurrence, double content) {
		super(timeOfOccurrence, new Reading(content));
	}

	@Override
	public String eventAsString() {
		return "Compteur(" + this.eventContentAsString() + ")";
	}

	@Override
	public String eventContentAsString() {
		return	"temps = " + this.getTimeOfOccurrence() + ", " +
				"quantite = " + ((Reading)this.getEventInformation()).value
												+ " mAh";
	}
	
	@Override
	public void	executeOn(AtomicModel model)
	{
		assert	model instanceof CompteurModel ;

		CompteurModel m = (CompteurModel)model ;
		m.setConsommation(((Reading)this.getEventInformation()).value);
	}
}
