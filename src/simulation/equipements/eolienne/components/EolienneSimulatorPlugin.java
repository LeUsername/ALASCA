package simulation.equipements.eolienne.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.equipements.eolienne.models.EolienneModel;

public class EolienneSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {

		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
	
		assert m instanceof EolienneModel;

		if (name.equals("production")) {
			return ((EolienneModel) m).getProduction();
		} else {
			assert name.equals("isOn");
			return ((EolienneModel) m).isOn();
		}
	}

}
