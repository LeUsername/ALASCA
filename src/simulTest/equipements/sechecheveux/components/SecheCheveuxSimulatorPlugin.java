package simulTest.equipements.sechecheveux.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulTest.equipements.sechecheveux.models.SecheCheveuxModel;

public class SecheCheveuxSimulatorPlugin extends AtomicSimulatorPlugin {
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

		assert m instanceof SecheCheveuxModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		if (name.equals("mode")) {
			return ((SecheCheveuxModel) m).getMode();
		} else if (name.equals("isOn")) {
			return ((SecheCheveuxModel) m).isOn();
		} else {
			assert name.equals("intensity");
			return ((SecheCheveuxModel) m).getIntensity();
		}
	}
}
