package simulTest.compteur.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulTest.compteur.models.CompteurModel;

public class CompteurSimulatorPlugin extends AtomicSimulatorPlugin {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
		// The only model in this example that provides access to some value
		// is the CompeurModel
		assert m instanceof CompteurModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		if (name.equals("consommation")) {
			return ((CompteurModel) m).getConsommation();
		} else {
			System.out.println("NE devrait pas entrer ici");
			return null;
		}
	}

}
