package simulation.plugins;

import fr.sorbonne_u.components.cyphy.plugins.devs.AtomicSimulatorPlugin;
import fr.sorbonne_u.devs_simulation.interfaces.ModelDescriptionI;
import simulation.models.electricmeter.ElectricMeterModel;

//------------------------------------------------------------------------------
/**
* The class <code>ElectricMeterSimulatorPlugin</code> implements the simulation
* plug-in for the component <code>ElectricMeter</code>.
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
public class ElectricMeterSimulatorPlugin extends AtomicSimulatorPlugin {
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

		assert m instanceof ElectricMeterModel;
		// The following is the implementation of the protocol converting
		// names used by the caller to the values provided by the model;
		// alternatively, the simulation model could take care of the
		// link between names and values.
		ElectricMeterModel model = (ElectricMeterModel) m;

		if (name.equals("fridgeConsumption")) {
			return model.getFridgeConsumption();
		} else if (name.equals("hairDryerConsumption")) {
			return model.getHairDryerConsumption();
		} else {
			assert name.equals("washingMachineConsumption");
			return model.getWashingMachineConsumption();
		}
	}
}
