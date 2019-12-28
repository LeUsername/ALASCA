package simulTest.deployment;

import java.util.HashMap;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.devs_simulation.simulators.SimulationEngine;
import simulTest.equipements.WattWattSupervisorComponent;
import simulTest.equipements.compteur.components.Compteur;
import simulTest.equipements.compteur.models.CompteurModel;
import simulTest.equipements.sechecheveux.components.SecheCheveux;
import simulTest.equipements.sechecheveux.models.SecheCheveuxModel;

public class CVM extends		AbstractCVM
{
	public				CVM() throws Exception
	{
		super() ;
		SimulationEngine.SIMULATION_STEP_SLEEP_TIME = 10L ;
	}

	/**
	 * @see fr.sorbonne_u.components.cvm.AbstractCVM#deploy()
	 */
	@Override
	public void			deploy() throws Exception
	{
		HashMap<String,String> hm = new HashMap<>() ;
		
		String secheCheveuxURI =
				AbstractComponent.createComponent(
						SecheCheveux.class.getCanonicalName(),
						new Object[]{}) ;
		hm.put(SecheCheveuxModel.URI, secheCheveuxURI);
		
		String compteurURI =
				AbstractComponent.createComponent(
						Compteur.class.getCanonicalName(),
						new Object[]{}) ;
		hm.put(CompteurModel.URI, compteurURI);
		
		@SuppressWarnings("unused")
		String supervisorURI =
			AbstractComponent.createComponent(
					WattWattSupervisorComponent.class.getCanonicalName(),
					new Object[]{hm}) ;

		super.deploy();
	}

	public static void	main(String[] args)
	{
		try {
			CVM c = new CVM() ;
			c.startStandardLifeCycle(10000L) ;
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
	}
}
