package simulation.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.washingmachine.WashingMachineModel;

//------------------------------------------------------------------------------
/**
* The class <code>WashingMachineSimulatorPlugin</code> implements the simulation
* plug-in for the component <code>WashingMachine</code>.
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
public class WashingMachineSimulatorPlugin extends AtomicSimulatorPlugin {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Object getModelStateValue(String modelURI, String name) throws Exception {
		// Get a Java reference on the object representing the corresponding
		// simulation model.
		ModelDescriptionI m = this.simulator.getDescendentModel(modelURI);
	
		assert m instanceof WashingMachineModel;

		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		if (name.equals("isOn")) {
			return ((WashingMachineModel) m).isOn();
		}else if(name.equals("consommation")){
			return ((WashingMachineModel) m).getIntensity();
		} else if(name.equals("lavageMode")) {
			return ((WashingMachineModel) m).getLavage();
		} else {
			assert name.equals("isWorking");
			return ((WashingMachineModel) m).isWorking();
		}
	}

}
