package simulation.equipements.controleur.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.equipements.controleur.models.ControllerModel;

public class ControleurSimulatorPlugin extends AtomicSimulatorPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof ControllerModel;

		// TODO
		return null;

	}

}
