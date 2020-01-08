package simulation.equipements.groupeelectrogene.components;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.equipements.groupeelectrogene.models.GroupeElectrogeneModel;

public class GroupeElectrogeneSimulatorPlugin extends AtomicSimulatorPlugin{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof GroupeElectrogeneModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		if (name.equals("isOn")) {
			return ((GroupeElectrogeneModel) m).isOn();
		} else if (name.equals("production")) {
			return ((GroupeElectrogeneModel) m).getProduction();
		} else if (name.equals("capacity")) {
			return ((GroupeElectrogeneModel) m).getCapacity();
		} else if (name.equals("isEmpty")) {
			return ((GroupeElectrogeneModel) m).isEmpty();
		}
		else {
			assert name.equals("isFull");
			return ((GroupeElectrogeneModel) m).isFull();
		}
	}

}
