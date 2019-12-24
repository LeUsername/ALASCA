package simulTest.equipements.controleur.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulTest.equipements.controleur.models.ControleurModel;

public class ControleurSimulatorPlugin extends AtomicSimulatorPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof ControleurModel;

		// TODO
		return null;

	}

}
