package simulation.equipements.refrigerateur.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.equipements.refrigerateur.models.RefrigerateurModel;

public class RefrigerateurSimulatorPlugin extends AtomicSimulatorPlugin {
	private static final long serialVersionUID = 1L;

	/**
	 * @see fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin#getModelStateValue(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof RefrigerateurModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		if (name.equals("door")) {
			return ((RefrigerateurModel) m).getDoorState();
		} else if (name.equals("consumption")) {
			return ((RefrigerateurModel) m).getConsumptionState();
		} else if (name.equals("temperature")) {
			return ((RefrigerateurModel) m).getTemperature();
		}else {
			assert name.equals("intensity");
			return ((RefrigerateurModel) m).getIntensity();
		}
	}
}
