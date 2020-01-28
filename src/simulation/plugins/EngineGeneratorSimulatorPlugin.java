package simulation.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.enginegenerator.EngineGeneratorModel;

//------------------------------------------------------------------------------
/**
* The class <code>EngineGeneratorSimulatorPlugin</code> implements the simulation
* plug-in for the component <code>EngineGenerator</code>.
*
* 
* <p>
* Created on : 2020-01-27
* </p>
* 
* @author
*         <p>
*         Bah Thierno, Zheng Pascal
*         </p>
*/
public class EngineGeneratorSimulatorPlugin extends AtomicSimulatorPlugin{

	private static final long serialVersionUID = 1L;
	
	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);

		assert m instanceof EngineGeneratorModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		if (name.equals("isOn")) {
			return ((EngineGeneratorModel) m).isOn();
		} else if (name.equals("production")) {
			return ((EngineGeneratorModel) m).getProduction();
		} else if (name.equals("capacity")) {
			return ((EngineGeneratorModel) m).getCapacity();
		} else if (name.equals("isEmpty")) {
			return ((EngineGeneratorModel) m).isEmpty();
		}
		else {
			assert name.equals("isFull");
			return ((EngineGeneratorModel) m).isFull();
		}
	}

}
